package fathertoast.deadlyworld.common.core.config;

import fathertoast.deadlyworld.common.core.config.field.*;
import fathertoast.deadlyworld.common.core.config.file.ToastConfigSpec;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.io.File;

/**
 * A config file for one set of features (e.g. spawners). Establishes framework and config options used by all features.
 */
public abstract class FeatureConfig extends Config.AbstractConfig {
    /** The name of this feature (e.g. "spawner feature"). */
    final String FEATURE_NAME;
    
    /** The parent group containing this feature config. */
    public final DimensionConfigGroup DIMENSION_CONFIGS;
    
    FeatureConfig( File dir, DimensionConfigGroup dimConfigs, String name ) {
        super( dir, "feature_" + Config.noSpaces( name ) + "s",
                "This config contains options for all " + name + " features specific to the",
                dimConfigs.longDimensionName() + "."
        );
        DIMENSION_CONFIGS = dimConfigs;
        FEATURE_NAME = name + " feature";
    }
    
    public static class FeatureTypeCategory extends Config.AbstractCategory {
        /** The name of this feature type (e.g. "lone spawner feature"). */
        final String FEATURE_TYPE_NAME;
        
        public final BooleanField debugMarker;
        
        public final DoubleField countPerChunk;
        
        public final IntField.RandomRange heights;
        
        FeatureTypeCategory( ToastConfigSpec parent, FeatureConfig feature, String name,
                             double placements, int minHeight, int maxHeight ) {
            super( parent, Config.noSpaces( name + "_" + feature.FEATURE_NAME ) + "s",
                    "Options to customize " + name + " " + feature.FEATURE_NAME + "s specific to the",
                    feature.DIMENSION_CONFIGS.longDimensionName() + "." );
            FEATURE_TYPE_NAME = name + " " + feature.FEATURE_NAME;
            
            debugMarker = SPEC.define( new BooleanField( "testing_markers", false,
                    "When set to true, places a 1x1 column of glass to the height limit from a few blocks above each",
                    "generated " + FEATURE_TYPE_NAME + ". This is game-breaking and laggy. Also prints a message to the console.",
                    "Consider using a tool to strip away all stone/dirt/etc. or xray after world gen for more intensive testing." ) );
            
            SPEC.newLine();
            
            countPerChunk = SPEC.define( new DoubleField( "placements", placements, DoubleField.Range.POSITIVE,
                    "The number of placement attempts per chunk (16x16 blocks) for " + FEATURE_TYPE_NAME,
                    "A decimal represents a chance for a placement attempt (e.g., 0.3 means 30% chance for one attempt)." ) );
            
            SPEC.newLine();
            
            heights = new IntField.RandomRange(
                    SPEC.define( new IntField( "height.min", minHeight, IntField.Range.NON_NEGATIVE,
                            "The minimum and maximum (inclusive) heights/y-values " + FEATURE_TYPE_NAME + "s can generate at." ) ),
                    SPEC.define( new IntField( "height.max", maxHeight, IntField.Range.NON_NEGATIVE ) )
            );
        }
        
        /** @return True if this config is for the Nether dimension. */
        protected boolean isNetherDimension( FeatureConfig feature ) {
            return World.NETHER.equals( feature.DIMENSION_CONFIGS.DIMENSION );
        }
        
        /** @return True if this config is for the End dimension. */
        protected boolean isEndDimension( FeatureConfig feature ) {
            return World.END.equals( feature.DIMENSION_CONFIGS.DIMENSION );
        }
    }
}