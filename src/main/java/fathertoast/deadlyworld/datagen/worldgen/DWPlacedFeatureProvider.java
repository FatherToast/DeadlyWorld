package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.deadlyworld.common.block.chest.ChestType;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapType;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.spike_trap.SpikeTrapType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.unstable.PitfallTrapType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.FeatureConfig;
import fathertoast.deadlyworld.common.config.dimension.WaterTrapConfig;
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
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.List;

import static fathertoast.deadlyworld.datagen.worldgen.DWConfiguredFeatureProvider.*;

/**
 * For vanilla ore placed features, see {@link net.minecraft.data.worldgen.placement.OrePlacements}.
 * For vanilla decoration placed features, see {@link net.minecraft.data.worldgen.placement.CavePlacements} (mostly).
 */
public class DWPlacedFeatureProvider {
    /** List of all ore placements that should generate in overworld biomes. */
    public static final List<ResourceKey<PlacedFeature>> OVERWORLD_ORE_FEATURES = new ArrayList<>();
    /** List of all ore placements that should generate in nether biomes. */
    public static final List<ResourceKey<PlacedFeature>> NETHER_ORE_FEATURES = new ArrayList<>();
    
    /** List of all vein placements. */
    public static final List<ResourceKey<PlacedFeature>> VEIN_FEATURES = new ArrayList<>();
    
    /** List of all decoration placements that should generate in overworld biomes. */
    public static final List<ResourceKey<PlacedFeature>> OVERWORLD_FEATURES = new ArrayList<>();
    /** List of all decoration placements that should generate in nether biomes. */
    public static final List<ResourceKey<PlacedFeature>> NETHER_FEATURES = new ArrayList<>();
    
    /**
     * List of all placements that don't care about dimension type and should generate anywhere,
     * AFTER {@link net.minecraft.world.level.levelgen.GenerationStep.Decoration#UNDERGROUND_DECORATION}.
     * Any restrictions are handled in the feature itself, usually config based.
     */
    public static final List<ResourceKey<PlacedFeature>> ANY_DIMENSION_POST_DECORATION = new ArrayList<>();
    
