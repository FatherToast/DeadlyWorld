package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.tile.DeadlySpawnerTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class DeadlySpawnerBlock extends ContainerBlock {

    public DeadlySpawnerBlock() {
        super(AbstractBlock.Properties.copy(Blocks.SPAWNER));

        /*
        super( Material.IRON, MapColor.STONE );
		setSoundType( SoundType.METAL );

		setHardness( Config.get( ).GENERAL.SPAWNER_HARDNESS );
		setResistance( Config.get( ).GENERAL.SPAWNER_RESISTANCE );

		setDefaultState( blockState.getBaseState( ).withProperty( EnumSpawnerType.PROPERTY, EnumSpawnerType.LONE ) );
         */
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new DeadlySpawnerTileEntity();
    }
}
