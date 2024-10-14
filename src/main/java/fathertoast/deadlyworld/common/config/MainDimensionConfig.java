package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;

public class MainDimensionConfig extends AbstractConfigFile {
    /** The parent group containing this config. */
    public final DimensionConfigGroup DIMENSION_CONFIGS;
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    MainDimensionConfig( ConfigManager manager, DimensionConfigGroup dimConfigs ) {
        super( manager, "_main",
                "This config contains general options that apply to the entire",
                dimConfigs.longDimensionName() + ".",
                "This includes a master on/off setting for world gen in this dimension."
        );
        DIMENSION_CONFIGS = dimConfigs;
        
        SPEC.newLine();
        SPEC.describeEntityList();
        SPEC.newLine();
        SPEC.describeBlockList();
        
        GENERAL = new General( this, dimConfigs );
    }
    
    public static class General extends AbstractConfigCategory<MainDimensionConfig> {
        
        public final BooleanField enabled;
        
        General( MainDimensionConfig parent, DimensionConfigGroup dimConfigs ) {
            super( parent, "general",
                    "Options that apply to all world generation in the " + dimConfigs.dimensionName() + "." );
            
            enabled = SPEC.define( new BooleanField( "enabled_for_dimension", true,
                    "If true, the mod's world generation will be enabled for the " + dimConfigs.dimensionName() + ".",
                    "Set this to false to prevent Deadly World from doing anything at all in this dimension.",
                    "The other configs for this dimension will still apply for Deadly World objects placed in this dimension by,",
                    "other means, such as creative mode players, commands, other mods, etc." ) );
        }
    }
}