    /** List of all lone chest placements. */
    public static final List<ResourceKey<PlacedFeature>> LONE_CHEST_FEATURES = new ArrayList<>();
    /** List of all spawner placements. */
    public static final List<ResourceKey<PlacedFeature>> SPAWNER_FEATURES = new ArrayList<>();
    /** List of all floor trap placements. */
    public static final List<ResourceKey<PlacedFeature>> FLOOR_TRAP_FEATURES = new ArrayList<>();
    /** List of all spike trap placements. */
    public static final List<ResourceKey<PlacedFeature>> SPIKE_TRAP_FEATURES = new ArrayList<>();
    /** List of all pitfall trap placements. */
    public static final List<ResourceKey<PlacedFeature>> PITFALL_TRAP_FEATURES = new ArrayList<>();
    /** List of all tower dispenser placements. */
    public static final List<ResourceKey<PlacedFeature>> TOWER_FEATURES = new ArrayList<>();
    /** List of all sea mine placements. */
    public static final List<ResourceKey<PlacedFeature>> SEA_MINE_FEATURES = new ArrayList<>();
    /** List of all dungeon placements. */
    public static final List<ResourceKey<PlacedFeature>> DUNGEON_FEATURES = new ArrayList<>();
    
    
    private static final BlockPredicate PREDICATE_ANY_FLUID = BlockPredicate.not( BlockPredicate.noFluid() );
    private static final BlockPredicate PREDICATE_WATER = BlockPredicate.matchesFluids( Fluids.WATER );
    private static final BlockPredicate PREDICATE_LAVA = BlockPredicate.matchesFluids( Fluids.LAVA );
    
    
    /** Called by registry set builder to generate our placed features. */
    public static void bootstrap( BootstapContext<PlacedFeature> context ) {
        final HolderGetter<ConfiguredFeature<?, ?>> getter = context.lookup( Registries.CONFIGURED_FEATURE );
        final DimensionConfigGroup overworldConfigs = Config.getDimensionConfigs( Level.OVERWORLD );
        final DimensionConfigGroup netherConfigs = Config.getDimensionConfigs( Level.NETHER );
        
        // Ore placements
        registerVein( context, getter, BASE_INFESTED_BLOCK_ORE, overworldConfigs, netherConfigs );
        registerVein( context, getter, ADDED_INFESTED_BLOCK_ORE, overworldConfigs, netherConfigs );
        registerVein( context, getter, WATER_ORE, overworldConfigs, netherConfigs );
        registerVein( context, getter, SAND_ORE, overworldConfigs, netherConfigs );
        
        // Standard lone chest placements
        registerLoneChest( context, getter, SIMPLE_LONE_CHEST, overworldConfigs, netherConfigs );
        registerLoneChest( context, getter, VALUABLE_LONE_CHEST, overworldConfigs, netherConfigs );
        registerLoneChest( context, getter, TNT_TRAP_LONE_CHEST, overworldConfigs, netherConfigs );
        registerLoneChest( context, getter, INFESTED_LONE_CHEST, overworldConfigs, netherConfigs );
        registerLoneChest( context, getter, SURPRISE_LONE_CHEST, overworldConfigs, netherConfigs );
        
        // Standard lone spawner placements
        registerLoneSpawner( context, getter, SIMPLE_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, STREAM_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, SWARM_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, BRUTAL_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, FLOATY_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, MINI_SPAWNER, overworldConfigs, netherConfigs );
        registerLoneSpawner( context, getter, SILVERFISH_NEST, overworldConfigs, netherConfigs );
        
        // Standard floor trap placements
        registerFloorTrap( context, getter, TNT_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, getter, TNT_MOB_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, getter, POTION_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, getter, LAVA_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, getter, FIRE_TRAP, overworldConfigs, netherConfigs );
        // Water trap variant
        register( context, getter, SEA_MINE_MOB_TRAP,
                undergroundWaterFloorFeature( FloorTrapType.SEA_MINE_MOB.getConfig( overworldConfigs ) ) );
        register( context, getter, SEA_MINE_MOB_TRAP_OCEAN, SEA_MINE_MOB_TRAP.configuredKey,
                oceanFloorFeature( ((WaterTrapConfig.SeaMineMobTrapTypeCategory)
                        FloorTrapType.SEA_MINE_MOB.getConfig( overworldConfigs )).countPerChunkInOcean ) );
        
        // Spike trap patch placements
        registerSpikePatch( context, getter, MUNDANE_SPIKES, overworldConfigs, netherConfigs );
        registerSpikePatch( context, getter, POISON_SPIKES, overworldConfigs, netherConfigs );
        registerSpikePatch( context, getter, FIERY_SPIKES, overworldConfigs, netherConfigs );
        registerSpikePatch( context, getter, WITHERING_SPIKES, overworldConfigs, netherConfigs );
        registerSpikePatch( context, getter, MECHANICAL_MUNDANE_SPIKES, overworldConfigs, netherConfigs );
        registerSpikePatch( context, getter, MECHANICAL_POISON_SPIKES, overworldConfigs, netherConfigs );
        registerSpikePatch( context, getter, MECHANICAL_FIERY_SPIKES, overworldConfigs, netherConfigs );
        registerSpikePatch( context, getter, MECHANICAL_WITHERING_SPIKES, overworldConfigs, netherConfigs );
        
        // Standard pitfall trap placements
        registerPitfallTrap( context, getter, SPIKES_PITFALL_TRAP, overworldConfigs, netherConfigs );
        registerPitfallTrap( context, getter, LAVA_PITFALL_TRAP, overworldConfigs, netherConfigs );
        registerPitfallTrap( context, getter, COBWEB_PITFALL_TRAP, overworldConfigs, netherConfigs );
        
        // Standard tower placements
        registerTower( context, getter, SIMPLE_TOWER, overworldConfigs, netherConfigs );
        registerTower( context, getter, FIRE_TOWER, overworldConfigs, netherConfigs );
        registerTower( context, getter, POTION_TOWER, overworldConfigs, netherConfigs );
        registerTower( context, getter, GATLING_TOWER, overworldConfigs, netherConfigs );
        registerTower( context, getter, FIREBALL_TOWER, overworldConfigs, netherConfigs );
        
        // Sea mines
        registerSeaMine( context, getter, NORMAL_SEA_MINE, overworldConfigs );
        registerSeaMine( context, getter, PUFFER_SEA_MINE, overworldConfigs );
        registerSeaMine( context, getter, GUARDIAN_SEA_MINE, overworldConfigs );
        
        // Simple dungeon
        register( context, getter, NORMAL_DUNGEON.overworldKeys,
                simpleFeature( overworldConfigs.DUNGEONS.NORMAL ) );
        register( context, getter, NORMAL_DUNGEON.netherKeys,
                simpleFeature( netherConfigs.DUNGEONS.NORMAL ) );
        register( context, getter, MINI_DUNGEON.overworldKeys,
                simpleFeature( overworldConfigs.DUNGEONS.MINI ) );
        register( context, getter, MINI_DUNGEON.netherKeys,
                simpleFeature( netherConfigs.DUNGEONS.MINI ) );
        
        // Post-decoration placements
        register( context, getter, BURIED_BLOCK_POST_DECOR, CountPlacement.of( 1 ) ); // Placement is handled in the feature itself
    }
    
