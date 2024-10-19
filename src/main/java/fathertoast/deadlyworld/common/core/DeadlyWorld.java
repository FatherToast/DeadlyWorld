package fathertoast.deadlyworld.common.core;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
     *      + F3+B bounding box renderer for tile entities
     *  o blocks
     *      o configurable physical properties
     *      o procedurally generated silverfish blocks
     *      - deadly spawner
     *      - mini spawner
     *      o floor trap
     *      o tower dispenser
     *      ? water trap - actual impl TBD
     *      + ceiling trap
     *      ? wall trap
     *      ? fast flowing lava
     *      ? cake
     *  o items
     *      - spawn eggs
     *      o feature tester
     *      o event
     *  o entities
     *      - configurable base attributes & stats
     *      - mini creeper
     *      - mini zombie
     *      - mini skeleton
     *          - mini arrow
     *      - mini spider
     *      - micro ghast
     *          - micro fireball
     *      o mimic chest
     *      + mimic spawner
     *      + mimic cake
     *      ? dispenser fish hook
     *      ? water monsters
     *      ? lava monsters
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
     *      o mini
     *      o tower
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
     *      ? random cake from cake item tag
     *  o spawner world gen
     *      o default
     *      o stream
     *      o swarm
     *      o brutal
     *      o silverfish nest
     *      o mimic
     *      o mini
     *      o dungeon-only version
     *      ? hanging from chain version (in large caves or perhaps elsewhere with high ceilings)
     *  o tower world gen
     *      o arrow
     *      o fire arrow
     *      o gatling arrow
     *      o potion
     *      o fireball
     *      + mini
     *      ? allow towers to generate on ceilings and/or walls
     *  ? combo world gen
     *      ? spawner + fish hook tower
     *      ? spider spawner + splash poison tower
     *      ? undead spawner + splash harm tower
     *      ? creeper spawner + lightning tower
     *      ? creeper spawner + lightning floor trap
     *      ? fire immune spawner + fireball tower
     *      ? fire immune spawner + fire floor trap
     *      ? chicken spawner + egg tower
     *  o floor trap world gen
     *      o tnt
     *      o tnt mob
     *      o potion
     *      o fire (from pre-1.12.2 version)
     *      + chicken
     *      + unique/boss mob
     *      ? ambush
     *      ? blackout ambush
     *      ? pit
     *  ? water trap world gen
     *      + vortex
     *      ? need more than just one!
     *  + ceiling trap world gen
     *      + cave-in
     *      + lava
     *      ? water
     *      ? anvil
     *      ? more would be nice
     *  ? combo feature world gen
     *      + spider spawner & splash poison dispenser
     *      + undead spawner & splash harm dispenser
     *      + any spawner & fish hook dispenser
     *      + any floor trap & fish hook dispenser
     *      + fire immune spawner & fireball dispenser
     *      + creeper spawner & lightning dispenser
     *  ? wall trap world gen
     *      + arrow
     *      + potion
     *      + lava
     *      ? water
     *      ? spike
     *      ? uhh what else?
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
    
    //    /** Packet handler instance */
    //    public PacketHandler packetHandler = new PacketHandler();
    
    
    public DeadlyWorld() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        //        DWDamageSources.init();
        //        packetHandler.registerMessages();
        
        eventBus.addListener( DWEntities::createAttributes );
        eventBus.addListener( this::onCommonSetup );
        
        //        MinecraftForge.EVENT_BUS.addListener( DWStructures::addDimensionalSpacing );
        
        DWBlocks.REGISTRY.register( eventBus );
        DWItems.REGISTRY.register( eventBus );
        DWCreativeModeTabs.REGISTRY.register( eventBus );
        DWEntities.REGISTRY.register( eventBus );
        DWBlockEntities.REGISTRY.register( eventBus );
        //        DWFeatures.REGISTRY.register( eventBus );
        //        DWBiomes.REGISTRY.register( eventBus );
        //        DWSounds.REGISTRY.register( eventBus );
        //        DWStructures.REGISTRY.register( eventBus );
        
        Config.preInitialize();
    }
    
    public void onCommonSetup( FMLCommonSetupEvent event ) {
        //        event.enqueueWork( () -> {
        //            DWStructures.setupStructures();
        //            DWConfiguredStructures.register();
        //            DWStructureProcessors.register();
        //        } );
    }
    
    /** @return A ResourceLocation with the mod's namespace. */
    public static ResourceLocation resourceLoc( String path ) { return new ResourceLocation( MOD_ID, path ); }
    
    public static String logPrefix( Class<?> clazz ) {
        return "[" + MOD_ID + "/" + clazz.getSimpleName() + "] ";
    }
    
    //** @return Returns a Forge registry entry as a string, or "null" if it is null. */
    //public static String toString( @Nullable ForgeRegistryEntry<?> regEntry ) { return regEntry == null ? "null" : toString( regEntry.getRegistryName() ); }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }
}