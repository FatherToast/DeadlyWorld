package fathertoast.deadlyworld.common.config.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.config.levelgen.setting.IntFieldSetting;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWFieldProviders;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;

/**
 * Modified copy-paste of {@link net.minecraft.world.level.levelgen.heightproviders.UniformHeight}.
 */
public class ConfigHeightProvider extends HeightProvider {
    public static final Codec<ConfigHeightProvider> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            IntFieldSetting.CODEC.fieldOf( "min_inclusive" ).forGetter( ConfigHeightProvider::getMinInclusive ),
            IntFieldSetting.CODEC.fieldOf( "max_inclusive" ).forGetter( ConfigHeightProvider::getMaxInclusive )
    ).apply( instance, ConfigHeightProvider::new ) );
    
    public static ConfigHeightProvider of( IntField.RandomRange range ) { return of( range.getMinField(), range.getMaxField() ); }
    
    public static ConfigHeightProvider of( IntField min, IntField max ) {
        return new ConfigHeightProvider( new IntFieldSetting( min ), new IntFieldSetting( max ) );
    }
    
    private final IntFieldSetting minInclusive;
    private final IntFieldSetting maxInclusive;
    
    private ConfigHeightProvider( IntFieldSetting min, IntFieldSetting max ) {
        minInclusive = min;
        maxInclusive = max;
    }
    
    public IntFieldSetting getMinInclusive() { return minInclusive; }
    
    public IntFieldSetting getMaxInclusive() { return maxInclusive; }
    
    @Override
    public int sample( RandomSource random, WorldGenerationContext context ) {
        Integer min = minInclusive.getRaw();
        Integer max = maxInclusive.getRaw();
        
        if( min == null || max == null ) {
            DeadlyWorld.LOG.error( "Invalid height range: {}", this );
            return Integer.MAX_VALUE;
        }
        if( min > max ) {
            DeadlyWorld.LOG.warn( "Empty height range: {}", this );
            return min;
        }
        return Mth.randomBetweenInclusive( random, min, max );
    }
    
    @Override
    public HeightProviderType<?> getType() { return DWFieldProviders.HEIGHT.get(); }
    
    @Override
    public String toString() { return "[@" + minInclusive + "-@" + maxInclusive + "]"; }
}