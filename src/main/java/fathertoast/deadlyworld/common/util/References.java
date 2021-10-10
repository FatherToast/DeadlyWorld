package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.DeadlyWorld;

import java.util.function.Supplier;

public class References {
    /** The base lang key for translating text from this mod. */
    public static final String LANG_KEY = DeadlyWorld.MOD_ID + ".";
    
    /** The plus or minus symbol (+/-). */
    public static final String PLUS_OR_MINUS = "\u00b1";
    /** The less than or equal to symbol (<=). */
    public static final String LESS_OR_EQUAL = "\u2264";
    /** The greater than or equal to symbol (>=). */
    public static final String GREATER_OR_EQUAL = "\u2265";


    public static Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<Supplier<String>>>>>>>>> IMPORTANT_SUPPLIER = () -> () -> () -> () -> () -> () -> () -> () -> () -> "toast";
}