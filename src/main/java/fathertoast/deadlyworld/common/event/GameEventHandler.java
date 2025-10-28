package fathertoast.deadlyworld.common.event;


import fathertoast.crust.api.lib.DeferredAction;
import fathertoast.deadlyworld.api.IFishingPrank;
import fathertoast.deadlyworld.common.block.IDeadlyBlock;
import fathertoast.deadlyworld.common.block.infested.DeadlyInfestedBlock;
import fathertoast.deadlyworld.common.block.infested.InfestedBlockAutoGen;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.MiniArrow;
import fathertoast.deadlyworld.common.entity.YeetTnt;
import fathertoast.deadlyworld.common.item.EventItem;
import fathertoast.deadlyworld.common.item.SeaMineBlockItem;
import fathertoast.deadlyworld.common.network.NetworkHelper;
import fathertoast.deadlyworld.common.util.DWDamageTypes;
import fathertoast.deadlyworld.common.util.MimicHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.List;

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
     * Called when a living entity is ticked in {@link LivingEntity#tick()}.
     * If this event is canceled, the entity does not update.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onLivingTick( LivingEvent.LivingTickEvent event ) {
        ItemStack itemOnHead = event.getEntity().getItemBySlot( EquipmentSlot.HEAD );
        
        if( itemOnHead.getItem() instanceof SeaMineBlockItem seaMine ) {
            seaMine.onLivingUpdate( event.getEntity(), itemOnHead );
        }
    }
    
    /**
     * Called at the start of {@link LivingEntity#hurt(DamageSource, float)} before all damage calculations.
     * If the event is canceled, no damage is dealt.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onLivingAttack( LivingAttackEvent event ) {
        // Cancel the dummy damage we use to trigger silverfish call for help; it has already done its job
        if( event.getSource().is( DWDamageTypes.TRIGGER_SILVERFISH ) ) {
            event.setCanceled( true );
        }
    }
    
    /**
     * Called during {@link LivingEntity#actuallyHurt(DamageSource, float)} after all damage calculations,
     * right before damage is applied.
     * If the event is canceled, no damage is dealt - however, armor damage and other on hit effects may
     * have already been applied.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onLivingDamage( LivingDamageEvent event ) {
        // Too lazy to override the on hit method for the mini arrow entity, setting damage to 1.0 here instead
        // Note, this kinda makes them ignore armor/enchant damage reduction, but still consumes durability
        if( event.getSource().getDirectEntity() instanceof MiniArrow && event.getAmount() > 0.0F ) {
            event.setAmount( 1.0F );
        }
        
        // Negate all damage from Yeet TNT
        else if( event.getSource().getDirectEntity() instanceof YeetTnt ) {
            event.setAmount( 0.0F );
        }
    }
    
    /**
     * Called when a block is placed. If canceled, the block will not be placed.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onEntityPlaceBlock( BlockEvent.EntityPlaceEvent event ) {
        if( event.isCanceled() || !(event.getLevel() instanceof ServerLevel level) ) return;
        
        // Initialize manually placed blocks with configured settings
        if( event.getPlacedBlock().getBlock() instanceof IDeadlyBlock deadlyBlock ) {
            deadlyBlock.initDeadly( level, event.getPos(), level.getRandom() );
        }
    }
    
    /**
     * Called whenever an entity is spawned during {@link Level#addFreshEntity(Entity)} through
     * {@link net.minecraft.world.level.entity.PersistentEntitySectionManager#addEntity(EntityAccess, boolean)}.
     * If the event is canceled, the entity will not be spawned.
     * <p>
     * Note: This event may be called before the underlying chunk is fully loaded; you will cause chunk
     * loading deadlocks if you do not delay world interactions!
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onEntityJoinLevel( EntityJoinLevelEvent event ) {
        if( event.isCanceled() || !(event.getLevel() instanceof ServerLevel level) ) return;
        
        // Check event item trigger
        if( event.getEntity() instanceof ItemEntity itemEntity ) {
            ItemStack itemStack = itemEntity.getItem();
            if( itemStack.getItem() instanceof EventItem<?> eventItem ) {
                BlockPos pos = itemEntity.blockPosition();
                // Just delete the item without doing anything if spawned in an unloaded chunk for some reason
                if( level.isLoaded( pos ) ) {
                    eventItem.triggerEvent( level, pos, Blocks.AIR.defaultBlockState(),
                            Direction.UP, null, itemStack );
                }
                event.setCanceled( true );
            }
        }
    }
    
    /**
     * Called when a block is about to be broken by a player.
     * Canceling this event will prevent the block from being broken.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void onBlockBreak( BlockEvent.BreakEvent event ) {
        if( event.isCanceled() || !(event.getLevel() instanceof ServerLevel level) ) return;
        
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState( pos );
        BlockEntity blockEntity = level.getExistingBlockEntity( pos );
        
        if( blockEntity instanceof ChestBlockEntity chest ) {
            if( MimicHelper.spawnChestMimicFrom( level, pos, state, chest, event.getPlayer() ) ) {
                level.removeBlock( pos, false );
            }
        }
    }
    
    /**
     * This event is fired on both sides whenever the player right clicks while targeting a block.
     * <p>
     * Used to check container block inventories for traps.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.HIGH )
    public static void onRightClickContainer( PlayerInteractEvent.RightClickBlock event ) {
        // Prevent spectators from generating loot tables and/or spawning mimics
        if( event.isCanceled() || event.getEntity().isSpectator() || !(event.getLevel() instanceof ServerLevel level) )
            return;
        
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState( pos );
        BlockEntity blockEntity = level.getExistingBlockEntity( pos );
        if( blockEntity == null ) return;
        
        // Check event item trigger(s)
        if( blockEntity instanceof Container container ) {
            MimicHelper.triggerEventsFrom( level, pos, state, container, event.getEntity() );
            // Do not cancel; allow right click event to proceed as normal
        }
        // Check mimic trigger
        if( blockEntity instanceof ChestBlockEntity chest ) {
            if( MimicHelper.spawnChestMimicFrom( level, pos, state, chest, event.getEntity() ) ) {
                level.removeBlock( pos, false );
                event.setCancellationResult( InteractionResult.SUCCESS );
                event.setCanceled( true );
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
        if( event.isCanceled() || !(event.getLevel() instanceof ServerLevel level) ) return;
        
        // Handle infested block cleansing
        if( DeadlyInfestedBlock.tryCleanseBlock( event, level ) ) {
            event.setCancellationResult( InteractionResult.SUCCESS );
            event.setCanceled( true );
        }
        // Handle deadly spawner setting
        else if( DeadlySpawnerBlock.spawnEggUseOn( event, level ) ) {
            event.setCancellationResult( InteractionResult.CONSUME );
            event.setCanceled( true );
        }
    }
    
    /**
     * Called when a block is about to be broken by a player.
     * Canceling this event will prevent the block from being broken.
     * <p>
     * Used to trigger falling dripstone.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.LOWEST )
    public static void onBlockAboutToBreak( BlockEvent.BreakEvent event ) {
        if( event.isCanceled() || !Config.MAIN.STALACTITE_OVERHAUL.spookyStalactites.get() ||
                !(event.getLevel() instanceof ServerLevel level) ||
                !Config.MAIN.STALACTITE_OVERHAUL.triggerChance.rollChance( level.random ) ) {
            return;
        }
        
        // Below surface and no skylight? Likely we are in a cave!
        BlockPos pos = event.getPos();
        if( level.dimensionType().hasCeiling() || level.getBrightness( LightLayer.SKY, pos ) <= 2 &&
                pos.getY() < level.getHeight( Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ() ) - 2 ) {
            
            // Move up until we hit something solid or reach the max height specified in config
            for( int offset = 1; offset <= Config.MAIN.STALACTITE_OVERHAUL.scanHeight.get(); offset++ ) {
                BlockPos abovePos = pos.above( offset );
                
                // Assume we hit the roof of a cave, check surrounding blocks
                if( level.getBlockState( abovePos ).isSolidRender( level, abovePos ) ) {
                    int r = Config.MAIN.STALACTITE_OVERHAUL.scanRange.get();
                    for( BlockPos cursor : BlockPos.betweenClosed( abovePos.offset( -r, -1, -r ),
                            abovePos.offset( r, 1, r ) ) ) {
                        
                        BlockState state = level.getBlockState( cursor );
                        if( state.is( Blocks.POINTED_DRIPSTONE ) && state.getValue( PointedDripstoneBlock.TIP_DIRECTION ) == Direction.DOWN ) {
                            // Dripstone moment!
                            BlockPos targetPos = cursor.immutable();
                            DeferredAction.queue( level.random.nextInt( 15 ), () -> {
                                PointedDripstoneBlock.spawnFallingStalactite( state, level, targetPos );
                                return true;
                            } );
                        }
                    }
                    break;
                }
            }
        }
    }
    
    /**
     * Called when a player fishes an item.
     * Canceling will cause the player to receive no items, but the rod will still take any damage specified.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.LOW )
    public static void onItemFished( ItemFishedEvent event ) {
        if( !event.getEntity().level().isClientSide && Config.FISHING_PRANKS.GENERAL.prankChance.rollChance( event.getEntity().getRandom() ) ) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            ServerLevel level = (ServerLevel) player.level();
            FishingHook hook = event.getHookEntity();
            
            IFishingPrank prank = Config.FISHING_PRANKS.GENERAL.prankList.get().next( level.random );
            
            // Either prank can't be executed here or there are no valid available pranks
            if( prank == null || !prank.canUse( level, player, hook.position() ) ) return;
            
            final double xDist = player.getX() - hook.getX();
            final double yDist = player.getY() - hook.getY();
            final double zDist = player.getZ() - hook.getZ();
            final double mul = 0.1D;
            
            Vec3 moveVec = new Vec3(
                    xDist * mul,
                    yDist * mul + Math.sqrt( Math.sqrt( xDist * xDist + yDist * yDist + zDist * zDist ) ) * 0.08D,
                    zDist * mul
            );
            // Its prank time!
            prank.prank( level, player, hook.position(), moveVec );
            event.setCanceled( true );
        }
    }
    
    /**
     * Fires when a player joins the server or when the reload command is ran.
     * Send data pack data to clients when this event fires.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onDatapackSync( OnDatapackSyncEvent event ) {
        NetworkHelper.syncPlaceableFeatures( event.getPlayers() );
    }
    
    /**
     * Called when game data is being read when a save file is being loaded or a server is syncing
     * registry data to a client during connection handshake.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onMissingMappings( MissingMappingsEvent event ) {
        // Missing block mappings
        List<MissingMappingsEvent.Mapping<Block>> blockMappings = event.getMappings( Registries.BLOCK, DeadlyWorld.MOD_ID );
        InfestedBlockAutoGen.remapMissingBlocks( blockMappings );
    }
}