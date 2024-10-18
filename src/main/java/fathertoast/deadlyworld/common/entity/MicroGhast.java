package fathertoast.deadlyworld.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class MicroGhast extends Ghast {
    
    public MicroGhast( EntityType<? extends Ghast> entityType, Level level ) {
        super( entityType, level );
        xpReward = 1;
        moveControl = new MicroGhastMoveControl( this );
    }
    
    public static boolean checkMicroGhastSpawnRules( EntityType<MicroGhast> entityType, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random ) {
        return level.getDifficulty() != Difficulty.PEACEFUL && random.nextInt( 20 ) == 0 && checkMobSpawnRules( entityType, level, spawnType, pos, random );
    }
    
    
    @Override
    protected void registerGoals() {
        goalSelector.addGoal( 5, new RandomFloatAroundGoal( this ) );
        goalSelector.addGoal( 7, new GhastLookGoal( this ) );
        goalSelector.addGoal( 7, new GhastShootFireballGoal( this ) );
        
        targetSelector.addGoal( 1, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
    }
    
    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }
    
    @Override
    protected float getStandingEyeHeight( Pose pose, EntityDimensions dimensions ) {
        return 0.04F;
    }
    
    
    static class GhastLookGoal extends Goal {
        private final Ghast ghast;
        
        public GhastLookGoal( Ghast ghast ) {
            this.ghast = ghast;
            setFlags( EnumSet.of( Goal.Flag.LOOK ) );
        }
        
        @Override
        public boolean canUse() {
            return true;
        }
        
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        
        @Override
        public void tick() {
            if( ghast.getTarget() == null ) {
                Vec3 vec3 = ghast.getDeltaMovement();
                ghast.setYRot( -((float) Mth.atan2( vec3.x, vec3.z )) * (180F / (float) Math.PI) );
                ghast.yBodyRot = ghast.getYRot();
            }
            else {
                LivingEntity target = ghast.getTarget();
                
                if( target.distanceToSqr( ghast ) < 1024.0D ) {
                    double x = target.getX() - ghast.getX();
                    double z = target.getZ() - ghast.getZ();
                    
                    ghast.setYRot( -((float) Mth.atan2( x, z )) * (180F / (float) Math.PI) );
                    ghast.yBodyRot = ghast.getYRot();
                }
            }
        }
    }
    
    static class MicroGhastMoveControl extends MoveControl {
        private final Ghast ghast;
        private int floatDuration;
        
        public MicroGhastMoveControl( Ghast ghast ) {
            super( ghast );
            this.ghast = ghast;
        }
        
        @Override
        public void tick() {
            if( operation == MoveControl.Operation.MOVE_TO ) {
                if( floatDuration-- <= 0 ) {
                    floatDuration += ghast.getRandom().nextInt( 5 ) + 2;
                    
                    Vec3 destVec = new Vec3(
                            wantedX - ghast.getX(),
                            wantedY - ghast.getY(),
                            wantedZ - ghast.getZ()
                    );
                    double vecLength = destVec.length();
                    destVec = destVec.normalize();
                    
                    if( canReach( destVec, Mth.ceil( vecLength ) ) ) {
                        ghast.setDeltaMovement( ghast.getDeltaMovement().add( destVec.scale( 0.025D ) ) );
                    }
                    else {
                        operation = MoveControl.Operation.WAIT;
                    }
                }
                
            }
        }
        
        private boolean canReach( Vec3 destination, int length ) {
            AABB boundingBox = ghast.getBoundingBox();
            
            for( int i = 1; i < length; ++i ) {
                boundingBox = boundingBox.move( destination );
                
                if( !ghast.level().noCollision( ghast, boundingBox ) ) {
                    return false;
                }
            }
            return true;
        }
    }
    
    static class GhastShootFireballGoal extends Goal {
        private final Ghast ghast;
        public int chargeTime;
        
        public GhastShootFireballGoal( Ghast ghast ) {
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
            ghast.setCharging( false );
        }
        
        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }
        
        @Override
        public void tick() {
            LivingEntity target = ghast.getTarget();
            
            if( target != null ) {
                if( target.distanceToSqr( ghast ) < 1024.0D && ghast.hasLineOfSight( target ) ) {
                    Level level = ghast.level();
                    ++chargeTime;
                    
                    if( chargeTime == 10 && !ghast.isSilent() ) {
                        level.levelEvent( null, 1015, ghast.blockPosition(), 0 );
                    }
                    
                    if( chargeTime == 20 ) {
                        Vec3 viewVec = ghast.getViewVector( 1.0F );
                        double x = target.getX() - (ghast.getX() + viewVec.x);
                        double y = target.getY( 0.5D ) - (ghast.getY( 0.5D ));
                        double z = target.getZ() - (ghast.getZ() + viewVec.z);
                        
                        if( !ghast.isSilent() ) {
                            level.levelEvent( null, 1016, ghast.blockPosition(), 0 );
                        }
                        MicroFireball fireball = new MicroFireball( ghast, x, y, z, level );
                        fireball.setPos( ghast.getX() + viewVec.x, ghast.getY( 0.5D ), fireball.getZ() + viewVec.z );
                        
                        level.addFreshEntity( fireball );
                        chargeTime = -40;
                    }
                }
                else if( chargeTime > 0 ) {
                    --chargeTime;
                }
                ghast.setCharging( chargeTime > 10 );
            }
        }
    }
    
    @Override
    public boolean shouldRenderAtSqrDistance( double dist ) {
        return dist < 1024.0D;
    }
    
    static class RandomFloatAroundGoal extends Goal {
        private final Ghast ghast;
        
        public RandomFloatAroundGoal( Ghast ghast ) {
            this.ghast = ghast;
            setFlags( EnumSet.of( Goal.Flag.MOVE ) );
        }
        
        @Override
        public boolean canUse() {
            MoveControl moveControl = ghast.getMoveControl();
            
            if( !moveControl.hasWanted() ) {
                return true;
            }
            else {
                double x = moveControl.getWantedX() - ghast.getX();
                double y = moveControl.getWantedY() - ghast.getY();
                double z = moveControl.getWantedZ() - ghast.getZ();
                double dist = x * x + y * y + z * z;
                
                return dist < 1.0D || dist > 3600.0D;
            }
        }
        
        @Override
        public boolean canContinueToUse() {
            return false;
        }
        
        @Override
        public void start() {
            RandomSource random = ghast.getRandom();
            
            double x = ghast.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 3.0F);
            double y = ghast.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 3.0F);
            double z = ghast.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 3.0F);
            
            ghast.getMoveControl().setWantedPosition( x, y, z, 0.5D );
        }
    }
}