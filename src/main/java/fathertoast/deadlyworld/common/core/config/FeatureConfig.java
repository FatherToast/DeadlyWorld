package fathertoast.deadlyworld.common.core.config;

import fathertoast.deadlyworld.common.core.config.field.*;
import fathertoast.deadlyworld.common.core.config.file.ToastConfigSpec;
import fathertoast.deadlyworld.common.core.config.util.BlockList;
import fathertoast.deadlyworld.common.core.config.util.EntityEntry;
import fathertoast.deadlyworld.common.core.config.util.EntityList;
import net.minecraft.entity.EntityType;

import java.io.File;

public abstract class FeatureConfig extends Config.AbstractConfig {
    /** The name of this feature (e.g. "spawner feature"). */
    final String FEATURE_NAME;
    
    /** The parent group containing this feature config. */
    public final DimensionConfigGroup DIMENSION_CONFIGS;
    
    FeatureConfig( File dir, DimensionConfigGroup dimConfigs, String name ) {
        super( dir, "feature_" + name.replace( ' ', '_' ) + "s",
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
        
        public final DoubleField placements;
        
        public final IntField.RandomRange heights;
        
        FeatureTypeCategory( ToastConfigSpec parent, FeatureConfig feature, String name,
                             double chance, int minHeight, int maxHeight ) {
            super( parent, (name + "_" + feature.FEATURE_NAME).replace( ' ', '_' ) + "s",
                    "Options to customize " + name + " " + feature.FEATURE_NAME + "s specific to the",
                    feature.DIMENSION_CONFIGS.longDimensionName() + "." );
            FEATURE_TYPE_NAME = name + " " + feature.FEATURE_NAME;
            
            debugMarker = SPEC.define( new BooleanField( "testing_markers", false,
                    "When set to true, places a 1x1 column of glass to the height limit from a few blocks above each",
                    "generated " + FEATURE_TYPE_NAME + ". This is game-breaking and laggy.",
                    "Consider using a tool to strip away all stone/dirt/etc. or xray after world gen for more intensive testing." ) );
            
            SPEC.newLine();
            
            placements = SPEC.define( new DoubleField( "placements", chance, DoubleField.Range.POSITIVE,
                    "The number of placement attempts per chunk (16x16 blocks) for " + FEATURE_TYPE_NAME,
                    "A decimal represents a chance for a placement attempt (e.g., 0.3 means 30% chance for one attempt)." ) );
            
            SPEC.newLine();
            
            heights = new IntField.RandomRange(
                    SPEC.define( new IntField( "height.min", minHeight, IntField.Range.NON_NEGATIVE,
                            "The minimum and maximum (inclusive) heights/y-values " + FEATURE_TYPE_NAME + "s can generate at." ) ),
                    SPEC.define( new IntField( "height.max", maxHeight, IntField.Range.NON_NEGATIVE ) )
            );
        }
    }
}