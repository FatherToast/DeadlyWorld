package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapTileEntity;
import fathertoast.deadlyworld.common.tile.spawner.DeadlySpawnerTileEntity;
import fathertoast.deadlyworld.common.tile.spawner.MiniSpawnerTileEntity;
import fathertoast.deadlyworld.common.tile.water.StormDrainTileEntity;
import fathertoast.deadlyworld.common.tile.tower.TowerDispenserTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@SuppressWarnings( "ConstantConditions" )
public class DWTileEntities {

    /** The deferred register for this mod's tile entities. */
    public static final DeferredRegister<TileEntityType<?>> REGISTRY = DeferredRegister.create( ForgeRegistries.TILE_ENTITIES, DeadlyWorld.MOD_ID );


    public static final RegistryObject<TileEntityType<DeadlySpawnerTileEntity>> DEADLY_SPAWNER = register( "deadly_spawner",
            () -> TileEntityType.Builder.of( DeadlySpawnerTileEntity::new, DWBlocks.spawnerBlocks() ).build( null ));
    
    public static final RegistryObject<TileEntityType<FloorTrapTileEntity>> FLOOR_TRAP = register( "floor_trap",
            () -> TileEntityType.Builder.of( FloorTrapTileEntity::new, DWBlocks.floorTrapBlocks() ).build( null ));

    public static final RegistryObject<TileEntityType<TowerDispenserTileEntity>> TOWER_DISPENSER = register("tower_dispenser",
            () -> TileEntityType.Builder.of( TowerDispenserTileEntity::new, DWBlocks.towerDispenserBlocks() ).build( null ));

    public static final RegistryObject<TileEntityType<MiniSpawnerTileEntity>> MINI_SPAWNER = register( "mini_spawner",
            () -> TileEntityType.Builder.of( MiniSpawnerTileEntity::new, DWBlocks.spawnerBlocks()).build( null ));

    public static final RegistryObject<TileEntityType<StormDrainTileEntity>> STORM_DRAIN = register( "storm_drain",
            () -> TileEntityType.Builder.of( StormDrainTileEntity::new, DWBlocks.STORM_DRAIN.get() ).build( null ));



    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register( String name, Supplier<TileEntityType<T>> tileEntityTypeSupplier ) {
        return REGISTRY.register( name, tileEntityTypeSupplier );
    }
}