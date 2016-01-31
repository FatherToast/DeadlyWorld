package toast.deadlyWorld;

import java.util.Random;

import net.minecraftforge.common.config.Configuration;
import toast.deadlyWorld.feature.WorldGenerator;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = _DeadlyWorld.MODID, name = "Deadly World", version = _DeadlyWorld.VERSION)
public class _DeadlyWorld {
    /* TO DO *\
    * Customization
        > Add a min/max value for vein size
        > Add boss equipment
        ? Add mod entities to spawners
    * New features
        / Cave-ins
        > Gravel traps
    * Active in other dimensions
        > Configurable feature height
        > Per-dimension configs
    \* ** ** */

    // This mod's id.
    public static final String MODID = "DeadlyWorld";
    // This mod's version.
    public static final String VERSION = "1.0.2";

    /// If true, this mod starts up in debug mode.
    public static final boolean debug = false;
    /// If false, this mod will not generate anything.
    public static boolean generation = false;
    /// This mod's random number generator.
    public static final Random random = new Random();

    /// Type arrays for various features in the mod.
    public static final String[] CAVE_INS = { "normal", "silverfish", "gravel" }; /// NYI
    public static final String[] CHESTS = { "normal", "trapped", "mine", "indie", "valuable" };
    public static final String[] DUNGEON_FEATURES = { "spawner", "tower", "brutal_spawner", "swarm_spawner" };
    public static final String[] DUNGEON_MOBS = { "Zombie", "Skeleton", "Spider", "CaveSpider", "Creeper", "Silverfish", "RANDOM" };
    public static final String[] MOBS = { "Zombie", "Skeleton", "Spider", "CaveSpider", "Creeper" };
    public static final String[] NESTS = { "redstone", "lapis", "gold", "emerald", "diamond", "chest", "surprise", "party" };
    public static final String[] POTIONS = { "harm", "poison", "daze" };
    public static final String[] SPAWNERS = { "Zombie", "Skeleton", "Spider", "CaveSpider", "Creeper", "RANDOM" };
    public static final String[] TOWERS = { "arrow", "arrow_fire", "double", "double_fire", "spawner", "spawner_fire", "chest", "chest_fire" };

    /// Called before initialization. Loads the properties/configurations.
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        _DeadlyWorld.debugConsole("Loading in debug mode!");
        Properties.init(new Configuration(event.getSuggestedConfigurationFile()));
    }

    /// Called during initialization. Registers entities, mob spawns, and renderers.
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (Properties.getBoolean(Properties.BLOCK_NAMES, "_name_overrides")) {
            LanguageRegistry.instance().addStringLocalization("tile.monsterStoneEgg.stone.name", Properties.getString(Properties.BLOCK_NAMES, "stone_egg"));
            LanguageRegistry.instance().addStringLocalization("tile.monsterStoneEgg.cobble.name", Properties.getString(Properties.BLOCK_NAMES, "stone_egg_cobble"));
            LanguageRegistry.instance().addStringLocalization("tile.monsterStoneEgg.brick.name", Properties.getString(Properties.BLOCK_NAMES, "stone_egg_brick"));
            LanguageRegistry.instance().addStringLocalization("tile.chestTrap.name", Properties.getString(Properties.BLOCK_NAMES, "trapped_chest"));
        }

        ChestBuilder.init();
        new EventHandler();
        new WorldGenerator();
    }

    /// Called after initialization. Used to check for dependencies.
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        // Do nothing.
    }

    /// Makes the first letter upper case.
    public static String cap(String string) {
        int length = string.length();
        if (length <= 0)
            return "";
        if (length == 1)
            return string.toUpperCase();
        return Character.toString(Character.toUpperCase(string.charAt(0))) + string.substring(1);
    }

    /// Makes the first letter lower case.
    public static String decap(String string) {
        int length = string.length();
        if (length <= 0)
            return "";
        if (length == 1)
            return string.toLowerCase();
        return Character.toString(Character.toLowerCase(string.charAt(0))) + string.substring(1);
    }

    /// Prints the message to the console with this mod's name tag.
    public static void console(String message) {
        System.out.println("[Deadly World] " + message);
    }

    /// Prints the message to the console with this mod's name tag if debugging is enabled.
    public static void debugConsole(String message) {
        if (_DeadlyWorld.debug) {
            System.out.println("[Deadly World] (debug) " + message);
        }
    }

    /// Throws a runtime exception with a message and this mod's name tag.
    public static void exception(String message) {
        throw new RuntimeException("[Deadly World] " + message);
    }

    /// Throws a runtime exception with a message and this mod's name tag if debugging is enabled.
    public static void debugException(String message) {
        if (_DeadlyWorld.debug)
            throw new RuntimeException("[Deadly World] " + message);
    }
}