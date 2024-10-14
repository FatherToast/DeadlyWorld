package fathertoast.deadlyworld.common.event;


import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.MiniArrow;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
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
    static void onServerStarting( ServerStartingEvent event ) {
        Config.initializeDynamic( event.getServer() );
    }
    
    /**
     * Called during LivingEntity#actuallyHurt after all damage calculations, right before damage is applied.
     *
     * @param event The event data.
     */
    @SubscribeEvent( priority = EventPriority.NORMAL )
    static void onLivingDamage( LivingDamageEvent event ) {
        // Too lazy to override the on hit method for the mini arrow entity, setting damage to 1.0 here instead
        // Note, this kinda makes them ignore armor/enchant damage reduction, but still consumes durability
        if( event.getAmount() > 0.0F && event.getSource().getDirectEntity() instanceof MiniArrow ) {
            event.setAmount( 1.0F );
        }
    }
}