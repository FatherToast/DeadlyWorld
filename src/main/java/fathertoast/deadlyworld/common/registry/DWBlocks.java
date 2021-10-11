package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DWBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create( ForgeRegistries.BLOCKS, DeadlyWorld.MOD_ID );
    
    public static final RegistryObject<DeadlySpawnerBlock> LONE_DEADLY_SPAWNER = registerSpawner( SpawnerType.LONE );
    public static final RegistryObject<DeadlySpawnerBlock> BRUTAL_DEADLY_SPAWNER = registerSpawner( SpawnerType.BRUTAL );
    public static final RegistryObject<DeadlySpawnerBlock> NEST_DEADLY_SPAWNER = registerSpawner( SpawnerType.NEST );
    public static final RegistryObject<DeadlySpawnerBlock> STREAM_DEADLY_SPAWNER = registerSpawner( SpawnerType.STREAM );
    public static final RegistryObject<DeadlySpawnerBlock> SWARM_DEADLY_SPAWNER = registerSpawner( SpawnerType.SWARM );



    /**
     * Registers a block and a simple BlockItem for it.
     */
    private static <T extends Block> RegistryObject<T> registerBlock( String name, Supplier<T> blockSupplier, ItemGroup itemGroup ) {
        RegistryObject<T> blockRegObject = BLOCKS.register( name, blockSupplier );
        DWItems.ITEMS.register( name, ( ) -> new BlockItem( blockRegObject.get( ), new Item.Properties( ).tab( itemGroup )));
        return blockRegObject;
    }

    /**
     * Registers a block without any BlockItem.
     */
    private static <T extends Block> RegistryObject<T> registerBlockNoItem( String name, Supplier<T> blockSupplier ) {
        return BLOCKS.register( name, blockSupplier );
    }

    private static RegistryObject<DeadlySpawnerBlock> registerSpawner( SpawnerType spawnerType ) {
        String regName = spawnerType.getSerializedName() + "_deadly_spawner";
        RegistryObject<DeadlySpawnerBlock> blockRegObject = BLOCKS.register(  regName, ( ) -> new DeadlySpawnerBlock( spawnerType ) );
        DWItems.registerBlockItem( regName, blockRegObject, new Item.Properties( ).tab( ItemGroup.TAB_DECORATIONS ));
        return blockRegObject;
    }
}