package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.tile.spawner.DeadlySpawnerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DWTileEntities {
    
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DeadlyWorld.MOD_ID );
    
    // Null data fixer is safe; they are not needed.
    @SuppressWarnings( "ConstantConditions" )
    public static final RegistryObject<TileEntityType<DeadlySpawnerTileEntity>> DEADLY_SPAWNER = register( "deadly_spawner",
            () -> TileEntityType.Builder.of( DeadlySpawnerTileEntity::new, DWTileEntities.SPAWNER_BLOCKS.get() ).build( null ) );
    
    
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
}