package fathertoast.deadlyworld.client;

import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {//TODO
    
    /** Makes the fog T H I C C in the sewer dimension. */
    @SubscribeEvent( priority = EventPriority.HIGH )
    public void onRenderFog( ViewportEvent.RenderFog event ) {
        //        if (Minecraft.getInstance().level != null) {
        //            if (Minecraft.getInstance().level.dimension().equals(DWDimensions.SEWERS_WORLD)) {
        //                event.setDensity(0.00F);
        //                event.setCanceled(true);
        //            }
        //        }
    }
}