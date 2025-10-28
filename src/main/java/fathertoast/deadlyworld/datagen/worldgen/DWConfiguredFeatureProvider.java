package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.chest.ChestType;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapType;
import fathertoast.deadlyworld.common.block.pitfall.PitfallTrapType;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.spike_trap.SpikeTrapType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWFeatures;
import fathertoast.deadlyworld.common.world.levelgen.FloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.PotionFloorTrapSettings;
import fathertoast.deadlyworld.common.world.levelgen.SpawnerSettings;
import fathertoast.deadlyworld.common.world.levelgen.dungeon.NormalDungeonFeature;
import fathertoast.deadlyworld.common.world.levelgen.dungeon.MiniDungeonFeature;
import fathertoast.deadlyworld.common.world.levelgen.misc.BuriedBlocksFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.FloorTrapFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.PotionFloorTrapFeature;
import fathertoast.deadlyworld.common.world.levelgen.trap.SilverfishNestFeature;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

/**
 * For vanilla ore configured features, see {@link net.minecraft.data.worldgen.features.OreFeatures}.
 * For vanilla decoration configured features, see {@link net.minecraft.data.worldgen.features.CaveFeatures} (mostly).
 */
public class DWConfiguredFeatureProvider extends DWAbstractCFProvider {
    // Ore features
    public static final FeatureKeys BURIED_BLOCK_POST_DECOR = FeatureKeys.anyDimPostDecoration( "buried_block" ).notPlaceable();
    
    public static final FeatureKeys.Vein BASE_INFESTED_BLOCK_ORE = FeatureKeys.Vein.of( ( dimConfigs ) -> dimConfigs.VEINS.INFESTED_VANILLA, "base_infested_block" );
    public static final FeatureKeys.Vein ADDED_INFESTED_BLOCK_ORE = FeatureKeys.Vein.of( ( dimConfigs ) -> dimConfigs.VEINS.INFESTED_ADDED, "added_infested_block" );
    public static final FeatureKeys.Vein WATER_ORE = FeatureKeys.Vein.of( ( dimConfigs ) -> dimConfigs.VEINS.WATER, "water" );
    public static final FeatureKeys.Vein SAND_ORE = FeatureKeys.Vein.of( ( dimConfigs ) -> dimConfigs.VEINS.SAND, "sand" );
    
    // Decoration features
    public static final FeatureKeys.LoneChest SIMPLE_LONE_CHEST = FeatureKeys.LoneChest.of( ChestType.SIMPLE, "simple_lone_chest" );
    public static final FeatureKeys.LoneChest VALUABLE_LONE_CHEST = FeatureKeys.LoneChest.of( ChestType.VALUABLE, "valuable_lone_chest" );
    public static final FeatureKeys.LoneChest TNT_TRAP_LONE_CHEST = FeatureKeys.LoneChest.of( ChestType.TNT_TRAP, "tnt_trap_lone_chest" );
    public static final FeatureKeys.LoneChest INFESTED_LONE_CHEST = FeatureKeys.LoneChest.of( ChestType.INFESTED, "infested_lone_chest" );
    public static final FeatureKeys.LoneChest SURPRISE_LONE_CHEST = FeatureKeys.LoneChest.of( ChestType.SURPRISE, "surprise_lone_chest" );
    
