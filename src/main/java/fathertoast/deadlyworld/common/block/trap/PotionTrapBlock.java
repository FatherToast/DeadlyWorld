package fathertoast.deadlyworld.common.block.trap;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.world.logic.BaseTrap;
import fathertoast.deadlyworld.common.world.logic.PotionTrap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class PotionTrapBlock extends DeadlyTrapBlock {

    public PotionTrapBlock() {
        super( TrapType.POTION );
    }

    @Override
    public BaseTrap newTrapLogic( DeadlyTrapBlockEntity blockEntity ) {
        return new PotionTrap( blockEntity ) {
            @Override
            public void triggerTrap(ServerLevel level, BlockPos pos ) {
                TrapType.POTION.triggerTrap( Config.getDimensionConfigs( level ), blockEntity );
            }
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state ) { return new PotionTrapBlockEntity( pos, state ); }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type ) {
        return getTicker( level, type, DWBlockEntities.POTION_TRAP.get() );
    }
}
