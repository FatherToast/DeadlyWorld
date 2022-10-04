package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.common.world.dimension.DWDimensions;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {

    /** Makes the fog THICC in the sewer dimension. */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderFog(EntityViewRenderEvent.RenderFogEvent.FogDensity event) {
        if (Minecraft.getInstance().level != null) {
            if (Minecraft.getInstance().level.dimension().equals(DWDimensions.SEWERS_WORLD)) {
                event.setDensity(0.00F);
                event.setCanceled(true);
            }
        }
    }
}
