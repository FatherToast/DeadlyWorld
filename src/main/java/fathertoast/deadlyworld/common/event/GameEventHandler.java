package fathertoast.deadlyworld.common.event;


import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.MiniArrow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Contains and automatically registers all needed forge events.
 */
@Mod.EventBusSubscriber( modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public final class GameEventHandler {
    /**
     * Called after ServerAboutToStartEvent and before ServerStartedEvent.
     * This event allows for customizations of the server.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onServerStarting( ServerStartingEvent event ) {
        Config.initializeDynamic( event.getServer() );
    }
    
    /**
     * Called during LivingEntity#actuallyHurt after all damage calculations, right before damage is applied.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onLivingDamage( LivingDamageEvent event ) {
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
    static void onEntityPlaceBlock( BlockEvent.EntityPlaceEvent event ) {
        if( event.getLevel() instanceof ServerLevel level ) {
            // Initialize placed blocks with configured settings
            if( event.getPlacedBlock().getBlock() instanceof DeadlySpawnerBlock spawner ) {
                spawner.initializeSpawner( level, event.getPos(), level.getRandom() );
            }
        }
    }
    
    /**
     * This event is fired on both sides whenever the player right clicks while targeting a block.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onRightClickBlock( PlayerInteractEvent.RightClickBlock event ) {
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
    static void onBlockBreak( BlockEvent.BreakEvent event ) {
        Level level = (Level) event.getLevel();
        BlockPos pos = event.getPos();

        // Below ocean and no skylight? Likely we are in a cave!
        if ( level.getBrightness( LightLayer.SKY, pos ) <= 2 && pos.getY() < level.getSeaLevel() ) {
            // Perform check a bit rarely, hopefully lowering the player's guard a bit
            if ( level.getRandom().nextInt( 10 ) == 0 ) {
                // Move up until we hit something solid or reach an offset of 10
                for ( int offset = 1; offset < 10; offset++ ) {
                    BlockState aboveState = level.getBlockState( pos.above( offset ) );

                    // Assume we hit the roof of a cave, check surrounding blocks
                    if ( aboveState.isSolidRender( level, pos ) ) {
                        for ( BlockPos p : BlockPos.betweenClosed( pos.offset( -1, offset - 1, -1 ), pos.offset( 1, offset + 1, 1) ) ) {
                            BlockState state = level.getBlockState( p );

                            if ( level instanceof ServerLevel && state.is( Blocks.POINTED_DRIPSTONE ) && state.getValue( PointedDripstoneBlock.TIP_DIRECTION ) == Direction.DOWN ) {
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
        spawnerBlockEntity.getSpawner().addSpawn(); // Let it spawn an extra mob, why not
        spawnerBlockEntity.setChanged();
        level.sendBlockUpdated( pos, spawner, spawner, Block.UPDATE_ALL );
        level.gameEvent( player, GameEvent.BLOCK_CHANGE, pos );
        if( !player.getAbilities().instabuild ) { // idk why the vanilla method doesn't need this, but we do
            spawnEgg.shrink( 1 );
        }
    }
}