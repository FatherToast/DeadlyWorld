package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.spike_trap.SpikeTrapType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.SpikeTrapConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigUniformIntProvider;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public record SpikePatchSettings(
        IntProvider patchSize,
        IntProvider xzSpread,
        IntProvider ySpread
) {
    public static final Codec<SpikePatchSettings> CODEC = RecordCodecBuilder.create( ( instance ) -> instance.group(
            IntProvider.CODEC.fieldOf( "patch_size" ).orElse( ConstantInt.of( 1 ) ).forGetter( SpikePatchSettings::patchSize ),
            IntProvider.CODEC.fieldOf( "xz_spread" ).forGetter( SpikePatchSettings::xzSpread ),
            IntProvider.CODEC.fieldOf( "y_spread" ).forGetter( SpikePatchSettings::ySpread )
    ).apply( instance, SpikePatchSettings::new ) );
    
    public static SpikePatchSettings of( SpikeTrapType type, DimensionConfigGroup dimConfigs ) { return of( type.getConfig( dimConfigs ) ); }
    
    public static SpikePatchSettings of( SpikeTrapConfig.SpikeTrapTypeCategory config ) {
        return new SpikePatchSettings(
                ConfigUniformIntProvider.of( config.patchSize ),
                ConfigConstantIntProvider.of( config.xzSpread ),
                ConfigConstantIntProvider.of( config.ySpread )
        );
    }
}