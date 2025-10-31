package fathertoast.deadlyworld.datagen.worldgen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import fathertoast.deadlyworld.common.block.chest.ChestType;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapType;
import fathertoast.deadlyworld.common.block.unstable.PitfallTrapType;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineBlock;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import fathertoast.deadlyworld.common.world.levelgen.*;
import fathertoast.deadlyworld.common.world.levelgen.misc.DeadlyOreFeature;
import fathertoast.deadlyworld.common.world.levelgen.misc.InfestedOreFeature;
import fathertoast.deadlyworld.common.world.levelgen.misc.LoneChestFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Base class for our feature provider.
 * Keeping convenience methods here so the
 * implementation doesn't get super bloated and insane looking.
 */
public abstract class DWAbstractCFProvider {
    /** List of all configurations that cannot be (reasonably) placed by a feature placer. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> NOT_PLACEABLE = new ArrayList<>();
    
    /**
     * List of all ore configurations that don't care about dimension type and should generate anywhere.
     * Any restrictions are handled in the feature itself, usually config based.
     */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> ANY_DIMENSION_ORE_FEATURES = new ArrayList<>();
    /** List of all ore configurations that should generate in overworld biomes. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> OVERWORLD_ORE_FEATURES = new ArrayList<>();
    /** List of all ore configurations that should generate in nether biomes. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> NETHER_ORE_FEATURES = new ArrayList<>();
    
    /** List of all vein configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> VEIN_FEATURES = new ArrayList<>();
    
    /** List of all decoration configurations that should generate in overworld biomes. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> OVERWORLD_FEATURES = new ArrayList<>();
    /** List of all decoration configurations that should generate in nether biomes. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> NETHER_FEATURES = new ArrayList<>();
    
    /** List of all lone chest configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> LONE_CHEST_FEATURES = new ArrayList<>();
    /** List of all spawner configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> SPAWNER_FEATURES = new ArrayList<>();
    /** List of all floor trap configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> FLOOR_TRAP_FEATURES = new ArrayList<>();
    /** List of all spike trap configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> SPIKE_TRAP_FEATURES = new ArrayList<>();
    /** List of all pitfall trap configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> PITFALL_TRAP_FEATURES = new ArrayList<>();
    /** List of all tower configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> TOWER_FEATURES = new ArrayList<>();
    /** List of all sea mine configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> SEA_MINE_FEATURES = new ArrayList<>();
    /** List of all dungeon configurations. */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> DUNGEON_FEATURES = new ArrayList<>();

    /**
     * List of all configurations that don't care about dimension type and should generate anywhere,
     * AFTER {@link net.minecraft.world.level.levelgen.GenerationStep.Decoration#UNDERGROUND_DECORATION}.
     * Any restrictions are handled in the feature itself, usually config based.
     */
    public static final List<ResourceKey<ConfiguredFeature<?, ?>>> ANY_DIMENSION_POST_DECORATION = new ArrayList<>();


    private static final ResourceLocation EMPTY_RESOURCE_LOCATION = ResourceLocation.fromNamespaceAndPath( "", "" );
    
    // We aren't adding any "actual" ores, so we just use the base stone replace rules like dirt, etc. does
    private static final RuleTest TARGET_RULE_OVERWORLD = new TagMatchTest( BlockTags.BASE_STONE_OVERWORLD );
    private static final RuleTest TARGET_RULE_NETHER = new TagMatchTest( BlockTags.BASE_STONE_NETHER );
    
    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( Supplier<? extends Block> block ) { return block( block.get() ); }
    
    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( Block block ) { return block( block.defaultBlockState() ); }
    
    /** Convenience method for making a simple block state provider. */
    protected static BlockStateProvider block( BlockState block ) { return BlockStateProvider.simple( block ); }

    /** Convenience method for making a simple weighted random state provider with equal weights. */
    protected static BlockStateProvider blocks( BlockState... blocks ) {
        SimpleWeightedRandomList.Builder<BlockState> builder = new SimpleWeightedRandomList.Builder<>();

        for ( BlockState state : blocks ) {
            builder.add( state, 1 );
        }
        return new WeightedStateProvider( builder );
    }

