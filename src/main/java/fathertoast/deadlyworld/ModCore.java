package fathertoast.deadlyworld;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * The core of the mod. Contains basic info about the mod, initializes configs, and hooks into FML.
 */
@Mod( ModCore.MOD_ID )
public class ModCore {
    /* TODO LIST:
     *  - everything
     *  - upgrade config to allow per-dimension files
     *  - veins
     *  - features
     *  - silverfish block
     *
     * Primary features:
     *  - chests
     *      + mimic 2.0 (custom entity)
     *      + cave-in (via surprise or combo)
     *  - water traps
     *      + vortex
     *  - floor traps
     *      + fire
     *  - ceiling traps
     *      + cave-in
     *      + lava
     *  - combo traps
     *      + spider spawner & splash poison dispenser
     *      + undead spawner & splash harm dispener
     *      ? any spawner & fish hook dispenser (custom entity)
     *      ? any floor trap & fish hook dispenser (custom entity)
     *      ? fire immune spawner & fireball dispenser
     *      ? creeper spawner & lightning dispenser
     *  ? support for custom potions in towers/floor traps/events
     *  ? wall traps
     *      + arrow traps
     *  - config tweaks
     *      ? option to allow floor traps to trigger vs creative mode players, and vice-versa for other traps
     *
     * Utility features:
     *  - modify vanilla structures?
     *  ? add chance to fail replacing blocks in config (notably per silverfish replaceable block and per vein)
     */
    
    /** The mod id and namespace used by this mod. */
    public static final String MOD_ID = "deadlyworld";
    
    /** The base lang key for translating text from this mod. */
    public static final String LANG_KEY = ModCore.MOD_ID + ".";
    
    /** The less than or equal to symbol (<=). */
    public static final String LESS_OR_EQUAL = "\u2264";
    /** The greater than or equal to symbol (>=). */
    public static final String GREATER_OR_EQUAL = "\u2265";
    
    /** The logger used by this mod. */
    public static final Logger LOG = LogManager.getLogger();
    
    /** @return Returns a Forge registry entry as a string, or "null" if it is null. */
    public static String toString( @Nullable ForgeRegistryEntry<?> regEntry ) { return regEntry == null ? "null" : toString( regEntry.getRegistryName() ); }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }
}