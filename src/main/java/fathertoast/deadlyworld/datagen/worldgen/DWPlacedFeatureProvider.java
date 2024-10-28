package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.FeatureConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.ArrayList;
import java.util.List;

import static fathertoast.deadlyworld.datagen.worldgen.DWConfiguredFeatureProvider.*;

public class DWPlacedFeatureProvider {
    /** List of all placements that should generate in overworld biomes. */
    public static final List<ResourceKey<PlacedFeature>> OVERWORLD_FEATURES = new ArrayList<>();
    /** List of all placements that should generate in nether biomes. */
    public static final List<ResourceKey<PlacedFeature>> NETHER_FEATURES = new ArrayList<>();
    
    static final ResourceKey<PlacedFeature> SIMPLE_SPAWNER_CAVE = overworldKey( "simple_spawner" );
    static final ResourceKey<PlacedFeature> STREAM_SPAWNER_CAVE = overworldKey( "stream_spawner" );
    static final ResourceKey<PlacedFeature> SWARM_SPAWNER_CAVE = overworldKey( "swarm_spawner" );
    static final ResourceKey<PlacedFeature> BRUTAL_SPAWNER_CAVE = overworldKey( "brutal_spawner" );
    static final ResourceKey<PlacedFeature> MINI_SPAWNER_CAVE = overworldKey( "mini_spawner" );
    static final ResourceKey<PlacedFeature> SILVERFISH_NEST_CAVE = overworldKey( "silverfish_nest" );
    
    //    public static final ResourceKey<PlacedFeature> SIMPLE_SPAWNER_NETHER = netherKey( "simple_spawner" );
    //    public static final ResourceKey<PlacedFeature> STREAM_SPAWNER_NETHER = netherKey( "stream_spawner" );
    //    public static final ResourceKey<PlacedFeature> SWARM_SPAWNER_NETHER = netherKey( "swarm_spawner" );
    //    public static final ResourceKey<PlacedFeature> BRUTAL_SPAWNER_NETHER = netherKey( "brutal_spawner" );
    
    private static final BlockPredicate PREDICATE_ANY_FLUID = BlockPredicate.not( BlockPredicate.noFluid() );
    
    /** Called by registry set builder to generate our placed features. */
    public static void bootstrap( BootstapContext<PlacedFeature> context ) {
        final HolderGetter<ConfiguredFeature<?, ?>> getter = context.lookup( Registries.CONFIGURED_FEATURE );
        
        DimensionConfigGroup overworldConfigs = Config.getDefaultConfigs();
        register( context, getter, SIMPLE_SPAWNER_CAVE, SIMPLE_SPAWNER,
                loneSpawner( SpawnerType.DEFAULT, overworldConfigs ) );
        register( context, getter, STREAM_SPAWNER_CAVE, STREAM_SPAWNER,
                loneSpawner( SpawnerType.STREAM, overworldConfigs ) );
        register( context, getter, SWARM_SPAWNER_CAVE, SWARM_SPAWNER,
                loneSpawner( SpawnerType.SWARM, overworldConfigs ) );
        register( context, getter, BRUTAL_SPAWNER_CAVE, BRUTAL_SPAWNER,
                loneSpawner( SpawnerType.BRUTAL, overworldConfigs ) );
        register( context, getter, MINI_SPAWNER_CAVE, MINI_SPAWNER,
                loneSpawner( SpawnerType.MINI, overworldConfigs ) );
        register( context, getter, SILVERFISH_NEST_CAVE, SILVERFISH_NEST,
                loneSpawner( SpawnerType.NEST, overworldConfigs ) );
    }
    
    /** @return Modifiers for a lone spawner feature. */
    protected static List<PlacementModifier> loneSpawner( SpawnerType type, DimensionConfigGroup dimConfigs ) {
        return floorFeature( type.getFeatureConfig( dimConfigs ) );
    }
    
    /** @return Modifiers for a feature that generates only on floors. */
    protected static List<PlacementModifier> floorFeature( FeatureConfig.FeatureTypeCategory config ) {
        return verticalScanFeature( config, false, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE );
    }
    
    /** @return Modifiers for a feature that generates only on ceilings. */
    protected static List<PlacementModifier> ceilFeature( FeatureConfig.FeatureTypeCategory config ) {
        return verticalScanFeature( config, true, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE );
    }
    
    /** @return Modifiers for a feature that generates only on floors or fluid surfaces. */
    protected static List<PlacementModifier> surfaceFeature( FeatureConfig.FeatureTypeCategory config ) {
        return verticalScanFeature( config, false, BlockPredicate.anyOf( BlockPredicate.solid(),
                PREDICATE_ANY_FLUID ), BlockPredicate.ONLY_IN_AIR_PREDICATE );
    }
    
    /** @return Modifiers for a feature that generates only on the bottom of fluids. */
    protected static List<PlacementModifier> fluidFloorFeature( FeatureConfig.FeatureTypeCategory config ) {
        return verticalScanFeature( config, false, BlockPredicate.solid(),
                BlockPredicate.anyOf( BlockPredicate.ONLY_IN_AIR_PREDICATE, PREDICATE_ANY_FLUID ) );
    }
    
    /** @return Modifiers for a feature that scans up or down (up to 12 blocks) for a potential valid location. */
    protected static List<PlacementModifier> verticalScanFeature( FeatureConfig.FeatureTypeCategory config, boolean up,
                                                                  BlockPredicate scanFor, BlockPredicate scanWhile ) {
        return verticalScanFeature( config, up, scanFor, scanWhile, 12 );
    }
    
    /** @return Modifiers for a feature that scans up or down for a potential valid location. */
    protected static List<PlacementModifier> verticalScanFeature( FeatureConfig.FeatureTypeCategory config, boolean up,
                                                                  BlockPredicate scanFor, BlockPredicate scanWhile, int scanRange ) {
        return new PlacementBuilder().multiply( config ).spreadInChunk().spreadInHeights( config )
                .move( up ? Direction.UP : Direction.DOWN, scanFor, scanWhile, scanRange )
                .moveVertical( up ? -1 : 1 ).requireBelowOceanFloor( 2 ).requireBiome().build();
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