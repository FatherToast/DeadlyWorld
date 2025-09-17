package fathertoast.deadlyworld.common.block.entity;

import fathertoast.deadlyworld.common.block.tower.TowerDispenserBlock;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.BaseTower;
import fathertoast.deadlyworld.common.world.logic.ITowerObject;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TowerDispenserBlockEntity extends BlockEntity implements ITowerObject {

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
    public boolean triggerEvent( int id, int type ) {
        return level != null && towerLogic.onEventTriggered( level, id ) ||
                super.triggerEvent( id, type );
    }

    @Override
    public boolean onlyOpCanSetNbt() { return true; }

    @Override // ITowerObject
    public void broadcastEvent( BaseTower trap, Level level, BlockPos pos, int eventId ) {
        level.blockEvent( pos, level.getBlockState( pos ).getBlock(), eventId, 0 );
    }

    @Override // ITowerObject
    public void spawnEffectParticle( BaseTower trap, Level level, BlockPos pos ) { }

    public BaseTower getTowerLogic() { return towerLogic; }
}
