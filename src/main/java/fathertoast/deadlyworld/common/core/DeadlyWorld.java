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
public final class DeadlyWorld {
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
     *      - auto-generated infested blocks
     *          - prevent silverfish from hiding immediately after spawning
     *      - deadly spawner
     *      - mini spawner
     *      - inactive buried spawner
     *      - floor trap
     *      - tower dispenser
     *      - sea mine
     *      - static spike trap
     *      - mechanical spike trap
     *      ? water trap - actual impl TBD
     *      + ceiling trap
     *      ? wall trap
     *      - runny lava
     *      ? cake
     *  - items
     *      - spawn eggs
     *      - feature placer
     *      - mimic core
     *      - infested event (spawn silverfish or mini spiders)
     *      - surprise event (tnt, lava, runny lava, poison gas, or withering gas)
     *      - runny lava bucket
     *  - entities
     *      - configurable base attributes & stats
     *      - mini creeper
     *      - mini zombie
     *      - mini skeleton
     *          - mini arrow
     *      - mini spider
     *      - micro ghast
     *          - micro fireball
     *      - mimic chest
     *      + mini mimic chest
     *      - mimic spawner
     *      - mini mimic spawner
     *      ? dispenser fish hook
     *      ? water monsters
     *      ? lava monsters
     *  - vein world gen
     *      - buried block (config-defined single block veins - water, lava, and runny lava by default)
     *      - infested block
     *      - water
     *      - sand
     *  - dungeon (monster room) world gen
     *      - simple (spawner/tower)
     *      - mini
     *      - vanilla dungeon disable
     *      ? other special dungeon types
     *  - chest world gen
     *      - simple
     *      - valuable
     *      - tnt floor trap
     *      - infested (event)
     *      - surprise (event)
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
     *      - buried (via buried block veins)
     *      - dungeon (in vanilla dungeons)
     *      ? hanging from chain version (in large caves or perhaps elsewhere with high ceilings)
     *  - tower world gen
     *      - arrow
     *      - fire arrow
     *      - gatling arrow
     *      - potion arrow
     *      - fireball
     *      + splash potion
     *      ? mini
     *      ? allow towers to generate on ceilings and/or walls
     *  - floor trap world gen
     *      - tnt
     *      - tnt mob
     *      - potion
     *      - lava
     *      - fire (from pre-1.12.2 version)
     *      + chicken
     *      + unique/boss mob
     *      ? ambush
     *      ? blackout ambush
     *  + spike trap world gen
     *      + static
     *      + mechanical
     *      ? potion
     *  + pitfall trap world gen
     *      + spikes
     *      + lava
     *      + cobwebs
     *  - water trap world gen
     *      - sea mine (normal/puffer/guardian)
     *      + vortex (storm drain thingy)
     *      ? need more than just two!
     *  + ceiling trap world gen
     *      + cave-in
     *      + lava
     *      ? water
     *      ? anvil
     *      ? more would be nice
     *  ? wall trap world gen
     *      + arrow
     *      + potion
     *      + lava
     *      ? water
     *      ? spike
     *      ? uhh what else?
     *  ? combo feature world gen
     *      + spider spawner & splash poison dispenser
     *      + undead spawner & splash harm dispenser
     *      + any spawner & fish hook dispenser
     *      + any floor trap & fish hook dispenser
     *      + fire immune spawner & fireball dispenser
     *      + fire immune spawner & fire floor trap
     *      + creeper spawner & lightning dispenser
     *      + creeper spawner & lightning floor trap
     *      + chicken spawner & egg dispenser
     *
     * Possible future additions:
     *  - option to allow floor traps to trigger vs creative mode players, and vice-versa for other traps
     *  - modify vanilla structures - if possible
     *  - add chance to fail replacing blocks in config (notably per silverfish replaceable block and per vein)
     *  - support for custom potions in towers/floor traps/events
     *  - allow vanilla dispensers to fire the custom fish hook entity when activating a fishing rod
     */
    
    /** The mod ID used by this mod. */
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
                ( workQueue ) -> workQueue.enqueueWork( ModList.get().getModContainerById( MOD_ID ).orElseThrow(),
                        Config::initialize )
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
    public static ResourceLocation rl( String path ) { return ResourceLocation.fromNamespaceAndPath( MOD_ID, path ); }
    
    public static String logPrefix( Class<?> clazz ) {
        return "[" + MOD_ID + "/" + clazz.getSimpleName() + "] ";
    }
    
    /** @return Returns the resource location as a string, or "null" if it is null. */
    public static String toString( @Nullable ResourceLocation res ) { return res == null ? "null" : res.toString(); }
    
    private static void checkImportantThings() {
        if( !References.IMPORTANT_SUPPLIER.get().get().get().get().get().get().get().get().get().equals( "toast" ) )
            System.exit( -1 );
    }
}