package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.ArrayList;
import java.util.List;

import static fathertoast.deadlyworld.datagen.worldgen.DWConfiguredFeatureProvider.*;

public class DWPlacedFeatureProvider {
    /** List of all placements that should generate in overworld biomes. */
    public static final List<ResourceKey<PlacedFeature>> OVERWORLD_FEATURES = new ArrayList<>();
    /** List of all placements that should generate in nether biomes. */
    public static final List<ResourceKey<PlacedFeature>> NETHER_FEATURES = new ArrayList<>();
    
    static final ResourceKey<PlacedFeature> SIMPLE_SPAWNER_CAVE = overworldKey( "simple_spawner_cave" );
    static final ResourceKey<PlacedFeature> STREAM_SPAWNER_CAVE = overworldKey( "stream_spawner_cave" );
    static final ResourceKey<PlacedFeature> SWARM_SPAWNER_CAVE = overworldKey( "swarm_spawner_cave" );
    static final ResourceKey<PlacedFeature> BRUTAL_SPAWNER_CAVE = overworldKey( "brutal_spawner_cave" );
    
    //    public static final ResourceKey<PlacedFeature> SIMPLE_SPAWNER_NETHER = netherKey( "simple_spawner" );
    //    public static final ResourceKey<PlacedFeature> STREAM_SPAWNER_NETHER = netherKey( "stream_spawner" );
    //    public static final ResourceKey<PlacedFeature> SWARM_SPAWNER_NETHER = netherKey( "swarm_spawner" );
    //    public static final ResourceKey<PlacedFeature> BRUTAL_SPAWNER_NETHER = netherKey( "brutal_spawner" );
    
    /** Called by registry set builder to generate our placed features. */
    public static void bootstrap( BootstapContext<PlacedFeature> context ) {
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
    
    /** Registers a placed feature. */
    protected static void register( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter, ResourceKey<PlacedFeature> placedFeatureKey, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers ) {
        register( context, placedFeatureKey, getter.getOrThrow( configuredFeature ), modifiers );
    }
    
    /** Registers a placed feature. */
    protected static void register( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter, ResourceKey<PlacedFeature> placedFeatureKey, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, List<PlacementModifier> modifiers ) {
        register( context, placedFeatureKey, getter.getOrThrow( configuredFeature ), modifiers );
    }
    
    /** Registers a placed feature. */
    protected static void register( BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers ) {
        register( context, placedFeatureKey, configuredFeature, List.of( modifiers ) );
    }
    
    /** Registers a placed feature. */
    protected static void register( BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> placedFeatureKey, Holder<ConfiguredFeature<?, ?>> configuredFeature, List<PlacementModifier> modifiers ) {
        context.register( placedFeatureKey, new PlacedFeature( configuredFeature, modifiers ) );
    }
    
    /** Creates a placed feature key that is automatically added to all overworld biomes. */
    protected static ResourceKey<PlacedFeature> overworldKey( String name ) {
        final ResourceKey<PlacedFeature> key = key( name );
        OVERWORLD_FEATURES.add( key );
        return key;
    }
    
    /** Creates a placed feature key that is automatically added to all nether biomes. */
    protected static ResourceKey<PlacedFeature> netherKey( String name ) {
        final ResourceKey<PlacedFeature> key = key( name + "_nether" );
        NETHER_FEATURES.add( key );
        return key;
    }
    
    /** Creates a placed feature key that is not added to any world gen. */
    protected static ResourceKey<PlacedFeature> key( String name ) {
        return ResourceKey.create( Registries.PLACED_FEATURE, DeadlyWorld.resourceLoc( name ) );
    }
}