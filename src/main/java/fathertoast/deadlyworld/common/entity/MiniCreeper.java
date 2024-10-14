package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.entity.ai.MiniSwellGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MiniCreeper extends Creeper {
    
    public MiniCreeper( EntityType<? extends Creeper> entityType, Level level ) { super( entityType, level ); }
    
    public static AttributeSupplier.Builder createAttributes() {
        return DWEntities.standardMiniAttributes( Creeper.createAttributes(), 0.25 );
    }
    
    @Override
    protected void registerGoals() {
        goalSelector.addGoal( 1, new FloatGoal( this ) );
        goalSelector.addGoal( 2, new MiniSwellGoal( this ) );
        goalSelector.addGoal( 3, new AvoidEntityGoal<>( this, Ocelot.class, 6.0F, 1.0, 1.2 ) );
        goalSelector.addGoal( 3, new AvoidEntityGoal<>( this, Cat.class, 6.0F, 1.0, 1.2 ) );
        goalSelector.addGoal( 4, new MeleeAttackGoal( this, 1.0, false ) );
        goalSelector.addGoal( 5, new WaterAvoidingRandomStrollGoal( this, 0.8 ) );
        goalSelector.addGoal( 6, new LookAtPlayerGoal( this, Player.class, 8.0F ) );
        goalSelector.addGoal( 6, new RandomLookAroundGoal( this ) );
        targetSelector.addGoal( 1, new NearestAttackableTargetGoal<>( this, Player.class, true ) );
        targetSelector.addGoal( 2, new HurtByTargetGoal( this ) );
    }
    
    @Override
    public int getMaxAirSupply() { return 100; }
    
    @Override
    protected void explodeCreeper() {
        if( !level().isClientSide ) {
            float f = (isPowered() ? 2.0F : 1.0F) / 2.0F; // Only change from vanilla method, halve explosion power
            dead = true;
            level().explode( this, getX(), getY(), getZ(), (float) explosionRadius * f, Level.ExplosionInteraction.MOB );
            discard();
            spawnLingeringCloud();
        }
    }
}