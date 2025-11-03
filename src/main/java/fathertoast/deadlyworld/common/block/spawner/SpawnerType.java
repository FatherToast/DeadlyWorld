package fathertoast.deadlyworld.common.block.spawner;

import fathertoast.deadlyworld.common.block.IFeatureConfigProvider;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.SpawnerConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.Supplier;

public enum SpawnerType implements IFeatureConfigProvider<SpawnerConfig.SpawnerTypeCategory> {
    
    // Standalone features
    SIMPLE( "simple", ( dimConfigs ) -> dimConfigs.SPAWNERS.SIMPLE ),
    STREAM( "stream", ( dimConfigs ) -> dimConfigs.SPAWNERS.STREAM ),
    SWARM( "swarm", ( dimConfigs ) -> dimConfigs.SPAWNERS.SWARM ),
    BRUTAL( "brutal", ( dimConfigs ) -> dimConfigs.SPAWNERS.BRUTAL ) {
        /** Applies any additional modifiers to entities spawned by spawners of this type. */
        @Override
        public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, Level level, BlockPos pos ) {
            super.initEntity( entity, dimConfigs, level, pos );
            
            // Apply potion effects
            if( !(entity instanceof Creeper) ) {
                final boolean hide = dimConfigs.SPAWNERS.BRUTAL.ambientFx.get();
                if( dimConfigs.SPAWNERS.BRUTAL.fireResistance.get() ) {
                    entity.addEffect( new MobEffectInstance( MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, hide, !hide ) );
                }
                if( dimConfigs.SPAWNERS.BRUTAL.waterBreathing.get() ) {
                    entity.addEffect( new MobEffectInstance( MobEffects.WATER_BREATHING, MobEffectInstance.INFINITE_DURATION, 0, hide, !hide ) );
                }
            }
        }
    },
    FLOATY( "floaty", ( dimConfigs ) -> dimConfigs.SPAWNERS.FLOATY ) {
        /** Applies any additional modifiers to entities spawned by spawners of this type. */
        @Override
        public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, Level level, BlockPos pos ) {
            super.initEntity( entity, dimConfigs, level, pos );
            
            // Apply potion effects
            final boolean hide = dimConfigs.SPAWNERS.FLOATY.ambientFx.get();
            final int slowFallingAmlp = dimConfigs.SPAWNERS.FLOATY.slowFallingAmpl.get();
            final int jumpBoostAmpl = dimConfigs.SPAWNERS.FLOATY.jumpBoostAmpl.get();
            
            if( slowFallingAmlp > -1 ) {
                entity.addEffect( new MobEffectInstance( MobEffects.SLOW_FALLING, MobEffectInstance.INFINITE_DURATION, slowFallingAmlp, hide, !hide ) );
            }
            if( jumpBoostAmpl > -1 ) {
                entity.addEffect( new MobEffectInstance( MobEffects.JUMP, MobEffectInstance.INFINITE_DURATION, jumpBoostAmpl, hide, !hide ) );
            }
        }
    },
    NEST( "nest", "silverfish nests", ( dimConfigs ) -> dimConfigs.SPAWNERS.NEST ),
    MINI( "mini", ( dimConfigs ) -> dimConfigs.SPAWNERS.MINI ) {
        @Override
        public Supplier<DeadlySpawnerBlock> getBlock() { return MiniSpawnerBlock::new; }
    },
    
    // Subfeatures
    BURIED( "buried", true, ( dimConfigs ) -> dimConfigs.SPAWNERS.BURIED ),
    DUNGEON( "dungeon", true, ( dimConfigs ) -> dimConfigs.SPAWNERS.DUNGEON );
    
    public static final String BLOCK_CATEGORY = "spawner";
    
    /** The unique id for this spawner type. This is used to save and load from disk. */
    private final String id;
    /** A human-readable name for this spawner type. Used in config descriptions, usually followed by " spawner" or " spawners". */
    private final String displayName;
    /** A function that returns the feature config associated with this spawner type for a given dimension config. */
    private final Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configGetter;
    
    /** True if this spawner type is only used as part of another feature. */
    private final boolean subfeature;
    
    SpawnerType( String name, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction ) {
        this( name, false, configFunction );
    }
    
    SpawnerType( String name, boolean sub, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction ) {
        this( name, name.replace( "_", " " ) + " spawners", sub, configFunction );
    }
    
    SpawnerType( String name, String prettyName, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction ) {
        this( name, prettyName, false, configFunction );
    }
    
    SpawnerType( String name, String prettyName, boolean sub, Function<DimensionConfigGroup, SpawnerConfig.SpawnerTypeCategory> configFunction ) {
        id = name;
        displayName = prettyName;
        configGetter = configFunction;
        subfeature = sub;
    }
    
    public String getDisplayName() { return displayName; }
    
    /** @return True if this type is a subfeature; false if it is a standalone feature. */
    public final boolean isSubfeature() { return subfeature; }
    
    /** @return A Supplier of the Spawner Block to register for this Spawner Type */
    public Supplier<DeadlySpawnerBlock> getBlock() { return () -> new DeadlySpawnerBlock( this ); }
    
    /**
     * Returns a SpawnerType from ID.
     * If there exists no SpawnerType with the given ID, default to {@link SpawnerType#SIMPLE}
     *
     * @param ID The ID of the SpawnerType.
     * @return A SpawnerType matching the given ID.
     */
    public static SpawnerType getFromID( String ID ) {
        for( SpawnerType spawnerType : values() ) {
            if( spawnerType.toString().equals( ID ) ) {
                return spawnerType;
            }
        }
        return SIMPLE;
    }
    
    @Override
    public String toString() { return id; }
    
    @Override
    public SpawnerConfig.SpawnerTypeCategory getConfig( Level level ) {
        return configGetter.apply( Config.getDimensionConfigs( level ) );
    }
    
    @Override
    public SpawnerConfig.SpawnerTypeCategory getConfig( DimensionConfigGroup dimConfig ) {
        return configGetter.apply( dimConfig );
    }
    
    /** Applies any additional modifiers to entities spawned by spawners of this type. */
    public void initEntity( LivingEntity entity, DimensionConfigGroup dimConfigs, Level level, BlockPos pos ) {
        final SpawnerConfig.SpawnerTypeCategory config = getConfig( dimConfigs );
        config.attributeAdjustments.apply( entity );
        entity.setHealth( entity.getMaxHealth() );
    }
    
    public static SpawnerType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to fetch invalid spawner type from index '{}'", index );
            return SIMPLE;
        }
        return values()[index];
    }
    
    private static void addAttribute( LivingEntity entity, Attribute attribute, double amount ) {
        AttributeInstance attributeInstance = entity.getAttribute( attribute );
        
        if( attributeInstance != null ) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue() + amount );
        }
    }
    
    private static void multAttribute( LivingEntity entity, Attribute attribute, double amount ) {
        AttributeInstance attributeInstance = entity.getAttribute( attribute );
        
        if( attributeInstance != null ) {
            attributeInstance.setBaseValue( attributeInstance.getBaseValue() * amount );
        }
    }
}