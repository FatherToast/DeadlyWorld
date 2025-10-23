package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.FeatureConfig;
import net.minecraft.world.level.Level;

public interface IFeatureConfigProvider<T extends FeatureConfig.FeatureTypeCategory> {

    T getConfig( Level level );

    T getConfig( DimensionConfigGroup dimConfig );
}
