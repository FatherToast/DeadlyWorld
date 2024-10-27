package fathertoast.deadlyworld.common.core.registry;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.config.levelgen.*;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class DWFieldProviders {
    public static final DeferredRegister<HeightProviderType<?>> HEIGHT_REGISTRY = DeferredRegister.create( BuiltInRegistries.HEIGHT_PROVIDER_TYPE.key(), DeadlyWorld.MOD_ID );
    public static final DeferredRegister<IntProviderType<?>> INT_REGISTRY = DeferredRegister.create( BuiltInRegistries.INT_PROVIDER_TYPE.key(), DeadlyWorld.MOD_ID );
    public static final DeferredRegister<FloatProviderType<?>> FLOAT_REGISTRY = DeferredRegister.create( BuiltInRegistries.FLOAT_PROVIDER_TYPE.key(), DeadlyWorld.MOD_ID );
    
    public static final RegistryObject<HeightProviderType<ConfigHeightProvider>> HEIGHT = registerHeightProvider(
            "config_uniform_height", ConfigHeightProvider.CODEC );
    
    public static final RegistryObject<IntProviderType<ConfigConstantIntProvider>> INT_CONSTANT = registerIntProvider(
            "config_constant_int", ConfigConstantIntProvider.CODEC );
    public static final RegistryObject<IntProviderType<ConfigUniformIntProvider>> INT_UNIFORM = registerIntProvider(
            "config_uniform_int", ConfigUniformIntProvider.CODEC );
    
    public static final RegistryObject<FloatProviderType<ConfigConstantFloatProvider>> FLOAT_CONSTANT = registerFloatProvider(
            "config_constant_float", ConfigConstantFloatProvider.CODEC );
    public static final RegistryObject<FloatProviderType<ConfigUniformFloatProvider>> FLOAT_UNIFORM = registerFloatProvider(
            "config_uniform_float", ConfigUniformFloatProvider.CODEC );
    
    public static void register( IEventBus eventBus ) {
        HEIGHT_REGISTRY.register( eventBus );
        INT_REGISTRY.register( eventBus );
        FLOAT_REGISTRY.register( eventBus );
    }
    
    private static <P extends HeightProvider> RegistryObject<HeightProviderType<P>> registerHeightProvider( String name, Codec<P> codec ) {
        return HEIGHT_REGISTRY.register( name, () -> () -> codec );
    }
    
    private static <P extends IntProvider> RegistryObject<IntProviderType<P>> registerIntProvider( String name, Codec<P> codec ) {
        return INT_REGISTRY.register( name, () -> () -> codec );
    }
    
    private static <P extends FloatProvider> RegistryObject<FloatProviderType<P>> registerFloatProvider( String name, Codec<P> codec ) {
        return FLOAT_REGISTRY.register( name, () -> () -> codec );
    }
}