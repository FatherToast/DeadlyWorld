package toast.deadlyWorld;

import java.util.HashMap;
import java.util.Random;

import net.minecraftforge.common.config.Configuration;

/**
 * This helper class automatically creates, stores, and retrieves properties.
 * Supported data types:
 * String, boolean, int, double
 * 
 * Any property can be retrieved as an Object or String.
 * Any non-String property can also be retrieved as any other non-String property.
 * Retrieving a number as a boolean will produce a randomized output depending on the value.
 * 
 * This version provides the option to provide an RNG with getBoolean(). Should be used for world generation.
 */
public abstract class Properties {
    /// Mapping of all properties in the mod to their values.
    private static final HashMap<Integer, HashMap<String, Object>> map = new HashMap<Integer, HashMap<String, Object>>();
    /// Common category names.
    public static final String FREQUENCY = "_frequencies";
    public static final String GENERAL = "_general";
    public static final String VEINS = "_veins";

    public static final String BLOCK_NAMES = "block_names";

    public static final String BOSSES = "bosses";
    public static final String BOSSES_ROGUE = "bosses_rogue";

    public static final String BRUTAL_MOBS = "brutal_mobs";
    public static final String BRUTAL_SPAWNERS = "brutal_spawners";

    public static final String CAVE_INS = "cave_ins"; // NYI

    public static final String CHESTS = "chests";

    public static final String DUNGEON_SPAWNERS = "dungeon_spawners";
    public static final String DUNGEON_TYPES = "dungeon_types";
    public static final String DUNGEONS = "dungeons";

    public static final String NESTS = "nests";

    public static final String POTION_TRAPS = "potion_traps";

    public static final String RANDOM_SPAWNERS = "random_spawners";

    public static final String TURRETS = "skeleton_turrets"; // NYI

    public static final String SPAWNER_TRAPS = "spawner_traps";

    public static final String SPAWNERS = "spawners";
    public static final String SPAWNER_SWARMS = "swarm_spawners";

    public static final String TOWERS = "towers";

    public static final String SPAWNER_VEINS = "spawner_veins"; // Removed

    /// Initializes these properties.
    public static void init(Configuration config) {
        Properties.init(config, 0);
    }