    public static final FeatureKeys.Spawner SIMPLE_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.SIMPLE, "simple_spawner" );
    public static final FeatureKeys.Spawner STREAM_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.STREAM, "stream_spawner" );
    public static final FeatureKeys.Spawner SWARM_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.SWARM, "swarm_spawner" );
    public static final FeatureKeys.Spawner BRUTAL_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.BRUTAL, "brutal_spawner" );
    public static final FeatureKeys.Spawner MINI_SPAWNER = FeatureKeys.Spawner.of( SpawnerType.MINI, "mini_spawner" );
    public static final FeatureKeys.Spawner SILVERFISH_NEST = FeatureKeys.Spawner.of( SpawnerType.NEST, "silverfish_nest" );
    
    public static final FeatureKeys.FloorTrap TNT_TRAP = FeatureKeys.FloorTrap.of( FloorTrapType.TNT, "tnt_trap" );
    public static final FeatureKeys.FloorTrap TNT_MOB_TRAP = FeatureKeys.FloorTrap.of( FloorTrapType.TNT_MOB, "tnt_mob_trap" );
    public static final FeatureKeys.FloorTrap POTION_TRAP = FeatureKeys.FloorTrap.of( FloorTrapType.POTION, "potion_trap" );
    public static final FeatureKeys.FloorTrap LAVA_TRAP = FeatureKeys.FloorTrap.of( FloorTrapType.LAVA, "lava_trap" );
    public static final FeatureKeys.FloorTrap FIRE_TRAP = FeatureKeys.FloorTrap.of( FloorTrapType.FIRE, "fire_trap" );
    public static final FeatureKeys SEA_MINE_MOB_TRAP = FeatureKeys.overworld( "sea_mine_mob_trap" );

    public static final FeatureKeys.PitfallTrap SPIKES_PITFALL_TRAP = FeatureKeys.PitfallTrap.of( PitfallTrapType.SPIKES, "spikes_pitfall_trap" );
    public static final FeatureKeys.PitfallTrap LAVA_PITFALL_TRAP = FeatureKeys.PitfallTrap.of( PitfallTrapType.LAVA, "lava_pitfall_trap" );
    public static final FeatureKeys.PitfallTrap COBWEB_PITFALL_TRAP = FeatureKeys.PitfallTrap.of( PitfallTrapType.COBWEB, "cobweb_pitfall_trap" );

    public static final FeatureKeys.TowerDispenser SIMPLE_TOWER = FeatureKeys.TowerDispenser.of( TowerType.SIMPLE, "simple_tower" );
    public static final FeatureKeys.TowerDispenser FIRE_TOWER = FeatureKeys.TowerDispenser.of( TowerType.FIRE, "fire_tower" );
    public static final FeatureKeys.TowerDispenser POTION_TOWER = FeatureKeys.TowerDispenser.of( TowerType.POTION, "potion_tower" );
    public static final FeatureKeys.TowerDispenser GATLING_TOWER = FeatureKeys.TowerDispenser.of( TowerType.GATLING, "gatling_tower" );
    public static final FeatureKeys.TowerDispenser FIREBALL_TOWER = FeatureKeys.TowerDispenser.of( TowerType.FIREBALL, "fireball_tower" );
    
    public static final FeatureKeys.SeaMine NORMAL_SEA_MINE = FeatureKeys.SeaMine.of( SeaMineType.NORMAL, "normal_sea_mine" );
    public static final FeatureKeys.SeaMine PUFFER_SEA_MINE = FeatureKeys.SeaMine.of( SeaMineType.PUFFER, "puffer_sea_mine" );
    public static final FeatureKeys.SeaMine GUARDIAN_SEA_MINE = FeatureKeys.SeaMine.of( SeaMineType.GUARDIAN, "guardian_sea_mine" );

    public static final FeatureKeys.SimpleDungeon NORMAL_DUNGEON = FeatureKeys.SimpleDungeon.of( "simple_dungeon" );
    public static final FeatureKeys.SimpleDungeon MINI_DUNGEON = FeatureKeys.SimpleDungeon.of( "mini_dungeon" );
    
    
    /** Called by registry set builder to generate our configured features. */
    public static void bootstrap( BootstapContext<ConfiguredFeature<?, ?>> context ) {
        final DimensionConfigGroup overworldConfigs = Config.getDimensionConfigs( Level.OVERWORLD );
        final DimensionConfigGroup netherConfigs = Config.getDimensionConfigs( Level.NETHER );
        
        // Ore features
        register( context, BURIED_BLOCK_POST_DECOR,
                new ConfiguredFeature<>( DWFeatures.BURIED_BLOCK.get(),
                        new BuriedBlocksFeature.Configuration( BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        
        registerInfestedVein( context, BASE_INFESTED_BLOCK_ORE, overworldConfigs, netherConfigs );
        registerInfestedVein( context, ADDED_INFESTED_BLOCK_ORE, overworldConfigs, netherConfigs );
        registerVein( context, WATER_ORE, overworldConfigs, Blocks.WATER, netherConfigs, Blocks.WATER );
        registerVein( context, SAND_ORE, overworldConfigs, Blocks.SAND, netherConfigs, Blocks.SAND );
        
        // Plain lone chest features
        registerLoneChest( context, SIMPLE_LONE_CHEST,
                overworldConfigs, block( Blocks.CHEST ), netherConfigs, block( Blocks.CHEST ), null );
        registerLoneChest( context, VALUABLE_LONE_CHEST,
                overworldConfigs, block( Blocks.CHEST ), netherConfigs, block( Blocks.CHEST ), null );
        registerLoneChest( context, TNT_TRAP_LONE_CHEST,
                overworldConfigs, block( Blocks.CHEST ), netherConfigs, block( Blocks.CHEST ), TNT_TRAP );
        registerLoneChest( context, INFESTED_LONE_CHEST,
                overworldConfigs, block( Blocks.CHEST ), netherConfigs, block( Blocks.CHEST ), null );
        registerLoneChest( context, SURPRISE_LONE_CHEST,
                overworldConfigs, block( Blocks.CHEST ), netherConfigs, block( Blocks.CHEST ), null );
        
        // Plain spawner features
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
        
        // Fancy spawner features
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
        register( context, SEA_MINE_MOB_TRAP, new ConfiguredFeature<>( DWFeatures.FLOOR_TRAP.get(),
                new FloorTrapFeature.Configuration( block( DWBlocks.floorTrap( FloorTrapType.SEA_MINE_MOB ) ),
                        FloorTrapSettings.of( FloorTrapType.SEA_MINE_MOB.getConfig( overworldConfigs ) ),
                        BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        
        // Potion floor traps
        register( context, POTION_TRAP.overworldKeys, new ConfiguredFeature<>( DWFeatures.POTION_FLOOR_TRAP.get(), new PotionFloorTrapFeature.Configuration(
                block( DWBlocks.floorTrap( POTION_TRAP.trapType ) ), PotionFloorTrapSettings.create( overworldConfigs.FLOOR_TRAPS.POTION ),
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, POTION_TRAP.netherKeys, new ConfiguredFeature<>( DWFeatures.POTION_FLOOR_TRAP.get(), new PotionFloorTrapFeature.Configuration(
                block( DWBlocks.floorTrap( POTION_TRAP.trapType ) ), PotionFloorTrapSettings.create( netherConfigs.FLOOR_TRAPS.POTION ),
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );

        // Pitfall traps
        registerPitfallTrap( context, SPIKES_PITFALL_TRAP,
                block( DWBlocks.spikeTrap( SpikeTrapType.STATIC ) ),
                blocks( Blocks.SAND.defaultBlockState(),
                        Blocks.GRAVEL.defaultBlockState() ),
                block( Blocks.AIR ),
                overworldConfigs, netherConfigs );
        registerPitfallTrap( context, LAVA_PITFALL_TRAP,
                block( Blocks.LAVA ),
                block( Blocks.SAND ),
                block( Blocks.AIR ),
                overworldConfigs, netherConfigs );
        registerPitfallTrap( context, COBWEB_PITFALL_TRAP,
                block( Blocks.COBWEB ),
                block( Blocks.SAND ),
                block( Blocks.COBWEB ),
                overworldConfigs, netherConfigs );

        // Towers
        registerTower( context, SIMPLE_TOWER,
                overworldConfigs, block( Blocks.COBBLESTONE ),
                netherConfigs, block( Blocks.NETHER_BRICKS ) );
        registerTower( context, FIRE_TOWER,
                overworldConfigs, block( Blocks.CUT_SANDSTONE ),
                netherConfigs, block( Blocks.POLISHED_BASALT ) );
        registerTower( context, POTION_TOWER,
                overworldConfigs, block( Blocks.MUD_BRICKS ),
                netherConfigs, block( Blocks.RED_NETHER_BRICKS ) );
        registerTower( context, GATLING_TOWER,
                overworldConfigs, block( Blocks.STONE_BRICKS ),
                netherConfigs, block( Blocks.POLISHED_BLACKSTONE_BRICKS ) );
        registerTower( context, FIREBALL_TOWER,
                overworldConfigs, block( Blocks.DEEPSLATE_TILES ),
                netherConfigs, block( Blocks.QUARTZ_PILLAR ) );
        
        // Sea mines
        registerSeaMine( context, NORMAL_SEA_MINE, overworldConfigs );
        registerSeaMine( context, PUFFER_SEA_MINE, overworldConfigs );
        registerSeaMine( context, GUARDIAN_SEA_MINE, overworldConfigs );
        
        // Dungeons
        register( context, NORMAL_DUNGEON.overworldKeys, new ConfiguredFeature<>( DWFeatures.NORMAL_DUNGEON.get(), NormalDungeonFeature.Configuration.of(
                overworldConfigs, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE,
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, NORMAL_DUNGEON.netherKeys, new ConfiguredFeature<>( DWFeatures.NORMAL_DUNGEON.get(), NormalDungeonFeature.Configuration.of(
                netherConfigs, Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS,
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, MINI_DUNGEON.overworldKeys, new ConfiguredFeature<>( DWFeatures.MINI_DUNGEON.get(), MiniDungeonFeature.Configuration.of(
                overworldConfigs, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE,
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
        register( context, MINI_DUNGEON.netherKeys, new ConfiguredFeature<>( DWFeatures.MINI_DUNGEON.get(), MiniDungeonFeature.Configuration.of(
                netherConfigs, Blocks.NETHER_BRICKS, Blocks.CRACKED_NETHER_BRICKS,
                BlockTags.FEATURES_CANNOT_REPLACE ) ) );
    }
}