    /** @return Modifiers for a lone spawner feature. */
    protected static List<PlacementModifier> loneChest( ChestType type, DimensionConfigGroup dimConfigs ) {
        return floorFeature( type.getConfig( dimConfigs ) );
    }
    
    /** @return Modifiers for a lone spawner feature. */
    protected static List<PlacementModifier> loneSpawner( SpawnerType type, DimensionConfigGroup dimConfigs ) {
        return floorFeature( type.getConfig( dimConfigs ) );
    }
    
    /** @return Modifiers for a floor trap feature. */
    protected static List<PlacementModifier> floorTrap( FloorTrapType type, DimensionConfigGroup dimConfigs ) {
        return floorFeature( type.getConfig( dimConfigs ) );
    }
    
    /** @return Modifiers for a spike patch trap feature. */
    protected static List<PlacementModifier> spikeTrapPatch( SpikeTrapType type, DimensionConfigGroup dimConfigs ) {
        return floorFeature( type.getConfig( dimConfigs ) );
    }
    
    /** @return Modifiers for a pitfall trap feature. */
    protected static List<PlacementModifier> pitfallTrap( PitfallTrapType type, DimensionConfigGroup dimConfigs ) {
        return floorFeature( type.getConfig( dimConfigs ) );
    }
    
    /** @return Modifiers for a tower feature. */
    protected static List<PlacementModifier> tower( TowerType type, DimensionConfigGroup dimConfigs ) {
        return surfaceFeature( type.getConfig( dimConfigs ) );
    }
    
    /** @return Modifiers for a feature that generates only on floors. */
    protected static List<PlacementModifier> floorFeature( FeatureConfig.FeatureTypeCategory config ) {
        return undergroundVerticalScanFeature( config, false, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE );
    }
    
    /** @return Modifiers for a feature that generates only on ceilings. */
    protected static List<PlacementModifier> ceilFeature( FeatureConfig.FeatureTypeCategory config ) {
        return undergroundVerticalScanFeature( config, true, BlockPredicate.solid(), BlockPredicate.ONLY_IN_AIR_PREDICATE );
    }
    
