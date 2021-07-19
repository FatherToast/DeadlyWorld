package fathertoast.deadlyworld.common.registry;

import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DWBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create( ForgeRegistries.BLOCKS, DeadlyWorld.MOD_ID );


    public static final RegistryObject<Block> DEADLY_SPAWNER_BLOCK = register("deadly_spawner", DeadlySpawnerBlock::new);


    private static <T extends Block> RegistryObject<T> register( String name, Supplier<T> blockSupplier ) {
        return BLOCKS.register( name, blockSupplier );
    }
}
