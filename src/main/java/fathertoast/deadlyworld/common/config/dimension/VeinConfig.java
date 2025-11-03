package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;

import static fathertoast.deadlyworld.common.util.References.*;

public class VeinConfig extends FeatureConfig {
    
    public final VeinCategory INFESTED_VANILLA;
    public final VeinCategory INFESTED_ADDED;
    public final VeinCategory WATER;
    public final VeinCategory SAND;
    
    /** Builds the config spec that should be used for this config. */
    VeinConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "vein" );
        
        final boolean isNether = isNetherDimension();
        
        INFESTED_VANILLA = new VeinCategory( this, "infested_block.base", 14, DEPTH_VOID, DEPTH_SEA_LEVEL,
                9, true ); // Settings identical to vanilla infested vein
        INFESTED_ADDED = new VeinCategory( this, "infested_block.added", 0.33, DEPTH_LAVA, DEPTH_3,
                33, true );
        WATER = new VeinCategory( this, "water", isNether ? 0 : 5, DEPTH_VOID, DEPTH_SEA_LEVEL,
                7, false );
        SAND = new VeinCategory( this, "sand", isNether ? 0 : 3, DEPTH_3, DEPTH_SKY,
                33, true );
    }
    
    
    public static class VeinCategory extends FeatureTypeCategory {
        
        public final IntField size;
        public final DoubleField exposureDiscardChance;
        
        VeinCategory( FeatureConfig parent, String name, double placements, int minHeight, int maxHeight,
                      int veinSize, boolean exposed ) {
            super( parent, name, placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            size = SPEC.define( new IntField( "size", veinSize, 0, 64,
                    "The size for " + FEATURE_TYPE_NAME + ".",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            exposureDiscardChance = SPEC.define( new DoubleField( "discard_chance_on_air_exposure", exposed ? 0.0 : 1.0, DoubleField.Range.PERCENT,
                    "The chance for blocks of this vein to fail to place when exposed to air.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
        }
    }
}