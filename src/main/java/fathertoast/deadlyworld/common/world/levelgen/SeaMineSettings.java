package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.WaterTrapConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigUniformIntProvider;
import net.minecraft.util.valueproviders.IntProvider;

public record SeaMineSettings(
        IntProvider distanceFromBottom
) {
    public static final Codec<SeaMineSettings> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
            IntProvider.CODEC.fieldOf( "min_distance_from_bottom" ).forGetter( SeaMineSettings::distanceFromBottom )
    ).apply( instance, SeaMineSettings::new ) );

    public static SeaMineSettings of( SeaMineType type, DimensionConfigGroup dimConfigs ) { return of( type.getFeatureConfig( dimConfigs ) ); }

    public static SeaMineSettings of( WaterTrapConfig.SeaMineCategory config ) {
        return new SeaMineSettings(
                ConfigUniformIntProvider.of( config.distanceFromBottom )
        );
    }
}
