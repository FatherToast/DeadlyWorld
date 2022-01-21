package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.DeadlyTrapBlock;
import fathertoast.deadlyworld.common.block.MiniSpawnerBlock;
import fathertoast.deadlyworld.common.block.StormDrainBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DWBlocks {
    /** The deferred register for this mod's blocks. */
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCKS, DeadlyWorld.MOD_ID );

    private static final List<RegistryObject<DeadlySpawnerBlock>> SPAWNERS;

    public static final RegistryObject<Block> STORM_DRAIN = registerBlock("storm_drain", StormDrainBlock::new, ItemGroup.TAB_MISC);
    public static final RegistryObject<Block> MINI_SPAWNER = registerBlock("mini_spawner", MiniSpawnerBlock::new, ItemGroup.TAB_MISC);


    static {
        final ArrayList<RegistryObject<DeadlySpawnerBlock>> spawners = new ArrayList<>( SpawnerType.values().length );
        for( SpawnerType type : SpawnerType.values() ) { spawners.add( type.ordinal(), registerSpawner( type ) ); }
        SPAWNERS = Collections.unmodifiableList( spawners );
    }
    
    /** @return The block registry object for a particular spawner type. */
    public static RegistryObject<DeadlySpawnerBlock> spawner( SpawnerType type ) {
        return SPAWNERS.get( type.ordinal() );
    }

    /** @return Creates an array of all spawner blocks and returns it. */
    public static Block[] spawnerBlocks() { return blockArray( SPAWNERS ); }

    /** @return Creates an array of all floor trap blocks and returns it. */
    public static Block[] floorTrapBlocks() { return new Block[0]; }

    /** @return Creates a new array referencing all the blocks represented by a list of block registry objects. */
    private static <T extends Block> Block[] blockArray( List<RegistryObject<T>> blockRegObjects ) {
        final Block[] blocks = new Block[blockRegObjects.size()];
        for( int i = 0; i < blocks.length; i++ ) { blocks[i] = blockRegObjects.get( i ).get(); }
        return blocks;
    }

    /**
     * Registers a block and a simple BlockItem for it.
     */
    private static <T extends Block> RegistryObject<T> registerBlock( String name, Supplier<T> blockSupplier, ItemGroup itemGroup ) {
        RegistryObject<T> blockRegObject = REGISTRY.register( name, blockSupplier );
        DWItems.REGISTRY.register( name, () -> new BlockItem( blockRegObject.get(), new Item.Properties().tab( itemGroup ) ) );
        return blockRegObject;
    }

    /**
     * Registers a block without any BlockItem.
     */
    private static <T extends Block> RegistryObject<T> registerBlockNoItem( String name, Supplier<T> blockSupplier ) {
        return REGISTRY.register( name, blockSupplier );
    }

    private static RegistryObject<DeadlySpawnerBlock> registerSpawner( SpawnerType spawnerType ) {
        String regName = spawnerType.getSerializedName() + "_deadly_spawner";
        RegistryObject<DeadlySpawnerBlock> blockRegObject = REGISTRY.register( regName, () -> new DeadlySpawnerBlock( spawnerType ) );
        DWItems.registerBlockItem( regName, blockRegObject, new Item.Properties().tab( ItemGroup.TAB_DECORATIONS ) );
        return blockRegObject;
    }
}