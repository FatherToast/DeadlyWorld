package fathertoast.deadlyworld.common.block.entity;

import fathertoast.crust.api.util.IDebugShape;
import fathertoast.crust.api.util.IDebugShapeProvider;
import fathertoast.crust.api.util.shape.SphereShape;
import fathertoast.deadlyworld.common.block.tower.TowerDispenserBlock;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.BaseTower;
import fathertoast.deadlyworld.common.world.logic.ITowerObject;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TowerDispenserBlockEntity extends BlockEntity implements ITowerObject, IDebugShapeProvider {
    
    protected final BaseTower towerLogic;
    protected final TowerType towerType;
    
    public TowerDispenserBlockEntity( BlockPos pos, BlockState state ) {
        this( DWBlockEntities.TOWER_DISPENSER.get(), pos, state );
    }
    
    public TowerDispenserBlockEntity( BlockEntityType<?> type, BlockPos pos, BlockState state ) {
        super( type, pos, state );
        towerType = ((TowerDispenserBlock) state.getBlock()).getTowerType();
        towerLogic = ((TowerDispenserBlock) state.getBlock()).newTowerLogic( this );
    }
    
    @Override
    public void load( CompoundTag loadTag ) {
        super.load( loadTag );
        towerLogic.load( level, worldPosition, loadTag );
    }
    
    @Override
    protected void saveAdditional( CompoundTag saveTag ) {
        super.saveAdditional( saveTag );
        towerLogic.save( saveTag );
    }
    
    public static void clientTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, TowerDispenserBlockEntity blockEntity ) {
        blockEntity.getTowerLogic().clientTick( level, pos );
    }
    
    public static void serverTick( Level level, BlockPos pos, @SuppressWarnings( "unused" ) BlockState state, TowerDispenserBlockEntity blockEntity ) {
        blockEntity.getTowerLogic().serverTick( (ServerLevel) level, pos );
    }
    
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create( this );
    }
    
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = saveWithoutMetadata();
        
        tag.remove( BaseTower.TAG_CHECK_SIGHT );
        tag.remove( BaseTower.TAG_MIN_ATTACK_DELAY );
        tag.remove( BaseTower.TAG_MAX_ATTACK_DELAY );
        tag.remove( BaseTower.TAG_ATTACK_DAMAGE );
        tag.remove( BaseTower.TAG_PROJECTILE_SPEED );
        tag.remove( BaseTower.TAG_PROJECTILE_VARIANCE );
        tag.remove( BaseTower.TAG_DELAY );
        
        return tag;
    }
    
    @Override
    public boolean triggerEvent( int id, int type ) {
        return level != null && towerLogic.onEventTriggered( level, id ) ||
                super.triggerEvent( id, type );
    }
    
    @Override
    public boolean onlyOpCanSetNbt() { return true; }
    
    public BaseTower getTowerLogic() { return towerLogic; }
    
    @Override // ITowerObject
    public void broadcastEvent( BaseTower trap, Level level, BlockPos pos, int eventId ) {
        level.blockEvent( pos, level.getBlockState( pos ).getBlock(), eventId, 0 );
    }
    
    @Override // ITowerObject
    public void spawnEffectParticle( BaseTower trap, Level level, BlockPos pos ) { }
    
    @Nullable
    @Override // IDebugShapeProvider
    public List<IDebugShape> getDebugShapes() {
        return List.of( new SphereShape( (float) getTowerLogic().getActivationRange() )
                .withColor( 1.0F, 0.0F, 0.0F )
        );
    }
}
