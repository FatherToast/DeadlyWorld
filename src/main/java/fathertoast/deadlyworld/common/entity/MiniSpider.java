package fathertoast.deadlyworld.common.entity;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

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
    
    @Nullable
    @Override
    @SuppressWarnings( "ConstantConditions" )
    public SpawnGroupData finalizeSpawn( ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                         @Nullable SpawnGroupData groupData, @Nullable CompoundTag groupTag ) {
        groupData = super.finalizeSpawn( level, difficulty, spawnType, groupData, groupTag );
        
        if( Config.ENTITIES.MINIS.spookySpiderChance.rollChance( level.getRandom() ) ) {
            if( !hasCustomName() ) {
                setCustomName( Component.translatable( DWEntities.MINI_SPIDER.get().getDescriptionId() + ".spooky" ) );
                setCustomNameVisible( true );
            }
            getAttribute( Attributes.MAX_HEALTH ).addPermanentModifier( new AttributeModifier( "Spooky spider bonus", 34.0, AttributeModifier.Operation.ADDITION ) );
            getAttribute( Attributes.ATTACK_DAMAGE ).addPermanentModifier( new AttributeModifier( "Spooky spider bonus", 4.0, AttributeModifier.Operation.ADDITION ) );
            getAttribute( Attributes.ARMOR ).addPermanentModifier( new AttributeModifier( "Spooky spider bonus", 8.0, AttributeModifier.Operation.ADDITION ) );
            setHealth( getMaxHealth() );
        }
        
        return groupData;
    }
    
    @SuppressWarnings( "deprecation" )
    @Override
    public float getLightLevelDependentMagicValue() {
        Component name = getCustomName();
        return name != null && name.getContents() instanceof TranslatableContents ? 0.0F : super.getLightLevelDependentMagicValue();
    }
}