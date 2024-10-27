package fathertoast.deadlyworld.common.core.registry;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class DWPlacementTypes {
    public static final DeferredRegister<PlacementModifierType<?>> REGISTRY = DeferredRegister.create( BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.key(), DeadlyWorld.MOD_ID );
    
    //public static final PlacementModifierType<?> LONE_SPAWNER = register( "lone_spawner", LoneSpawnerFeature::new );
    
    public static void initialize() { }
    
    private static <P extends PlacementModifier> RegistryObject<PlacementModifierType<P>> register( String name, Codec<P> codec ) {
        return REGISTRY.register( name, () -> () -> codec );
    }
}