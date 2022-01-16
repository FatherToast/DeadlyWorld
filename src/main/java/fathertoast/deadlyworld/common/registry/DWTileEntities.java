package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.tile.spawner.DeadlySpawnerTileEntity;
import fathertoast.deadlyworld.common.tile.spawner.DeadlyTrapTileEntity;
import fathertoast.deadlyworld.common.tile.spawner.StormDrainTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@SuppressWarnings( "ConstantConditions" )
public class DWTileEntities {
    
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DeadlyWorld.MOD_ID );
    
    // Null data fixer is safe; they are not needed.
    public static final RegistryObject<TileEntityType<DeadlySpawnerTileEntity>> DEADLY_SPAWNER = register("deadly_spawner",
            () -> TileEntityType.Builder.of(DeadlySpawnerTileEntity::new, DWTileEntities.SPAWNER_BLOCKS.get()).build(null));
    public static final RegistryObject<TileEntityType<DeadlyTrapTileEntity>> DEADLY_TRAP = register("deadly_trap",
            () -> TileEntityType.Builder.of(DeadlyTrapTileEntity::new, DWTileEntities.DEADLY_TRAP_BLOCKS.get()).build(null));
    public static final RegistryObject<TileEntityType<StormDrainTileEntity>> STORM_DRAIN = register("storm_drain",
            () -> TileEntityType.Builder.of(StormDrainTileEntity::new, DWTileEntities.STORM_DRAIN_BLOCKS.get()).build(null));
    
    
    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register( String name, Supplier<TileEntityType<T>> tileEntityTypeSupplier ) {
        return TILE_ENTITIES.register( name, tileEntityTypeSupplier );
    }

    /** Collections of valid blocks for our tile entities */
    private static final Supplier<Block[]> SPAWNER_BLOCKS = () -> new Block[] {
            DWBlocks.NEST_DEADLY_SPAWNER.get(),
            DWBlocks.SWARM_DEADLY_SPAWNER.get(),
            DWBlocks.BRUTAL_DEADLY_SPAWNER.get(),
            DWBlocks.LONE_DEADLY_SPAWNER.get(),
            DWBlocks.STREAM_DEADLY_SPAWNER.get()
    };

    private static final Supplier<Block[]> DEADLY_TRAP_BLOCKS = () -> new Block[] {

    };

    private static final Supplier<Block[]> STORM_DRAIN_BLOCKS = () -> new Block[] {
        DWBlocks.STORM_DRAIN.get()
    };
}