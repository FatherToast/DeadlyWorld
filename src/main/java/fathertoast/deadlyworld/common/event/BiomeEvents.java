package fathertoast.deadlyworld.common.event;

import fathertoast.deadlyworld.common.feature.DWConfiguredFeatures;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BiomeEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getName() == null) return;

        BiomeGenerationSettingsBuilder generationSettings = event.getGeneration();

        generationSettings.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, DWConfiguredFeatures.DEFAULT_SPAWNER);
        generationSettings.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, DWConfiguredFeatures.SWARM_SPAWNER);
        generationSettings.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, DWConfiguredFeatures.BRUTAL_SPAWNER);
        generationSettings.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, DWConfiguredFeatures.NEST_SPAWNER);
        generationSettings.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, DWConfiguredFeatures.STREAM_SPAWNER);
        generationSettings.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, DWConfiguredFeatures.MINI_SPAWNER);
    }
}
