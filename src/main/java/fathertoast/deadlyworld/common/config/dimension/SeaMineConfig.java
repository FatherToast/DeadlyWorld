package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;

import static fathertoast.deadlyworld.common.util.References.DEPTH_5;
import static fathertoast.deadlyworld.common.util.References.DEPTH_SEA_LEVEL;

public class SeaMineConfig extends FeatureConfig {
    
    public final SeaMineCategory NORMAL;
    public final SeaMineCategory PUFFER;
    public final SeaMineCategory GUARDIAN;
    
    
    SeaMineConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "sea mine" );
        
        flagAsWaterFeature();
        
        NORMAL = new SeaMineCategory( this, SeaMineType.NORMAL, 0.3, DEPTH_5, DEPTH_SEA_LEVEL, 2, 8 );
        PUFFER = new SeaMineCategory( this, SeaMineType.PUFFER, 0.1, DEPTH_5, DEPTH_SEA_LEVEL, 2, 8 );
        GUARDIAN = new SeaMineCategory( this, SeaMineType.GUARDIAN, 0.1, DEPTH_5, DEPTH_SEA_LEVEL, 2, 8 );
    }
    
    public static class SeaMineCategory extends FeatureTypeCategory {
        
        public final IntField.RandomRange distanceFromBottom;
        
        SeaMineCategory( FeatureConfig parent, SeaMineType type, double placements, int minHeight, int maxHeight,
                         int minDistFromBottom, int maxDistFromBottom ) {
            super( parent, type.toString(), placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            distanceFromBottom = new IntField.RandomRange( SPEC, "dist_from_bottom", minDistFromBottom, maxDistFromBottom, IntField.Range.NON_NEGATIVE,
                    "How far up from the ocean floor in blocks the mine will be placed, with a trail of chains underneath it.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );
        }
    }
}