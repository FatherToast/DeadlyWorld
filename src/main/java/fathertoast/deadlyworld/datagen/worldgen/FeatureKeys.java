package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.chest.ChestType;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapType;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.spike_trap.SpikeTrapType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.unstable.PitfallTrapType;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.VeinConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Function;

/** Used to link and simplify configured features and placed features that are one-to-one. */
public class FeatureKeys {
    
    // Ore features
    
    public static FeatureKeys overworldOre( String name ) {
        return new FeatureKeys( DWConfiguredFeatureProvider.overworldOreKey( name ),
                DWPlacedFeatureProvider.overworldOreKey( name ) ).notPlaceable();
    }
    
    public static FeatureKeys netherOre( String name ) {
        return new FeatureKeys( DWConfiguredFeatureProvider.netherOreKey( name ),
                DWPlacedFeatureProvider.netherOreKey( name ) ).notPlaceable();
    }
    
    
    // Decoration features
    
    public static FeatureKeys overworld( String name ) {
        return new FeatureKeys( DWConfiguredFeatureProvider.overworldKey( name ),
                DWPlacedFeatureProvider.overworldKey( name ) );
    }
    
    public static ResourceKey<PlacedFeature> overworldOcean( String name ) {
        return DWPlacedFeatureProvider.overworldKey( name + "_ocean" );
    }
    
    public static FeatureKeys nether( String name ) {
        return new FeatureKeys( DWConfiguredFeatureProvider.netherKey( name ),
                DWPlacedFeatureProvider.netherKey( name ) );
    }
    
    // Post-decoration features
    
    public static FeatureKeys anyDimPostDecoration( String name ) {
        return new FeatureKeys( DWConfiguredFeatureProvider.anyDimPostDecor( name ),
                DWPlacedFeatureProvider.anyDimPostDecor( name ) );
    }
    
    
    public final ResourceKey<ConfiguredFeature<?, ?>> configuredKey;
    public final ResourceKey<PlacedFeature> placedKey;
    
    protected FeatureKeys( ResourceKey<ConfiguredFeature<?, ?>> configured, ResourceKey<PlacedFeature> placed ) {
        configuredKey = configured;
        placedKey = placed;
    }
    
    /**
     * Marks the configured feature as 'not placeable' and returns itself for ease in constructing.
     * Automatically called for ore features.
     */
    public FeatureKeys notPlaceable() {
        DWAbstractCFProvider.NOT_PLACEABLE.add( configuredKey );
        return this;
    }
    
    /**
     * Marks the configured feature as 'post decor',
     * delaying generation to after {@link net.minecraft.world.level.levelgen.GenerationStep.Decoration#UNDERGROUND_DECORATION}.
     */
    public FeatureKeys postDecor() {
        DWAbstractCFProvider.ANY_DIMENSION_POST_DECORATION.add( configuredKey );
        return this;
    }
    
    /** Feature key-pair for features that generate only in dimensions with naturally generating water. */
    public static class WaterFeature {
        
        public final FeatureKeys overworldKeys;
        
        protected WaterFeature( FeatureKeys overworld ) {
            overworldKeys = overworld;
        }
    }
    
    /** Feature key-pair for features that generate in only in dimensions with naturally generating water. */
    public static class OceanFeature extends WaterFeature {
        
        public final ResourceKey<PlacedFeature> overworldOceanKey;
        
        protected OceanFeature( FeatureKeys overworld, ResourceKey<PlacedFeature> overworldOcean ) {
            super( overworld );
            overworldOceanKey = overworldOcean;
        }
    }
    
    /** Feature key-pair for features that generate in all supported dimensions. */
    public static class TypicalFeature extends WaterFeature {
        
        public final FeatureKeys netherKeys;
        
