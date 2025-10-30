package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;

import java.util.ArrayList;

public class BlockAutoGenConfig extends AbstractConfigFile {

    public final BlockAutoGenConfig.General GENERAL;


    /** Builds the config spec that should be used for this config. */
    BlockAutoGenConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for DeadlyWorld's auto-generated blocks (e.g. infested blocks and unstable blocks)."
        );
        GENERAL = new General( this );
    }

    public static class General extends AbstractConfigCategory<BlockAutoGenConfig> {

        public final StringListField blockAutoGenDependencies;

        General( BlockAutoGenConfig parent ) {
            super( parent, "general",
                    "General settings for all auto-generated blocks created and registered by DeadlyWorld." );

            blockAutoGenDependencies = SPEC.define( new StringListField( "block_auto_gen.dependencies", "mod_id",
                    new ArrayList<>(),
                    "By default (that is, when this list is empty), Deadly World will attempt to adjust load " +
                            "order such that it loads its blocks after all namespaces used in the \"host_blocks\" list " +
                            "and any blocks with a namespace that not equal to a loaded mod's id will be skipped.",
                    "If you enter any ids in this list, instead Deadly World will attempt to adjust load order after " +
                            "only the mods on this list and will not skip any blocks on the above list.",
                    "All load order adjustment is disabled if you only enter \"minecraft\" (or non-existent mods) in " +
                            "this list, if you prefer to just crash instead of mucking with load order mid-loading."
            ), RestartNote.GAME );
        }
    }
}
