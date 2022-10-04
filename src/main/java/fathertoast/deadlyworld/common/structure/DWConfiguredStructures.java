package fathertoast.deadlyworld.common.structure;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWStructures;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class DWConfiguredStructures {

    public static StructureFeature<?, ?> SEWER_DUNGEON;


    public static void register() {
        SEWER_DUNGEON = register("configured_sewer_dungeon", DWStructures.SEWER_DUNGEON.get().configured(IFeatureConfig.NONE));
    }


    private static <T extends StructureFeature<?, ?>> T register(String name, T structureFeature) {
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        T structureFeat = Registry.register(registry, new ResourceLocation(DeadlyWorld.MOD_ID, name), structureFeature);
        FlatGenerationSettings.STRUCTURE_FEATURES.put(structureFeat.feature, structureFeat);
        return structureFeat;
    }
}
