package fathertoast.deadlyworld.common.config.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.deadlyworld.common.config.levelgen.setting.FloatFieldSetting;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWFieldProviders;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.util.valueproviders.UniformFloat}.
 */
public class ConfigUniformFloatProvider extends FloatProvider {
    public static final Codec<ConfigUniformFloatProvider> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            FloatFieldSetting.CODEC.fieldOf( "min_inclusive" ).forGetter( ConfigUniformFloatProvider::getMinInclusive ),
            FloatFieldSetting.CODEC.fieldOf( "max_exclusive" ).forGetter( ConfigUniformFloatProvider::getMaxExclusive )
    ).apply( instance, ConfigUniformFloatProvider::new ) );
    
    public static ConfigUniformFloatProvider of( DoubleField.RandomRange range ) { return of( range.getMinField(), range.getMaxField() ); }
    
    public static ConfigUniformFloatProvider of( DoubleField min, DoubleField max ) {
        return new ConfigUniformFloatProvider( new FloatFieldSetting( min ), new FloatFieldSetting( max ) );
    }
    
    private final FloatFieldSetting minInclusive;
    private final FloatFieldSetting maxExclusive;
    
    private ConfigUniformFloatProvider( FloatFieldSetting min, FloatFieldSetting max ) {
        minInclusive = min;
        maxExclusive = max;
    }
    
    public FloatFieldSetting getMinInclusive() { return minInclusive; }
    
    public FloatFieldSetting getMaxExclusive() { return maxExclusive; }
    
    @Override
    public float sample( RandomSource random ) {
        Float min = minInclusive.getRaw();
        Float max = maxExclusive.getRaw();
        
        if( min == null || max == null ) {
            DeadlyWorld.LOG.error( "Invalid uniform int range: {}", this );
            return Integer.MAX_VALUE;
        }
        if( min > max ) {
            DeadlyWorld.LOG.warn( "Empty uniform int range: {}", this );
            return min;
        }
        return Mth.randomBetween( random, min, max );
    }
    
    @Override
    public float getMinValue() { return minInclusive.get( Integer.MAX_VALUE ); }
    
    @Override
    public float getMaxValue() { return maxExclusive.get( Integer.MIN_VALUE ); }
    
    @Override
    public FloatProviderType<?> getType() { return DWFieldProviders.FLOAT_UNIFORM.get(); }
    
    @Override
    public String toString() { return "[@" + minInclusive + "-@" + maxExclusive + "]"; }
}