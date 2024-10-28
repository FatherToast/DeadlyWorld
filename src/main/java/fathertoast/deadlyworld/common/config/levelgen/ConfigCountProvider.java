package fathertoast.deadlyworld.common.config.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.deadlyworld.common.core.registry.DWFieldProviders;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

public class ConfigCountProvider extends IntProvider {
    public static final Codec<ConfigCountProvider> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            FloatFieldSetting.CODEC.fieldOf( "count" ).forGetter( ConfigCountProvider::get )
    ).apply( instance, ConfigCountProvider::new ) );
    
    public static ConfigCountProvider of( DoubleField count ) {
        return new ConfigCountProvider( new FloatFieldSetting( count ) );
    }
    
    private final FloatFieldSetting value;
    
    private ConfigCountProvider( FloatFieldSetting count ) { value = count; }
    
    public FloatFieldSetting get() { return value; }
    
    public float getValue() { return value.get(); }
    
    @Override
    public int sample( RandomSource random ) {
        int min = getMinValue();
        float residual = getValue() - min;
        return residual > Float.MIN_NORMAL && random.nextFloat() < residual ? min + 1 : min;
    }
    
    @Override
    public int getMinValue() { return Mth.floor( getValue() ); }
    
    @Override
    public int getMaxValue() { return Mth.ceil( getValue() ); }
    
    @Override
    public IntProviderType<?> getType() { return DWFieldProviders.INT_COUNT.get(); }
    
    @Override
    public String toString() { return "[@" + value + "]"; }
}