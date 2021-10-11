package fathertoast.deadlyworld.common.event;


import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.Config;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

/**
 * Contains and automatically registers all needed forge events.
 */
@SuppressWarnings( "unused" )
@Mod.EventBusSubscriber( modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE )
public final class GameEventHandler {
    /**
     * Called after FMLServerAboutToStartEvent and before FMLServerStartedEvent.
     * This event allows for customizations of the server.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onServerStarting( FMLServerStartingEvent event ) {
        Config.initializeDynamic( event.getServer() );
    }
    
    /**
     * Called for the server at the start and end of each tick.
     * <p>
     * It is usually wise to check the phase (start/end) before doing anything.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onServerTick( TickEvent.ServerTickEvent event ) {
        //AIManager.onServerTick( event );
    }
    
    /**
     * Called when any entity is spawned in the world, including by chunk loading and dimension transition.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.LOW )
    public static void onJoinWorld( EntityJoinWorldEvent event ) {
        //AIManager.onJoinWorld( event );
    }
    
    /**
     * Called when a living entity dies for any reason.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    public static void onLivingDeath( LivingDeathEvent event ) {
        //AIManager.onLivingDeath( event );
    }
}