    private static void init(Configuration config, int dimId) {
        config.load();

        Properties.add(config, dimId, Properties.FREQUENCY, "boss", 0.2);
        Properties.add(config, dimId, Properties.FREQUENCY, "brutal_spawner", 0.1);
        ///add(config, dimId, FREQUENCY, "cave_in", 0.2);
        Properties.add(config, dimId, Properties.FREQUENCY, "chest", 0.1);
        Properties.add(config, dimId, Properties.FREQUENCY, "fire_trap", 0.35);
        Properties.add(config, dimId, Properties.FREQUENCY, "mine", 0.2);
        Properties.add(config, dimId, Properties.FREQUENCY, "potion_trap", 0.2);
        Properties.add(config, dimId, Properties.FREQUENCY, "silverfish_nest", 0.15);
        Properties.add(config, dimId, Properties.FREQUENCY, "spawner", 0.2);
        Properties.add(config, dimId, Properties.FREQUENCY, "spawner_trap", 0.15);
        Properties.add(config, dimId, Properties.FREQUENCY, "swarm_spawner", 0.1);
        Properties.add(config, dimId, Properties.FREQUENCY, "tower", 0.35);

        if (dimId == 0) {
            Properties.add(config, dimId, Properties.GENERAL, "automatic_disable", true, "If true, this mod will disable itself when loading a world not created with this mod installed.");
            Properties.add(config, dimId, Properties.GENERAL, "charged_creeper_chance", 2, "Percent chance (from 0 to 100) for creeper spawners to spawn charged creepers.");
            Properties.add(config, dimId, Properties.GENERAL, "modify_break_speed", "true", "(true/false/instant) If true, this mod will modify the break speed for silverfish blocks to be the same as the block being imitated. If instant, silverfish blocks will break instantly.");
        }
        Properties.add(config, dimId, Properties.GENERAL, "covered_trap_carpet_chance", 0.4, "Chance (from 0 to 1) for covered traps to have carpet instead of a pressure plate.");
        Properties.add(config, dimId, Properties.GENERAL, "covered_trap_chance", 0.8, "Chance (from 0 to 1) for traps to spawn with a cover block over them.");

        Properties.add(config, dimId, Properties.VEINS, "lava_count", 4.0, "Lava vein stats. Defaults: count=4, size=10, height=0-32.");
        Properties.add(config, dimId, Properties.VEINS, "lava_max_height", 32);
        Properties.add(config, dimId, Properties.VEINS, "lava_min_height", 0);
        Properties.add(config, dimId, Properties.VEINS, "lava_size", 10);

        Properties.add(config, dimId, Properties.VEINS, "sand_count", 0.3, "Sand vein stats. Defaults: count=0.3, size=64, height=0-62.");
        Properties.add(config, dimId, Properties.VEINS, "sand_max_height", 62);
        Properties.add(config, dimId, Properties.VEINS, "sand_min_height", 0);
        Properties.add(config, dimId, Properties.VEINS, "sand_size", 48);

        Properties.add(config, dimId, Properties.VEINS, "silverfish_count", 10.0, "Silverfish vein stats. Defaults: count=10, size=24, height=0-128.");
        Properties.add(config, dimId, Properties.VEINS, "silverfish_max_height", 128);
        Properties.add(config, dimId, Properties.VEINS, "silverfish_min_height", 0);
        Properties.add(config, dimId, Properties.VEINS, "silverfish_size", 24);

        Properties.add(config, dimId, Properties.VEINS, "spawner_count", 0.0, "Officially replaced by spawner traps, but you may re-enable spawner veins, if you like.");

        Properties.add(config, dimId, Properties.VEINS, "water_count", 8.0, "Water vein stats. Defaults: count=8, size=10, height=0-62.");
        Properties.add(config, dimId, Properties.VEINS, "water_max_height", 62);
        Properties.add(config, dimId, Properties.VEINS, "water_min_height", 0);
        Properties.add(config, dimId, Properties.VEINS, "water_size", 10);

        if (dimId == 0) {
            Properties.add(config, dimId, Properties.BLOCK_NAMES, "_name_overrides", true, "If true, these name overrides will be applied.");
            Properties.add(config, dimId, Properties.BLOCK_NAMES, "stone_egg", "\"Stone\"", "The name for silverfish stone. Does not override name in inventory.");
            Properties.add(config, dimId, Properties.BLOCK_NAMES, "stone_egg_brick", "\"Stone Bricks\"", "The name for silverfish stone bricks. Overrides name in inventory.");
            Properties.add(config, dimId, Properties.BLOCK_NAMES, "stone_egg_cobble", "\"Cobblestone\"", "The name for silverfish cobblestone. Overrides name in inventory.");
            Properties.add(config, dimId, Properties.BLOCK_NAMES, "trapped_chest", "Trapped Chest", "The name for trapped chests. Overrides name in inventory.");
        }

        Properties.add(config, dimId, Properties.BOSSES, "damage_bonus", 4.0, "How much more damage (in half-hearts) bosses deal than normal mobs.");
        Properties.add(config, dimId, Properties.BOSSES, "effect_chance", 0.3, "Chance (from 0 to 1) for a boss to have a random, permanent potion effect. (Technically, the effect only lasts about 3.4 years.)");
        Properties.add(config, dimId, Properties.BOSSES, "enchantment_chance", 0.25, "Chance (from 0 to 1) for any equipment a boss wears to be enchanted. The unique item will always be enchanted.");
        Properties.add(config, dimId, Properties.BOSSES, "equip_chance", 0.25, "Chance (from 0 to 1) for each piece of equipment to be equipped. The unique item will always be equipped.");
        Properties.add(config, dimId, Properties.BOSSES, "health_multiplier", 4.0, "How much health a boss has. (max health * health multipler = boss max health)");
        Properties.add(config, dimId, Properties.BOSSES, "knockback_resistance", 0.85, "How resistant (from 0 to 1) bosses are to being knocked back.");
        Properties.add(config, dimId, Properties.BOSSES, "level_up_chance", 0.25, "Chance (from 0 to 1) to increase the boss material level. Rolled three times. (Chainmail -> Gold -> Iron -> Diamond)");
        Properties.add(config, dimId, Properties.BOSSES, "regeneration", 1, "Regeneration potion amplifier. Yes, it works on undead. (0 heals 1 health every 2.5 sec, each rank halves the time between heals. -1 disables the effect.)");
        Properties.add(config, dimId, Properties.BOSSES, "resistance", 0, "Increases base damage resistance. (0 is -20% damage, each rank grants -20% damage, -1 disables the effect.)");
        Properties.add(config, dimId, Properties.BOSSES, "fire_resistance", true, "If true, bosses will be immune to fire damage (strongly recommended).");
        Properties.add(config, dimId, Properties.BOSSES, "water_breathing", true, "If true, bosses will not drown (strongly recommended).");
        Properties.add(config, dimId, Properties.BOSSES, "speed_multiplier", 0.85, "How fast bosses are compared to normal mobs.");

        Properties.addWeightedCategory(config, dimId, Properties.BOSSES_ROGUE, _DeadlyWorld.MOBS, 4, 1);

        if (dimId == 0) {
            Properties.add(config, dimId, Properties.BRUTAL_MOBS, "_fire_resistance", true, "If true, brutal mobs will be immune to fire damage.");
            Properties.add(config, dimId, Properties.BRUTAL_MOBS, "_regeneration", 1, "Regenerates health. Yes, it works on undead. (0 heals 1 health every 2.5 sec, each rank halves the time between heals.)");
            Properties.add(config, dimId, Properties.BRUTAL_MOBS, "_resistance", 3, "Increases damage resistance. (0 is -20% damage, each rank grants -20% damage.)");
            Properties.add(config, dimId, Properties.BRUTAL_MOBS, "_strength", 0, "Increases melee damage. (0 is +130% damage, each rank grants +130% damage.)");
            Properties.add(config, dimId, Properties.BRUTAL_MOBS, "_swiftness", 1, "Increases speed. (0 is +30% speed, each rank grants +30% speed.)");
            Properties.add(config, dimId, Properties.BRUTAL_MOBS, "_water_breathing", true, "If true, brutal mobs will not drown.");
        }

        Properties.add(config, dimId, Properties.BRUTAL_SPAWNERS, "_max_delay", 400, "Brutal spawner stats. Defaults: delay=200-400, nearby=6, playerrange=16, spawncount=6, spawnrange=4.");
        Properties.add(config, dimId, Properties.BRUTAL_SPAWNERS, "_min_delay", TagBuilder.MIN_DELAY);
        Properties.add(config, dimId, Properties.BRUTAL_SPAWNERS, "_nearby_entity_cap", TagBuilder.MAX_NEARBY);
        Properties.add(config, dimId, Properties.BRUTAL_SPAWNERS, "_player_range", TagBuilder.PLAYER_RANGE);
        Properties.add(config, dimId, Properties.BRUTAL_SPAWNERS, "_spawn_count", 6);
        Properties.add(config, dimId, Properties.BRUTAL_SPAWNERS, "_spawn_range", TagBuilder.SPAWN_RANGE);
        Properties.addWeightedCategory(config, dimId, Properties.BRUTAL_SPAWNERS, _DeadlyWorld.SPAWNERS, 4, 1);

        /* NYI
        Properties.addWeightedCategory(config, dimId, CAVE_INS, _DeadlyWorld.CAVE_INS, 4, 1);
        */

        Properties.addWeightedCategory(config, dimId, Properties.CHESTS, _DeadlyWorld.CHESTS, 2, 1);

        Properties.add(config, dimId, Properties.DUNGEON_SPAWNERS, "_max_delay", 400, "Spawner stats. Defaults: delay=200-400, nearby=6, playerrange=16, spawncount=4, spawnrange=4.");
        Properties.add(config, dimId, Properties.DUNGEON_SPAWNERS, "_min_delay", TagBuilder.MIN_DELAY);
        Properties.add(config, dimId, Properties.DUNGEON_SPAWNERS, "_nearby_entity_cap", TagBuilder.MAX_NEARBY);
        Properties.add(config, dimId, Properties.DUNGEON_SPAWNERS, "_player_range", TagBuilder.PLAYER_RANGE);
        Properties.add(config, dimId, Properties.DUNGEON_SPAWNERS, "_spawn_count", TagBuilder.SPAWN_COUNT);
        Properties.add(config, dimId, Properties.DUNGEON_SPAWNERS, "_spawn_range", TagBuilder.SPAWN_RANGE);
        Properties.addWeightedCategory(config, dimId, Properties.DUNGEON_SPAWNERS, _DeadlyWorld.DUNGEON_MOBS, 4, 1);

        Properties.addWeightedCategory(config, dimId, Properties.DUNGEON_TYPES, _DeadlyWorld.DUNGEON_FEATURES, 4, 1);

        Properties.add(config, dimId, Properties.DUNGEONS, "_armor_chance", 0.05, "Chance (from 0 to 1) for the spawner to be covered in obsidian (if it's a normal spawner).");
        Properties.add(config, dimId, Properties.DUNGEONS, "_place_attempts", 8.0, "The number of dungeon generation attempts per chunk. Be careful; increasing this far beyond 8.0 (vanilla) could cause lag. Example: 9.25 is 9 attempts with a 25% chance of a fourth attempt.");
        Properties.add(config, dimId, Properties.DUNGEONS, "_silverfish_chance", 0.2, "Chance (from 0 to 1) for any cobblestone block in a dungeon to instead be a silverfish block.");

        Properties.add(config, dimId, Properties.NESTS, "_angered_chance", 0.2, "Chance (from 0 to 1) for a silverfish nest to be abnormally aggressive.");
        Properties.add(config, dimId, Properties.NESTS, "_max_delay", 300, "Silverfish nest spawner stats. Defaults: delay=100-300, nearby=16, playerrange=5, spawncount=6, spawnrange=4.");
        Properties.add(config, dimId, Properties.NESTS, "_min_delay", 100);
        Properties.add(config, dimId, Properties.NESTS, "_nearby_entity_cap", 16);
        Properties.add(config, dimId, Properties.NESTS, "_player_range", 5);
        Properties.add(config, dimId, Properties.NESTS, "_spawn_count", 6);
        Properties.add(config, dimId, Properties.NESTS, "_spawn_range", TagBuilder.SPAWN_RANGE);
        Properties.addWeightedCategory(config, dimId, Properties.NESTS, _DeadlyWorld.NESTS, 4, 1);

        Properties.add(config, dimId, Properties.POTION_TRAPS, "_daze_duration", 1600, "The duration of potions shot by daze potion traps. (Affected by proximity to the splash.)");
        Properties.add(config, dimId, Properties.POTION_TRAPS, "_daze_potency", 0, "The strength of potions shot by daze potion traps.");
        Properties.add(config, dimId, Properties.POTION_TRAPS, "_harm_potency", 2, "The strength of potions shot by harm potion traps. (Affected by proximity to the splash.)");
        Properties.add(config, dimId, Properties.POTION_TRAPS, "_poison_duration", 2000, "The duration of potions shot by poison potion traps. (Affected by proximity to the splash.)");
        Properties.add(config, dimId, Properties.POTION_TRAPS, "_poison_potency", 0, "The strength of potions shot by poison potion traps.");
        Properties.addWeightedCategory(config, dimId, Properties.POTION_TRAPS, _DeadlyWorld.POTIONS, 2, 1);

        Properties.addWeightedCategory(config, dimId, Properties.RANDOM_SPAWNERS, _DeadlyWorld.MOBS, 2, 1);

        /* NYI (destroys spawn cap)
        add(config, dimId, TURRETS, "bow_power", 5, "The level of the Power enchantment on a skeleton turret's bow.");
        add(config, dimId, TURRETS, "fire_bow_chance", 0.25, "Chance (from 0 to 1) for a skeleton turret to spawn wielding a fire bow.");
        add(config, dimId, TURRETS, "health_multiplier", 2.0, "How much health a skeleturret has. (max health * health multipler = skeleturret health)");
        */

        Properties.add(config, dimId, Properties.SPAWNER_TRAPS, "_chest_chance", 0.1, "Chance (from 0 to 1) for a spawner trap to have a chest below it.");
        Properties.addWeightedCategory(config, dimId, Properties.SPAWNER_TRAPS, _DeadlyWorld.SPAWNERS, 4, 1);

        Properties.add(config, dimId, Properties.SPAWNER_VEINS, "_armor_chance", 0.05, "Chance (from 0 to 1) for a spawner vein to be covered in obsidian.");
        Properties.add(config, dimId, Properties.SPAWNER_VEINS, "_chest_chance", 0.1, "Chance (from 0 to 1) for a spawner vein to have a chest below it.");
        Properties.addWeightedCategory(config, dimId, Properties.SPAWNER_VEINS, _DeadlyWorld.SPAWNERS, 4, 1);

        Properties.add(config, dimId, Properties.SPAWNERS, "_armor_chance", 0.05, "Chance (from 0 to 1) for a mob spawner to be covered in obsidian.");
        Properties.add(config, dimId, Properties.SPAWNERS, "_chest_chance", 0.1, "Chance (from 0 to 1) for a mob spawner to have a chest below it. If the spawner is armored, its chest will also be armored and have better loot.");
        Properties.add(config, dimId, Properties.SPAWNERS, "_max_delay", 600, "Spawner stats. Defaults: delay=200-600, nearby=6, playerrange=16, spawncount=4, spawnrange=4.");
        Properties.add(config, dimId, Properties.SPAWNERS, "_min_delay", TagBuilder.MIN_DELAY);
        Properties.add(config, dimId, Properties.SPAWNERS, "_nearby_entity_cap", TagBuilder.MAX_NEARBY);
        Properties.add(config, dimId, Properties.SPAWNERS, "_player_range", TagBuilder.PLAYER_RANGE);
        Properties.add(config, dimId, Properties.SPAWNERS, "_spawn_count", TagBuilder.SPAWN_COUNT);
        Properties.add(config, dimId, Properties.SPAWNERS, "_spawn_range", TagBuilder.SPAWN_RANGE);
        Properties.add(config, dimId, Properties.SPAWNERS, "_trick_chance", 0.05, "Chance (from 0 to 1) for an armored mob spawner to be a chest instead, if it doesn't already have a chest below it.");
        Properties.addWeightedCategory(config, dimId, Properties.SPAWNERS, _DeadlyWorld.SPAWNERS, 4, 1);

        Properties.add(config, dimId, Properties.SPAWNER_SWARMS, "_max_delay", 600, "Swarm spawner stats. Defaults: delay=200-600, nearby=8, playerrange=8, spawncount=127, spawnrange=6.");
        Properties.add(config, dimId, Properties.SPAWNER_SWARMS, "_min_delay", TagBuilder.MIN_DELAY);
        Properties.add(config, dimId, Properties.SPAWNER_SWARMS, "_nearby_entity_cap", 8);
        Properties.add(config, dimId, Properties.SPAWNER_SWARMS, "_player_range", 8);
        Properties.add(config, dimId, Properties.SPAWNER_SWARMS, "_spawn_count", 127);
        Properties.add(config, dimId, Properties.SPAWNER_SWARMS, "_spawn_range", 6);
        Properties.addWeightedCategory(config, dimId, Properties.SPAWNER_SWARMS, _DeadlyWorld.MOBS, 4, 1);

        if (dimId == 0) {
            Properties.add(config, dimId, Properties.TOWERS, "_arrow_damage", 8.0, "Damage arrows from towers deal (min. 2). Translates roughly into half hearts of damage.");
        }
        Properties.addWeightedCategory(config, dimId, Properties.TOWERS, _DeadlyWorld.TOWERS, 4, 1);

        config.addCustomCategoryComment(Properties.FREQUENCY, "The frequencies for all features added by this mod. (from 0 to 1)");
        config.addCustomCategoryComment(Properties.GENERAL, "General and/or miscellaneous options.");
        config.addCustomCategoryComment(Properties.VEINS, "The number of vein generation attempts per chunk and the generation properties for those veins. Example: 3.25 is 3 attempts with a 25% chance of a fourth attempt.");

        config.addCustomCategoryComment(Properties.BLOCK_NAMES, "Names to override to counter mods that give tooltips of the block you look at (client-side!).");

        config.addCustomCategoryComment(Properties.BOSSES, "The stats and loot table for each boss mob.");
        config.addCustomCategoryComment(Properties.BOSSES_ROGUE, "The relative weights for each wandering boss mob type.");
        config.addCustomCategoryComment(Properties.BRUTAL_MOBS, "The potion strengths for mobs spawned by brutal spawners (aka brutal mobs). Potions can be disabled be setting their amplifiers to -1.");
        config.addCustomCategoryComment(Properties.BRUTAL_SPAWNERS, "The stats for brutal spawners and the relative weights for each brutal spawner type.");

        ///config.addCustomCategoryComment(CAVE_INS, "The relative weights for each cave-in type.");

        config.addCustomCategoryComment(Properties.CHESTS, "The relative weights for each chest type.");

        config.addCustomCategoryComment(Properties.DUNGEON_SPAWNERS, "The stats for dungeon spawners and the relative weights for each dungeon spawner type.");
        config.addCustomCategoryComment(Properties.DUNGEON_TYPES, "The relative weights for each dungeon type.");
        config.addCustomCategoryComment(Properties.DUNGEONS, "General options for dungeons and dungeon generation.");

        config.addCustomCategoryComment(Properties.NESTS, "The stats for silverfish nest spawners and the relative weights for each silverfish nest type.");

        config.addCustomCategoryComment(Properties.RANDOM_SPAWNERS, "The relative weights for each mob type to spawn from a random spawner.");
        ///config.addCustomCategoryComment(TURRETS, "The stats for skeleton turrets.");
        config.addCustomCategoryComment(Properties.SPAWNER_TRAPS, "The relative weights for each mob spawner trap type. Only applies to spawner traps.");
        config.addCustomCategoryComment(Properties.SPAWNER_VEINS, "The relative weights for each mob spawner vein type. Only applies to spawner veins.");
        config.addCustomCategoryComment(Properties.SPAWNERS, "The stats for mob spawners and the relative weights for each mob spawner type. Applies only to rogue spawners and tower mob spawners.");
        config.addCustomCategoryComment(Properties.SPAWNER_SWARMS, "The stats for swarm spawners and the relative weights for each swarm spawner type.");

        config.addCustomCategoryComment(Properties.TOWERS, "The damage towers deal and relative weights for each tower type.");
        config.save();
    }

