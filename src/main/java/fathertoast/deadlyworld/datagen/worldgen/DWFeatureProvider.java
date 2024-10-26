package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import fathertoast.deadlyworld.common.world.levelgen.LoneSpawnerFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DWFeatureProvider {
    public static final List<ResourceKey<PlacedFeature>> ALL_PLACEMENTS = new ArrayList<>();
    
    // Features
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIMPLE_SPAWNER = configuredKey( "simple_spawner" );
    public static final ResourceKey<ConfiguredFeature<?, ?>> STREAM_SPAWNER = configuredKey( "stream_spawner" );
    public static final ResourceKey<ConfiguredFeature<?, ?>> SWARM_SPAWNER = configuredKey( "swarm_spawner" );
    public static final ResourceKey<ConfiguredFeature<?, ?>> BRUTAL_SPAWNER = configuredKey( "brutal_spawner" );
    
    // Placements
    public static final ResourceKey<PlacedFeature> SIMPLE_SPAWNER_CAVE = placedKey( "simple_spawner_cave" );
    public static final ResourceKey<PlacedFeature> STREAM_SPAWNER_CAVE = placedKey( "stream_spawner_cave" );
    public static final ResourceKey<PlacedFeature> SWARM_SPAWNER_CAVE = placedKey( "swarm_spawner_cave" );
    public static final ResourceKey<PlacedFeature> BRUTAL_SPAWNER_CAVE = placedKey( "brutal_spawner_cave" );
    
    /** Called by registry set builder to generate our configured features. */
    public static void bootstrapConfigured( BootstapContext<ConfiguredFeature<?, ?>> context ) {
        registerLoneSpawner( context, SIMPLE_SPAWNER, SpawnerType.DEFAULT, block( Blocks.AIR ), false );
        registerLoneSpawner( context, STREAM_SPAWNER, SpawnerType.STREAM, block( Blocks.MUD_BRICKS ), false );
        registerLoneSpawner( context, SWARM_SPAWNER, SpawnerType.SWARM, block( Blocks.CHISELED_SANDSTONE ), false );
        registerLoneSpawner( context, BRUTAL_SPAWNER, SpawnerType.BRUTAL, block( Blocks.CHISELED_STONE_BRICKS ), true );
    }
    
    /** Called by registry set builder to generate our placed features. */
    public static void bootstrapPlaced( BootstapContext<PlacedFeature> context ) {
        final HolderGetter<ConfiguredFeature<?, ?>> getter = context.lookup( Registries.CONFIGURED_FEATURE );
        
        register( context, getter, SIMPLE_SPAWNER_CAVE, SIMPLE_SPAWNER,
                RarityFilter.onAverageOnceEvery( 1 ), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome() );
        register( context, getter, STREAM_SPAWNER_CAVE, STREAM_SPAWNER,
                RarityFilter.onAverageOnceEvery( 1 ), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome() );
        register( context, getter, SWARM_SPAWNER_CAVE, SWARM_SPAWNER,
                RarityFilter.onAverageOnceEvery( 1 ), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome() );
        register( context, getter, BRUTAL_SPAWNER_CAVE, BRUTAL_SPAWNER,
                RarityFilter.onAverageOnceEvery( 1 ), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome() );
    }
    
    // Just some convenience methods below
    
    protected static BlockStateProvider block( Supplier<? extends Block> block ) { return block( block.get() ); }
    
    protected static BlockStateProvider block( Block block ) { return block( block.defaultBlockState() ); }
    
    protected static BlockStateProvider block( BlockState block ) { return BlockStateProvider.simple( block ); }
    
    // Configured features
    
    protected static void registerLoneSpawner( BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey,
                                               SpawnerType type, BlockStateProvider topper, boolean vines ) {
        context.register( confFeatureKey, new ConfiguredFeature<>( DWFeatures.LONE_SPAWNER.get(),
                new LoneSpawnerFeature.Configuration( block( DWBlocks.spawner( type ) ), topper, BlockTags.FEATURES_CANNOT_REPLACE, vines ) ) );
    }
    
    protected static void register( BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( confFeatureKey, configuredFeature );
    }
    
    protected static ResourceKey<ConfiguredFeature<?, ?>> configuredKey( String name ) {
        return ResourceKey.create( Registries.CONFIGURED_FEATURE, DeadlyWorld.resourceLoc( name ) );
    }
    
    // Placed features
    
    protected static void register( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter, ResourceKey<PlacedFeature> placedFeatureKey, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers ) {
        register( context, placedFeatureKey, getter.getOrThrow( configuredFeature ), modifiers );
    }
    
    protected static void register( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter, ResourceKey<PlacedFeature> placedFeatureKey, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, List<PlacementModifier> modifiers ) {
        register( context, placedFeatureKey, getter.getOrThrow( configuredFeature ), modifiers );
    }
    
    protected static void register( BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers ) {
        register( context, placedFeatureKey, configuredFeature, List.of( modifiers ) );
    }
    
    protected static void register( BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, List<PlacementModifier> modifiers ) {
        context.register( placedFeatureKey, new PlacedFeature( configuredFeature, modifiers ) );
    }
    
    protected static ResourceKey<PlacedFeature> placedKey( String name ) {
        final ResourceKey<PlacedFeature> key = ResourceKey.create( Registries.PLACED_FEATURE, DeadlyWorld.resourceLoc( name ) );
        ALL_PLACEMENTS.add( key );
        return key;
    }
}