    /** @return Modifiers for a feature that generates only on floors or fluid surfaces. */
    protected static List<PlacementModifier> surfaceFeature( FeatureConfig.FeatureTypeCategory config ) {
        return undergroundVerticalScanFeature( config, false, BlockPredicate.anyOf( BlockPredicate.solid(),
                PREDICATE_ANY_FLUID ), BlockPredicate.ONLY_IN_AIR_PREDICATE );
    }
    
    /** @return Modifiers for a feature that generates only on the bottom of fluids, including surface fluids. */
    protected static List<PlacementModifier> fluidFloorFeature( FeatureConfig.FeatureTypeCategory config ) {
        return verticalScanFeature( config, false, BlockPredicate.solid(),
                BlockPredicate.anyOf( PREDICATE_ANY_FLUID ) );
    }
    
    /** @return Modifiers for a feature that generates only on the bottom of surface water. */
    protected static List<PlacementModifier> oceanFloorFeature( DoubleField countPerChunk ) {
        return baseOceanPlacement( countPerChunk )
                .move( Direction.DOWN, BlockPredicate.solid(), BlockPredicate.anyOf( PREDICATE_WATER ), 12 )
                .moveVertical( 1 ).requireAboveOceanFloor( 0 ).requireBiome().build();
    }
    
    /** @return Modifiers for a feature that generates only on the bottom of water. */
    protected static List<PlacementModifier> undergroundWaterFloorFeature( FeatureConfig.FeatureTypeCategory config ) {
        return baseVerticalScan( config, false, BlockPredicate.solid(),
                BlockPredicate.anyOf( PREDICATE_WATER ) )
                .requireBelowOceanFloor( 2 ).requireBiome().build();
    }
    
    /** @return Modifiers for a feature that generates only on the bottom of water, including surface water. */
    protected static List<PlacementModifier> waterFloorFeature( FeatureConfig.FeatureTypeCategory config ) {
        return verticalScanFeature( config, false, BlockPredicate.solid(),
                BlockPredicate.anyOf( PREDICATE_WATER ) );
    }
    
    /** @return Modifiers for a feature that generates only on the bottom of lava, including surface lava. */
    protected static List<PlacementModifier> lavaFloorFeature( FeatureConfig.FeatureTypeCategory config ) {
        return verticalScanFeature( config, false, BlockPredicate.solid(),
                BlockPredicate.anyOf( PREDICATE_LAVA ) );
    }
    
    /** @return Modifiers for a feature that scans up or down (up to 12 blocks) for a potential valid underground location. */
    protected static List<PlacementModifier> undergroundVerticalScanFeature( FeatureConfig.FeatureTypeCategory config, boolean up,
                                                                             BlockPredicate scanFor, BlockPredicate scanWhile ) {
        return undergroundVerticalScanFeature( config, up, scanFor, scanWhile, 12 );
    }
    
    /** @return Modifiers for a feature that scans up or down for a potential valid underground location. */
    protected static List<PlacementModifier> undergroundVerticalScanFeature( FeatureConfig.FeatureTypeCategory config, boolean up,
                                                                             BlockPredicate scanFor, BlockPredicate scanWhile, int scanRange ) {
        return baseVerticalScan( config, up, scanFor, scanWhile, scanRange )
                .requireBelowOceanFloor( 2 ).requireBiome().build();
    }
    
    /** @return Modifiers for a feature that scans up or down (up to 12 blocks) for a potential valid location, including above ground. */
    protected static List<PlacementModifier> verticalScanFeature( FeatureConfig.FeatureTypeCategory config, boolean up,
                                                                  BlockPredicate scanFor, BlockPredicate scanWhile ) {
        return verticalScanFeature( config, up, scanFor, scanWhile, 12 );
    }
    
    /** @return Modifiers for a feature that scans up or down for a potential valid location, including above ground. */
    protected static List<PlacementModifier> verticalScanFeature( FeatureConfig.FeatureTypeCategory config, boolean up,
                                                                  BlockPredicate scanFor, BlockPredicate scanWhile, int scanRange ) {
        return baseVerticalScan( config, up, scanFor, scanWhile, scanRange ).requireBiome().build();
    }
    
