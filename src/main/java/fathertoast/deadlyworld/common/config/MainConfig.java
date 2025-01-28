package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.RestartNote;
import fathertoast.crust.api.config.common.field.StringListField;
import net.minecraft.world.level.Level;

import java.util.List;

public class MainConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    MainConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for miscellaneous features in the mod."
        );
        
        GENERAL = new General( this );
    }
    
    public static class General extends AbstractConfigCategory<MainConfig> {
        
        public final BooleanField activateTrapsInPeaceful;
        public final BooleanField activateTrapsVsCreative;
        
        public final BooleanField activateSpawnersVsCreative;
        
        public final StringListField extraDimensions;

        public final BooleanField pointedDripstoneSniping;
        public final BooleanField spookyStalactites;

        
        General( MainConfig parent ) {
            super( parent, "general",
                    "Options to customize misc settings that apply to the mod as a whole." );
            
            activateTrapsInPeaceful = SPEC.define( new BooleanField( "trigger_traps_in_peaceful", true,
                    "If true, this mod's traps will be allowed to trigger in peaceful mode. (Redstone-based " +
                            "traps ignore this setting.)" ) );
            activateTrapsVsCreative = SPEC.define( new BooleanField( "trigger_traps_vs_creative", false,
                    "If true, creative mode players will trigger this mod's traps. (Redstone-based traps " +
                            "ignore this setting.)" ) );
            
            SPEC.newLine();
            
            activateSpawnersVsCreative = SPEC.define( new BooleanField( "activate_spawners_vs_creative", true,
                    "If true, creative mode players will activate this mod's spawners." ) );
            
            SPEC.newLine();
            
            extraDimensions = SPEC.define( new StringListField( "extra_dimensions", "Dimension Type",
                    List.of( Level.NETHER.location().toString() ),
                    "List of extra dimension types for this mod to generate configs for. If this list is empty, " +
                            "world gen configs will only generate for the overworld. All dimensions NOT in this list " +
                            "will default to the 'minecraft:overworld' configs.",
                    "NOTE: Having configs for a dimension does NOT add world gen to it. Your data pack determines all " +
                            "world gen, and can also overwrite most world gen config settings. This mod generally only " +
                            "supports the default values here without the use of a data pack."
            ), RestartNote.GAME );

            SPEC.newLine();

            pointedDripstoneSniping = SPEC.define( new BooleanField("pointed_dripstone_sniping", true,
                    "If enabled, pointed dripstone blocks will break when hit with any projectile entity tagged as 'minecraft:impact_projectiles'.",
                    "In vanilla, only thrown tridents can break pointed dripstone, but this setting allows entities like arrows, snowballs and others to also do so.") );

            spookyStalactites = SPEC.define( new BooleanField( "spooky_stalactites", true,
                    "If enabled, there is a chance for nearby pointed dripstone (stalactites) in the ceiling to break off and fall when the player " +
                            "is breaking blocks.",
                    "Here are a list of conditions that must be met for stalactites to fall:",
                    "A random number between 0 to 9 is picked. If it is 0, proceed (10% chance per block broken).",
                    "Skylight level must be less than 3, and position of block broken must be below sea level.",
                    "Lastly, stalactites must be within 10 block range above the broken block."
                    ) );

            SPEC.newLine();
        }
    }
}