    /** Convenience method for making a simple weighted random state provider. */
    protected static BlockStateProvider weightedBlocks( Pair<BlockState, Integer>... stateWeightPairs ) {
        SimpleWeightedRandomList.Builder<BlockState> builder = new SimpleWeightedRandomList.Builder<>();

        for ( Pair<BlockState, Integer> pair : stateWeightPairs ) {
            builder.add( pair.getFirst(), pair.getSecond() );
        }
        return new WeightedStateProvider( builder );
    }
    
    /** Registers a configured vein type feature to each supported dimension. */
    protected static void registerInfestedVein( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.Vein featureKeys,
                                                DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        register( context, featureKeys.overworldKeys, new ConfiguredFeature<>( DWFeatures.INFESTED_ORE.get(),
                InfestedOreFeature.Configuration.of( featureKeys.configGetter.apply( overworldConfigs ) ) ) );
        register( context, featureKeys.netherKeys, new ConfiguredFeature<>( DWFeatures.INFESTED_ORE.get(),
                InfestedOreFeature.Configuration.of( featureKeys.configGetter.apply( netherConfigs ) ) ) );
    }
    
    /** Registers a configured vein type feature to each supported dimension. */
    protected static void registerVein( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.Vein featureKeys,
                                        DimensionConfigGroup overworldConfigs, Block overworldBlock,
                                        DimensionConfigGroup netherConfigs, Block netherBlock ) {
        register( context, featureKeys.overworldKeys, new ConfiguredFeature<>( DWFeatures.DEADLY_ORE.get(),
                DeadlyOreFeature.Configuration.of( featureKeys.configGetter.apply( overworldConfigs ),
                        ImmutableList.of( OreConfiguration.target( TARGET_RULE_OVERWORLD, overworldBlock.defaultBlockState() ) ) ) ) );
        register( context, featureKeys.netherKeys, new ConfiguredFeature<>( DWFeatures.DEADLY_ORE.get(),
                DeadlyOreFeature.Configuration.of( featureKeys.configGetter.apply( netherConfigs ),
                        ImmutableList.of( OreConfiguration.target( TARGET_RULE_NETHER, netherBlock.defaultBlockState() ) ) ) ) );
    }
    
    /** Registers a configured lone chest type feature to each supported dimension. */
    protected static void registerLoneChest( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.LoneChest featureKeys,
                                             DimensionConfigGroup overworldConfigs, BlockStateProvider overworldChest,
                                             DimensionConfigGroup netherConfigs, BlockStateProvider netherChest,
                                             @Nullable FeatureKeys.FloorTrap trapFeature ) {
        registerLoneChest( context, featureKeys.overworldKeys, featureKeys.chestType, overworldConfigs, overworldChest,
                trapFeature == null ? null : trapFeature.overworldKeys );
        registerLoneChest( context, featureKeys.netherKeys, featureKeys.chestType, netherConfigs, netherChest,
                trapFeature == null ? null : trapFeature.netherKeys );
    }
    
