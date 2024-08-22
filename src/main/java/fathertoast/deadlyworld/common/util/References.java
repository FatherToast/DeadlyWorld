package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.DeadlyWorld;

import java.util.function.Supplier;

public class References {
    /** The base lang key for translating text from this mod. */
    public static final String LANG_KEY = DeadlyWorld.MOD_ID + ".";
    
    /** The base path for all loot tables from this mod. */
    public static final String LOOT_PATH = "loot_tables/";
    
    /** The base path for event loot tables from this mod. */
    public static final String EVENT_LOOT_PATH = LOOT_PATH + "events/";
    /** The base path for block loot tables from this mod. */
    public static final String BLOCK_LOOT_PATH = LOOT_PATH + "blocks/";
    /** The base path for chest loot tables from this mod. */
    public static final String CHEST_LOOT_PATH = LOOT_PATH + "chests/";
    
    /** The plus or minus symbol (+/-). */
    public static final String PLUS_OR_MINUS = "\u00b1";
    /** The less than or equal to symbol (<=). */
    public static final String LESS_OR_EQUAL = "\u2264";
    /** The greater than or equal to symbol (>=). */
    public static final String GREATER_OR_EQUAL = "\u2265";

    public static class Language {

        public static final String BLUEPRINT_NO_INS = "item.deadlyworld.device_blueprint.tooltip.invalid";
        public static final String BLUEPRINT_DEVICE_NAME = "item.deadlyworld.device_blueprint.tooltip.device_name";
    }
    
    
    public static Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<String>>>>>>>>> IMPORTANT_SUPPLIER = () -> () -> () -> () -> () -> () -> () -> () -> () -> "toast";
}