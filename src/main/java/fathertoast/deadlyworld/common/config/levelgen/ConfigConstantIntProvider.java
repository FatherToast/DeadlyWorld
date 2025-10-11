package fathertoast.deadlyworld.common.config.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.config.levelgen.setting.IntFieldSetting;
import fathertoast.deadlyworld.common.core.registry.DWFieldProviders;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.util.valueproviders.ConstantInt}.
 */
public class ConfigConstantIntProvider extends IntProvider {
    public static final Codec<ConfigConstantIntProvider> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            IntFieldSetting.CODEC.fieldOf( "value" ).forGetter( ConfigConstantIntProvider::get )
    ).apply( instance, ConfigConstantIntProvider::new ) );
    
    public static ConfigConstantIntProvider of( IntField value ) {
        return new ConfigConstantIntProvider( new IntFieldSetting( value ) );
    }
    
    private final IntFieldSetting value;
    
    private ConfigConstantIntProvider( IntFieldSetting val ) { value = val; }
    
    public IntFieldSetting get() { return value; }
    
    public int getValue() { return value.get(); }
    
    @Override
    public int sample( RandomSource random ) { return getValue(); }
    
    @Override
    public int getMinValue() { return getValue(); }
    
    @Override
    public int getMaxValue() { return getValue(); }
    
    @Override
    public IntProviderType<?> getType() { return DWFieldProviders.INT_CONSTANT.get(); }
    
    @Override
    public String toString() { return "[@" + value + "]"; }
}