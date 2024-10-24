package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class DWFeatureProvider {


    public static final ResourceKey<ConfiguredFeature<?, ?>> SPONGE_LAKE = configuredKey("sponge_lake");

    public static final ResourceKey<PlacedFeature> PLACED_SPONGE_LAKE_SURFACE = placedKey("sponge_lake_surface");



    /** Called by registry set builder to generate our configured features. */
    public static void bootstrapConfigured(BootstapContext<ConfiguredFeature<?, ?>> context) {
        register(context, DWFeatureProvider.SPONGE_LAKE, new ConfiguredFeature<>(Feature.LAKE, new LakeFeature.Configuration(BlockStateProvider.simple(Blocks.SPONGE.defaultBlockState()), BlockStateProvider.simple(Blocks.DIRT.defaultBlockState()))));
    }


    /** Called by registry set builder to generate our placed features. */
    public static void bootstrapPlaced(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> getter = context.lookup(Registries.CONFIGURED_FEATURE);

        final Holder<ConfiguredFeature<?, ?>> SPONGE_LAKE = getter.getOrThrow(DWFeatureProvider.SPONGE_LAKE);

        register(context, DWFeatureProvider.PLACED_SPONGE_LAKE_SURFACE, SPONGE_LAKE, List.of(RarityFilter.onAverageOnceEvery(100), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
    }


    // Just some convenience methods below

    protected static void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey, ConfiguredFeature<?, ?> configuredFeature) {
        context.register(confFeatureKey, configuredFeature);
    }

    protected static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers) {
        register(context, placedFeatureKey, configuredFeature, List.of(modifiers));
    }

    protected static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, List<PlacementModifier> modifiers) {
        context.register(placedFeatureKey, new PlacedFeature(configuredFeature, modifiers));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, DeadlyWorld.resourceLoc(name));
    }

    public static ResourceKey<PlacedFeature> placedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, DeadlyWorld.resourceLoc(name));
    }
}