    /// Gets the mod's random number generator.
    public static Random random() {
        return _DeadlyWorld.random;
    }

    /// Passes to the mod.
    public static void debugException(String message) {
        _DeadlyWorld.debugException(message);
    }

    /// Loads the property as the specified value.
    public static void add(Configuration config, int dimId, String category, String field, String defaultValue, String comment) {
        Properties.getMap(dimId).put(category + "@" + field, config.get(category, field, defaultValue, comment).getString());
    }

    public static void add(Configuration config, int dimId, String category, String field, int defaultValue, String comment) {
        Properties.getMap(dimId).put(category + "@" + field, Integer.valueOf(config.get(category, field, defaultValue, comment).getInt(defaultValue)));
    }

    public static void add(Configuration config, int dimId, String category, String field, int defaultValue) {
        Properties.getMap(dimId).put(category + "@" + field, Integer.valueOf(config.get(category, field, defaultValue).getInt(defaultValue)));
    }

    public static void add(Configuration config, int dimId, String category, String field, boolean defaultValue, String comment) {
        Properties.getMap(dimId).put(category + "@" + field, Boolean.valueOf(config.get(category, field, defaultValue, comment).getBoolean(defaultValue)));
    }

    public static void add(Configuration config, int dimId, String category, String field, double defaultValue, String comment) {
        Properties.getMap(dimId).put(category + "@" + field, Double.valueOf(config.get(category, field, defaultValue, comment).getDouble(defaultValue)));
    }

