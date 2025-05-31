package fathertoast.deadlyworld.common.event;


import com.mojang.math.Axis;
import fathertoast.deadlyworld.common.block.IDeadlyBlock;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.tower.TowerDispenserBlock;
import fathertoast.deadlyworld.common.block.trap.DeadlyTrapBlock;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import fathertoast.deadlyworld.common.entity.ChestMimic;
import fathertoast.deadlyworld.common.entity.MiniArrow;
import fathertoast.deadlyworld.common.util.MimicHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Contains and automatically registers all needed forge events.
 */
@Mod.EventBusSubscriber( modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public final class GameEventHandler {
    //    /**
    //     * Called after ServerAboutToStartEvent and before ServerStartedEvent.
    //     * This event allows for customizations of the server.
    //     *
    //     * @param event The event data.
    //     */
    //    @SubscribeEvent( priority = EventPriority.NORMAL )
    //    static void onServerStarting( ServerStartingEvent event ) {
    //        Config.initializeDynamic( event.getServer() );
    //    }
    
    /**
     * Called during LivingEntity#actuallyHurt after all damage calculations, right before damage is applied.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onLivingDamage( LivingDamageEvent event ) {
        // Too lazy to override the on hit method for the mini arrow entity, setting damage to 1.0 here instead
        // Note, this kinda makes them ignore armor/enchant damage reduction, but still consumes durability
        if( event.getAmount() > 0.0F && event.getSource().getDirectEntity() instanceof MiniArrow ) {
            event.setAmount( 1.0F );
        }
    }
    
    /**
     * Called when a block is placed.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onEntityPlaceBlock( BlockEvent.EntityPlaceEvent event ) {
        if( event.getLevel() instanceof ServerLevel level ) {
            // Initialize placed blocks with configured settings
            if ( event.getPlacedBlock().getBlock() instanceof IDeadlyBlock deadlyBlock ) {
                deadlyBlock.initDeadly( level, event.getPos(), level.getRandom() );
            }
        }
    }

    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void onDestroyBlock( BlockEvent.BreakEvent event ) {
        if ( event.getLevel() instanceof ServerLevel serverLevel ) {
            BlockPos pos = event.getPos();
            BlockState state = serverLevel.getBlockState( pos );
            BlockEntity blockEntity = serverLevel.getExistingBlockEntity( pos );

            if ( blockEntity instanceof ChestBlockEntity chest ) {
                if ( MimicHelper.spawnChestMimicFrom( serverLevel, pos, state, chest, event.getPlayer() ) ) {
                    serverLevel.removeBlock( pos, false );
                    event.setCanceled( true );
                }
            }
        }
    }

    /**
     * This event is fired on both sides whenever the player right clicks while targeting a block.<br><br>
     * This specific handler method checks when the player right-clicks a chest, and if a Chest Mimic should spawn.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.HIGHEST )
    public static void onRightClickChest( PlayerInteractEvent.RightClickBlock event ) {
        // Prevent spectators from generating loot tables and/or spawning mimics
        if ( event.getEntity().isSpectator() ) return;

        BlockPos pos = event.getPos();
        Level level = event.getLevel();
        BlockState state = level.getBlockState( pos );
        BlockEntity blockEntity = level.getExistingBlockEntity( pos );

        if ( level instanceof ServerLevel serverLevel && blockEntity instanceof ChestBlockEntity chest ) {
            if ( MimicHelper.spawnChestMimicFrom( serverLevel, pos, state, chest, event.getEntity() ) ) {
                level.removeBlock( pos, false );
            }
        }
    }
    
    /**
     * This event is fired on both sides whenever the player right clicks while targeting a block.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onRightClickBlock( PlayerInteractEvent.RightClickBlock event ) {
        if( !event.isCanceled() && event.getLevel() instanceof ServerLevel level ) {
            Player player = event.getEntity();
            ItemStack itemStack = player.getItemInHand( event.getHand() );
            if( itemStack.getItem() instanceof SpawnEggItem ) {
                BlockPos pos = event.getPos();
                BlockState blockState = level.getBlockState( pos );
                if( blockState.getBlock() instanceof DeadlySpawnerBlock && level.getBlockEntity( pos ) instanceof DeadlySpawnerBlockEntity blockEntity ) {
                    spawnEggUseOnDWSpawner( level, player, pos, itemStack, blockState, blockEntity );
                    // Cancel the event; we've fully handled the interaction
                    event.setCancellationResult( InteractionResult.CONSUME );
                    event.setCanceled( true );
                }
            }
        }
    }
    
    /**
     * Fired when a player is about to destroy a block. Cancelable.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void onBlockBreak( BlockEvent.BreakEvent event ) {
        if ( !Config.MAIN.STALACTITE_OVERHAUL.spookyStalactites.get() ) return;

        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();
        
        // Below ocean and no skylight? Likely we are in a cave!
        if( level.getBrightness( LightLayer.SKY, pos ) <= 2 && pos.getY() < level.getSeaLevel() ) {

            if( Config.MAIN.STALACTITE_OVERHAUL.triggerChance.rollChance( level.random ) ) {
                // Move up until we hit something solid or reach the max height specified in config
                for( int offset = 1; offset < Config.MAIN.STALACTITE_OVERHAUL.scanHeight.get(); offset++ ) {
                    BlockState aboveState = level.getBlockState( pos.above( offset ) );
                    
                    // Assume we hit the roof of a cave, check surrounding blocks
                    if( aboveState.isSolidRender( level, pos ) ) {
                        for( BlockPos p : BlockPos.betweenClosed( pos.offset( -1, offset - 1, -1 ), pos.offset( 1, offset + 1, 1 ) ) ) {
                            BlockState state = level.getBlockState( p );
                            
                            if( level instanceof ServerLevel && state.is( Blocks.POINTED_DRIPSTONE ) && state.getValue( PointedDripstoneBlock.TIP_DIRECTION ) == Direction.DOWN ) {
                                // Dripstone moment!
                                PointedDripstoneBlock.spawnFallingStalactite( state, (ServerLevel) level, p );
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Modified copy-paste of the spawner portion of {@link SpawnEggItem#useOn(UseOnContext)}.
     */
    private static void spawnEggUseOnDWSpawner( ServerLevel level, Player player, BlockPos pos, ItemStack spawnEgg,
                                                BlockState spawner, DeadlySpawnerBlockEntity spawnerBlockEntity ) {
        EntityType<?> spawnEntity = ((SpawnEggItem) spawnEgg.getItem()).getType( spawnEgg.getTag() );
        spawnerBlockEntity.setEntityId( spawnEntity, level.getRandom() );
        spawnerBlockEntity.getSpawnerLogic().addSpawn(); // Let it spawn an extra mob, why not
        spawnerBlockEntity.setChanged();
        level.sendBlockUpdated( pos, spawner, spawner, Block.UPDATE_ALL );
        level.gameEvent( player, GameEvent.BLOCK_CHANGE, pos );
        if( !player.getAbilities().instabuild ) { // idk why the vanilla method doesn't need this, but we do
            spawnEgg.shrink( 1 );
        }
    }
}