    /** @return Modifiers for a feature that simply tries placing randomly. */
    protected static List<PlacementModifier> undergroundSimpleFeature( FeatureConfig.FeatureTypeCategory config ) {
        return basePlacement( config ).requireBelowOceanFloor( 2 ).requireBiome().build();
    }
    
    /** @return Modifiers for a feature that simply tries placing randomly, including above ground. */
    protected static List<PlacementModifier> simpleFeature( FeatureConfig.FeatureTypeCategory config ) {
        return basePlacement( config ).requireBiome().build();
    }
    
    
    /** @return Modifiers for a feature that scans up or down for a potential valid location, including above ground. */
    protected static PlacementBuilder baseVerticalScan( FeatureConfig.FeatureTypeCategory config, boolean up,
                                                        BlockPredicate scanFor, BlockPredicate scanWhile ) {
        return baseVerticalScan( config, up, scanFor, scanWhile, 12 );
    }
    
    /** @return Modifiers for a feature that scans up or down for a potential valid location, including above ground. */
    protected static PlacementBuilder baseVerticalScan( FeatureConfig.FeatureTypeCategory config, boolean up,
                                                        BlockPredicate scanFor, BlockPredicate scanWhile, int scanRange ) {
        return basePlacement( config )
                .move( up ? Direction.UP : Direction.DOWN, scanFor, scanWhile, scanRange )
                .moveVertical( up ? -1 : 1 );
    }
    
    /** @return A new placement builder with the typical modifiers for an ocean feature already included. */
    protected static PlacementBuilder baseOceanPlacement( DoubleField countPerChunk ) {
        return new PlacementBuilder().multiply( countPerChunk ).spreadInChunk().spreadInOceanHeights();
    }
    
