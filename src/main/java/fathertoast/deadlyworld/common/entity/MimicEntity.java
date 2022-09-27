package fathertoast.deadlyworld.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class MimicEntity extends MonsterEntity {
    
    public MimicEntity( EntityType<? extends MonsterEntity> entityType, World world ) {
        super( entityType, world );
        // Lol!
        maxUpStep = 1.0F;
    }
    
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add( Attributes.MOVEMENT_SPEED, 0.35 )
                .add( Attributes.MAX_HEALTH, 30.0 );
    }
    
    @Override
    protected void registerGoals() {
        goalSelector.addGoal( 1, new SwimGoal( this ) );
        goalSelector.addGoal( 3, new MeleeAttackGoal( this, 1.0, true ) );
        goalSelector.addGoal( 4, new WaterAvoidingRandomWalkingGoal( this, 0.8 ) );
        goalSelector.addGoal( 5, new LookAtGoal( this, PlayerEntity.class, 8.0F ) );
        goalSelector.addGoal( 5, new LookRandomlyGoal( this ) );
        
        targetSelector.addGoal( 1, new NearestAttackableTargetGoal<>( this, PlayerEntity.class, true ) );
        targetSelector.addGoal( 2, new HurtByTargetGoal( this ) );
    }
    
    @Override
    public boolean canBreatheUnderwater() { return true; }
}