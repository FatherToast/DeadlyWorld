package fathertoast.deadlyworld.common.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.CreeperEntity;

import java.util.EnumSet;

/**
 * Copy-pasted from {@link CreeperEntity}
 *
 * Slightly modified to reduce the distance
 * needed for the mini creeper to swell.
 */
public class MiniCreeperSwellGoal extends Goal {

    private final CreeperEntity creeper;
    private LivingEntity target;

    public MiniCreeperSwellGoal(CreeperEntity creeper) {
        this.creeper = creeper;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingEntity = this.creeper.getTarget();
        return this.creeper.getSwellDir() > 0 || livingEntity != null && this.creeper.distanceToSqr(livingEntity) < 2.5D;
    }

    @Override
    public void start() {
        this.creeper.getNavigation().stop();
        this.target = this.creeper.getTarget();
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.target == null) {
            this.creeper.setSwellDir(-1);
        }
        else if (this.creeper.distanceToSqr(this.target) > 18.0D) {
            this.creeper.setSwellDir(-1);
        }
        else if (!this.creeper.getSensing().canSee(this.target)) {
            this.creeper.setSwellDir(-1);
        }
        else {
            this.creeper.setSwellDir(1);
        }
    }
}