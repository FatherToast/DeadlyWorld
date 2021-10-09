package fathertoast.deadlyworld.common.block.properties;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.MainDimensionConfig;
import fathertoast.deadlyworld.common.core.config.SpawnerConfig;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

@MethodsReturnNonnullByDefault
public enum SpawnerType implements IStringSerializable {
    
    LONE( "lone", ( dimConfigs ) -> dimConfigs.SPAWNERS.LONE ),
    STREAM( "stream", ( dimConfigs ) -> dimConfigs.SPAWNERS.STREAM ),
    SWARM( "swarm", ( dimConfigs ) -> dimConfigs.SPAWNERS.SWARM ),
    BRUTAL( "brutal", ( dimConfigs ) -> dimConfigs.SPAWNERS.BRUTAL ) {
        @Override
        public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, World world, BlockPos pos ) {
            super.initEntity( entity, dimConfigs, world, pos );
            
            // Apply potion effects
            if( !(entity instanceof CreeperEntity) ) {
                final boolean hide = dimConfigs.SPAWNERS.BRUTAL.ambientFx.get();
                if( dimConfigs.SPAWNERS.BRUTAL.fireResistance.get() ) {
                    entity.addEffect( new EffectInstance( Effects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, hide, !hide ) );
                }
                if( dimConfigs.SPAWNERS.BRUTAL.waterBreathing.get() ) {
                    entity.addEffect( new EffectInstance( Effects.WATER_BREATHING, Integer.MAX_VALUE, 0, hide, !hide ) );
                }
            }
        }
    },
    NEST( "nest", "silverfish nest", ( dimConfigs ) -> dimConfigs.SPAWNERS.NEST );
    
    /** The unique id for this spawner type. This is used to save and load from disk. */
    final String ID;
    /** A human-readable name for this spawner type. Used in config descriptions, usually followed by " spawner" or " spawners". */
    public final String NAME;
    
    /** A function that returns the feature config associated with this spawner type for a given dimension config. */
    final Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> CONFIG_FUNCTION;
    
    SpawnerType( String idName, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> featureConfigFunc ) {
        this( idName, idName, featureConfigFunc );
    }
    
    SpawnerType( String id, String name, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction ) {
        ID = id;
        NAME = name;
        CONFIG_FUNCTION = configFunction;
    }
    
    @Override
    public String getSerializedName() { return ID; }
    
    @Override
    public String toString() { return getSerializedName(); }
    
    public SpawnerConfig.SpawnerTypeCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) { return CONFIG_FUNCTION.apply( dimConfigs ); }
    
    /* TODO - Move decoration to the Feature itself
    public abstract
    void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, DimensionConfig dimConfig, World world, Random random );
    */
    
    public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, World world, BlockPos pos ) {
        final SpawnerConfig.SpawnerTypeCategory config = getFeatureConfig( dimConfigs );
        
        // Apply attribute modifiers
        addModifier( entity, Attributes.FOLLOW_RANGE, config.addedFollowRange.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.MAX_HEALTH, config.addedMaxHealth.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.MAX_HEALTH, config.increasedMaxHealth.get(), AttributeModifier.Operation.MULTIPLY_BASE );
        addModifier( entity, Attributes.KNOCKBACK_RESISTANCE, config.addedKnockbackResist.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.ARMOR, config.addedArmor.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.ARMOR_TOUGHNESS, config.addedArmorToughness.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.ATTACK_DAMAGE, config.addedDamage.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.ATTACK_DAMAGE, config.increasedDamage.get(), AttributeModifier.Operation.MULTIPLY_BASE );
        addModifier( entity, Attributes.ATTACK_KNOCKBACK, config.addedKnockback.get(), AttributeModifier.Operation.ADDITION );
        addModifier( entity, Attributes.MOVEMENT_SPEED, config.increasedSpeed.get(), AttributeModifier.Operation.MULTIPLY_BASE );
        addModifier( entity, Attributes.FLYING_SPEED, config.increasedSpeed.get(), AttributeModifier.Operation.MULTIPLY_BASE );
        
        entity.setHealth( entity.getMaxHealth() );
    }
    
    /** Adds a custom attribute modifier to the entity. */
    private void addModifier( LivingEntity entity, Attribute attribute, double value, AttributeModifier.Operation operation ) {
        if( value != 0.0 ) {
            ModifiableAttributeInstance attributeInstance = entity.getAttribute( attribute );
            if( attributeInstance != null ) {
                attributeInstance.addPermanentModifier(
                        new AttributeModifier( DeadlyWorld.MOD_ID + ":" + ID + " spawner bonus", value, operation ) );
            }
        }
    }
    
    public static SpawnerType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to load invalid spawner type from index '{}'", index );
            return LONE;
        }
        return values()[index];
    }
    
    private static void addAttribute( LivingEntity entity, Attribute attribute, double amount ) {
        ModifiableAttributeInstance attributeInstance = entity.getAttribute( attribute );
        
        if( attributeInstance != null ) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue() + amount );
        }
    }
    
    private static void multAttribute( LivingEntity entity, Attribute attribute, double amount ) {
        ModifiableAttributeInstance attributeInstance = entity.getAttribute( attribute );
        
        if( attributeInstance != null ) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue() * amount );
        }
    }
}