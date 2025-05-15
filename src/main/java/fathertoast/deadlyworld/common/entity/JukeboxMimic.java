package fathertoast.deadlyworld.common.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class JukeboxMimic extends PathfinderMob implements Enemy {

    public final AnimationState idleAnimationState = new AnimationState();


    public JukeboxMimic( EntityType<? extends PathfinderMob> entityType, Level level ) {
        super( entityType, level );
        // Lol!
        setMaxUpStep( 1.0F );
    }


    public static AttributeSupplier.Builder createJukeboxMimicAttributes() {
        return Monster.createMonsterAttributes()
                .add( Attributes.MOVEMENT_SPEED, 0.30D )
                .add( Attributes.MAX_HEALTH, 15.0D );
    }


    @Override
    protected void registerGoals() {
        goalSelector.addGoal( 0, new FloatGoal( this ) );
        goalSelector.addGoal( 1, new MeleeAttackGoal( this, 1.0D, true ) );
        goalSelector.addGoal( 2, new WaterAvoidingRandomStrollGoal( this, 0.8D ) );
        goalSelector.addGoal( 5, new LookAtPlayerGoal( this, Player.class, 8.0F ) );
        goalSelector.addGoal( 5, new RandomLookAroundGoal( this ) );

        targetSelector.addGoal( 0, new HurtByTargetGoal( this ) );
        targetSelector.addGoal( 1, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
    }

    @Override
    public void tick() {
        if (level().isClientSide) {
            idleAnimationState.startIfStopped(tickCount);
        }
        super.tick();
    }

    @Override
    protected SoundEvent getHurtSound( DamageSource damageSource ) {
        return super.getHurtSound( damageSource );
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return super.getAmbientSound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return super.getDeathSound();
    }
}
