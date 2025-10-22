package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class DWSoundEvents {

    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create( ForgeRegistries.SOUND_EVENTS, DeadlyWorld.MOD_ID );


    public static final RegistryObject<SoundEvent> TOWER_DISPENSER_SHOOT = register( "block.tower_dispenser.shoot" );

    public static final RegistryObject<SoundEvent> MIMIC_APPEAR = register( "entity.mimic.appear" );

    public static final RegistryObject<SoundEvent> CHEST_MIMIC_HURT = register( "entity.chest_mimic.hurt" );
    public static final RegistryObject<SoundEvent> CHEST_MIMIC_DEATH = register( "entity.chest_mimic.death" );

    public static final RegistryObject<SoundEvent> SPAWNER_MIMIC_STEP = register( "entity.spawner_mimic.step" );
    public static final RegistryObject<SoundEvent> SPAWNER_MIMIC_HURT = register( "entity.spawner_mimic.hurt" );
    public static final RegistryObject<SoundEvent> SPAWNER_MIMIC_DEATH = register( "entity.spawner_mimic.death" );

    public static final RegistryObject<SoundEvent> MINI_CHEST_OPEN = register( "block.mini_chest.open" );
    public static final RegistryObject<SoundEvent> MINI_CHEST_CLOSE = register( "block.mini_chest.close" );


    private static RegistryObject<SoundEvent> register( String name ) {
        return REGISTRY.register( name, () -> SoundEvent.createVariableRangeEvent( DeadlyWorld.rl( name ) ) );
    }
}
