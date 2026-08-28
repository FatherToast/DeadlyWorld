package fathertoast.deadlyworld.common.fishpranks;

import fathertoast.deadlyworld.api.registry.fishprank.IFishingPrank;
import fathertoast.deadlyworld.common.config.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class SnagPrank implements IFishingPrank {
    
    @Override
    public void prank( ServerLevel level, ServerPlayer player, Vec3 hookPos, Vec3 moveVec ) {
        final double speed = Config.FISHING_PRANKS.SNAG.speed.get();
        player.setDeltaMovement( // Invert the x and z directions, ensure y is always positive
                moveVec.x() * -speed, moveVec.y() * Math.abs( speed ), moveVec.z() * -speed );
        player.hurtMarked = true;
    }
}