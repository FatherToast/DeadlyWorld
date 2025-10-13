package fathertoast.deadlyworld.common.world.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.SeaMineConfig;
import fathertoast.deadlyworld.common.config.levelgen.ConfigConstantIntProvider;
import net.minecraft.util.valueproviders.IntProvider;

public record SeaMineSettings(
        IntProvider minDistanceFromBottom, IntProvider maxDistanceFromBottom
) {
    public static final Codec<SeaMineSettings> CODEC = RecordCodecBuilder.create( (instance ) -> instance.group(
            IntProvider.CODEC.fieldOf( "min_distance_from_bottom" ).forGetter( SeaMineSettings::minDistanceFromBottom ),
            IntProvider.CODEC.fieldOf( "max_distance_from_bottom" ).forGetter( SeaMineSettings::maxDistanceFromBottom )
    ).apply( instance, SeaMineSettings::new ) );

    public static SeaMineSettings of( SeaMineType type, DimensionConfigGroup dimConfigs ) { return of( type.getFeatureConfig( dimConfigs ) ); }

    public static SeaMineSettings of( SeaMineConfig.SeaMineCategory config ) {
        return new SeaMineSettings(
                ConfigConstantIntProvider.of( config.distanceFromBottom.getMinField() ),
                ConfigConstantIntProvider.of( config.distanceFromBottom.getMaxField() )
        );
    }
}
