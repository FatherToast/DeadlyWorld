package fathertoast.deadlyworld.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

/**
 * Interface for blocks that have "trap" block entities
 * that needs to have their config-based logic setup on placement.
 */
public interface IDeadlyBlock {

    void initDeadly( ServerLevel level, BlockPos pos, RandomSource random );
}
