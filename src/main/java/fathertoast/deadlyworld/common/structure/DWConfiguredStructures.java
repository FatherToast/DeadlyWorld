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

    public static StructureFeature<?, ?> SEWER_DUNGEON = DWStructures.SEWER_DUNGEON.get().configured(IFeatureConfig.NONE);;

    public static void register() {
        register("configured_sewer_dungeon", SEWER_DUNGEON);
    }

    private static void register(String name, StructureFeature<?, ?> structureFeature) {
        Registry<StructureFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE;
        Registry.register(registry, new ResourceLocation(DeadlyWorld.MOD_ID, name), structureFeature);
        /* Ok so, this part may be hard to grasp but basically, just add your structure to this to
        * prevent any sort of crash or issue with other mod's custom ChunkGenerators. If they use
        * FlatGenerationSettings.STRUCTURE_FEATURES in it and you don't add your structure to it, the game
        * could crash later when you attempt to add the StructureSeparationSettings to the dimension.
        *
        * (It would also crash with superflat worldtype if you omit the below line
        * and attempt to add the structure's StructureSeparationSettings to the world
         */
        FlatGenerationSettings.STRUCTURE_FEATURES.put(structureFeature.feature, structureFeature);
    }
}
