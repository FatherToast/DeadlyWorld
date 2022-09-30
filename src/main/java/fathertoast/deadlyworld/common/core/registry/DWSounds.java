package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DWSounds {

    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DeadlyWorld.MOD_ID);


    public static final RegistryObject<SoundEvent> SEWER_BIOME_LOOP = register("sewer_biome_loop");


    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> new SoundEvent(DeadlyWorld.resourceLoc(name)));
    }
}
