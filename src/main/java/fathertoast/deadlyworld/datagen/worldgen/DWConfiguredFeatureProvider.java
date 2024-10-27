package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import fathertoast.deadlyworld.common.world.levelgen.LoneSpawnerFeature;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.function.Supplier;

public class DWConfiguredFeatureProvider {
    
    static final ResourceKey<ConfiguredFeature<?, ?>> SIMPLE_SPAWNER = key( "simple_spawner" );
    static final ResourceKey<ConfiguredFeature<?, ?>> STREAM_SPAWNER = key( "stream_spawner" );
    static final ResourceKey<ConfiguredFeature<?, ?>> SWARM_SPAWNER = key( "swarm_spawner" );
    static final ResourceKey<ConfiguredFeature<?, ?>> BRUTAL_SPAWNER = key( "brutal_spawner" );
    
    /** Called by registry set builder to generate our configured features. */
    public static void bootstrap( BootstapContext<ConfiguredFeature<?, ?>> context ) {
        registerLoneSpawner( context, SIMPLE_SPAWNER, SpawnerType.DEFAULT, block( Blocks.AIR ), false );
        registerLoneSpawner( context, STREAM_SPAWNER, SpawnerType.STREAM, block( Blocks.MUD_BRICKS ), false );
        registerLoneSpawner( context, SWARM_SPAWNER, SpawnerType.SWARM, block( Blocks.CHISELED_SANDSTONE ), false );
        registerLoneSpawner( context, BRUTAL_SPAWNER, SpawnerType.BRUTAL, block( Blocks.CHISELED_STONE_BRICKS ), true );
    }
    
    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( Supplier<? extends Block> block ) { return block( block.get() ); }
    
    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( Block block ) { return block( block.defaultBlockState() ); }
    
    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( BlockState block ) { return BlockStateProvider.simple( block ); }
    
    /** Registers a configured lone spawner type feature. */ //TODO make the spawner settings dimension-sensitive
    protected static void registerLoneSpawner( BootstapContext<ConfiguredFeature<?, ?>> context,
                                               ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey,
                                               SpawnerType type, BlockStateProvider topper, boolean vines ) {
        register( context, confFeatureKey, new ConfiguredFeature<>( DWFeatures.LONE_SPAWNER.get(),
                new LoneSpawnerFeature.Configuration( block( DWBlocks.spawner( type ) ), topper,
                        SpawnerSettings.of( type.getFeatureConfig( Config.getDefaultConfigs() ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE, vines ) ) );
    }
    
    /** Registers a configured feature. */
    protected static void register( BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( confFeatureKey, configuredFeature );
    }
    
    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> key( String name ) {
        return ResourceKey.create( Registries.CONFIGURED_FEATURE, DeadlyWorld.resourceLoc( name ) );
    }
}