package fathertoast.deadlyworld.common.block.spawner;

import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.BaseTrap;
import fathertoast.deadlyworld.common.world.logic.ITrapObject;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DeadlyTrapBlockEntity extends BlockEntity implements ITrapObject {
    
    protected final BaseTrap trapLogic;
    
    public DeadlyTrapBlockEntity( BlockPos pos, BlockState state ) {
        this( DWBlockEntities.DEADLY_TRAP.get(), pos, state );
    }
    
    public DeadlyTrapBlockEntity( BlockEntityType<?> type, BlockPos pos, BlockState state ) {
        super( type, pos, state );
        trapLogic = ((DeadlyTrapBlock) state.getBlock()).newTrapLogic( this );
    }
    
    @Override
    public void load( CompoundTag loadTag ) {
        super.load( loadTag );
        trapLogic.load( level, worldPosition, loadTag );
    }
    
    @Override
    protected void saveAdditional( CompoundTag saveTag ) {
        super.saveAdditional( saveTag );
        trapLogic.save( saveTag );
    }
    
    public static void clientTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, DeadlyTrapBlockEntity blockEntity ) {
        blockEntity.getTrapLogic().clientTick( level, pos );
    }
    
    public static void serverTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, DeadlyTrapBlockEntity blockEntity ) {
        blockEntity.getTrapLogic().serverTick( (ServerLevel) level, pos );
    }
    
    public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create( this ); }
    
    @Override
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }
    
    @Override
    public boolean triggerEvent( int id, int type ) {
        return level != null && trapLogic.onEventTriggered( level, id ) ||
                super.triggerEvent( id, type );
    }
    
    @Override
    public boolean onlyOpCanSetNbt() { return true; }
    
    @Override // ITrapObject
    public void broadcastEvent( BaseTrap trap, Level level, BlockPos pos, int eventId ) {
        level.blockEvent( pos, level.getBlockState( pos ).getBlock(), eventId, 0 );
    }
    
    @Override // ITrapObject
    public void spawnEffectParticle( BaseTrap trap, Level level, BlockPos pos ) { }
    
    public BaseTrap getTrapLogic() { return trapLogic; }
}