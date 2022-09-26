package fathertoast.deadlyworld.common.core.config;

import fathertoast.deadlyworld.common.core.config.field.BooleanField;
import fathertoast.deadlyworld.common.core.config.file.ToastConfigSpec;

import java.io.File;

public class GeneralConfig extends Config.AbstractConfig {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    GeneralConfig( File dir, String fileName ) {
        super( dir, fileName,
                "This config contains options for several miscellaneous features in the mod, such as:",
                "NON-DESCRIBED THINGS!"
        );
        
        SPEC.newLine();
        SPEC.describeEntityList();
        SPEC.newLine();
        SPEC.describeBlockList();
        
        GENERAL = new General( SPEC );
    }
    
    public static class General extends Config.AbstractCategory {
        

        public final BooleanField activateTrapsInPeaceful;
        
        General( ToastConfigSpec parent ) {
            super( parent, "general",
                    "Options to customize misc global settings." );

            activateTrapsInPeaceful = SPEC.define( new BooleanField( "activate_traps_in_peaceful", true,
                    "If enabled, all non-spawner range activated traps will still trigger in peaceful mode." ));
        }
    }
}