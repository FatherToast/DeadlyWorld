package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.util.References;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

public class MiniSpider extends Spider {
    
    public MiniSpider( EntityType<? extends Spider> entityType, Level level ) {
        super( entityType, level );
    }
    
    @Override
    public int getMaxAirSupply() { return 100; }
    
    @Override
    public float getVoicePitch() { return super.getVoicePitch() + References.MINI_PITCH_SHIFT; }
    
    @Override
    protected float getStandingEyeHeight( Pose pose, EntityDimensions entitySize ) { return 0.225F; }
}