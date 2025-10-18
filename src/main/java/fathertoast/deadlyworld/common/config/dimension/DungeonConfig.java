package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.PredicateStringListField;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import fathertoast.deadlyworld.datagen.worldgen.DWConfiguredFeatureProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static fathertoast.deadlyworld.common.util.References.*;

public class DungeonConfig extends FeatureConfig {
    
    public final SimpleDungeonCategory NORMAL;
    public final FeatureTypeCategory MINI;
    
    /** Builds the config spec that should be used for this config. */
    DungeonConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "dungeon" );
        
        NORMAL = new SimpleDungeonCategory( this, "normal", 10, DEPTH_LAVA, DEPTH_SKY );
        MINI = new FeatureTypeCategory( this, "mini", 2, DEPTH_LAVA, DEPTH_0 );
    }
    
    
    public static class SimpleDungeonCategory extends FeatureTypeCategory {
        
        public final PredicateStringListField subfeatures;
        
        SimpleDungeonCategory( FeatureConfig parent, String name, double placements, int minHeight, int maxHeight ) {
            super( parent, name, placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            subfeatures = SPEC.define( new PredicateStringListField( "subfeatures", makeDefaultSubfeatures(), ResourceLocation::isValidResourceLocation,
                    "A list of registry IDs for subfeatures that can generate in the middle of the dungeon room.",
                    "By default, this includes most spawners and tower dispensers from Deadly World.",
                    "You can technically specify the ID of any configured feature here, but whether it will generate correctly is never guaranteed.",
                    DimensionConfigHelper.MESSAGE_WORK_IN_PROGRESS_OVERRIDE ) ); // TODO
        }
        
        /** @return The default subfeature ID list to use for the simple dungeon type and dimension. */
        private List<String> makeDefaultSubfeatures() {
            if( isNetherDimension() ) {
                return List.of(
                        stringFromKey( DWConfiguredFeatureProvider.SIMPLE_SPAWNER.netherKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.BRUTAL_SPAWNER.netherKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.SWARM_SPAWNER.netherKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.STREAM_SPAWNER.netherKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.SILVERFISH_NEST.netherKeys.configuredKey ),
                        
                        stringFromKey( DWConfiguredFeatureProvider.SIMPLE_TOWER.netherKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.POTION_TOWER.netherKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.GATLING_TOWER.netherKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.FIRE_TOWER.netherKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.FIREBALL_TOWER.netherKeys.configuredKey )
                );
            }
            else {
                return List.of(
                        stringFromKey( DWConfiguredFeatureProvider.SIMPLE_SPAWNER.overworldKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.BRUTAL_SPAWNER.overworldKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.SWARM_SPAWNER.overworldKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.STREAM_SPAWNER.overworldKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.SILVERFISH_NEST.overworldKeys.configuredKey ),
                        
                        stringFromKey( DWConfiguredFeatureProvider.SIMPLE_TOWER.overworldKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.POTION_TOWER.overworldKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.GATLING_TOWER.overworldKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.FIRE_TOWER.overworldKeys.configuredKey ),
                        stringFromKey( DWConfiguredFeatureProvider.FIREBALL_TOWER.overworldKeys.configuredKey )
                );
            }
        }
        
        private static String stringFromKey( ResourceKey<?> key ) {
            return key.location().toString();
        }
    }
}