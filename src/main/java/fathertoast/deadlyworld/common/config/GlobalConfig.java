package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;

public class GlobalConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    GlobalConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for miscellaneous features in the mod."
        );
        
        GENERAL = new General( this );
    }
    
    public static class General extends AbstractConfigCategory<GlobalConfig> {
        
        public final BooleanField activateTrapsInPeaceful;
        public final BooleanField activateTrapsVsCreative;
        
        public final BooleanField activateSpawnersVsCreative;
        
        General( GlobalConfig parent ) {
            super( parent, "general",
                    "Options to customize misc global settings." );
            
            activateTrapsInPeaceful = SPEC.define( new BooleanField( "trigger_traps_in_peaceful", true,
                    "If true, this mod's traps will be allowed to trigger in peaceful mode. (Redstone-based traps ignore this setting.)" ) );
            activateTrapsVsCreative = SPEC.define( new BooleanField( "trigger_traps_vs_creative", false,
                    "If true, creative mode players will trigger this mod's traps. (Redstone-based traps ignore this setting.)" ) );
            
            SPEC.newLine();
            
            activateSpawnersVsCreative = SPEC.define( new BooleanField( "activate_spawners_vs_creative", true,
                    "If true, creative mode players will activate this mod's spawners." ) );
        }
    }
}