package fathertoast.deadlyworld.common.event;

import fathertoast.deadlyworld.common.feature.DWConfiguredFeatures;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BiomeEvents {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getName() == null) return;

        BiomeGenerationSettingsBuilder generationSettings = event.getGeneration();

        for ( ConfiguredFeature<NoFeatureConfig, ?> feature : DWConfiguredFeatures.SIMPLE_FEATURES ) {
            generationSettings.addFeature( GenerationStage.Decoration.UNDERGROUND_DECORATION, feature );
        }
    }
}