    /** Registers a configured lone chest type feature. */
    protected static void registerLoneChest( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                             ChestType type, DimensionConfigGroup dimConfigs, BlockStateProvider chest, @Nullable FeatureKeys trapFeatureKeys ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.LONE_CHEST.get(),
                new LoneChestFeature.Configuration( chest, ChestSettings.of( type, dimConfigs ),
                        trapFeatureKeys == null ? EMPTY_RESOURCE_LOCATION : trapFeatureKeys.configuredKey.location(),
                        BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }
    
    
    /** Registers a configured lone spawner type feature to each supported dimension. */
    protected static void registerLoneSpawner( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.Spawner featureKeys,
                                               DimensionConfigGroup overworldConfigs, BlockStateProvider overworldTopper, boolean overworldVines,
                                               DimensionConfigGroup netherConfigs, BlockStateProvider netherTopper, boolean netherVines ) {
        registerLoneSpawner( context, featureKeys.overworldKeys, featureKeys.spawnerType, overworldConfigs, overworldTopper, overworldVines );
        registerLoneSpawner( context, featureKeys.netherKeys, featureKeys.spawnerType, netherConfigs, netherTopper, netherVines );
    }
    
    /** Registers a configured lone spawner type feature. */
    protected static void registerLoneSpawner( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                               SpawnerType type, DimensionConfigGroup dimConfigs, BlockStateProvider topper, boolean vines ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.LONE_SPAWNER.get(),
                new LoneSpawnerFeature.Configuration( block( DWBlocks.spawner( type ) ), topper,
                        SpawnerSettings.of( type.getConfig( dimConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE, vines ) ) );
    }

    /** Registers a configured floor trap type feature to each supported dimension. */
    protected static void registerFloorTrap( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.FloorTrap featureKeys,
                                             DimensionConfigGroup overworldConfigs, DimensionConfigGroup netherConfigs ) {
        registerFloorTrap( context, featureKeys.overworldKeys, featureKeys.trapType, overworldConfigs );
        registerFloorTrap( context, featureKeys.netherKeys, featureKeys.trapType, netherConfigs );
    }
    
    /** Registers a configured floor trap type feature. */
    protected static void registerFloorTrap( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                             FloorTrapType type, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.FLOOR_TRAP.get(),
                new FloorTrapFeature.Configuration( block( DWBlocks.floorTrap( type ) ),
                        FloorTrapSettings.of( type.getConfig( dimConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }

    /** Registers a configured pitfall trap type feature to each supported dimension. */
    protected static void registerPitfallTrap( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.PitfallTrap featureKeys,
                                               BlockStateProvider overworldCoverProvider, BlockStateProvider overworldFillProvider,
                                               BlockStateProvider overworldFloorProvider,
                                               BlockStateProvider netherCoverProvider, BlockStateProvider netherFillProvider,
                                               BlockStateProvider netherFloorProvider,
                                               DimensionConfigGroup overworldConfigs,
                                               DimensionConfigGroup netherConfigs ) {
        registerPitfallTrap( context, featureKeys.overworldKeys, featureKeys.trapType,
                overworldCoverProvider, overworldFillProvider, overworldFloorProvider, overworldConfigs );
        registerPitfallTrap( context, featureKeys.netherKeys, featureKeys.trapType,
                netherCoverProvider, netherFillProvider, netherFloorProvider, netherConfigs );
    }

    /** Registers a configured pitfall trap type feature. */
    protected static void registerPitfallTrap( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                               PitfallTrapType type, BlockStateProvider coverProvider, BlockStateProvider fillProvider,
                                               BlockStateProvider floorProvider, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.PITFALL_TRAP.get(),
                new PitfallTrapFeature.Configuration(
                        coverProvider, fillProvider, floorProvider,
                        PitfallTrapSettings.of( type, type.getConfig( dimConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }
    
    
    /** Registers a configured tower dispenser type feature to each supported dimension. */
    protected static void registerTower( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.TowerDispenser featureKeys,
                                         DimensionConfigGroup overworldConfigs, BlockStateProvider overworldBase,
                                         DimensionConfigGroup netherConfigs, BlockStateProvider netherBase ) {
        registerTower( context, featureKeys.overworldKeys, featureKeys.towerType, overworldBase, overworldConfigs );
        registerTower( context, featureKeys.netherKeys, featureKeys.towerType, netherBase, netherConfigs );
    }
    
    /** Registers a configured tower dispenser type feature. */
    protected static void registerTower( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                         TowerType type, BlockStateProvider base, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.TOWER.get(),
                new TowerFeature.Configuration( block( DWBlocks.towerDispenser( type ) ), base,
                        TowerDispenserSettings.of( type.getConfig( dimConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }
    
    
    /** Registers a configured registerSeaMine type feature to each supported dimension. */
    protected static void registerSeaMine( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.SeaMine featureKeys,
                                           DimensionConfigGroup overworldConfigs ) {
        registerSeaMine( context, featureKeys.overworldKeys, featureKeys.seaMineType, block( Blocks.CHAIN.defaultBlockState()
                        .setValue( ChainBlock.AXIS, Direction.Axis.Y ).setValue( ChainBlock.WATERLOGGED, true ) ),
                overworldConfigs );
    }
    
    /** Registers a configured sea mine type feature. */
    protected static void registerSeaMine( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys,
                                           SeaMineType type, BlockStateProvider trailProvider, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.SEA_MINE.get(),
                new SeaMineFeature.Configuration( block( DWBlocks.seaMine( type ).get().defaultBlockState()
                        .setValue( SeaMineBlock.WATERLOGGED, true ) ), trailProvider,
                        SeaMineSettings.of( type.getConfig( dimConfigs ) ) ) ) );
    }
    
    /** Registers a configured spike trap type feature. */
    // TODO
    /*
    protected static void registerSpikeTrap( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys.SpikeTrap featureKeys,
                                            SpikeTrapType type, BlockStateProvider trailProvider, DimensionConfigGroup dimConfigs ) {
        register( context, featureKeys, new ConfiguredFeature<>( DWFeatures.SEA_MINE.get(),
                new SeaMineFeature.Configuration( block( DWBlocks.spikeTrap( type ).get().defaultBlockState()
                        .setValue( SeaMineBlock.WATERLOGGED, true ) ), trailProvider,
                        SeaMineSettings.of( type.getConfig( dimConfigs ) ) ) ) );
    }

     */
    
    /** Registers a configured feature. */
    protected static void register( BootstapContext<ConfiguredFeature<?, ?>> context, FeatureKeys featureKeys, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( featureKeys.configuredKey, configuredFeature );
    }
    
    /** Registers a configured feature. */
    protected static void register( BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> confFeatureKey, ConfiguredFeature<?, ?> configuredFeature ) {
        context.register( confFeatureKey, configuredFeature );
    }
    
    
    /** Creates a configured ore feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> anyDimOreKey( String name ) {
        ResourceKey<ConfiguredFeature<?, ?>> key = key( name + "_any_dimension_ore" );
        ANY_DIMENSION_ORE_FEATURES.add( key );
        return key;
    }

    /** Creates a configured key for an "any dimension" post-decor feature. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> anyDimPostDecor( String name ) {
        ResourceKey<ConfiguredFeature<?, ?>> key = key( name + "_any_dimension_post_decoration" );
        ANY_DIMENSION_POST_DECORATION.add( key );
        return key;
    }
    
    /** Creates a configured ore feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> overworldOreKey( String name ) {
        final ResourceKey<ConfiguredFeature<?, ?>> key = key( name + "_ore" );
        OVERWORLD_ORE_FEATURES.add( key );
        return key;
    }
    
    /** Creates a configured ore feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> netherOreKey( String name ) {
        final ResourceKey<ConfiguredFeature<?, ?>> key = key( name + "_nether_ore" );
        NETHER_ORE_FEATURES.add( key );
        return key;
    }
    
    /** Creates a configured decoration feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> overworldKey( String name ) {
        final ResourceKey<ConfiguredFeature<?, ?>> key = key( name );
        OVERWORLD_FEATURES.add( key );
        return key;
    }
    
    /** Creates a configured decoration feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> netherKey( String name ) {
        final ResourceKey<ConfiguredFeature<?, ?>> key = key( name + "_nether" );
        NETHER_FEATURES.add( key );
        return key;
    }
    
    /** Creates a configured feature key. */
    protected static ResourceKey<ConfiguredFeature<?, ?>> key( String name ) {
        return ResourceKey.create( Registries.CONFIGURED_FEATURE, DeadlyWorld.rl( name ) );
    }
}