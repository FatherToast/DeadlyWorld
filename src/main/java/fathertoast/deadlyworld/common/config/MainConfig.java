package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.common.config.field.WeightedRegEntryList;
import fathertoast.deadlyworld.common.config.field.WeightedRegEntryListField;
import fathertoast.deadlyworld.common.core.registry.DWFishingPranks;
import fathertoast.deadlyworld.api.FishingPrank;
import net.minecraft.world.level.Level;

import java.util.List;

public class MainConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final StalactiteOverhaul STALACTITE_OVERHAUL;
    public final FishingPranks FISHING_PRANKS;
    
    /** Builds the config spec that should be used for this config. */
    MainConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for miscellaneous features in the mod."
        );
        
        GENERAL = new General( this );
        STALACTITE_OVERHAUL = new StalactiteOverhaul( this );
        FISHING_PRANKS = new FishingPranks( this );
    }
    
    public static class General extends AbstractConfigCategory<MainConfig> {
        
        public final BooleanField activateTrapsInPeaceful;
        public final BooleanField activateTrapsVsCreative;
        
        public final BooleanField activateSpawnersVsCreative;
        
        public final StringListField extraDimensions;

        
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
        }
    }

    public static class StalactiteOverhaul extends AbstractConfigCategory<MainConfig> {

        public final BooleanField pointedDripstoneSniping;

        public final BooleanField spookyStalactites;
        public final DoubleField triggerChance;
        public final IntField scanHeight;


        StalactiteOverhaul( MainConfig parent ) {
            super( parent, "stalactite_overhaul",
                    "Misc settings related to interactions with Pointed Dripstone in the world." );

            pointedDripstoneSniping = SPEC.define( new BooleanField("pointed_dripstone_sniping", true,
                    "If enabled, pointed dripstone blocks will break when hit with any projectile entity tagged as 'minecraft:impact_projectiles'.",
                    "In vanilla, only thrown tridents can break pointed dripstone, but this setting allows entities like arrows, snowballs and others to also do so.") );

            SPEC.newLine();

            spookyStalactites = SPEC.define( new BooleanField( "spooky_stalactites", true,
                    "If enabled, there is a chance for nearby pointed dripstone (stalactites) in the ceiling to break off and fall when the player " +
                            "is breaking blocks.",
                    "Skylight level must be less than 3, and the position of the destroyed block must be below sea level.",
                    "Also, stalactites must be pointing downwards and be within scan range (specified in the below field \"scan_height\")."
            ) );

            triggerChance = SPEC.define( new DoubleField( "trigger_chance", 0.1D, DoubleField.Range.PERCENT,
                    "If \"spooky_stalactites\" is enabled, this field determines the chance for nearby stalactites to break off and fall " +
                            "when the player breaks a block." ) );

            scanHeight = SPEC.define( new IntField( "scan_height", 10, IntField.Range.POSITIVE,
                    "If \"spooky_stalactites\" is enabled, this determines the vertical scan height used when checking for " +
                            "Pointed Dripstone above the player." ) );

            SPEC.newLine();
        }
    }

    public static class FishingPranks extends AbstractConfigCategory<MainConfig> {

        public final DoubleField prankChance;

        public final WeightedRegEntryListField<FishingPrank> prankList;


        FishingPranks( MainConfig parent ) {
            super( parent, "fishing_pranks",
                    "Settings related to traps and pranks that may trigger when reeling in your catch." );

            prankChance = SPEC.define( new DoubleField("prank_chance", 0.05, DoubleField.Range.PERCENT,
                    "The chance for a \"prank\" to trigger when the player is fishing and right-clicks to reel in their catch",
                    "Setting this to 0.0 effectively disabled fishing pranks." ) );

            SPEC.newLine();

            prankList = SPEC.define( new WeightedRegEntryListField<>( "prank_list", makeDefaultPrankList(),
                    "Weighted list of fishing pranks to pick from when pranking a player that is fishing.",
                    "Note that some pranks have custom checks to see if they can be executed under the current circumstances.",
                    "If the prank cannot be executed, nothing happens.") );

            SPEC.newLine();
        }

        private WeightedRegEntryList<FishingPrank> makeDefaultPrankList() {
            return new WeightedRegEntryList<>( DWRegistries.FISHING_PRANKS_REGISTRY,
                    new RegistryValueEntry<>( DWFishingPranks.SINGLE_TNT.getId(), 10 )
            );
        }
    }
}