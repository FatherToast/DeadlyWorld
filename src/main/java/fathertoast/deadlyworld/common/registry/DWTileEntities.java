package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DWTileEntities {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create( ForgeRegistries.TILE_ENTITIES, DeadlyWorld.MOD_ID );


    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register( String name, Supplier<TileEntityType<T>> tileEntityTypeSupplier ) {
        return TILE_ENTITIES.register( name, tileEntityTypeSupplier );
    }
}
