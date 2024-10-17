package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DWBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create( ForgeRegistries.BLOCKS, DeadlyWorld.MOD_ID );
    
    public static final List<RegistryObject<DeadlySpawnerBlock>> SPAWNERS;
    //    public static final List<RegistryObject<FloorTrapBlock>> FLOOR_TRAPS;
    //    public static final List<RegistryObject<TowerDispenserBlock>> TOWER_DISPENSERS;
    
    //    public static final RegistryObject<Block> STORM_DRAIN = registerBlock( "storm_drain", StormDrainBlock::new, ItemGroup.TAB_MISC );
    //    public static final RegistryObject<Block> SEWER_BEDROCK = registerBlock( "sewer_bedrock", () -> new Block( AbstractBlock.Properties.of( Material.STONE, MaterialColor.COLOR_GRAY ).strength( -1.0F, 3600000.0F ).noDrops().sound( SoundType.STONE ) ), ItemGroup.TAB_BUILDING_BLOCKS );
    
    static {
        final ArrayList<RegistryObject<DeadlySpawnerBlock>> spawners = new ArrayList<>();
        for( SpawnerType type : SpawnerType.values() ) {
            spawners.add( type.ordinal(), registerBlock( type + "_deadly_spawner", type.getBlock() ) );
        }
        spawners.trimToSize();
        SPAWNERS = Collections.unmodifiableList( spawners );
        
        //        final ArrayList<RegistryObject<FloorTrapBlock>> floorTraps = new ArrayList<>();
        //        for( FloorTrapType type : FloorTrapType.values() ) {
        //            floorTraps.add( type.ordinal(), registerFloorTrap( type ) );
        //        }
        //        floorTraps.trimToSize();
        //        FLOOR_TRAPS = Collections.unmodifiableList( floorTraps );
        
        //        final ArrayList<RegistryObject<TowerDispenserBlock>> towerDispensers = new ArrayList<>();
        //        for( TowerType type : TowerType.values() ) {
        //            towerDispensers.add( type.ordinal(), registerTowerDispenser( type ) );
        //        }
        //        towerDispensers.trimToSize();
        //        TOWER_DISPENSERS = Collections.unmodifiableList( towerDispensers );
    }
    
    /** @return The block registry object for a particular spawner type. */
    public static RegistryObject<DeadlySpawnerBlock> spawner( SpawnerType type ) { return SPAWNERS.get( type.ordinal() ); }
    
    //    /** @return The block registry object for a particular floor trap type. */
    //    public static RegistryObject<FloorTrapBlock> floorTrap( FloorTrapType type ) { return FLOOR_TRAPS.get( type.ordinal() ); }
    
    //    /** @return The block registry object for a particular tower dispenser type. */
    //    public static RegistryObject<TowerDispenserBlock> towerDispenser( TowerType type ) { return TOWER_DISPENSERS.get( type.ordinal() ); }
    
    /** Registers a block with a simple item. */
    private static <T extends Block> RegistryObject<T> registerBlock( String name, Supplier<T> blockSupplier ) {
        RegistryObject<T> block = registerBlockNoItem( name, blockSupplier );
        DWItems.registerBlockItem( name, block );
        return block;
    }
    
    /** Registers a block without an item. */
    private static <T extends Block> RegistryObject<T> registerBlockNoItem( String name, Supplier<T> blockSupplier ) {
        return REGISTRY.register( name, blockSupplier );
    }
}