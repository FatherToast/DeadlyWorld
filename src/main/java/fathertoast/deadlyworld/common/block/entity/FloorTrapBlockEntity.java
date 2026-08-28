package fathertoast.deadlyworld.common.block.entity;

import fathertoast.crust.api.util.IDebugShape;
import fathertoast.crust.api.util.IDebugShapeProvider;
import fathertoast.crust.api.util.shape.SphereShape;
import fathertoast.deadlyworld.api.registry.decoy.DecoyType;
import fathertoast.deadlyworld.api.registry.decoy.IDecoyProvider;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapBlock;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapType;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.BaseTrap;
import fathertoast.deadlyworld.common.world.logic.ITrapObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class FloorTrapBlockEntity extends BlockEntity implements ITrapObject, IDecoyProvider, IDebugShapeProvider {
    
    private static final BlockState DEFAULT_APPEARANCE = Blocks.DROPPER.defaultBlockState()
            .setValue( DropperBlock.FACING, Direction.UP );
    
    protected final BaseTrap trapLogic;
    protected final FloorTrapType trapType;
    
    public FloorTrapBlockEntity( BlockPos pos, BlockState state ) {
        this( DWBlockEntities.DEADLY_TRAP.get(), pos, state );
    }
    
    public FloorTrapBlockEntity( BlockEntityType<?> type, BlockPos pos, BlockState state ) {
        super( type, pos, state );
        trapType = ((FloorTrapBlock) state.getBlock()).getTrapType();
        trapLogic = ((FloorTrapBlock) state.getBlock()).newTrapLogic( this );
    }
    
    public BlockState getCamoState() {
        return trapLogic.getCamoState().isAir() ? DEFAULT_APPEARANCE : trapLogic.getCamoState();
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
    
    public static void clientTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, FloorTrapBlockEntity blockEntity ) {
        blockEntity.getTrapLogic().clientTick( level, pos );
    }
    
    public static void serverTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, FloorTrapBlockEntity blockEntity ) {
        blockEntity.getTrapLogic().serverTick( (ServerLevel) level, pos );
    }
    
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = saveWithoutMetadata();
        
        tag.remove( BaseTrap.TAG_CHECK_SIGHT );
        tag.remove( BaseTrap.TAG_MIN_RESET_TIME );
        tag.remove( BaseTrap.TAG_MAX_RESET_TIME );
        tag.remove( BaseTrap.TAG_MAX_TRIGGER_DELAY );
        tag.remove( BaseTrap.TAG_TRIGGERS_REMAINING );
        tag.remove( BaseTrap.TAG_DELAY );
        
        return tag;
    }
    
    @Override
    public boolean triggerEvent( int id, int type ) {
        return level != null && trapLogic.onEventTriggered( level, id ) ||
                super.triggerEvent( id, type );
    }
    
    @Override
    public boolean onlyOpCanSetNbt() { return true; }
    
    @Override
    public AABB getRenderBoundingBox() {
        // Return a bigger box to account for decoys
        return new AABB( getBlockPos() ).inflate( 5.0 );
    }
    
    public BaseTrap getTrapLogic() { return trapLogic; }
    
    @Override // ITrapObject
    public void broadcastEvent( BaseTrap trap, Level level, BlockPos pos, int eventId ) {
        level.blockEvent( pos, level.getBlockState( pos ).getBlock(), eventId, 0 );
    }
    
    @Override // ITrapObject
    public void spawnEffectParticle( BaseTrap trap, Level level, BlockPos pos ) { }
    
    @Override
    @Nullable // IDecoyProvider
    public Level getProviderLevel() {
        return getLevel();
    }
    
    @Override // IDecoyProvider
    public BlockPos getProviderPos() {
        return getBlockPos();
    }
    
    @Override // IDecoyProvider
    @Nullable
    public DecoyType getDecoyType() {
        return getTrapLogic().getDecoyType();
    }
    
    @Override // IDecoyProvider
    public boolean isDecoyActive() {
        return trapLogic.getDecoyType() != null;
    }
    
    @Nullable
    @Override // IDebugShapeProvider
    public List<IDebugShape> getDebugShapes() {
        return List.of( new SphereShape( (float) getTrapLogic().getActivationRange() )
                .withColor( 0.0F, 0.0F, 1.0F )
        );
    }
}