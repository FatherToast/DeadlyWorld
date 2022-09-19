package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.entity.ai.MiniCreeperSwellGoal;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MiniCreeperEntity extends CreeperEntity {
    
    public MiniCreeperEntity( EntityType<? extends CreeperEntity> entityType, World world ) { super( entityType, world ); }
    
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return DWEntities.standardMiniAttributes( CreeperEntity.createAttributes(), 0.25 );
    }
    
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal( 1, new SwimGoal( this ) );
        this.goalSelector.addGoal( 2, new MiniCreeperSwellGoal( this ) );
        this.goalSelector.addGoal( 3, new AvoidEntityGoal<>( this, OcelotEntity.class, 6.0F, 1.0, 1.2 ) );
        this.goalSelector.addGoal( 3, new AvoidEntityGoal<>( this, CatEntity.class, 6.0F, 1.0, 1.2 ) );
        this.goalSelector.addGoal( 4, new MeleeAttackGoal( this, 1.0, false ) );
        this.goalSelector.addGoal( 5, new WaterAvoidingRandomWalkingGoal( this, 0.8 ) );
        this.goalSelector.addGoal( 6, new LookAtGoal( this, PlayerEntity.class, 8.0F ) );
        this.goalSelector.addGoal( 6, new LookRandomlyGoal( this ) );
        this.targetSelector.addGoal( 1, new NearestAttackableTargetGoal<>( this, PlayerEntity.class, true ) );
        this.targetSelector.addGoal( 2, new HurtByTargetGoal( this ) );
    }
    
    @Override
    public int getMaxAirSupply() { return 100; }
    
    @Override
    public void explodeCreeper() {
        if( !this.level.isClientSide ) {
            Explosion.Mode explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent( this.level, this ) ? Explosion.Mode.DESTROY : Explosion.Mode.NONE;
            float f = (this.isPowered() ? 2.0F : 1.0F) / 2.0F; // Only change from vanilla method, halve explosion power
            this.dead = true;
            this.level.explode( this, this.getX(), this.getY(), this.getZ(), (float) this.explosionRadius * f, explosion$mode );
            this.remove();
            this.spawnLingeringCloud();
        }
    }
}