    /** @return A new placement builder with the typical modifiers for a feature already included. */
    protected static PlacementBuilder basePlacement( FeatureConfig.FeatureTypeCategory config ) {
        return new PlacementBuilder().multiply( config ).spreadInChunk().spreadInHeights( config );
    }
    
    
    /** Registers a placed vein feature to each supported dimension. */
    protected static void registerVein( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                        FeatureKeys.Vein featureKeys, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, featureKeys.overworldKeys, simpleFeature( featureKeys.configGetter.apply( overworldConfigs ) ) );
        register( context, getter, featureKeys.netherKeys, simpleFeature( featureKeys.configGetter.apply( netherConfigs ) ) );
    }
    
    /** Registers a placed lone chest type feature to each supported dimension. */
    protected static void registerLoneChest( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                             FeatureKeys.LoneChest featureKeys, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, featureKeys.overworldKeys, loneChest( featureKeys.chestType, overworldConfigs ) );
        register( context, getter, featureKeys.netherKeys, loneChest( featureKeys.chestType, netherConfigs ) );
    }
    
    /** Registers a placed lone spawner type feature to each supported dimension. */
    protected static void registerLoneSpawner( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                               FeatureKeys.Spawner featureKeys, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, featureKeys.overworldKeys, loneSpawner( featureKeys.spawnerType, overworldConfigs ) );
        register( context, getter, featureKeys.netherKeys, loneSpawner( featureKeys.spawnerType, netherConfigs ) );
    }
    
    /** Registers a placed floor trap type feature to each supported dimension. */
    protected static void registerFloorTrap( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                             FeatureKeys.FloorTrap featureKeys, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, featureKeys.overworldKeys, floorTrap( featureKeys.trapType, overworldConfigs ) );
        register( context, getter, featureKeys.netherKeys, floorTrap( featureKeys.trapType, netherConfigs ) );
    }
    
    /** Registers a placed spike trap patch feature to each supported dimension. */
    protected static void registerSpikePatch( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                              FeatureKeys.SpikeTrap featureKeys, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, featureKeys.overworldKeys, spikeTrapPatch( featureKeys.trapType, overworldConfigs ) );
        register( context, getter, featureKeys.netherKeys, spikeTrapPatch( featureKeys.trapType, netherConfigs ) );
    }
    
    /** Registers a placed floor trap type feature to each supported dimension. */
    protected static void registerPitfallTrap( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                               FeatureKeys.PitfallTrap featureKeys, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, featureKeys.overworldKeys, pitfallTrap( featureKeys.trapType, overworldConfigs ) );
        register( context, getter, featureKeys.netherKeys, pitfallTrap( featureKeys.trapType, netherConfigs ) );
    }
    
    /** Registers a placed tower type feature to each supported dimension. */
    protected static void registerTower( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                         FeatureKeys.TowerDispenser featureKeys, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, getter, featureKeys.overworldKeys, tower( featureKeys.towerType, overworldConfigs ) );
        register( context, getter, featureKeys.netherKeys, tower( featureKeys.towerType, netherConfigs ) );
    }
    
    /** Registers a placed sea mine type feature for the overworld. */
    protected static void registerSeaMine( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter,
                                           FeatureKeys.SeaMine featureKeys, DimensionConfigGroup overworldConfigs ) {
        register( context, getter, featureKeys.overworldKeys, undergroundWaterFloorFeature( featureKeys.seaMineType.getConfig( overworldConfigs ) ) );
        register( context, getter, featureKeys, oceanFloorFeature( featureKeys.seaMineType.getConfig( overworldConfigs ).countPerChunkInOcean ) );
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
    protected static void register( BootstapContext<PlacedFeature> context, HolderGetter<ConfiguredFeature<?, ?>> getter, FeatureKeys.OceanFeature oceanFeatureKeys, List<PlacementModifier> modifiers ) {
        register( context, getter, oceanFeatureKeys.overworldOceanKey, oceanFeatureKeys.overworldKeys.configuredKey, modifiers );
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
    
    
    /** Creates a placed ore feature key that is automatically added to all overworld biomes. */
    protected static ResourceKey<PlacedFeature> overworldOreKey( String name ) {
        final ResourceKey<PlacedFeature> key = key( name + "_ore" );
        OVERWORLD_ORE_FEATURES.add( key );
        return key;
    }
    
    /** Creates a placed ore feature key that is automatically added to all nether biomes. */
    protected static ResourceKey<PlacedFeature> netherOreKey( String name ) {
        final ResourceKey<PlacedFeature> key = key( name + "_nether_ore" );
        NETHER_ORE_FEATURES.add( key );
        return key;
    }
    
    /** Creates a placed decoration feature key that is automatically added to all overworld biomes. */
    protected static ResourceKey<PlacedFeature> overworldKey( String name ) {
        final ResourceKey<PlacedFeature> key = key( name );
        OVERWORLD_FEATURES.add( key );
        return key;
    }
    
    /** Creates a placed decoration feature key that is automatically added to all nether biomes. */
    protected static ResourceKey<PlacedFeature> netherKey( String name ) {
        final ResourceKey<PlacedFeature> key = key( name + "_nether" );
        NETHER_FEATURES.add( key );
        return key;
    }
    
    /** Creates a placed post-decoration feature key that is automatically added to all biomes. */
    protected static ResourceKey<PlacedFeature> anyDimPostDecor( String name ) {
        final ResourceKey<PlacedFeature> key = key( name + "_any_dimension_post_decoration" );
        ANY_DIMENSION_POST_DECORATION.add( key );
        return key;
    }
    
    /** Creates a placed feature key that is not added to any world gen. */
    protected static ResourceKey<PlacedFeature> key( String name ) {
        return ResourceKey.create( Registries.PLACED_FEATURE, DeadlyWorld.rl( name ) );
    }
}