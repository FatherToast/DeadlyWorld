package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.sea_mine.SeaMineBlock;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import fathertoast.deadlyworld.common.world.levelgen.FloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.SeaMineSettings;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import fathertoast.deadlyworld.common.world.levelgen.TowerDispenserSettings;
import fathertoast.deadlyworld.common.world.levelgen.trap.FloorTrapFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.LoneSpawnerFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.SeaMineFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.TowerFeature;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Base class for our feature provider.
 * Keeping convenience methods here so the
 * implementation doesn't get super bloated and insane looking.
 */
public abstract class AbstractCFProvider {
    /** List of all configurations that cannot be (reasonably) placed by a feature placer. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> NOT_PLACEABLE = new ArrayList<>();
    
    /**
     * List of all configurations that don't care about dimension type and should generate anywhere.
     * Any restrictions are handled in the feature itself, usually config based.
     */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> ANY_DIMENSION_FEATURES = new ArrayList<>();
    /** List of all configurations that should generate in overworld biomes. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> OVERWORLD_FEATURES = new ArrayList<>();
    /** List of all configurations that should generate in nether biomes. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> NETHER_FEATURES = new ArrayList<>();

    /** List of all spawner configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> SPAWNER_FEATURES = new ArrayList<>();
    /** List of all trap configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> TRAP_FEATURES = new ArrayList<>();
    /** List of all tower dispenser configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> TOWER_FEATURES = new ArrayList<>();
    /** List of all sea mine configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> SEA_MINE_FEATURES = new ArrayList<>();
    /** List of all dungeon configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> DUNGEON_FEATURES = new ArrayList<>();
    
    
    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( Supplier<? extends Block> block ) { return block( block.get() ); }
    
    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( Block block ) { return block( block.defaultBlockState() ); }
    
    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( BlockState block ) { return BlockStateProvider.simple( block ); }
    
    
    /** Registers a configured lone spawner type feature to each supported dimension. */
    protected static void registerLoneSpawner( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.Spawner feature,
                                               DimensionConfigGroup overworldConfigs, BlockStateProvider overworldTopper, boolean overworldVines,
                                               DimensionConfigGroup netherConfigs, BlockStateProvider netherTopper, boolean netherVines ) {
        registerLoneSpawner( context, feature.overworldKeys, feature.spawnerType, overworldConfigs, overworldTopper, overworldVines );
        registerLoneSpawner( context, feature.netherKeys, feature.spawnerType, netherConfigs, netherTopper, netherVines );
    }
    
    /** Registers a configured lone spawner type feature. */
    protected static void registerLoneSpawner( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                               SpawnerType type, DimensionConfigGroup dimConfigs, BlockStateProvider topper, boolean vines ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.LONE_SPAWNER.get(),
                new LoneSpawnerFeature.Configuration( block( DWBlocks.spawner( type ) ), topper,
                        SpawnerSettings.of( type.getFeatureConfig( dimConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE, vines ) ) );
    }
    
    
    /** Registers a configured floor trap type feature to each supported dimension. */
    protected static void registerFloorTrap( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.Trap feature,
                                             DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        registerFloorTrap( context, feature.overworldKeys, feature.trapType, overworldConfigs );
        registerFloorTrap( context, feature.netherKeys, feature.trapType, netherConfigs );
    }
    
    /** Registers a configured floor trap type feature. */
    protected static void registerFloorTrap( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                             TrapType type, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.FLOOR_TRAP.get(),
                new FloorTrapFeature.Configuration( block( DWBlocks.trap( type ) ),
                        FloorTrapSettings.of( type.getFeatureConfig( dimConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }


    /** Registers a configured tower dispenser type feature to each supported dimension. */
    protected static void registerTower( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.TowerDispenser feature,
                                         DimensionConfigGroup overworldConfigs, BlockStateProvider overworldBase,
                                         DimensionConfigGroup netherConfigs, BlockStateProvider netherBase ) {
        registerTower( context, feature.overworldKeys, feature.towerType, overworldBase, overworldConfigs );
        registerTower( context, feature.netherKeys, feature.towerType, netherBase, netherConfigs );
    }
    
    /** Registers a configured tower dispenser type feature. */
    protected static void registerTower( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                         TowerType type, BlockStateProvider base, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.TOWER.get(),
                new TowerFeature.Configuration( block( DWBlocks.towerDispenser( type ) ), base,
                        TowerDispenserSettings.of( type.getFeatureConfig( dimConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }


    /** Registers a configured registerSeaMine type feature to each supported dimension. */
    protected static void registerSeaMine( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.SeaMine feature,
                                           DimensionConfigGroup overworldConfigs ) {
        registerSeaMine( context,
                feature.overworldKeys, feature.seaMineType,
                block( Blocks.CHAIN.defaultBlockState()
                        .setValue( ChainBlock.AXIS, Direction.Axis.Y )
                        .setValue( ChainBlock.WATERLOGGED, true ) ),
                overworldConfigs );
    }

    /** Registers a configured sea mine type feature. */
    protected static void registerSeaMine( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                          SeaMineType type, BlockStateProvider trailProvider, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.SEA_MINE.get(),
                new SeaMineFeature.Configuration(
                        block( DWBlocks.seaMine( type ).get().defaultBlockState().setValue( SeaMineBlock.WATERLOGGED, true ) ),
                        trailProvider,
                        SeaMineSettings.of( type.getFeatureConfig( dimConfigs ) ) ) ) );
    }

    
    /** Registers a configured feature. */
    protected static void register( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( featureKeys.configuredKey, configuredFeature );
    }
    
    /** Registers a configured feature. */
    protected static void register( BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( confFeatureKey, configuredFeature );
    }
    
    
    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> overworldKey( String name ) {
        final ResourceKey<ConfiguredFeature<?, ?>> key = key( name );
        OVERWORLD_FEATURES.add( key );
        return key;
    }
    
    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> netherKey( String name ) {
        final ResourceKey<ConfiguredFeature<?, ?>> key = key( name + "_nether" );
        NETHER_FEATURES.add( key );
        return key;
    }
    
    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> anyDimKey( String name ) {
        final ResourceKey<ConfiguredFeature<?, ?>> key = key( name + "_any_dimension" );
        ANY_DIMENSION_FEATURES.add( key );
        return key;
    }
    
    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> key( String name ) {
        return ResourceKey.create( Registries.CONFIGURED_FEATURE, DeadlyWorld.resourceLoc( name ) );
    }
}