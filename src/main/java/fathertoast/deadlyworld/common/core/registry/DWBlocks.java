package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.block.*;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapTileEntity;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapType;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import fathertoast.deadlyworld.common.tile.tower.TowerType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
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
    private static final List<RegistryObject<FloorTrapBlock>> FLOOR_TRAPS;
    private static final List<RegistryObject<TowerDispenserBlock>> TOWER_DISPENSERS;

    public static final RegistryObject<Block> STORM_DRAIN = registerBlock( "storm_drain", StormDrainBlock::new, ItemGroup.TAB_MISC );
    public static final RegistryObject<Block> SEWER_EXIT = registerBlock("sewer_exit", SewerExitBlock::new, ItemGroup.TAB_BUILDING_BLOCKS);
    public static final RegistryObject<Block> SEWER_BEDROCK = registerBlock("sewer_bedrock", () -> new Block(AbstractBlock.Properties.of(Material.STONE, MaterialColor.COLOR_GRAY).strength(-1.0F, 3600000.0F).noDrops().sound(SoundType.STONE)), ItemGroup.TAB_BUILDING_BLOCKS );



    static {
        final ArrayList<RegistryObject<DeadlySpawnerBlock>> spawners = new ArrayList<>();
        final ArrayList<RegistryObject<FloorTrapBlock>> floorTraps = new ArrayList<>();
        final ArrayList<RegistryObject<TowerDispenserBlock>> towerDispensers = new ArrayList<>();

        for(SpawnerType type : SpawnerType.values()) {
            spawners.add(type.ordinal(), registerSpawner(type));
        }
        spawners.trimToSize();
        SPAWNERS = Collections.unmodifiableList( spawners );

        for(FloorTrapType type : FloorTrapType.values()) {
            floorTraps.add(type.ordinal(), registerFloorTrap(type));
        }
        floorTraps.trimToSize();
        FLOOR_TRAPS = Collections.unmodifiableList( floorTraps );

        for (TowerType type : TowerType.values()) {
            towerDispensers.add(type.ordinal(), registerTowerDispenser(type));
        }
        towerDispensers.trimToSize();
        TOWER_DISPENSERS = Collections.unmodifiableList( towerDispensers );
    }


    /** @return The block registry object for a particular spawner type. */
    public static RegistryObject<DeadlySpawnerBlock> spawner(SpawnerType type) {
        return SPAWNERS.get(type.ordinal());
    }

    /** @return The block registry object for a particular floor trap type. */
    public static RegistryObject<FloorTrapBlock> floorTrap(FloorTrapType type) {
        return FLOOR_TRAPS.get(type.ordinal());
    }
    
    /** @return Creates an array of all spawner blocks and returns it. */
    public static Block[] spawnerBlocks() { return blockArray(SPAWNERS); }
    
    /** @return Creates an array of all floor trap blocks and returns it. */
    public static Block[] floorTrapBlocks() { return blockArray(FLOOR_TRAPS); }

    public static Block[] towerDispenserBlocks() {
        return blockArray(TOWER_DISPENSERS);
    }
    
    /** @return Creates a new array referencing all the blocks represented by a list of block registry objects. */
    private static <T extends Block> Block[] blockArray(List<RegistryObject<T>> blockRegObjects) {
        final Block[] blocks = new Block[blockRegObjects.size()];

        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = blockRegObjects.get(i).get();
        }
        return blocks;
    }
    
    /**
     * Registers a block and a simple BlockItem for it.
     */
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockSupplier, ItemGroup itemGroup) {
        RegistryObject<T> blockRegObject = REGISTRY.register(name, blockSupplier);
        DWItems.REGISTRY.register(name, () -> new BlockItem(blockRegObject.get(), new Item.Properties().tab(itemGroup)));
        return blockRegObject;
    }
    
    /**
     * Registers a block without any BlockItem.
     */
    private static <T extends Block> RegistryObject<T> registerBlockNoItem( String name, Supplier<T> blockSupplier ) {
        return REGISTRY.register( name, blockSupplier );
    }
    
    private static RegistryObject<DeadlySpawnerBlock> registerSpawner(SpawnerType spawnerType) {
        String regName = spawnerType.getSerializedName() + "_deadly_spawner";
        RegistryObject<DeadlySpawnerBlock> blockRegObject = REGISTRY.register( regName, spawnerType.getBlock() );
        DWItems.registerBlockItem( regName, blockRegObject, new Item.Properties().tab( ItemGroup.TAB_DECORATIONS ) );
        return blockRegObject;
    }

    private static RegistryObject<FloorTrapBlock> registerFloorTrap(FloorTrapType trapType) {
        String regName = trapType.getSerializedName() + "_floor_trap";
        RegistryObject<FloorTrapBlock> blockRegObject = REGISTRY.register( regName, trapType.getBlock() );
        DWItems.registerBlockItem( regName, blockRegObject, new Item.Properties().tab( ItemGroup.TAB_DECORATIONS ) );
        return blockRegObject;
    }

    private static RegistryObject<TowerDispenserBlock> registerTowerDispenser(TowerType towerType) {
        String regName = towerType.getSerializedName() + "_tower_dispenser";
        RegistryObject<TowerDispenserBlock> blockRegObject = REGISTRY.register( regName, towerType.getBlock() );
        DWItems.registerBlockItem( regName, blockRegObject, new Item.Properties().tab( ItemGroup.TAB_DECORATIONS ) );
        return blockRegObject;
    }

    public static void onBlockRegister(RegistryEvent.Register<Block> event) {

    }
}