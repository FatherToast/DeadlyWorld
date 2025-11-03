package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.block.entity.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.entity.MiniSpawnerBlockEntity;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import fathertoast.deadlyworld.common.core.registry.DWSoundEvents;
import fathertoast.deadlyworld.common.entity.ChestMimic;
import fathertoast.deadlyworld.common.entity.JukeboxMimic;
import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import fathertoast.deadlyworld.common.item.EventItem;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public final class MimicHelper {
    
    // Event stuff here, "mimic adjacent"
    
    /**
     * Attempts to trigger an event item from a container at the given location.
     *
     * @param level     The world we live in. Absolutely mad.
     * @param pos       The block position of the container we are spawning from.
     * @param state     The block state of the container.
     * @param container The container.
     * @param player    Optionally the player who is triggering events.
     */
    public static void triggerEventsFrom( ServerLevel level, BlockPos pos, BlockState state,
                                          Container container, @Nullable Player player ) {
        if( container instanceof RandomizableContainerBlockEntity lootable ) lootable.unpackLootTable( player );
        
        Direction facing = state.hasProperty( BlockStateProperties.FACING ) ?
                state.getValue( BlockStateProperties.FACING ) : Direction.UP;
        
        // If inventory contains any event items, trigger them
        Iterator<ItemStack> itr = new ContainerIterator( container );
        while( itr.hasNext() ) {
            ItemStack item = itr.next();
            if( item.getItem() instanceof EventItem<?> eventItem ) {
                eventItem.triggerEvent( level, pos, state, facing, player, item );
                itr.remove();
            }
        }
    }
    
    private static class ContainerIterator implements Iterator<ItemStack> {
        final Container container;
        final int size;
        
        int index = 0;
        
        ContainerIterator( Container inv ) {
            container = inv;
            size = container.getContainerSize();
        }
        
        @Override
        public boolean hasNext() { return index < size; }
        
        @Override
        public ItemStack next() { return container.getItem( index++ ); }
        
        @Override
        public void remove() { container.setItem( index - 1, ItemStack.EMPTY ); }
    }
    
    
    // Now starting the actual mimic stuff
    
    /**
     * Attempts to spawn a Chest Mimic from a chest at the given location.
     *
     * @param level            The world we live in. Absolutely mad.
     * @param pos              The block position of the chest we are spawning from.
     * @param state            The block state of the chest.
     * @param chestBlockEntity The chest block entity.
     * @param player           Optionally the player who caused this mimic to be spawned.
     * @return True if the mimic was successfully spawned, false if not.
     */
    public static boolean spawnChestMimicFrom( ServerLevel level, BlockPos pos, BlockState state,
                                               ChestBlockEntity chestBlockEntity, @Nullable Player player ) {
        chestBlockEntity.unpackLootTable( player );
        
        // If inventory contains mimic core, do mimic stuff
        boolean spawnMimic = false;
        for( ItemStack itemStack : chestBlockEntity.getItems() ) {
            if( itemStack.getItem() == DWItems.MIMIC_CORE.get() ) {
                spawnMimic = true;
                break;
            }
        }
        if( !spawnMimic ) return false;
        
        // Spawn mimic!
        ChestMimic chestMimic = (state.getBlock() == DWBlocks.MINI_CHEST.get() ? DWEntities.MINI_CHEST_MIMIC :
                DWEntities.CHEST_MIMIC).get().create( level );
        if( chestMimic != null ) {
            ForgeEventFactory.onFinalizeSpawn( chestMimic, level, level.getCurrentDifficultyAt( pos ),
                    MobSpawnType.TRIGGERED, null, null );
            
            chestMimic.setDisguiseState( state );
            
            // Copy block rotation over to mimic for smoother transition
            Direction facing = state.hasProperty( ChestBlock.FACING ) ? state.getValue( ChestBlock.FACING ) :
                    Direction.Plane.HORIZONTAL.getRandomDirection( level.random );
            chestMimic.setPos( pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5 );
            chestMimic.setYHeadRot( facing.toYRot() );
            
            level.addFreshEntity( chestMimic );
            if( chestMimic.isAddedToWorld() ) {
                if( player != null ) chestMimic.setTarget( player );
                
                chestMimic.playSound( DWSoundEvents.MIMIC_APPEAR.get() );
                
                // Poof cloud
                TrapHelper.spawnPoofCloud( level, pos );
                
                // Copy items from chest and save them to the mimic
                chestMimic.setItems( chestBlockEntity.getItems() );
                chestBlockEntity.clearContent();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Attempts to spawn a spawner mimic at the given location, with the same
     * spawner logic as the provided deadly spawner block entity.
     *
     * @param pos                The block position to spawn the mimic at.
     * @param mimicType          The type of spawner mimic to spawn.
     * @param clearAbove         If true, the block at the position above the given block pos will be destroyed
     *                           to prevent the mimic from getting stuck.
     * @param spawnerBlockEntity The spawner block entity that this mimic should inherit its
     *                           spawner logic from.
     * @return True if a mimic was successfully spawned.
     */
    public static boolean spawnSpawnerMimicFrom( ServerLevel level, BlockPos pos, EntityType<? extends SpawnerMimic> mimicType,
                                                 boolean clearAbove, DeadlySpawnerBlockEntity spawnerBlockEntity ) {
        if( !spawnerBlockEntity.getSpawnerLogic().isMimic() ) return false;
        
        SpawnerMimic spawnerMimic = mimicType.create( level );
        
        if( spawnerMimic == null ) return false;
        
        ForgeEventFactory.onFinalizeSpawn( spawnerMimic, level, level.getCurrentDifficultyAt( pos ),
                MobSpawnType.TRIGGERED, null, null );
        
        if( spawnerBlockEntity instanceof MiniSpawnerBlockEntity ) {
            // Make mini mimics align better with their block state
            Vec3 offsets = spawnerBlockEntity.getEntityRenderOffset();
            spawnerMimic.setPos( pos.getX() + offsets.x(), pos.getY() + offsets.y() - 0.15, pos.getZ() + offsets.z() );
        }
        else {
            spawnerMimic.setPos( pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5 );
        }
        
        ProgressiveDelaySpawner oldSpawner = spawnerBlockEntity.getSpawnerLogic();
        ProgressiveDelaySpawner newSpawner = new ProgressiveDelaySpawner( oldSpawner.getSpawnerType(), spawnerMimic, spawnerMimic );
        newSpawner.load( level, pos, oldSpawner.save( new CompoundTag() ) );
        newSpawner.setMimic( false );
        spawnerMimic.setSpawner( newSpawner );
        
        level.addFreshEntity( spawnerMimic );
        
        if( spawnerMimic.isAddedToWorld() ) {
            // Destroy above block for mimics taller than one block, if clearAbove
            if( clearAbove && spawnerMimic.getBoundingBox().getYsize() > 1.0 ) {
                BlockState aboveState = level.getBlockState( pos.above() );
                // Only destroy if there is collision
                if( aboveState.blocksMotion() )
                    level.destroyBlock( pos.above(), false );
            }
            
            // Funny sound
            spawnerMimic.playSound( DWSoundEvents.MIMIC_APPEAR.get() );
            
            // Poof cloud
            TrapHelper.spawnPoofCloud( level, pos );
            return true;
        }
        return false;
    }
    
    /**
     * Attempts to spawn a jukebox mimic at the given location.
     *
     * @param pos        The block position to spawn the mimic at.
     * @param clearAbove If true, the block at the position above the given block pos will be destroyed
     *                   to prevent the mimic from getting stuck.
     * @return True if a mimic was successfully spawned.
     */
    public static boolean spawnJukeboxMimicFrom( ServerLevel level, BlockPos pos, boolean clearAbove ) {
        if( !(level.getBlockEntity( pos ) instanceof JukeboxBlockEntity jukeboxBlockEntity) ) return false;
        
        // We generally won't have a record in, since the pop out interaction has higher priority than putting the core in
        jukeboxBlockEntity.popOutRecord();
        
        JukeboxMimic jukeboxMimic = DWEntities.JUKEBOX_MIMIC.get().create( level );
        if( jukeboxMimic == null ) return false;
        
        ForgeEventFactory.onFinalizeSpawn( jukeboxMimic, level, level.getCurrentDifficultyAt( pos ),
                MobSpawnType.TRIGGERED, null, null );
        
        jukeboxMimic.setPos( pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5 );
        
        level.addFreshEntity( jukeboxMimic );
        
        if( jukeboxMimic.isAddedToWorld() ) {
            // Destroy above block for mimics taller than one block, if clearAbove
            if( clearAbove && jukeboxMimic.getBoundingBox().getYsize() > 1.0 ) {
                BlockState aboveState = level.getBlockState( pos.above() );
                // Only destroy if there is collision
                if( aboveState.blocksMotion() )
                    level.destroyBlock( pos.above(), false );
            }
            
            // Funny sound
            jukeboxMimic.playSound( DWSoundEvents.MIMIC_APPEAR.get() );
            
            // Poof cloud
            TrapHelper.spawnPoofCloud( level, pos );
            
            // Remove jukebox
            level.removeBlock( pos, false );
            return true;
        }
        return false;
    }
}