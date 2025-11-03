package fathertoast.deadlyworld.common.block.spike_trap;

import fathertoast.deadlyworld.common.block.IFeatureConfigProvider;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.SpikeTrapConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.util.DWDamageTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public enum SpikeTrapType implements IFeatureConfigProvider<SpikeTrapConfig.SpikeTrapTypeCategory> {
    
    MUNDANE( "mundane", ( dimConfigs ) -> dimConfigs.SPIKE_TRAPS.MUNDANE ),
    POISON( "poison", ( dimConfigs ) -> dimConfigs.SPIKE_TRAPS.POISON ) {
        @Override
        public void hurtEntity( Level level, BlockPos pos, LivingEntity entity ) {
            super.hurtEntity( level, pos, entity );
            
            SpikeTrapConfig.PotionSpikeTrapTypeCategory potionConfig =
                    (SpikeTrapConfig.PotionSpikeTrapTypeCategory) getConfig( level );
            final int effectDuration = potionConfig.effectDuration.get();
            final int effectAmpl = potionConfig.effectAmpl.get();
            
            entity.addEffect( new MobEffectInstance( MobEffects.POISON, effectDuration, effectAmpl ) );
        }
    },
    FIERY( "fiery", 7, ( dimConfigs ) -> dimConfigs.SPIKE_TRAPS.FIERY ) {
        @Override
        public void hurtEntity( Level level, BlockPos pos, LivingEntity entity ) {
            super.hurtEntity( level, pos, entity );
            
            SpikeTrapConfig.FierySpikeTrapTypeCategory fieryConfig =
                    (SpikeTrapConfig.FierySpikeTrapTypeCategory) getConfig( level );
            final int ticksOnFire = fieryConfig.secondsOnFire.get();
            
            entity.setSecondsOnFire( ticksOnFire );
        }
    },
    WITHERING( "withering", ( dimConfigs ) -> dimConfigs.SPIKE_TRAPS.WITHERING ) {
        @Override
        public void hurtEntity( Level level, BlockPos pos, LivingEntity entity ) {
            super.hurtEntity( level, pos, entity );
            
            SpikeTrapConfig.PotionSpikeTrapTypeCategory potionConfig =
                    (SpikeTrapConfig.PotionSpikeTrapTypeCategory) getConfig( level );
            final int effectDuration = potionConfig.effectDuration.get();
            final int effectAmpl = potionConfig.effectAmpl.get();
            
            entity.addEffect( new MobEffectInstance( MobEffects.WITHER, effectDuration, effectAmpl ) );
        }
    },
    MECHANICAL_MUNDANE( "mechanical_mundane", ( dimConfigs ) -> dimConfigs.SPIKE_TRAPS.MECHANICAL_MUNDANE ) {
        @Override
        public Supplier<BaseSpikeTrapBlock> getBlock() {
            return () -> new MechanicalSpikeTrapBlock( this );
        }
    },
    MECHANICAL_POISON( "mechanical_poison", ( dimConfigs ) -> dimConfigs.SPIKE_TRAPS.MECHANICAL_POISON ) {
        @Override
        public Supplier<BaseSpikeTrapBlock> getBlock() {
            return () -> new MechanicalSpikeTrapBlock( this );
        }
        
        @Override
        public void hurtEntity( Level level, BlockPos pos, LivingEntity entity ) {
            super.hurtEntity( level, pos, entity );
            
            SpikeTrapConfig.PotionSpikeTrapTypeCategory potionConfig =
                    (SpikeTrapConfig.PotionSpikeTrapTypeCategory) getConfig( level );
            final int effectDuration = potionConfig.effectDuration.get();
            final int effectAmpl = potionConfig.effectAmpl.get();
            
            entity.addEffect( new MobEffectInstance( MobEffects.POISON, effectDuration, effectAmpl ) );
        }
    },
    MECHANICAL_FIERY( "mechanical_fiery", 7, ( dimConfigs ) -> dimConfigs.SPIKE_TRAPS.MECHANICAL_FIERY ) {
        @Override
        public Supplier<BaseSpikeTrapBlock> getBlock() {
            return () -> new MechanicalSpikeTrapBlock( this );
        }
        
        @Override
        public void hurtEntity( Level level, BlockPos pos, LivingEntity entity ) {
            super.hurtEntity( level, pos, entity );
            
            SpikeTrapConfig.FierySpikeTrapTypeCategory fieryConfig =
                    (SpikeTrapConfig.FierySpikeTrapTypeCategory) getConfig( level );
            final int ticksOnFire = fieryConfig.secondsOnFire.get();
            
            entity.setSecondsOnFire( ticksOnFire );
        }
    },
    MECHANICAL_WITHERING( "mechanical_withering", ( dimConfigs ) -> dimConfigs.SPIKE_TRAPS.MECHANICAL_WITHERING ) {
        @Override
        public Supplier<BaseSpikeTrapBlock> getBlock() {
            return () -> new MechanicalSpikeTrapBlock( this );
        }
        
        @Override
        public void hurtEntity( Level level, BlockPos pos, LivingEntity entity ) {
            super.hurtEntity( level, pos, entity );
            
            SpikeTrapConfig.PotionSpikeTrapTypeCategory potionConfig =
                    (SpikeTrapConfig.PotionSpikeTrapTypeCategory) getConfig( level );
            final int effectDuration = potionConfig.effectDuration.get();
            final int effectAmpl = potionConfig.effectAmpl.get();
            
            entity.addEffect( new MobEffectInstance( MobEffects.WITHER, effectDuration, effectAmpl ) );
        }
    };
    
    
    public static final String BLOCK_CATEGORY = "spike_trap";
    
    /** The unique id for this spike trap type. This is used to save and load from disk. */
    private final String id;
    /** The light level of this type's spike trap block. */
    private final int lightLevel;
    
    /** A function that returns the feature config associated with this spike trap type for a given dimension config. */
    private final Function<DimensionConfigGroup, SpikeTrapConfig.SpikeTrapTypeCategory> configGetter;
    
    
    SpikeTrapType( String name, Function<DimensionConfigGroup, SpikeTrapConfig.SpikeTrapTypeCategory> configGetter ) {
        this( name, 0, configGetter );
    }
    
    SpikeTrapType( String name, int lightLevel, Function<DimensionConfigGroup, SpikeTrapConfig.SpikeTrapTypeCategory> configGetter ) {
        id = name;
        this.lightLevel = lightLevel;
        this.configGetter = configGetter;
    }
    
    /** Called when a living entity is standing inside a spike trap block. */
    public void hurtEntity( Level level, BlockPos pos, LivingEntity entity ) {
        float damage = getConfig( level ).damage.getFloat();
        entity.hurt( DWDamageTypes.of( level, DWDamageTypes.SPIKE_TRAP ), damage );
    }
    
    /** @return A Supplier of the spike trap block to register for this spike trap type */
    public Supplier<BaseSpikeTrapBlock> getBlock() { return () -> new BaseSpikeTrapBlock( this ); }
    
    /** @return The light level to be used by this type's spike trap block. */
    public int getLightLevel() {
        return lightLevel;
    }
    
    @Override
    public SpikeTrapConfig.SpikeTrapTypeCategory getConfig( Level level ) {
        return configGetter.apply( Config.getDimensionConfigs( level ) );
    }
    
    @Override
    public SpikeTrapConfig.SpikeTrapTypeCategory getConfig( DimensionConfigGroup dimConfig ) {
        return configGetter.apply( dimConfig );
    }
    
    /**
     * Returns a SpikeTrapType from ID.
     * If there exists no type with the given ID, we return null.
     *
     * @param ID The ID of the spike trap type.
     * @return A spike trap type matching the given ID.
     */
    @Nullable
    public static SpikeTrapType getFromID( String ID ) {
        for( SpikeTrapType spikeTrapType : values() ) {
            if( spikeTrapType.toString().equals( ID ) ) {
                return spikeTrapType;
            }
        }
        return null;
    }
    
    @Override
    public String toString() { return id; }
    
    public static SpikeTrapType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to fetch invalid spike trap type from index '{}'", index );
            return MUNDANE;
        }
        return values()[index];
    }
}
