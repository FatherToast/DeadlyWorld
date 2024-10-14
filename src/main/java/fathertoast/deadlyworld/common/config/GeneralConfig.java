package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;

public class GeneralConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    GeneralConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for miscellaneous features in the mod."
        );
        
        GENERAL = new General( this );
    }
    
    public static class General extends AbstractConfigCategory<GeneralConfig> {
        public final BooleanField activateTrapsInPeaceful;
        
        General( GeneralConfig parent ) {
            super( parent, "general",
                    "Options to customize misc global settings." );
            
            activateTrapsInPeaceful = SPEC.define( new BooleanField( "activate_traps_in_peaceful", true,
                    "If enabled, all non-spawner range activated traps will still trigger in peaceful mode." ) );
        }
    }
}