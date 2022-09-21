package fathertoast.deadlyworld.common.feature;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.ArrayList;
import java.util.List;

public class DWConfiguredFeatures {

    public static List<ConfiguredFeature<NoFeatureConfig, ?>> SIMPLE_FEATURES;

    public static void register() {
        registerSimpleFeatures();
    }

    private static void registerSimpleFeatures() {
        SIMPLE_FEATURES = new ArrayList<>();

        DWFeatures.SPAWNERS.forEach((regObj) -> {
            ConfiguredFeature<NoFeatureConfig, ?> configuredFeature = register(regObj.getId().getPath(), regObj.get().configured(IFeatureConfig.NONE));
            SIMPLE_FEATURES.add(configuredFeature);
        });
        DWFeatures.FLOOR_TRAPS.forEach((regObj) -> {
            ConfiguredFeature<NoFeatureConfig, ?> configuredFeature = register(regObj.getId().getPath(), regObj.get().configured(IFeatureConfig.NONE));
            SIMPLE_FEATURES.add(configuredFeature);
        });
    }

    private static <C extends IFeatureConfig> ConfiguredFeature<C, ?> register(String key, ConfiguredFeature<C, ?> configuredFeature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, DeadlyWorld.MOD_ID + ":" + key, configuredFeature);
    }
}
