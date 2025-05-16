package fathertoast.deadlyworld.common.core.registry;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.loot.glm.SimpleAddLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DWLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTRY = DeferredRegister.create( ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DeadlyWorld.MOD_ID );


    public static final RegistryObject<Codec<SimpleAddLootModifier>> SIMPLE_ADD = register( "simple_add", SimpleAddLootModifier.CODEC );


    private static <T extends Codec<? extends IGlobalLootModifier>> RegistryObject<T> register( String name, Supplier<T> supplier ) {
        return REGISTRY.register( name, supplier );
    }
}
