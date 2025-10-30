package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.pitfall.PitfallTrapType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.PitfallTrapConfig;
import fathertoast.deadlyworld.common.config.dimension.WaterTrapConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import fathertoast.deadlyworld.common.config.levelgen.ConfigUniformIntProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.GenerationStep;

public record PitfallTrapSettings(
        IntProvider pitRadius,
        IntProvider pitDepth,
        String pitfallTrapId
) {
    public static final Codec<PitfallTrapSettings> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
            IntProvider.CODEC.fieldOf( "pit_radius" ).forGetter( PitfallTrapSettings::pitRadius ),
            IntProvider.CODEC.fieldOf( "pit_depth" ).forGetter( PitfallTrapSettings::pitDepth ),
            Codec.STRING.fieldOf( "pitfall_trap_id" ).forGetter( PitfallTrapSettings::pitfallTrapId )
    ).apply( instance, PitfallTrapSettings::new ) );

    public static PitfallTrapSettings of( PitfallTrapType type, PitfallTrapConfig.PitfallTrapTypeCategory config ) {
        return new PitfallTrapSettings(
                ConfigUniformIntProvider.of( config.radius ),
                ConfigUniformIntProvider.of( config.depth ),
                type.toString()
        );
    }
}
