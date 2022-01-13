package fathertoast.deadlyworld.common.feature;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.registry.DWFeatures;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class DWConfiguredFeatures {

    public static ConfiguredFeature<NoFeatureConfig, ?> SPAWNER;

    public static void register() {

        SPAWNER = register("spawner", DWFeatures.SPAWNER.get().configured(IFeatureConfig.NONE));
    }

    private static <C extends IFeatureConfig> ConfiguredFeature<C, ?> register(String key, ConfiguredFeature<C, ?> configuredFeature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, DeadlyWorld.MOD_ID + ":" + key, configuredFeature);
    }
}
