package fathertoast.deadlyworld.common.entity.ai;

import fathertoast.deadlyworld.common.entity.MicroFireballEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class MicroGhastFireballGoal extends Goal {

    private final GhastEntity ghast;
    public int chargeTime;

    public MicroGhastFireballGoal( GhastEntity ghast ) {
        this.ghast = ghast;
    }

    @Override
    public boolean canUse() {
        return ghast.getTarget() != null;
    }

    @Override
    public void start() {
        chargeTime = 0;
    }

    @Override
    public void stop() {
        ghast.setCharging(false);
    }

    @Override
    public void tick() {
        LivingEntity target = ghast.getTarget();

        if (target.distanceToSqr(ghast) < 256.0D && ghast.canSee(target)) {
            World world = ghast.level;
            ++chargeTime;

            if (this.chargeTime == 10 && !ghast.isSilent()) {
                world.levelEvent(null, 1015, ghast.blockPosition(), 0);
            }

            if (chargeTime == 20) {
                Vector3d viewVec = ghast.getViewVector(1.0F);
                double x = target.getX() - (ghast.getX() + viewVec.x * 4.0D);
                double y = target.getY(0.5D) - (0.5D + ghast.getY(0.5D));
                double z = target.getZ() - (ghast.getZ() + viewVec.z * 4.0D);

                if (!ghast.isSilent()) {
                    world.levelEvent(null, 1016, ghast.blockPosition(), 0);
                }
                MicroFireballEntity fireball = new MicroFireballEntity(world, ghast, x, y, z);
                fireball.setPos(ghast.getX() + viewVec.x * 4.0D, ghast.getY(0.5D) + 0.5D, fireball.getZ() + viewVec.z * 4.0D);
                world.addFreshEntity(fireball);
                chargeTime = -40;
            }
        }
        else if (chargeTime > 0) {
            --chargeTime;
        }
        ghast.setCharging(chargeTime > 10);
    }
}