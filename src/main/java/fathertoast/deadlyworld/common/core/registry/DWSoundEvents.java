package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DWSoundEvents {

    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create( ForgeRegistries.SOUND_EVENTS, DeadlyWorld.MOD_ID );

    public static final RegistryObject<SoundEvent> MIMIC_APPEAR = register( "entity.mimic.appear" );

    public static final RegistryObject<SoundEvent> CHEST_MIMIC_HURT = register( "entity.chest_mimic.hurt" );
    public static final RegistryObject<SoundEvent> CHEST_MIMIC_DEATH = register( "entity.chest_mimic.death" );

    public static final RegistryObject<SoundEvent> SPAWNER_MIMIC_STEP = register( "entity.spawner_mimic.step" );
    public static final RegistryObject<SoundEvent> SPAWNER_MIMIC_HURT = register( "entity.spawner_mimic.hurt" );
    public static final RegistryObject<SoundEvent> SPAWNER_MIMIC_DEATH = register( "entity.spawner_mimic.death" );


    private static RegistryObject<SoundEvent> register( String name ) {
        return REGISTRY.register( name, () -> SoundEvent.createVariableRangeEvent( DeadlyWorld.resourceLoc( name ) ) );
    }
}
