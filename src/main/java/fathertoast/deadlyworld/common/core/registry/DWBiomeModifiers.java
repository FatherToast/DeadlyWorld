package fathertoast.deadlyworld.common.core.registry;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.biome.modifier.GlobalAddFeaturesModifier;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class DWBiomeModifiers {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> REGISTRY = DeferredRegister.create( ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, DeadlyWorld.MOD_ID );


    public static final RegistryObject<Codec<GlobalAddFeaturesModifier>> GLOBAL_ADD_FEATURES = register( "global_add_features", GlobalAddFeaturesModifier.codecForRegistry() );


    private static <T extends BiomeModifier> RegistryObject<Codec<T>> register( String name, Supplier<Codec<T>> supplier ) {
        return REGISTRY.register( name, supplier );
    }
}
