package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.FeatureConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
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
    
    private static final BlockPredicate PREDICATE_ANY_FLUID = BlockPredicate.not( BlockPredicate.noFluid() );
    
    /** Called by registry set builder to generate our placed features. */
    public static void bootstrap( BootstapContext<PlacedFeature> context ) {
        final HolderGetter<ConfiguredFeature<?, ?>> getter = context.lookup( Registries.CONFIGURED_FEATURE );
        final DimensionConfigGroup overworldConfigs = Config.getDimensionConfigs( Level.OVERWORLD );
        final DimensionConfigGroup netherConfigs = Config.getDimensionConfigs( Level.NETHER );
        
        // Standard lone spawner placements
        registerLoneSpawner( context, getter, SIMPLE_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, STREAM_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, SWARM_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, BRUTAL_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, MINI_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, SILVERFISH_NEST, overworldConfigs, netherConfigs );

        // Standard floor trap placements
        registerFloorTrap( context, getter, TNT_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, getter, TNT_MOB_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, getter, POTION_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, getter, LAVA_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, getter, FIRE_TRAP, overworldConfigs, netherConfigs );

        // Standards tower dispenser placements
        registerTowerDispenser( context, getter, SIMPLE_TOWER, overworldConfigs, netherConfigs );
        registerTowerDispenser( context, getter, FIRE_TOWER, overworldConfigs, netherConfigs );
        registerTowerDispenser( context, getter, POTION_TOWER, overworldConfigs, netherConfigs );
        registerTowerDispenser( context, getter, GATLING_TOWER, overworldConfigs, netherConfigs );
        registerTowerDispenser( context, getter, FIREBALL_TOWER, overworldConfigs, netherConfigs );
    }
    
    /** @return Modifiers for a lone spawner feature. */
    protected static List<PlacementModifier> loneSpawner( SpawnerType type, DimensionConfigGroup dimConfigs ) {
        return floorFeature( type.getFeatureConfig( dimConfigs ) );
    }

    /** @return Modifiers for a floor trap feature. */
    protected static List<PlacementModifier> floorTrap( TrapType type, DimensionConfigGroup dimConfigs ) {
        return floorFeature( type.getFeatureConfig( dimConfigs ) );
    }

    /** @return Modifiers for a floor trap feature. */
    protected static List<PlacementModifier> towerDispenser( TowerType type, DimensionConfigGroup dimConfigs ) {
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
    
    /** Registers a placed lone spawner type feature to each supported dimension. */
    protected static void registerLoneSpawner( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                               FeatureKeys.Spawner feature, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, feature.overworldKeys, loneSpawner( feature.spawnerType, overworldConfigs ) );
        register( context, getter, feature.netherKeys, loneSpawner( feature.spawnerType, netherConfigs ) );
    }

    /** Registers a placed floor trap type feature to each supported dimension. */
    protected static void registerFloorTrap( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                             FeatureKeys.Trap feature, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, feature.overworldKeys, floorTrap( feature.trapType, overworldConfigs ) );
        register( context, getter, feature.netherKeys, floorTrap( feature.trapType, netherConfigs ) );
    }

    /** Registers a placed floor trap type feature to each supported dimension. */
    protected static void registerTowerDispenser( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                             FeatureKeys.TowerDispenser feature, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, feature.overworldKeys, towerDispenser( feature.towerType, overworldConfigs ) );
        register( context, getter, feature.netherKeys, towerDispenser( feature.towerType, netherConfigs ) );
    }
    
    /** Registers a placed feature. */
    protected static void register( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter, FeatureKeys featureKeys, PlacementModifier... modifiers ) {
        register( context, getter, featureKeys.placedKey, featureKeys.configuredKey, modifiers );
    }
    
    /** Registers a placed feature. */
    protected static void register( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter, ResourceKey<PlacedFeature> placedFeatureKey, ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, PlacementModifier... modifiers ) {
        register( context, placedFeatureKey, getter.getOrThrow( configuredFeature ), modifiers );
    }
    
    /** Registers a placed feature. */
    protected static void register( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter, FeatureKeys featureKeys, List<PlacementModifier> modifiers ) {
        register( context, getter, featureKeys.placedKey, featureKeys.configuredKey, modifiers );
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