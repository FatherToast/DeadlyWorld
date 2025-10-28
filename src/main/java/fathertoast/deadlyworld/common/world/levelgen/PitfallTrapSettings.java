package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.pitfall.PitfallTrapType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.PitfallTrapConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.GenerationStep;

public record PitfallTrapSettings(
        IntProvider pitRadius,
        IntProvider pitDepth
) {
    public static final Codec<PitfallTrapSettings> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
            IntProvider.CODEC.fieldOf( "pit_radius" ).forGetter( PitfallTrapSettings::pitRadius ),
            IntProvider.CODEC.fieldOf( "pit_depth" ).forGetter( PitfallTrapSettings::pitDepth )
    ).apply( instance, PitfallTrapSettings::new ) );

    public static PitfallTrapSettings of( PitfallTrapType type, DimensionConfigGroup dimConfigs ) { return of( type.getConfig( dimConfigs ) ); }

    public static PitfallTrapSettings of( PitfallTrapConfig.PitfallTrapTypeCategory config ) {
        return new PitfallTrapSettings(
                ConfigConstantIntProvider.of( config.radius ),
                ConfigConstantIntProvider.of( config.depth )
        );
    }
}
