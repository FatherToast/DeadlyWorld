package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import fathertoast.deadlyworld.common.core.registry.DWSoundEvents;
import fathertoast.deadlyworld.common.entity.ChestMimic;
import fathertoast.deadlyworld.common.item.EventItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public final class MimicHelper {
    
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
        ChestMimic chestMimic = DWEntities.CHEST_MIMIC.get().create( level );
        if( chestMimic != null ) {
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
                level.sendParticles( ParticleTypes.CLOUD,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        10,
                        level.random.nextGaussian(), level.random.nextGaussian(), level.random.nextGaussian(),
                        0.1 );
                
                // Copy items from chest and save them to the mimic
                chestMimic.setItems( chestBlockEntity.getItems() );
                chestBlockEntity.clearContent();
                return true;
            }
        }
        return false;
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
}