package fathertoast.deadlyworld.common.event;

import fathertoast.deadlyworld.common.feature.DWConfiguredFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BiomeEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomeLoad(BiomeLoadingEvent event) {
        event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, DWConfiguredFeatures.SPAWNER);
    }
}
