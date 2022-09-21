package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.world.World;

public class MiniSpiderEntity extends SpiderEntity {

    public MiniSpiderEntity(EntityType<? extends SpiderEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return DWEntities.standardMiniAttributes( SpiderEntity.createAttributes(), 0.25 )
                .add( Attributes.ATTACK_DAMAGE, Attributes.ATTACK_DAMAGE.getDefaultValue() / 2.0 );
    }

    @Override
    public int getMaxAirSupply() { return 100; }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entitySize) {
        return 0.225F;
    }
}
