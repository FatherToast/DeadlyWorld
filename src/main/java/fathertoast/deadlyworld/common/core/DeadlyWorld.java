package fathertoast.deadlyworld.common.core;

import fathertoast.deadlyworld.common.event.BiomeEvents;
import fathertoast.deadlyworld.common.feature.DWConfiguredFeatures;
import fathertoast.deadlyworld.common.network.PacketHandler;
import fathertoast.deadlyworld.common.registry.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

/**
 * The core of the mod. Contains basic info about the mod, initializes configs, and hooks into FML.
 */
@Mod( DeadlyWorld.MOD_ID )
public class DeadlyWorld {
    /* TODO LIST:
     *  - everything
     *  - veins
     *  - features
     *  - silverfish block
     *  - FUNNIE STORM DRAIN TRAP #FunnyTrolling #MustSee
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
     *      + undead spawner & splash harm dispenser
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
     *  TODO - Note that vanilla structures are saved as NBT files and only so much can be changed.
     *   - For instance, we may do post processing, but we cannot change anything before the structure has
     *   - been generated
     *  - modify vanilla structures?
     *  ? add chance to fail replacing blocks in config (notably per silverfish replaceable block and per vein)
     */
    
    /** The mod id and namespace used by this mod. */
    public static final String MOD_ID = "deadlyworld";
    
    /** The logger used by this mod. */
    public static final Logger LOG = LogManager.getLogger( MOD_ID );

    /** Packet handler instance */
    public PacketHandler packetHandler = new PacketHandler();
    
    
    public DeadlyWorld() {
        IEventBus eventBus = FMLJavaModLoadingContext.get( ).getModEventBus( );

        this.packetHandler.registerMessages();

        MinecraftForge.EVENT_BUS.register(new BiomeEvents());

        DWBlocks.BLOCKS.register( eventBus );
        DWItems.ITEMS.register( eventBus );
        DWTileEntities.TILE_ENTITIES.register( eventBus );
        DWFeatures.FEATURES.register( eventBus );
    }

    /** @return A ResourceLocation with the mod's namespace. */
    public static ResourceLocation resourceLoc( String path ) { return new ResourceLocation( MOD_ID, path ); }
    
    /** @return Returns a Forge registry entry as a string, or "null" if it is null. */
    public static String toString( @Nullable ForgeRegistryEntry<?> regEntry ) { return regEntry == null ? "null" : toString( regEntry.getRegistryName() ); }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }
}