package fathertoast.deadlyworld.common.util;

import java.util.function.Supplier;

public class References {
    //    /** The base lang key for translating text from this mod. */
    //    public static final String LANG_KEY = DeadlyWorld.MOD_ID + ".";
    
    /** The base path for all loot tables from this mod. */
    public static final String LOOT_PATH = "loot_tables/";
    
    //    /** The base path for event loot tables from this mod. */
    //    public static final String EVENT_LOOT_PATH = LOOT_PATH + "events/";
    //    /** The base path for block loot tables from this mod. */
    //    public static final String BLOCK_LOOT_PATH = LOOT_PATH + "blocks/";
    /** The base path for chest loot tables from this mod. */
    public static final String CHEST_LOOT_PATH = LOOT_PATH + "chests/";
    
    public static Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Comparable<String>>>>>>>>>>
            IMPORTANT_SUPPLIER = () -> () -> () -> () -> () -> () -> () -> () -> () -> "toast";
}