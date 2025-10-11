package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import fathertoast.deadlyworld.common.world.levelgen.trap.FloorTrapFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.LoneSpawnerFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.SimpleTowerDispenserFeature;
import fathertoast.deadlyworld.common.world.levelgen.FloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import fathertoast.deadlyworld.common.world.levelgen.TowerDispenserSettings;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Supplier;

/**
 *  Base class for our feature provider.
 *  Keeping convenience methods here so the
 *  implementation doesn't get super bloated and insane looking.
 */
public abstract class AbstractCFProvider {



    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block(Supplier<? extends Block> block ) { return block( block.get() ); }

    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( Block block ) { return block( block.defaultBlockState() ); }

    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( BlockState block ) { return BlockStateProvider.simple( block ); }




    /** Registers a configured lone spawner type feature to each supported dimension. */
    protected static void registerLoneSpawner(BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.Spawner feature,
                                              DimensionConfigGroup overworldConfigs, BlockStateProvider overworldTopper, boolean overworldVines,
                                              DimensionConfigGroup netherConfigs, BlockStateProvider netherTopper, boolean netherVines ) {
        registerLoneSpawner( context, feature.overworldKeys, feature.spawnerType, overworldConfigs, overworldTopper, overworldVines );
        registerLoneSpawner( context, feature.netherKeys, feature.spawnerType, netherConfigs, netherTopper, netherVines );
    }

    /** Registers a configured lone spawner type feature. */
    protected static void registerLoneSpawner(BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
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
    protected static void registerFloorTrap(BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                            TrapType type, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.FLOOR_TRAP.get(),
                new FloorTrapFeature.Configuration( block( DWBlocks.trap( type ) ),
                        FloorTrapSettings.of( type.getFeatureConfig( dimConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }



    /** Registers a configured tower dispenser type feature to each supported dimension. */
    protected static void registerTowerDispenser( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.TowerDispenser feature, BlockStateProvider baseProvider, DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        registerTowerDispenser( context, feature.overworldKeys, feature.towerType, baseProvider, overworldConfigs );
        registerTowerDispenser( context, feature.netherKeys, feature.towerType, baseProvider, netherConfigs );
    }

    /** Registers a configured tower dispenser type feature. */
    protected static void registerTowerDispenser(BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                                 TowerType type, BlockStateProvider baseProvider, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.TOWER_DISPENSER.get(),
                new SimpleTowerDispenserFeature.Configuration(
                        baseProvider,
                        block( DWBlocks.towerDispenser( type ) ),
                        TowerDispenserSettings.of( type.getFeatureConfig( dimConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }


    /** Registers a configured feature. */
    protected static void register( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( featureKeys.configuredKey, configuredFeature );
    }

    /** Registers a configured feature. */
    protected static void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( confFeatureKey, configuredFeature );
    }





    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> overworldKey( String name ) { return key( name ); }

    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> netherKey( String name ) { return key( name + "_nether" ); }

    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> anyDimKey( String name ) { return key( name + "_any_dimension" ); }

    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> key( String name ) {
        return ResourceKey.create( Registries.CONFIGURED_FEATURE, DeadlyWorld.resourceLoc( name ) );
    }
}