        protected TypicalFeature( FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld );
            netherKeys = nether;
        }
    }
    
    public static class Vein extends TypicalFeature {
        
        public static Vein of( Function<DimensionConfigGroup, VeinConfig.VeinCategory> configGetter, String name ) {
            return new Vein( configGetter, overworldOre( name ), netherOre( name ) );
        }
        
        public final Function<DimensionConfigGroup, VeinConfig.VeinCategory> configGetter;
        
        protected Vein( Function<DimensionConfigGroup, VeinConfig.VeinCategory> getter, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            configGetter = getter;
            
            DWAbstractCFProvider.VEIN_FEATURES.add( overworld.configuredKey );
            DWAbstractCFProvider.VEIN_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.VEIN_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.VEIN_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class LoneChest extends TypicalFeature {
        
        public static LoneChest of( ChestType type, String name ) { return new LoneChest( type, overworld( name ), nether( name ) ); }
        
        public final ChestType chestType;
        
        protected LoneChest( ChestType type, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            chestType = type;
            
            DWAbstractCFProvider.LONE_CHEST_FEATURES.add( overworld.configuredKey );
            DWAbstractCFProvider.LONE_CHEST_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.LONE_CHEST_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.LONE_CHEST_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class Spawner extends TypicalFeature {
        
        public static Spawner of( SpawnerType type, String name ) { return new Spawner( type, overworld( name ), nether( name ) ); }
        
        public final SpawnerType spawnerType;
        
        protected Spawner( SpawnerType type, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            spawnerType = type;
            
            DWAbstractCFProvider.SPAWNER_FEATURES.add( overworld.configuredKey );
            DWAbstractCFProvider.SPAWNER_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.SPAWNER_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.SPAWNER_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class FloorTrap extends TypicalFeature {
        
        public static FloorTrap of( FloorTrapType type, String name ) { return new FloorTrap( type, overworld( name ), nether( name ) ); }
        
        public final FloorTrapType trapType;
        
        protected FloorTrap( FloorTrapType type, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            trapType = type;
            
            DWAbstractCFProvider.FLOOR_TRAP_FEATURES.add( overworld.configuredKey );
            DWAbstractCFProvider.FLOOR_TRAP_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.FLOOR_TRAP_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.FLOOR_TRAP_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class SpikeTrap extends TypicalFeature {
        
        public static SpikeTrap of( SpikeTrapType type, String name ) { return new SpikeTrap( type, overworld( name ), nether( name ) ); }
        
        public final SpikeTrapType trapType;
        
        protected SpikeTrap( SpikeTrapType type, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            trapType = type;
            
            DWAbstractCFProvider.SPIKE_TRAP_FEATURES.add( overworld.configuredKey );
            DWAbstractCFProvider.SPIKE_TRAP_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.SPIKE_TRAP_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.SPIKE_TRAP_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class PitfallTrap extends TypicalFeature {
        
        public static PitfallTrap of( PitfallTrapType type, String name ) { return new PitfallTrap( type, overworld( name ), nether( name ) ); }
        
        public final PitfallTrapType trapType;
        
        protected PitfallTrap( PitfallTrapType type, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            trapType = type;
            
            DWAbstractCFProvider.PITFALL_TRAP_FEATURES.add( overworld.configuredKey );
            DWAbstractCFProvider.PITFALL_TRAP_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.PITFALL_TRAP_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.PITFALL_TRAP_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class TowerDispenser extends TypicalFeature {
        
        public static TowerDispenser of( TowerType type, String name ) { return new TowerDispenser( type, overworld( name ), nether( name ) ); }
        
        public final TowerType towerType;
        
        protected TowerDispenser( TowerType type, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            towerType = type;
            
            DWAbstractCFProvider.TOWER_FEATURES.add( overworld.configuredKey );
            DWAbstractCFProvider.TOWER_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.TOWER_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.TOWER_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class SimpleDungeon extends TypicalFeature {
        
        public static SimpleDungeon of( String name ) { return new SimpleDungeon( overworld( name ), nether( name ) ); }
        
        protected SimpleDungeon( FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            
            DWAbstractCFProvider.DUNGEON_FEATURES.add( overworld.configuredKey );
            DWAbstractCFProvider.DUNGEON_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.DUNGEON_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.DUNGEON_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class SeaMine extends OceanFeature {
        
        public static SeaMine of( SeaMineType type, String name ) { return new SeaMine( type, overworld( name ), overworldOcean( name ) ); }
        
        public final SeaMineType seaMineType;
        
        protected SeaMine( SeaMineType type, FeatureKeys overworld, ResourceKey<PlacedFeature> overworldOcean ) {
            super( overworld, overworldOcean );
            seaMineType = type;
            
            DWAbstractCFProvider.SEA_MINE_FEATURES.add( overworld.configuredKey );
            DWPlacedFeatureProvider.SEA_MINE_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.SEA_MINE_FEATURES.add( overworldOcean );
        }
    }
}