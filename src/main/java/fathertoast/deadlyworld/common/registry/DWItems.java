package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DWItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create( ForgeRegistries.ITEMS, DeadlyWorld.MOD_ID );


    protected static <T extends Block> RegistryObject<BlockItem> registerBlockItem( String name, RegistryObject<T> blockRegistryObject ) {
        return ITEMS.register( name, ( ) -> new BlockItem(blockRegistryObject.get( ), new Item.Properties() ) );
    }

    protected static <T extends Block> RegistryObject<BlockItem> registerBlockItem( String name, RegistryObject<T> blockRegistryObject, Item.Properties properties ) {
        return ITEMS.register( name, ( ) -> new BlockItem(blockRegistryObject.get( ), properties) );
    }
}
