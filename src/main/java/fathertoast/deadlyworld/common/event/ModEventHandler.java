package fathertoast.deadlyworld.common.event;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.feature.DWConfiguredFeatures;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Contains and automatically registers all needed mod events.
 */
@SuppressWarnings( "unused" )
@Mod.EventBusSubscriber( modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public final class ModEventHandler {

    /**
     * Called after registry events, but before the client- and server-specific setup events.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void setup( FMLCommonSetupEvent event ) {
        Config.initialize();
        DWConfiguredFeatures.register();
        DWEntities.registerSpawnPlacements();
    }
}