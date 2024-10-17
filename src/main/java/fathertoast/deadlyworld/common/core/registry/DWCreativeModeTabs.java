package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DWCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create( Registries.CREATIVE_MODE_TAB, DeadlyWorld.MOD_ID );
    
    public static final CreativeTabRegObj MOD_TAB = register( "all", () -> CreativeModeTab.builder()
            .icon( () -> new ItemStack( Blocks.SPAWNER ) )
            .title( Component.translatable( "itemGroup." + DeadlyWorld.MOD_ID ) )
            .build() );
    
    
    private static CreativeTabRegObj register( @SuppressWarnings( "SameParameterValue" ) String name, Supplier<CreativeModeTab> supplier ) {
        return new CreativeTabRegObj( REGISTRY.register( name, supplier ), ResourceKey.create( Registries.CREATIVE_MODE_TAB, DeadlyWorld.resourceLoc( name ) ) );
    }
    
    public record CreativeTabRegObj(RegistryObject<CreativeModeTab> regObj, ResourceKey<CreativeModeTab> key) { }
}