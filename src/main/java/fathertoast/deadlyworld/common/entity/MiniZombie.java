package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.util.References;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class MiniZombie extends Zombie {
    
    public MiniZombie( EntityType<? extends Zombie> entityType, Level level ) {
        super( entityType, level );
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
    public float getVoicePitch() { return super.getVoicePitch() + References.MINI_PITCH_SHIFT; }
    
    @Override
    protected float getStandingEyeHeight( Pose pose, EntityDimensions entitySize ) { return 0.7F; }
}