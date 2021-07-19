package fathertoast.deadlyworld.common.block.properties;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
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

public enum SpawnerType implements IStringSerializable {

    LONE( "lone", ( dimConfig ) -> dimConfig.SPAWNER_LONE ),
    STREAM( "stream", ( dimConfig ) -> dimConfig.SPAWNER_STREAM ),
    SWARM( "swarm", ( dimConfig ) -> dimConfig.SPAWNER_SWARM ),
    NEST( "nest", ( dimConfig ) -> dimConfig.SPAWNER_NEST ),
    BRUTAL( "brutal" , ( dimConfig ) -> dimConfig.SPAWNER_BRUTAL ) {

        @Override
        public void initEntity( LivingEntity entity, Config dimConfig, World world, BlockPos pos ) {
            super.initEntity( entity, dimConfig, world, pos );

            // Apply potion effects
			if( !(entity instanceof CreeperEntity) ) {
                boolean hide = dimConfig.SPAWNER_BRUTAL.AMBIENT_FX;
                if (dimConfig.SPAWNER_BRUTAL.FIRE_RESISTANCE) {
                    entity.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, hide, !hide));
                }
                if (dimConfig.SPAWNER_BRUTAL.WATER_BREATHING) {
                    entity.addEffect(new EffectInstance(Effects.WATER_BREATHING, Integer.MAX_VALUE, 0, hide, !hide));
                }
            }
        }
    };


    SpawnerType(String name, Function<Config, Config.SpawnerFeatures> function ) {
        this.name = name;
        this.function = function;
    }

    final String name;
    final Function<Config, Config.SpawnerFeatures> function;


    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    // TODO - Toasty config stuff
    public Config.SpawnerFeatures getFeatureConfig( Config dimConfig ) {
        return this.function.apply( dimConfig );
    }

    // TODO - Move decoration to the Feature itself
    /*
    public abstract
    void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random );
     */

    public void initEntity( LivingEntity entity, Config dimConfig, World world, BlockPos pos ) {
        Config.SpawnerFeatures config = this.getFeatureConfig( dimConfig );

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
        entity.setHealth( entity.getMaxHealth( ) );
    }

    public static SpawnerType fromIndex( int index ) {
        if( index < 0 || index >= values( ).length ) {
            DeadlyWorld.LOG.warn( "Attempted to load invalid spawner type from index '{}'", index );
            return LONE;
        }
        return values( )[ index ];
    }

    private static void addAttribute(LivingEntity entity, Attribute attribute, double amount ) {
        ModifiableAttributeInstance attributeInstance = entity.getAttribute( attribute );

        if (attributeInstance != null) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue( ) + amount );
        }
    }

    private static void multAttribute( LivingEntity entity, Attribute attribute, double amount ) {
        ModifiableAttributeInstance attributeInstance = entity.getAttribute( attribute );

        if (attributeInstance != null) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue() * amount );
        }
    }
}
