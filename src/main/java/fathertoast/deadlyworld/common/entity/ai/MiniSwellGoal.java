package fathertoast.deadlyworld.common.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Creeper;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * Copy-pasted from Creeper's {@link net.minecraft.world.entity.ai.goal.SwellGoal}.
 * <p>
 * Slightly modified to reduce the distance needed for the mini creeper to swell.
 */
public class MiniSwellGoal extends Goal {
    
    private final Creeper smolCreeper;
    @Nullable
    private LivingEntity target;
    
    public MiniSwellGoal( Creeper creeper ) {
        smolCreeper = creeper;
        setFlags( EnumSet.of( Flag.MOVE ) );
    }
    
    @Override
    public boolean canUse() {
        LivingEntity maybeTarget = smolCreeper.getTarget();
        return smolCreeper.getSwellDir() > 0 || maybeTarget != null && smolCreeper.distanceToSqr( maybeTarget ) < 2.5;
    }
    
    @Override
    public void start() {
        smolCreeper.getNavigation().stop();
        target = smolCreeper.getTarget();
    }
    
    @Override
    public void stop() { target = null; }
    
    @Override
    public boolean requiresUpdateEveryTick() { return true; }
    
    @Override
    public void tick() {
        if( target == null ) {
            smolCreeper.setSwellDir( -1 );
        }
        else if( smolCreeper.distanceToSqr( target ) > 18.0 ) {
            smolCreeper.setSwellDir( -1 );
        }
        else if( !smolCreeper.getSensing().hasLineOfSight( target ) ) {
            smolCreeper.setSwellDir( -1 );
        }
        else {
            smolCreeper.setSwellDir( 1 );
        }
    }
}