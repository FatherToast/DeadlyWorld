package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;

public class MiniSpider extends Spider {
    
    public MiniSpider( EntityType<? extends Spider> entityType, Level level ) {
        super( entityType, level );
    }
    
    public static AttributeSupplier.Builder createAttributes() {
        return DWEntities.standardMiniAttributes( Spider.createAttributes(), 0.25 )
                .add( Attributes.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE.getDefaultValue() / 2.0 );
    }
    
    @Override
    public int getMaxAirSupply() { return 100; }
    
    @Override
    protected float getStandingEyeHeight( Pose pose, EntityDimensions entitySize ) { return 0.225F; }
}