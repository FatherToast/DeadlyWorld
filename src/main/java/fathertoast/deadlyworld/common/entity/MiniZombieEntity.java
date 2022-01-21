package fathertoast.deadlyworld.common.entity;

import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;

public class MiniZombieEntity extends ZombieEntity {

    public MiniZombieEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30F)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.ARMOR, 0.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

    /** Do not allow for mini baby zombies to exist. */
    @Override
    public void setBaby(boolean isBaby) {
        super.setBaby(false);
    }

    @Override
    public boolean canBreakDoors() {
        return false;
    }

    // TODO - Consider mini drowned?
    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntitySize entitySize) {
        return 0.7F;
    }
}
