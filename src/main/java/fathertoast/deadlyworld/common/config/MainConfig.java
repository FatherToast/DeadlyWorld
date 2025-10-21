package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class MainConfig extends AbstractConfigFile {
    
    public final General GENERAL;
    public final StalactiteOverhaul STALACTITE_OVERHAUL;
    
    /** Builds the config spec that should be used for this config. */
    MainConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for miscellaneous features in the mod."
        );
        
        GENERAL = new General( this );
        STALACTITE_OVERHAUL = new StalactiteOverhaul( this );
    }
    
    public static class General extends AbstractConfigCategory<MainConfig> {
        
        public final StringListField extraDimensions;
        
        public final BooleanField activateTrapsInPeaceful;
        public final BooleanField activateTrapsVsCreative;
        
        public final BooleanField activateSpawnersVsCreative;
        
        public final BooleanField disableVanillaMonsterRooms;
        
        General( MainConfig parent ) {
            super( parent, "general",
                    "Options to customize misc settings that apply to the mod as a whole." );
            
            extraDimensions = SPEC.define( new StringListField( "extra_dimensions", "Dimension Type",
                    List.of( Level.NETHER.location().toString() ),
                    "List of extra dimension types for this mod to generate configs for. If this list is " +
                            "empty, world gen configs will only generate for the overworld. All dimensions NOT in this " +
                            "list will default to the '" + Level.OVERWORLD.location() + "' configs.",
                    "NOTE: Having configs for a dimension does NOT add world gen to it. Your data pack determines all " +
                            "world gen, and can also overwrite most world gen config settings. This mod generally only " +
                            "supports the default values here without the use of a data pack."
            ), RestartNote.GAME );
            
            SPEC.newLine();
            
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
            
            disableVanillaMonsterRooms = SPEC.define( new BooleanField( "disable_vanilla_monster_rooms", true,
                            "If true, the vanilla monster room feature / dungeon that generates underground " +
                                    "in the overworld will be disabled.",
                            "Handy if you prefer only DeadlyWorld's dungeon room feature to generate instead." ),
                    RestartNote.WORLD );
        }
    }
    
    public static class StalactiteOverhaul extends AbstractConfigCategory<MainConfig> {
        
        public final BooleanField pointedDripstoneSniping;
        
        public final BooleanField spookyStalactites;
        public final DoubleField triggerChance;
        public final IntField scanHeight;
        public final IntField scanRange;
        
        StalactiteOverhaul( MainConfig parent ) {
            super( parent, "stalactite_overhaul",
                    "Settings related to interactions with Pointed Dripstone in the world." );
            
            pointedDripstoneSniping = SPEC.define( new BooleanField( "pointed_dripstone_sniping", true,
                    "If enabled, pointed dripstone blocks will break when hit with any projectile entity " +
                            "tagged as 'minecraft:impact_projectiles'.",
                    "In vanilla, only thrown tridents can break pointed dripstone, but this setting allows entities " +
                            "like arrows, snowballs and others to also do so." ) );
            
            SPEC.newLine();
            
            spookyStalactites = SPEC.define( new BooleanField( "spooky_stalactites", true,
                    "If enabled, there is a chance for nearby pointed dripstone (stalactites) in the " +
                            "ceiling to break off and fall when the player is breaking blocks.",
                    "Skylight level must be less than 3, and the position of the destroyed block must be below sea " +
                            "level. Also, stalactites must be pointing downwards and be within scan range (specified " +
                            "in the below field \"scan_height\")." ) );
            triggerChance = SPEC.define( new DoubleField( "trigger_chance", 0.1, DoubleField.Range.PERCENT,
                    "If \"spooky_stalactites\" is enabled, this field determines the chance for nearby " +
                            "stalactites to break off and fall when the player breaks a block." ) );
            scanHeight = SPEC.define( new IntField( "scan_height", 18, IntField.Range.POSITIVE,
                    "If \"spooky_stalactites\" is enabled, this determines the vertical scan height used " +
                            "when checking for Pointed Dripstone above the player." ) );
            scanRange = SPEC.define( new IntField( "scan_range", 2, IntField.Range.POSITIVE,
                    "If \"spooky_stalactites\" is enabled, this determines the horizontal scan range used " +
                            "when checking for Pointed Dripstone above the player." ) );
        }
    }
}