    public static void add(Configuration config, int dimId, String category, String field, double defaultValue) {
        Properties.getMap(dimId).put(category + "@" + field, Double.valueOf(config.get(category, field, defaultValue).getDouble(defaultValue)));
    }

    /// Loads the weighted choice category with the given defaults.
    public static void addWeightedCategory(Configuration config, int dimId, String category, String[] fields, int firstDefaultValue, int defaultValue) {
        Properties.add(config, dimId, category, fields[0].toLowerCase(), firstDefaultValue);
        for (int i = fields.length; i-- > 1;) {
            Properties.add(config, dimId, category, fields[i].toLowerCase(), defaultValue);
        }
    }

    /// Gets the property map for the given dimension, or creates a new one.
    public static HashMap<String, Object> getMap(int dimId) {
        HashMap<String, Object> map = Properties.map.get(Integer.valueOf(dimId));
        if (map == null) {
            map = new HashMap<String, Object>();
            Properties.map.put(Integer.valueOf(dimId), map);
        }
        return map;
    }

    /// Gets the Object property.
    public static Object getProperty(int dimId, String category, String field) {
        return Properties.getMap(dimId).get(category + "@" + field);
    }

    /// Gets the value of the property (instead of an Object representing it).
    public static String getString(int dimId, String category, String field) {
        return Properties.getProperty(dimId, category, field).toString();
    }

