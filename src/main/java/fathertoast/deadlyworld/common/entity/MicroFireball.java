package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;

@SuppressWarnings("resource")
public class MicroFireball extends Fireball {


    public MicroFireball( EntityType<? extends Fireball> entityType, Level level ) {
        super( entityType, level );
    }

    public MicroFireball( double x, double y, double z, double xPower, double yPower, double zPower, Level level ) {
        super( DWEntities.MICRO_FIREBALL.get(), x, y, z, xPower, yPower, zPower, level );
    }

    public MicroFireball( LivingEntity shooter, double xPower, double yPower, double zPower, Level level ) {
        super( DWEntities.MICRO_FIREBALL.get(), shooter, xPower, yPower, zPower, level);
    }


    @Override
    protected void onHitEntity( EntityHitResult entityResult ) {
        super.onHitEntity( entityResult );

        if ( !level().isClientSide ) {
            Entity target = entityResult.getEntity();
            Entity owner = getOwner();
            int remainingFireTicks = target.getRemainingFireTicks();
            target.setSecondsOnFire( 1 );

            if ( !target.hurt( damageSources().fireball( this, owner ), 5.0F ) ) {
                target.setRemainingFireTicks( remainingFireTicks );
            }
            else if ( owner instanceof LivingEntity livingOwner ) {
                // The owner is living, this is so cool u guys
                doEnchantDamageEffects( livingOwner, target );
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onHitBlock( BlockHitResult blockResult ) {
        super.onHitBlock( blockResult );

        if ( !level().isClientSide ) {

            if ( random.nextInt( 5 ) == 0 ) {
                Entity owner = getOwner();

                if ( !( owner instanceof Mob ) || ForgeEventFactory.getMobGriefingEvent( level(), owner ) ) {
                    BlockPos pos = blockResult.getBlockPos().relative( owner.getDirection() );

                    if ( level().isEmptyBlock( pos ) ) {
                        level().setBlockAndUpdate( pos, BaseFireBlock.getState( level(), pos ) );
                    }
                }
            }
            else {
                level().playSound( null, blockResult.getBlockPos(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.AMBIENT, 1.0F, 1.0F );
            }
        }
    }

    @Override
    protected void onHit( HitResult hitResult ) {
        super.onHit( hitResult );

        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurt( DamageSource damageSource, float damage ) {
        return false;
    }
}
