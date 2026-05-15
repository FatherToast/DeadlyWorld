package fathertoast.deadlyworld.common.core.registry;

import com.mojang.serialization.Codec;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class DWPlacementTypes {//TODO do we need this?
    public static final DeferredRegister<PlacementModifierType<?>> REGISTRY = DeferredRegister.create( BuiltInRegistries.PLACEMENT_MODIFIER_TYPE.key(), DeadlyWorld.MOD_ID );
    
    //public static final RegistryObject<PlacementModifierType<P>> RARITY_FILTER = register(...);
    
    private static <P extends PlacementModifier> RegistryObject<PlacementModifierType<P>> register( String name, Codec<P> codec ) {
        return REGISTRY.register( name, () -> () -> codec );
    }
}