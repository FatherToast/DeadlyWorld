package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.block.chest.MiniChestBlock;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapBlock;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapType;
import fathertoast.deadlyworld.common.block.fluid.RunnyLavaBlock;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineBlock;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.block.spawner.BuriedSpawnerBlock;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.spike_trap.BaseSpikeTrapBlock;
import fathertoast.deadlyworld.common.block.spike_trap.SpikeTrapType;
import fathertoast.deadlyworld.common.block.tower.TowerDispenserBlock;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.item.AutoGenBlockItem;
import fathertoast.deadlyworld.common.item.MiniChestBlockItem;
import fathertoast.deadlyworld.common.item.SeaMineBlockItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class DWBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCKS, DeadlyWorld.MOD_ID );
    
    public static final List<RegistryObject<DeadlySpawnerBlock>> SPAWNERS;
    public static final List<RegistryObject<FloorTrapBlock>> FLOOR_TRAPS;
    public static final List<RegistryObject<TowerDispenserBlock>> TOWER_DISPENSERS;
    public static final List<RegistryObject<SeaMineBlock>> SEA_MINES;
    public static final List<RegistryObject<BaseSpikeTrapBlock>> SPIKE_TRAPS;
    
    
    //    public static final RegistryObject<Block> STORM_DRAIN = registerBlock( "storm_drain", StormDrainBlock::new, ItemGroup.TAB_MISC );
    //    public static final RegistryObject<Block> SEWER_BEDROCK = registerBlock( "sewer_bedrock", () -> new Block( AbstractBlock.Properties.of( Material.STONE, MaterialColor.COLOR_GRAY ).strength( -1.0F, 3600000.0F ).noDrops().sound( SoundType.STONE ) ), ItemGroup.TAB_BUILDING_BLOCKS );
    
    public static final RegistryObject<Block> INACTIVE_BURIED_SPAWNER = registerBlock( "inactive_" + SpawnerType.BURIED + "_deadly_spawner", BuriedSpawnerBlock::new );
    
    public static final RegistryObject<Block> MINI_CHEST = registerBlock( "mini_chest",
            () -> new MiniChestBlock( BlockBehaviour.Properties.of().mapColor( MapColor.WOOD ).instrument( NoteBlockInstrument.BASS ).strength( 2.5F ).sound( SoundType.WOOD ).ignitedByLava() ),
            () -> new MiniChestBlockItem( DWBlocks.MINI_CHEST.get() ) );
    
    public static final RegistryObject<LiquidBlock> RUNNY_LAVA = registerBlockNoItem( "runny_lava", () -> new RunnyLavaBlock( DWFluids.RUNNY_LAVA_SOURCE ) );
    
    static List<RegistryObject<? extends IAutoGenBlock>> AUTO_GEN_BLOCKS;
    
    static {
        // SPAWNERS
        final ArrayList<RegistryObject<DeadlySpawnerBlock>> spawners = new ArrayList<>();
        for( SpawnerType type : SpawnerType.values() ) {
            spawners.add( type.ordinal(), registerBlock( type + "_deadly_spawner", type.getBlock() ) );
        }
        spawners.trimToSize();
        SPAWNERS = Collections.unmodifiableList( spawners );
        
        // FLOOR TRAPS
        final ArrayList<RegistryObject<FloorTrapBlock>> floorTraps = new ArrayList<>();
        for( FloorTrapType type : FloorTrapType.values() ) {
            floorTraps.add( type.ordinal(), registerBlock( type + "_floor_trap", type.getBlock() ) );
        }
        floorTraps.trimToSize();
        FLOOR_TRAPS = Collections.unmodifiableList( floorTraps );
        
        // SPIKE TRAPS
        final ArrayList<RegistryObject<BaseSpikeTrapBlock>> spikeTraps = new ArrayList<>();
        for( SpikeTrapType type : SpikeTrapType.values() ) {
            spikeTraps.add( type.ordinal(), registerBlock( type + "_spike_trap", type.getBlock() ) );
        }
        spikeTraps.trimToSize();
        SPIKE_TRAPS = Collections.unmodifiableList( spikeTraps );
        
        // TOWER DISPENSERS
        final ArrayList<RegistryObject<TowerDispenserBlock>> towerDispensers = new ArrayList<>();
        for( TowerType type : TowerType.values() ) {
            towerDispensers.add( type.ordinal(), registerBlock( type + "_tower_dispenser", type.getBlock() ) );
        }
        towerDispensers.trimToSize();
        TOWER_DISPENSERS = Collections.unmodifiableList( towerDispensers );
        
        // SEA MINES
        final ArrayList<RegistryObject<SeaMineBlock>> seaMines = new ArrayList<>();
        for( SeaMineType type : SeaMineType.values() ) {
            Supplier<SeaMineBlock> block = type.getBlock();
            String name = type + "_sea_mine";
            RegistryObject<SeaMineBlock> regObj = registerBlockNoItem( name, block );
            seaMines.add( type.ordinal(), regObj );
            DWItems.register( name, () -> new SeaMineBlockItem( regObj.get(), new Item.Properties() ) );
        }
        seaMines.trimToSize();
        SEA_MINES = Collections.unmodifiableList( seaMines );
    }
    
    /** @return The list of block registry objects for auto-generated blocks. */
    public static List<RegistryObject<? extends IAutoGenBlock>> getAutoGenBlocks() { return AUTO_GEN_BLOCKS; }
    
    /** @return The block registry object for a particular spawner type. */
    public static RegistryObject<DeadlySpawnerBlock> spawner( SpawnerType type ) { return SPAWNERS.get( type.ordinal() ); }
    
    /** @return The block registry object for a particular floor trap type. */
    public static RegistryObject<FloorTrapBlock> floorTrap( FloorTrapType type ) { return FLOOR_TRAPS.get( type.ordinal() ); }
    
    /** @return The block registry object for a particular spike trap type. */
    public static RegistryObject<BaseSpikeTrapBlock> spikeTrap( SpikeTrapType type ) { return SPIKE_TRAPS.get( type.ordinal() ); }
    
    /** @return The block registry object for a particular tower dispenser type. */
    public static RegistryObject<TowerDispenserBlock> towerDispenser( TowerType type ) { return TOWER_DISPENSERS.get( type.ordinal() ); }
    
    /** @return The block registry object for a particular sea mine type. */
    public static RegistryObject<SeaMineBlock> seaMine( SeaMineType type ) { return SEA_MINES.get( type.ordinal() ); }
    
    /** Registers an auto-generated block with a simple item. */
    static <T extends Block & IAutoGenBlock> RegistryObject<T> registerAutoGenBlock(
            String blockKey, ResourceLocation originBlockLoc, BiFunction<Block, ResourceLocation, T> factory ) {
        String name = BlockAutoGen.pathFor( blockKey, originBlockLoc );
        RegistryObject<T> block = registerBlockNoItem( name, () -> BlockAutoGen.generate( originBlockLoc, factory ) );
        DWItems.register( name, () -> new AutoGenBlockItem( blockKey, block.get(), new Item.Properties() ) );
        return block;
    }
    
    /** Registers a block with a simple item. */
    private static <T extends Block> RegistryObject<T> registerBlock( String name, Supplier<T> blockSupplier ) {
        RegistryObject<T> block = registerBlockNoItem( name, blockSupplier );
        DWItems.registerBlockItem( name, block );
        return block;
    }
    
    /** Registers a block with the given item. */
    private static <T extends Block> RegistryObject<T> registerBlock( String name, Supplier<T> blockSupplier, Supplier<Item> blockItemSupplier ) {
        RegistryObject<T> block = registerBlockNoItem( name, blockSupplier );
        DWItems.register( name, blockItemSupplier );
        return block;
    }
    
    /** Registers a block without an item. */
    private static <T extends Block> RegistryObject<T> registerBlockNoItem( String name, Supplier<T> blockSupplier ) {
        return REGISTRY.register( name, blockSupplier );
    }
}