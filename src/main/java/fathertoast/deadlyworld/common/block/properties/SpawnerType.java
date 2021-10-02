package fathertoast.deadlyworld.common.block.properties;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.config.MainDimensionConfig;
import fathertoast.deadlyworld.common.core.config.SpawnerConfig;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

@MethodsReturnNonnullByDefault
public enum SpawnerType implements IStringSerializable {
    
    LONE( "lone", ( dimConfigs ) -> dimConfigs.SPAWNERS.LONE ),
    STREAM( "stream", ( dimConfigs ) -> dimConfigs.SPAWNERS.STREAM ),
    SWARM( "swarm", ( dimConfigs ) -> dimConfigs.SPAWNERS.SWARM ),
    NEST( "nest", "silverfish nest", ( dimConfigs ) -> dimConfigs.SPAWNERS.NEST ),
    BRUTAL( "brutal", ( dimConfigs ) -> dimConfigs.SPAWNERS.BRUTAL ) {
        @Override
        public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, World world, BlockPos pos ) {
            super.initEntity( entity, dimConfigs, world, pos );
            
            // Apply potion effects
            /* TODO Brutal spawner potion options
            if( !(entity instanceof CreeperEntity) ) {
                final boolean hide = dimConfigs.SPAWNER_BRUTAL.AMBIENT_FX;
                if( dimConfigs.SPAWNER_BRUTAL.FIRE_RESISTANCE ) {
                    entity.addEffect( new EffectInstance( Effects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, hide, !hide ) );
                }
                if( dimConfigs.SPAWNER_BRUTAL.WATER_BREATHING ) {
                    entity.addEffect( new EffectInstance( Effects.WATER_BREATHING, Integer.MAX_VALUE, 0, hide, !hide ) );
                }
            }
            */
        }
    };
    
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
        
        /* TODO Attribute options
        // Flat boosts
        if( config.ADDED_ARMOR > 0.0F ) {
            addAttribute( entity, Attributes.ARMOR, config.ADDED_ARMOR );
        }
        if( config.ADDED_KNOCKBACK_RESIST > 0.0F ) {
            addAttribute( entity, Attributes.KNOCKBACK_RESISTANCE, config.ADDED_KNOCKBACK_RESIST );
        }
        if( config.ADDED_ARMOR_TOUGHNESS > 0.0F ) {
            addAttribute( entity, Attributes.ARMOR_TOUGHNESS, config.ADDED_ARMOR_TOUGHNESS );
        }
        
        // Multipliers
        if( config.MULTIPLIER_DAMAGE != 1.0F ) {
            multAttribute( entity, Attributes.ATTACK_DAMAGE, config.MULTIPLIER_DAMAGE );
        }
        if( config.MULTIPLIER_HEALTH != 1.0F ) {
            multAttribute( entity, Attributes.MAX_HEALTH, config.MULTIPLIER_HEALTH );
        }
        if( config.MULTIPLIER_SPEED != 1.0F ) {
            multAttribute( entity, Attributes.MOVEMENT_SPEED, config.MULTIPLIER_SPEED );
        }
        */
        entity.setHealth( entity.getMaxHealth() );
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