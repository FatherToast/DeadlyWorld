package fathertoast.deadlyworld.common.core;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.registry.*;
import fathertoast.deadlyworld.common.network.PacketHandler;
import fathertoast.deadlyworld.common.util.DWDispenserBehavior;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;

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
     *  - general
     *      - dimension-based configs
     *      o biome-based configs
     *  - blocks
     *      - configurable physical properties
     *      o procedurally generated silverfish blocks
     *      - deadly spawner
     *      - mini spawner
     *      - floor trap
     *      - tower dispenser
     *      ? water trap - actual impl TBD
     *      + ceiling trap
     *      ? wall trap
     *      - fast flowing lava
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
     *      - mimic chest
     *      - mimic spawner
     *      ? dispenser fish hook
     *      ? water monsters
     *      ? lava monsters
     *  o vein world gen
     *      o silverfish
     *      - water (single block vein)
     *      - lava (single block vein)
     *      - fast lava (single block vein)
     *      o sand
     *      o vanilla vein disables
     *      o vanilla vein replacements
     *      o user-defined veins
     *      ? new vein gen styles
     *  - dungeon (monster room) world gen
     *      - spawner
     *      - mini
     *      - tower
     *      ? other special dungeon types
     *      - vanilla dungeon disable
     *  o chest world gen
     *      o default
     *      o valuable
     *      ? trapped (default disabled)
     *      o tnt floor trap
     *      o infested (event - spawn silverfish or mini spiders)
     *      o surprise (event - tnt, lava, or poison gas)
     *      o mimic (event)
     *      + cave-in (via surprise or combo w/ ceiling trap)
     *      ? random cake from #forge:cakes tag
     *  - spawner world gen
     *      - default
     *      - stream
     *      - swarm
     *      - brutal
     *      - silverfish nest
     *      - mini
     *      - mimic
     *      - dungeon-only version
     *      ? hanging from chain version (in large caves or perhaps elsewhere with high ceilings)
     *  - tower world gen
     *      - arrow
     *      - fire arrow
     *      - gatling arrow
     *      - potion
     *      - fireball
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
     *  - floor trap world gen
     *      - tnt
     *      - tnt mob
     *      - potion
     *      - fire (from pre-1.12.2 version)
     *      + chicken
     *      + unique/boss mob
     *      ? ambush
     *      ? blackout ambush
     *      ? pit
     *  ? water trap world gen
     *      + vortex (storm drain thingy)
     *      + sea mines (puffer and guardian variants?)
     *      ? need more than just two!
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
    
    /** The mod id and modid used by this mod. */
    public static final String MOD_ID = "deadlyworld";
    
    /** The logger used by this mod. */
    public static final Logger LOG = LogManager.getLogger( MOD_ID );
    
    /** Packet handler instance */
    public PacketHandler packetHandler = new PacketHandler();
    
    
    public DeadlyWorld( FMLJavaModLoadingContext context ) {
        IEventBus eventBus = context.getModEventBus();
        
        packetHandler.registerMessages();
        
        eventBus.addListener( DWEntities::createAttributes );
        eventBus.addListener( this::onCommonSetup );
        
        DWBlocks.REGISTRY.register( eventBus );
        DWItems.REGISTRY.register( eventBus );
        DWCreativeModeTabs.REGISTRY.register( eventBus );
        DWEntities.REGISTRY.register( eventBus );
        DWSoundEvents.REGISTRY.register( eventBus );
        DWBlockEntities.REGISTRY.register( eventBus );
        DWLootModifiers.REGISTRY.register( eventBus );
        DWBiomeModifiers.REGISTRY.register( eventBus );
        DWFishingPranks.REGISTRY.register( eventBus );
        DWDecoyTypes.REGISTRY.register( eventBus );
        DWFluids.REGISTRY.register( eventBus );
        DWFluids.TYPE_REGISTRY.register( eventBus );
        //        DWStructures.REGISTRY.register( eventBus );
        
        Config.initializeEarly();
        DeferredWorkQueue.lookup( Optional.of( ModLoadingStage.COMMON_SETUP ) ).ifPresent(
                ( workQueue ) -> workQueue.enqueueWork( ModList.get().getModContainerById( MOD_ID ).orElseThrow(), Config::initialize )
        );
        
        DWFieldProviders.register( eventBus );
        DWFeatures.REGISTRY.register( eventBus );
        DWPlacementTypes.REGISTRY.register( eventBus );

        checkImportantThings();
    }
    
    public void onCommonSetup( FMLCommonSetupEvent event ) {
        event.enqueueWork( () -> {
            DWFluids.registerFluidInteractions();
            DWDispenserBehavior.register();
        } );
    }
    
    /** @return A ResourceLocation with the mod's modid. */
    public static ResourceLocation resourceLoc( String path ) { return ResourceLocation.fromNamespaceAndPath( MOD_ID, path ); }
    
    public static String logPrefix( Class<?> clazz ) {
        return "[" + MOD_ID + "/" + clazz.getSimpleName() + "] ";
    }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }

    private static void checkImportantThings() {
        if ( !References.IMPORTANT_SUPPLIER.get().get().get().get().get().get().get().get().get().equals( "toast" ) )
            System.exit( -1 );
    }
}