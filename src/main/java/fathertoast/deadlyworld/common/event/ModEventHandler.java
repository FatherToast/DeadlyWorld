package fathertoast.deadlyworld.common.event;

import fathertoast.deadlyworld.common.block.infested.InfestedBlockAutoGen;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Contains and automatically registers all needed mod events.
 */
@Mod.EventBusSubscriber( modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public final class ModEventHandler {
    
    /**
     * Called after registry events, but before the client- and server-specific setup events.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onCommonSetup( FMLCommonSetupEvent event ) {
        //Config.initialize();
        //DWConfiguredFeatures.register();
    }
    
    /**
     * This event is called to allow each entity type to register its own spawn predicate.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onRegisterSpawnPlacement( SpawnPlacementRegisterEvent event ) {
        DWEntities.registerMonsterSpawnPlacements( event );
    }
    
    /**
     * This event is called for reasons.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onAddPackFinders( AddPackFindersEvent event ) {
        if( event.getPackType() == PackType.SERVER_DATA ) {
            InfestedBlockAutoGen.injectServerData();
        }
    }
}