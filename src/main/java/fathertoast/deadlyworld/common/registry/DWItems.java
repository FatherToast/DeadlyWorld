package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.block.MiniSpawnerBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.tile.spawner.MiniSpawnerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DWItems {
    
    private static final ItemGroup DEFAULT_TAB = ItemGroup.TAB_DECORATIONS;
    
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create( ForgeRegistries.ITEMS, DeadlyWorld.MOD_ID );

    // Spawn eggs
    public static final RegistryObject<ForgeSpawnEggItem> MIMIC_SPAWN_EGG = registerSpawnEgg(
            DWEntities.MIMIC, 0xAB792D, 0x443C30, false );
    public static final RegistryObject<ForgeSpawnEggItem> MINI_CREEPER_SPAWN_EGG = registerSpawnEgg(
            DWEntities.MINI_CREEPER, 0xDA70B, 0x000000, true );
    public static final RegistryObject<ForgeSpawnEggItem> MINI_ZOMBIE_SPAWN_EGG = registerSpawnEgg(
            DWEntities.MINI_ZOMBIE, 0xAFAF, 0x799C65, true );
    public static final RegistryObject<ForgeSpawnEggItem> MINI_SKELETON_SPAWN_EGG = registerSpawnEgg(
            DWEntities.MINI_SKELETON, 0xC1C1C1, 0x494949, true );

    protected static <T extends Entity> RegistryObject<ForgeSpawnEggItem> registerSpawnEgg( RegistryObject<EntityType<T>> typeRegistryObject, int backgroundColor, int highlightColor, boolean mini ) {
        String name = typeRegistryObject.getId().getPath() + "_spawn_egg";
        RegistryObject<ForgeSpawnEggItem> spawnEgg = REGISTRY.register( name, () -> new ForgeSpawnEggItem( typeRegistryObject, backgroundColor, highlightColor, new Item.Properties().tab( ItemGroup.TAB_MISC) ) );

        if ( mini ) {
            MiniSpawnerBlock.ACCEPTED_EGGS.add( spawnEgg );
        }
        return spawnEgg;
    }

    protected static <T extends Block> RegistryObject<BlockItem> registerBlockItem( String name, RegistryObject<T> blockRegistryObject ) {
        return registerBlockItem( name, blockRegistryObject, DEFAULT_TAB );
    }
    
    protected static <T extends Block> RegistryObject<BlockItem> registerBlockItem( String name, RegistryObject<T> blockRegistryObject, ItemGroup tab ) {
        return registerBlockItem( name, blockRegistryObject, new Item.Properties().tab( tab ) );
    }
    
    protected static <T extends Block> RegistryObject<BlockItem> registerBlockItem( String name, RegistryObject<T> blockRegistryObject, Item.Properties properties ) {
        return REGISTRY.register( name, () -> new BlockItem( blockRegistryObject.get(), properties ) );
    }
}