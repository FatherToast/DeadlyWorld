package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.entity.ai.MicroGhastFireballGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.Random;

import static net.minecraft.entity.monster.MonsterEntity.isDarkEnoughToSpawn;

public class MicroGhastEntity extends GhastEntity {

    public MicroGhastEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
        xpReward = 1;
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return GhastEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 0.5F)
                .add(Attributes.FOLLOW_RANGE, 10.0D);
    }

    public static boolean checkMicroGhastSpawnRules(EntityType<? extends GhastEntity> entityType, IServerWorld world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getDifficulty() != Difficulty.PEACEFUL && isDarkEnoughToSpawn(world, pos, random) && checkMobSpawnRules(entityType, world, spawnReason, pos, random);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(5, new RandomFlyGoal(this));
        goalSelector.addGoal(7, new LookAroundGoal(this));
        goalSelector.addGoal(7, new MicroGhastFireballGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    protected float getStandingEyeHeight( Pose pose, EntitySize size ) {
        return 0.05F;
    }


    static class LookAroundGoal extends Goal {
        private final GhastEntity ghast;

        public LookAroundGoal( GhastEntity ghast ) {
            this.ghast = ghast;
            setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (ghast.getTarget() == null) {
                Vector3d vector3d = ghast.getDeltaMovement();
                ghast.yRot = -((float) MathHelper.atan2(vector3d.x, vector3d.z)) * (180F / (float)Math.PI);
                ghast.yBodyRot = ghast.yRot;
            }
            else {
                LivingEntity target = ghast.getTarget();

                if (target.distanceToSqr(ghast) < 4096.0D) {
                    double x = target.getX() - ghast.getX();
                    double z = target.getZ() - ghast.getZ();
                    ghast.yRot = -((float)MathHelper.atan2(x, z)) * (180F / (float)Math.PI);
                    ghast.yBodyRot = ghast.yRot;
                }
            }
        }
    }

    static class RandomFlyGoal extends Goal {
        private final GhastEntity ghast;

        public RandomFlyGoal( GhastEntity ghast ) {
            this.ghast = ghast;
            setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MovementController moveControl = ghast.getMoveControl();

            if (!moveControl.hasWanted()) {
                return true;
            }
            else {
                double x = moveControl.getWantedX() - ghast.getX();
                double y = moveControl.getWantedY() - ghast.getY();
                double z = moveControl.getWantedZ() - ghast.getZ();
                double dist = x * x + y * y + z * z;

                return dist < 1.0D || dist > 1800.0D;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            Random random = ghast.getRandom();

            double x = ghast.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);
            double y = ghast.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 4.0F);
            double z = ghast.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 8.0F);

            ghast.getMoveControl().setWantedPosition(x, y, z, 0.1D);
        }
    }
}
