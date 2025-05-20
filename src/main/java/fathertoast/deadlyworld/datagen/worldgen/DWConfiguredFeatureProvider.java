package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import fathertoast.deadlyworld.common.world.levelgen.FloorTrapFeature;
import fathertoast.deadlyworld.common.world.levelgen.LoneSpawnerFeature;
import fathertoast.deadlyworld.common.world.levelgen.PotionFloorTrapFeature;
import fathertoast.deadlyworld.common.world.levelgen.SilverfishNestFeature;
import fathertoast.deadlyworld.common.world.levelgen.settings.FloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.settings.PotionFloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.settings.SpawnerSettings;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Supplier;

public class DWConfiguredFeatureProvider {
    
    static final FeatureKeys.Spawner SIMPLE_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.SIMPLE, "simple_spawner" );
    static final FeatureKeys.Spawner STREAM_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.STREAM, "stream_spawner" );
    static final FeatureKeys.Spawner SWARM_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.SWARM, "swarm_spawner" );
    static final FeatureKeys.Spawner BRUTAL_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.BRUTAL, "brutal_spawner" );
    static final FeatureKeys.Spawner MINI_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.MINI, "mini_spawner" );
    static final FeatureKeys.Spawner SILVERFISH_NEST = FeatureKeys.Spawner.of( SpawnerType.NEST, "silverfish_nest" );

    static final FeatureKeys.Trap TNT_TRAP = FeatureKeys.Trap.of( TrapType.TNT, "tnt_trap" );
    static final FeatureKeys.Trap TNT_MOB_TRAP = FeatureKeys.Trap.of( TrapType.TNT_MOB, "tnt_mob_trap" );
    static final FeatureKeys.Trap POTION_TRAP = FeatureKeys.Trap.of( TrapType.POTION, "potion_trap" );
    static final FeatureKeys.Trap LAVA_TRAP = FeatureKeys.Trap.of( TrapType.LAVA, "lava_trap" );


    /** Called by registry set builder to generate our configured features. */
    public static void bootstrap( BootstapContext<ConfiguredFeature<?, ?>> context ) {
        final DimensionConfigGroup overworldConfigs = Config.getDimensionConfigs( Level.OVERWORLD );
        final DimensionConfigGroup netherConfigs = Config.getDimensionConfigs( Level.NETHER );
        
        // Plain lone spawner features
        registerLoneSpawner( context, SIMPLE_SPAWNER,
                overworldConfigs, block( Blocks.AIR ), false,
                netherConfigs, block( Blocks.AIR ), false );
        registerLoneSpawner( context, STREAM_SPAWNER,
                overworldConfigs, block( Blocks.MUD_BRICKS ), false,
                netherConfigs, block( Blocks.RED_NETHER_BRICKS ), false );
        registerLoneSpawner( context, SWARM_SPAWNER,
                overworldConfigs, block( Blocks.CHISELED_SANDSTONE ), false,
                netherConfigs, block( Blocks.CHISELED_RED_SANDSTONE ), false );
        registerLoneSpawner( context, BRUTAL_SPAWNER,
                overworldConfigs, block( Blocks.CHISELED_STONE_BRICKS ), true,
                netherConfigs, block( Blocks.CHISELED_QUARTZ_BLOCK ), true );
        registerLoneSpawner( context, MINI_SPAWNER,
                overworldConfigs, block( Blocks.AIR ), false,
                netherConfigs, block( Blocks.AIR ), false );

        // Fancy lone spawner features
        register( context, SILVERFISH_NEST.overworldKeys, new ConfiguredFeature<>( DWFeatures.SILVERFISH_NEST.get(), new SilverfishNestFeature.Configuration(
                block( DWBlocks.spawner( SILVERFISH_NEST.spawnerType ) ), block( Blocks.INFESTED_COBBLESTONE ),
                SpawnerSettings.of( SILVERFISH_NEST.spawnerType, overworldConfigs ), BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, SILVERFISH_NEST.netherKeys, new ConfiguredFeature<>( DWFeatures.SILVERFISH_NEST.get(), new SilverfishNestFeature.Configuration(
                block( DWBlocks.spawner( SILVERFISH_NEST.spawnerType ) ), block( Blocks.INFESTED_DEEPSLATE ),
                SpawnerSettings.of( SILVERFISH_NEST.spawnerType, netherConfigs ), BlockTags.FEATURES_CANNOT_REPLACE ) ) );

        // Floor traps
        registerFloorTrap( context, TNT_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, TNT_MOB_TRAP, overworldConfigs, netherConfigs );
        registerFloorTrap( context, LAVA_TRAP, overworldConfigs, netherConfigs );

        // Potion floor trap
        register( context, POTION_TRAP.overworldKeys, new ConfiguredFeature<>( DWFeatures.POTION_FLOOR_TRAP.get(), new PotionFloorTrapFeature.Configuration(
                block( DWBlocks.trap( POTION_TRAP.trapType ) ), PotionFloorTrapSettings.create( overworldConfigs.TRAPS.POTION ),
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, POTION_TRAP.netherKeys, new ConfiguredFeature<>( DWFeatures.POTION_FLOOR_TRAP.get(), new PotionFloorTrapFeature.Configuration(
                block( DWBlocks.trap( POTION_TRAP.trapType ) ), PotionFloorTrapSettings.create( netherConfigs.TRAPS.POTION ),
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }
    
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

    /** Registers a configured lone spawner type feature to each supported dimension. */
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
    
    /** Registers a configured feature. */
    protected static void register( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( featureKeys.configuredKey, configuredFeature );
    }
    
    /** Registers a configured feature. */
    protected static void register( BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( confFeatureKey, configuredFeature );
    }
    
    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> overworldKey( String name ) { return key( name ); }
    
    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> netherKey( String name ) { return key( name + "_nether" ); }
    
    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> key( String name ) {
        return ResourceKey.create( Registries.CONFIGURED_FEATURE, DeadlyWorld.resourceLoc( name ) );
    }
}