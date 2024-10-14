package fathertoast.deadlyworld.common.event;


import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Contains and automatically registers all needed forge events.
 */
@Mod.EventBusSubscriber( modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public final class GameEventHandler {
    /**
     * Called after ServerAboutToStartEvent and before ServerStartedEvent.
     * This event allows for customizations of the server.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onServerStarting( ServerStartingEvent event ) {
        Config.initializeDynamic( event.getServer() );
    }
}