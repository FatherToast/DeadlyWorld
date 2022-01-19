package fathertoast.deadlyworld.common.core;

import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.event.BiomeEvents;
import fathertoast.deadlyworld.common.network.PacketHandler;
import fathertoast.deadlyworld.common.registry.*;
import fathertoast.deadlyworld.common.util.DWDamageSources;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
     *  - finish all features; see features list
     *
     * Features list:
     * (KEY: - = complete in current version, o = incomplete feature from previous version,
     *       + = incomplete new feature, ? = feature to consider adding)
     *  o general
     *      o dimension-based configs
     *      o biome-based configs
     *      + bounding box renderer
     *  o blocks
     *      o procedurally generated silverfish blocks
     *      o deadly spawner
     *      o floor trap
     *      o tower dispenser
     *      ? water trap - actual impl TBD
     *      + ceiling trap
     *      ? wall trap
     *      + mini spawner
     *  o items
     *      o feature tester
     *      o event
     *  o entities
     *      ? mimic
     *      ? dispenser fish hook
     *      + mini mobs
     *  o vein world gen
     *      o silverfish
     *      o lava
     *      o water
     *      o sand
     *      o vanilla vein disables
     *      o vanilla vein replacements
     *      o user-defined veins
     *      ? new vein gen styles
     *  o dungeon world gen
     *      o spawner
     *      ? tower
     *      ? other special dungeon types
     *      o vanilla dungeon disable
     *  o chest world gen
     *      o default
     *      o valuable
     *      o trapped (default disabled)
     *      o tnt floor trap
     *      o infested
     *      o surprise
     *      o mimic
     *      + cave-in (via surprise or combo w/ ceiling trap)
     *      ? mimic 2.0 (custom entity)
     *      ? random cake from cake item tag
     *  o spawner world gen
     *      o default
     *      o stream
     *      o swarm
     *      o brutal
     *      o silverfish nest
     *      o dungeon-only version
     *      ? spider (combo)
     *      ? undead (combo)
     *      ? creeper (combo)
     *      ? fire immunity (combo)
     *  o tower world gen
     *      o default
     *      o fire
     *      o potion
     *      o gatling
     *      o fireball
     *      ? splash potion
     *      ? fish hook (combo, custom entity)
     *      ? splash poison (combo)
     *      ? splash harm (combo)
     *      ? lightning (combo)
     *      ? allow towers to generate on ceilings and/or walls
     *  o floor trap world gen
     *      o tnt
     *      o tnt mob
     *      o potion
     *      + fire (from pre-1.12.2 version)
     *  + water trap world gen
     *      + vortex
     *      ? need more than just one!
     *  + ceiling trap world gen
     *      + cave-in
     *      + lava
     *      ? more would be nice
     *  ? combo feature world gen
     *      ? spider spawner & splash poison dispenser
     *      ? undead spawner & splash harm dispenser
     *      ? any spawner & fish hook dispenser
     *      ? any floor trap & fish hook dispenser
     *      ? fire immune spawner & fireball dispenser
     *      ? creeper spawner & lightning dispenser
     *  ? new monsters - maybe these belong in a different mod?
     *      ? water monsters
     *      ? lava monsters
     *  ? wall trap world gen
     *      ? arrow traps
     *
     * Possible future additions:
     *  - option to allow floor traps to trigger vs creative mode players, and vice-versa for other traps
     *  - modify vanilla structures - if possible
     *  - add chance to fail replacing blocks in config (notably per silverfish replaceable block and per vein)
     *  - support for custom potions in towers/floor traps/events
     *  - allow vanilla dispensers to fire the custom fish hook entity when activating a fishing rod
     */
    
    /** The mod id and namespace used by this mod. */
    public static final String MOD_ID = "deadlyworld";
    
    /** The logger used by this mod. */
    public static final Logger LOG = LogManager.getLogger( MOD_ID );
    
    /** Packet handler instance */
    public PacketHandler packetHandler = new PacketHandler();
    
    
    public DeadlyWorld() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        DWDamageSources.init();
        this.packetHandler.registerMessages();
        
        MinecraftForge.EVENT_BUS.register( new BiomeEvents() );

        eventBus.addListener(DWEntities::createAttributes);
        
        DWBlocks.REGISTRY.register( eventBus );
        DWItems.REGISTRY.register( eventBus );
        DWEntities.REGISTRY.register( eventBus );
        DWTileEntities.REGISTRY.register( eventBus );
        DWFeatures.REGISTRY.register( eventBus );
        
        Config.preInitialize();
    }
    
    /** @return A ResourceLocation with the mod's namespace. */
    public static ResourceLocation resourceLoc( String path ) { return new ResourceLocation( MOD_ID, path ); }
    
    /** @return Returns a Forge registry entry as a string, or "null" if it is null. */
    public static String toString( @Nullable ForgeRegistryEntry<?> regEntry ) { return regEntry == null ? "null" : toString( regEntry.getRegistryName() ); }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }
}