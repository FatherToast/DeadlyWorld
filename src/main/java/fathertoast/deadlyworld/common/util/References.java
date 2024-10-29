package fathertoast.deadlyworld.common.util;

import java.util.function.Supplier;

public final class References {
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
    
    // Overworld depth constants
    public static final int DEPTH_SKY = 319; // 0 below world gen limit
    public static final int DEPTH_0 = 54;
    public static final int DEPTH_1 = 34;
    public static final int DEPTH_2 = 14;
    public static final int DEPTH_3 = 0;
    public static final int DEPTH_4 = -14;
    public static final int DEPTH_5 = -34;
    public static final int DEPTH_LAVA = -54; // 1 above lava layer
    public static final int DEPTH_VOID = -63; // 1 above bedrock bottom
    
    // Nether depth constants
    public static final int DEPTH_NETHER_SKY = 126; // 1 below world gen limit
    public static final int DEPTH_NETHER_CEIL = 120; // 1 below solid ceiling
    public static final int DEPTH_NETHER_LAVA = 32; // 1 above lava layer
    public static final int DEPTH_NETHER_VOID = 1; // 1 above bedrock bottom
    
    @SuppressWarnings( "unused" ) // This is really actually used in one of the places
    public static final Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Comparable<String>>>>>>>>>>
            IMPORTANT_SUPPLIER = () -> () -> () -> () -> () -> () -> () -> () -> () -> "toast";
}