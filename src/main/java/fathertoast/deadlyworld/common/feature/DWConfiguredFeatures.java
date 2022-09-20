package fathertoast.deadlyworld.common.feature;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class DWConfiguredFeatures {

    public static ConfiguredFeature<NoFeatureConfig, ?> DEFAULT_SPAWNER;
    public static ConfiguredFeature<NoFeatureConfig, ?> DUNGEON_SPAWNER;
    public static ConfiguredFeature<NoFeatureConfig, ?> SWARM_SPAWNER;
    public static ConfiguredFeature<NoFeatureConfig, ?> NEST_SPAWNER;
    public static ConfiguredFeature<NoFeatureConfig, ?> BRUTAL_SPAWNER;
    public static ConfiguredFeature<NoFeatureConfig, ?> STREAM_SPAWNER;
    public static ConfiguredFeature<NoFeatureConfig, ?> MINI_SPAWNER;

    public static ConfiguredFeature<NoFeatureConfig, ?> TNT_FLOOR_TRAP;
    public static ConfiguredFeature<NoFeatureConfig, ?> TNT_MOB_FLOOR_TRAP;
    public static ConfiguredFeature<NoFeatureConfig, ?> POTION_FLOOR_TRAP;



    public static void register() {
        DEFAULT_SPAWNER = register("default_spawner", DWFeatures.DEFAULT_SPAWNER.get().configured(IFeatureConfig.NONE));
        DUNGEON_SPAWNER = register("dungeon_spawner", DWFeatures.DUNGEON_SPAWNER.get().configured(IFeatureConfig.NONE));
        SWARM_SPAWNER = register("swarm_spawner", DWFeatures.SWARM_SPAWNER.get().configured(IFeatureConfig.NONE));
        NEST_SPAWNER = register("nest_spawner", DWFeatures.NEST_SPAWNER.get().configured(IFeatureConfig.NONE));
        BRUTAL_SPAWNER = register("brutal_spawner", DWFeatures.BRUTAL_SPAWNER.get().configured(IFeatureConfig.NONE));
        STREAM_SPAWNER = register("stream_spawner", DWFeatures.STREAM_SPAWNER.get().configured(IFeatureConfig.NONE));
        MINI_SPAWNER = register("mini_spawner", DWFeatures.MINI_SPAWNER.get().configured(IFeatureConfig.NONE));

        TNT_FLOOR_TRAP = register("tnt_floor_trap", DWFeatures.TNT_FLOOR_TRAP.get().configured(IFeatureConfig.NONE));
        TNT_MOB_FLOOR_TRAP = register("tnt_mob_floor_trap", DWFeatures.TNT_MOB_FLOOR_TRAP.get().configured(IFeatureConfig.NONE));
        POTION_FLOOR_TRAP = register("potion_floor_trap", DWFeatures.POTION_FLOOR_TRAP.get().configured(IFeatureConfig.NONE));
    }

    private static <C extends IFeatureConfig> ConfiguredFeature<C, ?> register(String key, ConfiguredFeature<C, ?> configuredFeature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, DeadlyWorld.MOD_ID + ":" + key, configuredFeature);
    }
}
