package fathertoast.deadlyworld.common.block.tower;

import fathertoast.deadlyworld.common.block.trap.DeadlyTrapBlockEntity;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TowerDispenserBlock extends BaseEntityBlock {

    private final TowerType towerType;

    public TowerDispenserBlock( TowerType type ) {
        super( Config.BLOCKS.get( type ).adjustBlockProperties( BlockBehaviour.Properties.copy( Blocks.DISPENSER ) ) );
        towerType = type;
    }

    public void initializeTrap(ServerLevel level, BlockPos pos, RandomSource random ) {
        if( level.getBlockEntity( pos ) instanceof TowerDispenserBlockEntity towerBlockEntity ) {
           // towerBlockEntity.getTowerLogic().initializeTrap( level, pos, random );
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity( BlockPos pos, BlockState state ) {
        return new TowerDispenserBlockEntity( pos, state );
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState state, BlockEntityType<T> type ) {
        return getTicker( level, type, DWBlockEntities.TOWER_DISPENSER.get() );
    }

    @Nullable
    public <T extends BlockEntity, V extends TowerDispenserBlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockEntityType<T> type, BlockEntityType<V> expectedType ) {
        return createTickerHelper( type, expectedType,
                level.isClientSide ? TowerDispenserBlockEntity::clientTick : TowerDispenserBlockEntity::serverTick );
    }
}
