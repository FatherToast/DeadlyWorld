package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DWItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create( ForgeRegistries.ITEMS, DeadlyWorld.MOD_ID );
    
    //public static final RegistryObject<DeviceBlueprintItem> DEVICE_BLUEPRINT = register( "device_blueprint", DeviceBlueprintItem::new );
    
    // Spawn eggs
    //    public static final RegistryObject<ForgeSpawnEggItem> MIMIC_SPAWN_EGG = registerSpawnEgg(
    //            DWEntities.MIMIC, 0xAB792D, 0x443C30 );
    public static final RegistryObject<ForgeSpawnEggItem> MINI_CREEPER_SPAWN_EGG = registerSpawnEgg(
            DWEntities.MINI_CREEPER, 0xDA70B, 0x000000 );
    public static final RegistryObject<ForgeSpawnEggItem> MINI_ZOMBIE_SPAWN_EGG = registerSpawnEgg(
            DWEntities.MINI_ZOMBIE, 0xAFAF, 0x799C65 );
    public static final RegistryObject<ForgeSpawnEggItem> MINI_SKELETON_SPAWN_EGG = registerSpawnEgg(
            DWEntities.MINI_SKELETON, 0xC1C1C1, 0x494949 );
    public static final RegistryObject<ForgeSpawnEggItem> MINI_SPIDER_SPAWN_EGG = registerSpawnEgg(
            DWEntities.MINI_SPIDER, 0x342D27, 0xA80E0E );
    public static final RegistryObject<ForgeSpawnEggItem> MICRO_GHAST_SPAWN_EGG = registerSpawnEgg(
            DWEntities.MICRO_GHAST, 0xF9F9F9, 0xBCBCBC );
    
    /** Registers an item. */
    private static <T extends Item> RegistryObject<T> register( String name, Supplier<T> supplier ) {
        return REGISTRY.register( name, supplier );
    }
    
    /** Registers a simple spawn egg item for an entity type. */
    private static <T extends Mob> RegistryObject<ForgeSpawnEggItem> registerSpawnEgg(
            RegistryObject<EntityType<T>> entityType, int eggBaseColor, int eggSpotsColor ) {
        final String name = entityType.getId().getPath() + "_spawn_egg";
        return register( name, () ->
                new ForgeSpawnEggItem( entityType, eggBaseColor, eggSpotsColor, new Item.Properties() )
        );
    }
    
    /** Registers a simple item for a block. */
    static <T extends Block> void registerBlockItem( String name, RegistryObject<T> block ) {
        registerBlockItem( name, block, new Item.Properties() );
    }
    
    /** Registers a simple item with custom properties for a block. */
    static <T extends Block> void registerBlockItem( String name, RegistryObject<T> block, Item.Properties properties ) {
        register( name, () -> new BlockItem( block.get(), properties ) );
    }
}