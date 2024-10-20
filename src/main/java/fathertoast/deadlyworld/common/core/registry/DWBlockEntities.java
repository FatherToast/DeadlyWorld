package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.spawner.MiniSpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DWBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCK_ENTITY_TYPES, DeadlyWorld.MOD_ID );
    
    public static final RegistryObject<BlockEntityType<DeadlySpawnerBlockEntity>> DEADLY_SPAWNER = registerMultiple(
            "deadly_spawner", DeadlySpawnerBlockEntity::new, DWBlockEntities::getStandardSpawnerBlocks );
    public static final RegistryObject<BlockEntityType<MiniSpawnerBlockEntity>> MINI_SPAWNER = register(
            "mini_spawner", MiniSpawnerBlockEntity::new, DWBlocks.spawner( SpawnerType.MINI ) );
    
    //    public static final RegistryObject<BlockEntityType<FloorTrapBlockEntity>> FLOOR_TRAP = register(
    //            "floor_trap", FloorTrapBlockEntity::new, DWBlocks.FLOOR_TRAPS );
    
    //    public static final RegistryObject<BlockEntityType<TowerDispenserBlockEntity>> TOWER_DISPENSER = register(
    //            "tower_dispenser", TowerDispenserBlockEntity::new, DWBlocks.TOWER_DISPENSERS );
    
    //    public static final RegistryObject<BlockEntityType<StormDrainBlockEntity>> STORM_DRAIN = register( "storm_drain",
    //            () -> BlockEntityType.Builder.of( StormDrainBlockEntity::new, DWBlocks.STORM_DRAIN.get() ).build( null ) );
    
    /** Registers a block entity to a list of blocks. */
    private static <T extends BlockEntity, B extends Block> RegistryObject<BlockEntityType<T>> register(
            String name, BlockEntityType.BlockEntitySupplier<T> blockEntity, Supplier<B> block ) {
        // It doesn't like passing null into the build method, but the game passes a nullable into it...
        //noinspection ConstantConditions
        return register( name, () -> BlockEntityType.Builder
                .of( blockEntity, block.get() ).build( null ) );
    }
    
    /** Registers a block entity to a list of blocks. */
    private static <T extends BlockEntity, B extends Block> RegistryObject<BlockEntityType<T>> registerMultiple(
            String name, BlockEntityType.BlockEntitySupplier<T> blockEntity, Supplier<B[]> blocks ) {
        // It doesn't like passing null into the build method, but the game passes a nullable into it...
        //noinspection ConstantConditions
        return register( name, () -> BlockEntityType.Builder
                .of( blockEntity, blocks.get() ).build( null ) );
    }
    
    /** Registers a block entity type. */
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(
            String name, Supplier<BlockEntityType<T>> blockEntityType ) {
        return REGISTRY.register( name, blockEntityType );
    }
    
    /** @return Creates a new array of all blocks extracted from a list of block registry objects. */
    private static DeadlySpawnerBlock[] getStandardSpawnerBlocks() {
        List<DeadlySpawnerBlock> blocks = new ArrayList<>();
        for( RegistryObject<DeadlySpawnerBlock> block : DWBlocks.SPAWNERS ) {
            if( DeadlySpawnerBlock.class.equals( block.get().getClass() ) ) blocks.add( block.get() );
        }
        return blocks.toArray( new DeadlySpawnerBlock[0] );
    }
}