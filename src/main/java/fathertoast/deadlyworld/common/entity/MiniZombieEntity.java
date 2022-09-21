package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

public class MiniZombieEntity extends ZombieEntity {
    
    public MiniZombieEntity( EntityType<? extends ZombieEntity> entityType, World world ) {
        super( entityType, world );
    }
    
    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return DWEntities.standardMiniAttributes( ZombieEntity.createAttributes(), 0.23 )
                .add( Attributes.FOLLOW_RANGE, 25.0 )
                .add( Attributes.ATTACK_DAMAGE, 1.5 );
    }
    
    /** Do not allow for mini baby zombies to exist. */
    @Override
    public void setBaby( boolean isBaby ) { super.setBaby( false ); }
    
    @Override
    public boolean canBreakDoors() { return false; }
    
    // Should we consider mini drowned?
    @Override
    protected boolean convertsInWater() { return false; }

    @Override
    public int getMaxAirSupply() { return 100; }
    
    @Override
    protected float getStandingEyeHeight( Pose pose, EntitySize entitySize ) {
        return 0.7F;
    }
}