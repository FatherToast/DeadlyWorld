package fathertoast.deadlyworld.common.block.floor_trap;

import fathertoast.deadlyworld.common.block.ICamoTrap;
import fathertoast.deadlyworld.common.block.IDeadlyBlock;
import fathertoast.deadlyworld.common.block.entity.DeadlyTrapBlockEntity;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.BaseTrap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class FloorTrapBlock extends BaseEntityBlock implements ICamoTrap, IDeadlyBlock {

    private final FloorTrapType trapType;

    public FloorTrapBlock(FloorTrapType trapType ) {
        super( Config.BLOCKS.get( trapType ).adjustBlockProperties( BlockBehaviour.Properties.copy( Blocks.DISPENSER ) ) );
        this.trapType = trapType;
    }

    public FloorTrapType getTrapType() {
        return trapType;
    }

    public BaseTrap newTrapLogic( DeadlyTrapBlockEntity blockEntity ) {
        return new BaseTrap( trapType, blockEntity ) {
            @Override
            public void triggerTrap( ServerLevel level, BlockPos pos ) {
                trapType.triggerTrap( Config.getDimensionConfigs( level ), blockEntity );
            }
        };
    }

    @Override
    public void initDeadly(ServerLevel level, BlockPos pos, RandomSource random) {
        if( level.getBlockEntity( pos ) instanceof DeadlyTrapBlockEntity trapBlockEntity ) {
            trapBlockEntity.getTrapLogic().initializeTrap( level, pos, random );
        }
    }

    @Override
    public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) { return new DeadlyTrapBlockEntity( pos, state ); }
    
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState state, BlockEntityType<T> type ) {
        return getTicker( level, type, DWBlockEntities.DEADLY_TRAP.get() );
    }
    
    @Nullable
    public <T extends BlockEntity, V extends DeadlyTrapBlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockEntityType<T> type, BlockEntityType<V> expectedType ) {
        return createTickerHelper( type, expectedType,
                level.isClientSide ? DeadlyTrapBlockEntity::clientTick : DeadlyTrapBlockEntity::serverTick );
    }
    
    @Override
    public int getExpDrop( BlockState state, LevelReader level, RandomSource random, BlockPos pos, int fortune, int silkTouch ) {
        return 15 + random.nextInt( 15 ) + random.nextInt( 15 );
    }
    
    @Override
    public RenderShape getRenderShape( BlockState state ) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}