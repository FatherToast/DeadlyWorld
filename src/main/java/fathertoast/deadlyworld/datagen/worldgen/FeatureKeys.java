package fathertoast.deadlyworld.datagen.worldgen;

import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
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
}