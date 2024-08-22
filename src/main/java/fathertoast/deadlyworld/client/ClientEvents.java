package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.common.world.dimension.DWDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.ControlsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;

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
