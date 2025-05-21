package fathertoast.deadlyworld.common.entity.ai;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.jetbrains.annotations.Nullable;

/** Simple override of {@link HurtByTargetGoal} that doesn't run in peaceful difficulty. */
public class PeacefulHurtByTargetGoal extends HurtByTargetGoal {

    public PeacefulHurtByTargetGoal( PathfinderMob pathfinderMob, Class<?>... ignoreDamageFor ) {
        super( pathfinderMob, ignoreDamageFor );
    }

    @Override
    protected boolean canAttack( @Nullable LivingEntity target, TargetingConditions conditions ) {
        if ( mob.level().getDifficulty() == Difficulty.PEACEFUL )
            return false;

        return super.canAttack( target, conditions );
    }
}