    public static boolean getBoolean(int dimId, String category, String field) {
        return Properties.getBoolean(dimId, category, field, Properties.random());
    }

    public static boolean getBoolean(int dimId, String category, String field, Random random) {
        Object property = Properties.getProperty(dimId, category, field);
        if (property instanceof Boolean)
            return ((Boolean) property).booleanValue();
        if (property instanceof Integer)
            return random.nextInt( ((Number) property).intValue()) == 0;
        if (property instanceof Double)
            return random.nextDouble() < ((Number) property).doubleValue();
        Properties.debugException("Tried to get boolean for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return false;
    }

    public static int getInt(int dimId, String category, String field) {
        Object property = Properties.getProperty(dimId, category, field);
        if (property instanceof Number)
            return ((Number) property).intValue();
        if (property instanceof Boolean)
            return ((Boolean) property).booleanValue() ? 1 : 0;
        Properties.debugException("Tried to get int for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return 0;
    }

    public static double getDouble(int dimId, String category, String field) {
        Object property = Properties.getProperty(dimId, category, field);
        if (property instanceof Number)
            return ((Number) property).doubleValue();
        if (property instanceof Boolean)
            return ((Boolean) property).booleanValue() ? 1.0 : 0.0;
        Properties.debugException("Tried to get double for invalid property! @" + property == null ? "(null)" : property.getClass().getName());
        return 0.0;
    }
}