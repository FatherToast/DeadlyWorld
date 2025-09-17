package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.block.entity.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.entity.MiniSpawnerBlockEntity;
import fathertoast.deadlyworld.common.block.spawner.*;
import fathertoast.deadlyworld.common.block.entity.PotionTowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.block.tower.TowerDispenserBlock;
import fathertoast.deadlyworld.common.block.entity.TowerDispenserBlockEntity;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.block.trap.DeadlyTrapBlock;
import fathertoast.deadlyworld.common.block.entity.DeadlyTrapBlockEntity;
import fathertoast.deadlyworld.common.block.entity.PotionTrapBlockEntity;
import fathertoast.deadlyworld.common.block.trap.TrapType;
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

public final class DWBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCK_ENTITY_TYPES, DeadlyWorld.MOD_ID );
    
    public static final RegistryObject<BlockEntityType<DeadlySpawnerBlockEntity>> DEADLY_SPAWNER = registerMultiple(
            "deadly_spawner", DeadlySpawnerBlockEntity::new, DWBlockEntities::getStandardSpawnerBlocks );
    public static final RegistryObject<BlockEntityType<MiniSpawnerBlockEntity>> MINI_SPAWNER = register(
            "mini_spawner", MiniSpawnerBlockEntity::new, DWBlocks.spawner( SpawnerType.MINI ) );
    
    public static final RegistryObject<BlockEntityType<DeadlyTrapBlockEntity>> DEADLY_TRAP = registerMultiple(
            "deadly_trap", DeadlyTrapBlockEntity::new, DWBlockEntities::getStandardTrapBlocks );
    public static final RegistryObject<BlockEntityType<PotionTrapBlockEntity>> POTION_TRAP = register(
            "potion_trap", PotionTrapBlockEntity::new, DWBlocks.trap( TrapType.POTION ) );
    
    public static final RegistryObject<BlockEntityType<TowerDispenserBlockEntity>> TOWER_DISPENSER = registerMultiple(
            "tower_dispenser", TowerDispenserBlockEntity::new, DWBlockEntities::getStandardTowerDisBlocks );
    public static final RegistryObject<BlockEntityType<PotionTowerDispenserBlockEntity>> POTION_TOWER = register(
            "potion_tower_dispenser", PotionTowerDispenserBlockEntity::new, DWBlocks.towerDispenser( TowerType.POTION ) );
    
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

    /** @return Creates a new array of all blocks extracted from a list of block registry objects. */
    private static DeadlyTrapBlock[] getStandardTrapBlocks() {
        List<DeadlyTrapBlock> blocks = new ArrayList<>();
        for( RegistryObject<DeadlyTrapBlock> block : DWBlocks.TRAPS ) {
            if( DeadlyTrapBlock.class.equals( block.get().getClass() ) ) blocks.add( block.get() );
        }
        return blocks.toArray( new DeadlyTrapBlock[0] );
    }

    private static TowerDispenserBlock[] getStandardTowerDisBlocks() {
        List<TowerDispenserBlock> blocks = new ArrayList<>();
        for( RegistryObject<TowerDispenserBlock> block : DWBlocks.TOWER_DISPENSERS ) {
            if( TowerDispenserBlock.class.equals( block.get().getClass() ) ) blocks.add( block.get() );
        }
        return blocks.toArray( new TowerDispenserBlock[0] );
    }
}