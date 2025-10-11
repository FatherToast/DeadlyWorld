package fathertoast.deadlyworld.common.block.entity;

import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.core.registry.DWSoundEvents;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

public class MiniChestBlockEntity extends ChestBlockEntity {
    
    private static final int CONTAINER_SIZE = 9;
    
    public MiniChestBlockEntity( BlockPos pos, BlockState state ) {
        super( DWBlockEntities.MINI_CHEST.get(), pos, state );
        this.items = NonNullList.withSize( CONTAINER_SIZE, ItemStack.EMPTY );
        this.openersCounter = new ContainerOpenersCounter() {
            @Override
            protected void onOpen( Level level, BlockPos pos, BlockState state ) {
                playSound( level, pos, DWSoundEvents.MINI_CHEST_OPEN.get() );
            }
            
            @Override
            protected void onClose( Level level, BlockPos pos, BlockState state ) {
                playSound( level, pos, DWSoundEvents.MINI_CHEST_CLOSE.get() );
            }
            
            @Override
            protected void openerCountChanged( Level level, BlockPos pos, BlockState state, int eventId, int eventData ) {
                MiniChestBlockEntity.this.signalOpenCount( level, pos, state, eventId, eventData );
            }
            
            @Override
            protected boolean isOwnContainer( Player player ) {
                if( player.containerMenu instanceof ChestMenu ) {
                    Container container = ((ChestMenu) player.containerMenu).getContainer();
                    return container == MiniChestBlockEntity.this
                            || container instanceof CompoundContainer
                            && ((CompoundContainer) container).contains( MiniChestBlockEntity.this );
                }
                return false;
            }
        };
    }
    
    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }
    
    @Override
    protected Component getDefaultName() {
        return Component.translatable( "container.deadlyworld.mini_chest" );
    }
    
    @Override
    protected AbstractContainerMenu createMenu( int id, Inventory inventory ) {
        return new ChestMenu( MenuType.GENERIC_9x1, id, inventory, this, 1 );
    }
    
    @Override
    @SuppressWarnings( "RedundantMethodOverride" )
    protected void signalOpenCount( Level level, BlockPos pos, BlockState state, int eventId, int eventData ) {
        Block block = state.getBlock();
        level.blockEvent( pos, block, 1, eventData );
    }
    
    static void playSound( Level level, BlockPos pos, SoundEvent soundEvent ) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.3D;
        double z = pos.getZ() + 0.5D;
        
        level.playSound( null, x, y, z, soundEvent, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F + References.MINI_PITCH_SHIFT );
    }
}