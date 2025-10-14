package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/** Used to link and simplify configured features and placed features that are one-to-one. */
public class FeatureKeys {
    
    public static FeatureKeys overworld( String name ) {
        return new FeatureKeys( DWConfiguredFeatureProvider.overworldKey( name ),
                DWPlacedFeatureProvider.overworldKey( name ) );
    }
    
    public static FeatureKeys nether( String name ) {
        return new FeatureKeys( DWConfiguredFeatureProvider.netherKey( name ),
                DWPlacedFeatureProvider.netherKey( name ) );
    }
    
    public static FeatureKeys anyDimension( String name ) {
        return new FeatureKeys( DWConfiguredFeatureProvider.anyDimKey( name ),
                DWPlacedFeatureProvider.anyDimKey( name ) );
    }
    
    public final ResourceKey<ConfiguredFeature<?, ?>> configuredKey;
    public final ResourceKey<PlacedFeature> placedKey;
    
    protected FeatureKeys( ResourceKey<ConfiguredFeature<?, ?>> configured, ResourceKey<PlacedFeature> placed ) {
        configuredKey = configured;
        placedKey = placed;
    }
    
    /** Marks the configured feature as 'not placeable' and returns itself for ease in constructing. */
    public FeatureKeys notPlaceable() {
        AbstractCFProvider.NOT_PLACEABLE.add( configuredKey );
        return this;
    }
    
    /** Feature key-pair for features that generate in both the overworld and nether. */
    public static class BiDimensional {
        
        public static BiDimensional of( String name ) { return new BiDimensional( overworld( name ), nether( name ) ); }
        
        public final FeatureKeys overworldKeys;
        public final FeatureKeys netherKeys;
        
        protected BiDimensional( FeatureKeys overworld, FeatureKeys nether ) {
            overworldKeys = overworld;
            netherKeys = nether;
        }
    }
    
    public static class Spawner extends BiDimensional {
        
        public static Spawner of( SpawnerType type, String name ) { return new Spawner( type, overworld( name ), nether( name ) ); }
        
        public final SpawnerType spawnerType;
        
        protected Spawner( SpawnerType type, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            spawnerType = type;
            
            AbstractCFProvider.SPAWNER_FEATURES.add( overworld.configuredKey );
            AbstractCFProvider.SPAWNER_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.SPAWNER_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.SPAWNER_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class Trap extends BiDimensional {
        
        public static Trap of( TrapType type, String name ) { return new Trap( type, overworld( name ), nether( name ) ); }
        
        public final TrapType trapType;
        
        protected Trap( TrapType type, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            trapType = type;
            
            AbstractCFProvider.TRAP_FEATURES.add( overworld.configuredKey );
            AbstractCFProvider.TRAP_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.TRAP_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.TRAP_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class TowerDispenser extends BiDimensional {
        
        public static TowerDispenser of( TowerType type, String name ) { return new TowerDispenser( type, overworld( name ), nether( name ) ); }
        
        public final TowerType towerType;
        
        protected TowerDispenser( TowerType type, FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            towerType = type;
            
            AbstractCFProvider.TOWER_FEATURES.add( overworld.configuredKey );
            AbstractCFProvider.TOWER_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.TOWER_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.TOWER_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class SimpleDungeon extends BiDimensional {
        
        public static SimpleDungeon of( String name ) { return new SimpleDungeon( overworld( name ), nether( name ) ); }
        
        
        protected SimpleDungeon( FeatureKeys overworld, FeatureKeys nether ) {
            super( overworld, nether );
            
            AbstractCFProvider.DUNGEON_FEATURES.add( overworld.configuredKey );
            AbstractCFProvider.DUNGEON_FEATURES.add( nether.configuredKey );
            DWPlacedFeatureProvider.DUNGEON_FEATURES.add( overworld.placedKey );
            DWPlacedFeatureProvider.DUNGEON_FEATURES.add( nether.placedKey );
        }
    }
    
    public static class SeaMine {
        
        public final FeatureKeys overworldKeys;
        
        public static SeaMine of( SeaMineType type, String name ) { return new SeaMine( type, overworld( name ) ); }
        
        public final SeaMineType seaMineType;
        
        protected SeaMine( SeaMineType type, FeatureKeys overworld ) {
            overworldKeys = overworld;
            seaMineType = type;
            
            AbstractCFProvider.SEA_MINE_FEATURES.add( overworld.configuredKey );
            DWPlacedFeatureProvider.SEA_MINE_FEATURES.add( overworld.placedKey );
        }
    }
}