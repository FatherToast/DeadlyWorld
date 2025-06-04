package fathertoast.deadlyworld.common.entity.ai;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/** Simple override of {@link NearestAttackableTargetGoal} that doesn't run in peaceful difficulty. */
public class PeacefulNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {

    public PeacefulNearestAttackableTargetGoal( Mob mob, Class<T> toTarget, boolean mustSee ) {
        this( mob, toTarget, 10, mustSee, false, null );
    }

    public PeacefulNearestAttackableTargetGoal( Mob mob, Class<T> toTarget, boolean mustSee, Predicate<LivingEntity> predicate ) {
        this( mob, toTarget, 10, mustSee, false, predicate );
    }

    public PeacefulNearestAttackableTargetGoal( Mob mob, Class<T> toTarget, boolean mustSee, boolean mustReach ) {
        this( mob, toTarget, 10, mustSee, mustReach, null );
    }

    public PeacefulNearestAttackableTargetGoal( Mob mob, Class<T> toTarget, int randomInterval, boolean mustSee,
                                                boolean mustReach, @Nullable Predicate<LivingEntity> predicate ) {
        super( mob, toTarget, randomInterval, mustSee, mustReach, predicate );
    }

    @Override
    protected boolean canAttack( @Nullable LivingEntity target, TargetingConditions conditions ) {
        if ( mob.level().getDifficulty() == Difficulty.PEACEFUL )
            return false;

        return super.canAttack( target, conditions );
    }
}
