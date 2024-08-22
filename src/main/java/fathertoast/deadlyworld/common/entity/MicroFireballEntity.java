package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;

public class MicroFireballEntity extends AbstractFireballEntity {

    public MicroFireballEntity( EntityType<? extends AbstractFireballEntity> entityType, World world ) {
        super(entityType, world);
    }

    public MicroFireballEntity( World world, LivingEntity shooter, double x, double y, double z ) {
        super( DWEntities.MICRO_FIREBALL.get(), shooter, x, y, z, world);
    }

    public MicroFireballEntity( World world, double x, double y, double z, double xPower, double yPower, double zPower ) {
        super( DWEntities.MICRO_FIREBALL.get(), x, y, z, xPower, yPower, zPower, world);
    }

    @Override
    protected void onHitEntity( EntityRayTraceResult result ) {
        super.onHitEntity(result);

        if (!level.isClientSide) {
            if (random.nextInt(5) == 0) {
                Entity entity = result.getEntity();

                if (!entity.fireImmune()) {
                    Entity owner = getOwner();
                    int remainingFireTicks = entity.getRemainingFireTicks();
                    entity.setSecondsOnFire(5);
                    boolean flag = entity.hurt(DamageSource.fireball(this, owner), 5.0F);

                    if (!flag) {
                        entity.setRemainingFireTicks(remainingFireTicks);
                    } else if (owner instanceof LivingEntity) {
                        doEnchantDamageEffects((LivingEntity) owner, entity);
                    }
                }
            }
            else {
                level.playSound(null, blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 1.3F, 0.5F);
            }
        }
    }

    @Override
    protected void onHitBlock( BlockRayTraceResult result ) {
        super.onHitBlock(result);

        if (!level.isClientSide) {
            if (random.nextInt(10) == 0) {
                Entity entity = this.getOwner();

                if (!(entity instanceof MobEntity) || ForgeEventFactory.getMobGriefingEvent(level, getEntity())) {
                    BlockPos blockpos = result.getBlockPos().relative(result.getDirection());

                    if (level.isEmptyBlock(blockpos)) {
                        level.setBlockAndUpdate(blockpos, AbstractFireBlock.getState(level, blockpos));
                    }
                }
            }
            else {
                level.playSound(null, blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundCategory.NEUTRAL, 1.3F, 0.5F);
            }
        }
    }

    @Override
    protected void onHit( RayTraceResult result ) {
        super.onHit(result);

        if (!level.isClientSide) {
            remove();
        }
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();

        if (level.isClientSide || (entity == null || !entity.removed) && level.hasChunkAt(blockPosition())) {
            super.tick();

            if (shouldBurn()) {
                setSecondsOnFire(1);
            }
            RayTraceResult result = ProjectileHelper.getHitResult(this, this::canHitEntity);
            if (result.getType() != RayTraceResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, result)) {
                onHit(result);
            }
            checkInsideBlocks();
            Vector3d deltaMovement = getDeltaMovement();
            double x = getX() + deltaMovement.x;
            double y = getY() + deltaMovement.y;
            double z = getZ() + deltaMovement.z;

            ProjectileHelper.rotateTowardsMovement(this, 0.2F);
            float inertia = getInertia();

            if (isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    level.addParticle(ParticleTypes.BUBBLE, x - deltaMovement.x * 0.25D, y - deltaMovement.y * 0.25D, z - deltaMovement.z * 0.25D, deltaMovement.x, deltaMovement.y, deltaMovement.z);
                }
                inertia = 0.8F;
            }
            setDeltaMovement(deltaMovement.add(xPower, yPower, zPower).scale(inertia));
            setPos(x, y, z);
        }
        else {
            remove();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt( DamageSource damageSource, float amount ) {
        return false;
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket( this );
    }
}
