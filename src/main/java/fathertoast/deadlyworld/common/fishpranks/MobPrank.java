package fathertoast.deadlyworld.common.fishpranks;

import fathertoast.deadlyworld.api.FishingPrank;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.phys.Vec3;

public class MobPrank implements FishingPrank {

    @Override
    public void prank( ServerLevel level, ServerPlayer player, Vec3 hookPos, Vec3 moveVec ) {

    }

    @Override
    public boolean canUse( ServerLevel level, ServerPlayer player, Vec3 hookPos ) {
        if ( level.getDifficulty() == Difficulty.PEACEFUL ) return false;

        // The body of water must be at least 2 blocks deep
        BlockPos pos = new BlockPos( (int) hookPos.x(), (int) hookPos.y(), (int) hookPos.z() );

        return level.getFluidState( pos.below() ).is( FluidTags.WATER )
                && level.getFluidState( pos.below( 2 ) ).is( FluidTags.WATER );
    }
}
