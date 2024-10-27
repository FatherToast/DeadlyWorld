package fathertoast.deadlyworld.common.config.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.deadlyworld.common.core.registry.DWFieldProviders;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.util.valueproviders.ConstantFloat}.
 */
public class ConfigConstantFloatProvider extends FloatProvider {
    public static final Codec<ConfigConstantFloatProvider> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            FloatFieldSetting.CODEC.fieldOf( "value" ).forGetter( ConfigConstantFloatProvider::get )
    ).apply( instance, ConfigConstantFloatProvider::new ) );
    
    public static ConfigConstantFloatProvider of( DoubleField value ) {
        return new ConfigConstantFloatProvider( new FloatFieldSetting( value ) );
    }
    
    private final FloatFieldSetting value;
    
    private ConfigConstantFloatProvider( FloatFieldSetting val ) { value = val; }
    
    public FloatFieldSetting get() { return value; }
    
    public float getValue() { return value.get(); }
    
    @Override
    public float sample( RandomSource random ) { return getValue(); }
    
    @Override
    public float getMinValue() { return getValue(); }
    
    @Override
    public float getMaxValue() { return getValue() + 1.0F; } // if vanilla jumps off a bridge, we jump too
    
    @Override
    public FloatProviderType<?> getType() { return DWFieldProviders.FLOAT_CONSTANT.get(); }
    
    @Override
    public String toString() { return "[@" + value + "]"; }
}