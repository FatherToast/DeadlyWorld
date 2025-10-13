package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import fathertoast.deadlyworld.common.world.levelgen.PotionFloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import fathertoast.deadlyworld.common.world.levelgen.dungeon.MiniDungeonFeature;
import fathertoast.deadlyworld.common.world.levelgen.dungeon.SimpleDungeonFeature;
import fathertoast.deadlyworld.common.world.levelgen.misc.BuriedLiquidFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.PotionFloorTrapFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.SilverfishNestFeature;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class DWConfiguredFeatureProvider extends AbstractCFProvider {
    
    public static final FeatureKeys.Spawner SIMPLE_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.SIMPLE, "simple_spawner" );
    public static final FeatureKeys.Spawner STREAM_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.STREAM, "stream_spawner" );
    public static final FeatureKeys.Spawner SWARM_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.SWARM, "swarm_spawner" );
    public static final FeatureKeys.Spawner BRUTAL_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.BRUTAL, "brutal_spawner" );
    public static final FeatureKeys.Spawner MINI_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.MINI, "mini_spawner" );
    public static final FeatureKeys.Spawner SILVERFISH_NEST = FeatureKeys.Spawner.of( SpawnerType.NEST, "silverfish_nest" );
    
    public static final FeatureKeys.Trap TNT_TRAP = FeatureKeys.Trap.of( TrapType.TNT, "tnt_trap" );
    public static final FeatureKeys.Trap TNT_MOB_TRAP = FeatureKeys.Trap.of( TrapType.TNT_MOB, "tnt_mob_trap" );
    public static final FeatureKeys.Trap POTION_TRAP = FeatureKeys.Trap.of( TrapType.POTION, "potion_trap" );
    public static final FeatureKeys.Trap LAVA_TRAP = FeatureKeys.Trap.of( TrapType.LAVA, "lava_trap" );
    public static final FeatureKeys.Trap FIRE_TRAP = FeatureKeys.Trap.of( TrapType.FIRE, "fire_trap" );

    public static final FeatureKeys.SeaMine NORMAL_SEA_MINE = FeatureKeys.SeaMine.of( SeaMineType.NORMAL, "normal_sea_mine" );
    public static final FeatureKeys.SeaMine PUFFER_SEA_MINE = FeatureKeys.SeaMine.of( SeaMineType.PUFFER, "puffer_sea_mine" );
    public static final FeatureKeys.SeaMine GUARDIAN_SEA_MINE = FeatureKeys.SeaMine.of( SeaMineType.GUARDIAN, "guardian_sea_mine" );

    public static final FeatureKeys.TowerDispenser SIMPLE_TOWER = FeatureKeys.TowerDispenser.of( TowerType.SIMPLE, "simple_tower_dispenser" );
    public static final FeatureKeys.TowerDispenser FIRE_TOWER = FeatureKeys.TowerDispenser.of( TowerType.FIRE, "fire_tower_dispenser" );
    public static final FeatureKeys.TowerDispenser POTION_TOWER = FeatureKeys.TowerDispenser.of( TowerType.POTION, "potion_tower_dispenser" );
    public static final FeatureKeys.TowerDispenser GATLING_TOWER = FeatureKeys.TowerDispenser.of( TowerType.GATLING, "gatling_tower_dispenser" );
    public static final FeatureKeys.TowerDispenser FIREBALL_TOWER = FeatureKeys.TowerDispenser.of( TowerType.FIREBALL, "fireball_tower_dispenser" );
    
    public static final FeatureKeys BURIED_LIQUID_ANY_DIMENSION = FeatureKeys.anyDimension( "buried_liquid" ).notPlaceable();
    
    public static final FeatureKeys.BiDimensional SIMPLE_DUNGEON = FeatureKeys.BiDimensional.of( "simple_dungeon" );
    public static final FeatureKeys.BiDimensional MINI_DUNGEON = FeatureKeys.BiDimensional.of( "mini_dungeon" );
    
    
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
                netherConfigs, block( Blocks.CHISELED_NETHER_BRICKS ), false );
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
        registerFloorTrap( context, FIRE_TRAP, overworldConfigs, netherConfigs );
        
        // Potion floor trap
        register( context, POTION_TRAP.overworldKeys, new ConfiguredFeature<>( DWFeatures.POTION_FLOOR_TRAP.get(), new PotionFloorTrapFeature.Configuration(
                block( DWBlocks.trap( POTION_TRAP.trapType ) ), PotionFloorTrapSettings.create( overworldConfigs.FLOOR_TRAPS.POTION ),
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, POTION_TRAP.netherKeys, new ConfiguredFeature<>( DWFeatures.POTION_FLOOR_TRAP.get(), new PotionFloorTrapFeature.Configuration(
                block( DWBlocks.trap( POTION_TRAP.trapType ) ), PotionFloorTrapSettings.create( netherConfigs.FLOOR_TRAPS.POTION ),
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        
        // Tower dispensers
        registerTowerDispenser( context, SIMPLE_TOWER, block( Blocks.COBBLESTONE_WALL ), overworldConfigs, netherConfigs );
        registerTowerDispenser( context, FIRE_TOWER, block( Blocks.GRANITE_WALL ), overworldConfigs, netherConfigs );
        registerTowerDispenser( context, POTION_TOWER, block( Blocks.MUD_BRICK_WALL ), overworldConfigs, netherConfigs );
        registerTowerDispenser( context, GATLING_TOWER, block( Blocks.MOSSY_STONE_BRICK_WALL ), overworldConfigs, netherConfigs );
        registerTowerDispenser( context, FIREBALL_TOWER, block( Blocks.NETHER_BRICK_WALL ), overworldConfigs, netherConfigs );

        // Sea mines
        registerSeaMine( context, NORMAL_SEA_MINE, overworldConfigs );
        registerSeaMine( context, PUFFER_SEA_MINE, overworldConfigs );
        registerSeaMine( context, GUARDIAN_SEA_MINE, overworldConfigs );

        // Special stuff
        register( context, BURIED_LIQUID_ANY_DIMENSION,
                new ConfiguredFeature<>( DWFeatures.BURIED_LIQUID.get(),
                        new BuriedLiquidFeature.Configuration( BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        
        // Simple dungeons
        register( context, SIMPLE_DUNGEON.overworldKeys,
                new ConfiguredFeature<>( DWFeatures.SIMPLE_DUNGEON.get(),
                        new SimpleDungeonFeature.Configuration(
                                block( Blocks.COBBLESTONE ),
                                block( Blocks.MOSSY_COBBLESTONE ),
                                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, SIMPLE_DUNGEON.netherKeys,
                new ConfiguredFeature<>( DWFeatures.SIMPLE_DUNGEON.get(),
                        new SimpleDungeonFeature.Configuration(
                                block( Blocks.NETHER_BRICKS ),
                                block( Blocks.CRACKED_NETHER_BRICKS ),
                                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, MINI_DUNGEON.overworldKeys,
                new ConfiguredFeature<>( DWFeatures.MINI_DUNGEON.get(),
                        new MiniDungeonFeature.Configuration(
                                block( Blocks.COBBLESTONE ),
                                block( Blocks.MOSSY_COBBLESTONE ),
                                SpawnerSettings.of( MINI_SPAWNER.spawnerType, overworldConfigs ),
                                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, MINI_DUNGEON.netherKeys,
                new ConfiguredFeature<>( DWFeatures.MINI_DUNGEON.get(),
                        new MiniDungeonFeature.Configuration(
                                block( Blocks.NETHER_BRICKS ),
                                block( Blocks.CRACKED_NETHER_BRICKS ),
                                SpawnerSettings.of( MINI_SPAWNER.spawnerType, netherConfigs ),
                                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }
}