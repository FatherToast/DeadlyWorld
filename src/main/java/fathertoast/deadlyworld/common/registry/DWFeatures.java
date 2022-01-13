package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.feature.features.SpawnerFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DWFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, DeadlyWorld.MOD_ID);


    public static final RegistryObject<Feature<NoFeatureConfig>> SPAWNER = register("spawner", () -> new SpawnerFeature(NoFeatureConfig.CODEC, DWBlocks.LONE_DEADLY_SPAWNER));


    private static <FC extends IFeatureConfig, T extends Feature<FC>> RegistryObject<T> register(String name, Supplier<T> featureSupplier ) {
        return FEATURES.register(name, featureSupplier);
    }
}
