package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class MiniZombie extends Zombie {
    
    public MiniZombie( EntityType<? extends Zombie> entityType, Level level ) {
        super( entityType, level );
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return DWEntities.standardMiniAttributes( Zombie.createAttributes(), 0.23 )
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
    protected float getStandingEyeHeight( Pose pose, EntityDimensions entitySize ) { return 0.7F; }
}