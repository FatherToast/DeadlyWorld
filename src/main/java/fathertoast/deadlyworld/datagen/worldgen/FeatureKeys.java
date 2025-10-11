package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.ForgeBiomeModifiers;

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
    
    public static class Spawner {
        
        public static Spawner of( SpawnerType type, String name ) { return new Spawner( type, overworld( name ), nether( name ) ); }
        
        public final SpawnerType spawnerType;
        public final FeatureKeys overworldKeys;
        public final FeatureKeys netherKeys;
        
        protected Spawner( SpawnerType type, FeatureKeys overworld, FeatureKeys nether ) {
            spawnerType = type;
            overworldKeys = overworld;
            netherKeys = nether;
        }
    }

    public static class Trap {

        public static Trap of( TrapType type, String name ) { return new Trap( type, overworld( name ), nether( name ) ); }

        public final TrapType trapType;
        public final FeatureKeys overworldKeys;
        public final FeatureKeys netherKeys;

        protected Trap( TrapType type, FeatureKeys overworld, FeatureKeys nether ) {
            trapType = type;
            overworldKeys = overworld;
            netherKeys = nether;
        }
    }

    public static class TowerDispenser {

        public static TowerDispenser of( TowerType type, String name ) { return new TowerDispenser( type, overworld( name ), nether( name ) ); }

        public final TowerType towerType;
        public final FeatureKeys overworldKeys;
        public final FeatureKeys netherKeys;

        protected TowerDispenser( TowerType type, FeatureKeys overworld, FeatureKeys nether ) {
            towerType = type;
            overworldKeys = overworld;
            netherKeys = nether;
        }
    }

    public static class SimpleDungeon {

        public static SimpleDungeon of( String name ) { return new SimpleDungeon( overworld( name ), nether( name ) ); }

        public final FeatureKeys overworldKeys;
        public final FeatureKeys netherKeys;

        protected SimpleDungeon( FeatureKeys overworld, FeatureKeys nether ) {
            overworldKeys = overworld;
            netherKeys = nether;
        }
    }
}