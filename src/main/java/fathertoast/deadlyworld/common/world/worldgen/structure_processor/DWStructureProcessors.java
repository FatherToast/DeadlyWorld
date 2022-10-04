package fathertoast.deadlyworld.common.world.worldgen.structure_processor;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.StructureProcessor;

public class DWStructureProcessors {

    public static IStructureProcessorType<CobwebDecoratorProcessor> COOL_PROCESSOR;


    public static void register() {
        COOL_PROCESSOR = register("cobweb_decorator", CobwebDecoratorProcessor.CODEC);
    }


    private static <P extends StructureProcessor> IStructureProcessorType<P> register(String name, Codec<P> codec) {
        Registry<IStructureProcessorType<?>> registry = Registry.STRUCTURE_PROCESSOR;
        return Registry.register(registry, new ResourceLocation(DeadlyWorld.MOD_ID, name), () -> codec);
    }
}
