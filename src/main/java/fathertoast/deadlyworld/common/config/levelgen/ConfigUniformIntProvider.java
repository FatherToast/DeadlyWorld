package fathertoast.deadlyworld.common.config.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.config.levelgen.setting.IntFieldSetting;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWFieldProviders;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.util.valueproviders.UniformInt}.
 */
public class ConfigUniformIntProvider extends IntProvider {
    public static final Codec<ConfigUniformIntProvider> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            IntFieldSetting.CODEC.fieldOf( "min_inclusive" ).forGetter( ConfigUniformIntProvider::getMinInclusive ),
            IntFieldSetting.CODEC.fieldOf( "max_inclusive" ).forGetter( ConfigUniformIntProvider::getMaxInclusive )
    ).apply( instance, ConfigUniformIntProvider::new ) );

    public static ConfigUniformIntProvider of( IntField.RandomRange range ) { return of( range.getMinField(), range.getMaxField() ); }
    
    public static ConfigUniformIntProvider of( IntField min, IntField max ) {
        return new ConfigUniformIntProvider( new IntFieldSetting( min ), new IntFieldSetting( max ) );
    }
    
    private final IntFieldSetting minInclusive;
    private final IntFieldSetting maxInclusive;
    
    private ConfigUniformIntProvider( IntFieldSetting min, IntFieldSetting max ) {
        minInclusive = min;
        maxInclusive = max;
    }
    
    public IntFieldSetting getMinInclusive() { return minInclusive; }
    
    public IntFieldSetting getMaxInclusive() { return maxInclusive; }
    
    @Override
    public int sample( RandomSource random ) {
        Integer min = minInclusive.getRaw();
        Integer max = maxInclusive.getRaw();
        
        if( min == null || max == null ) {
            DeadlyWorld.LOG.error( "Invalid uniform int range: {}", this );
            return Integer.MAX_VALUE;
        }
        if( min > max ) {
            DeadlyWorld.LOG.warn( "Empty uniform int range: {}", this );
            return min;
        }
        return Mth.randomBetweenInclusive( random, min, max );
    }
    
    @Override
    public int getMinValue() { return minInclusive.get( Integer.MAX_VALUE ); }
    
    @Override
    public int getMaxValue() { return maxInclusive.get( Integer.MIN_VALUE ); }
    
    @Override
    public IntProviderType<?> getType() { return DWFieldProviders.INT_UNIFORM.get(); }
    
    @Override
    public String toString() { return "[@" + minInclusive + "-@" + maxInclusive + "]"; }
}