package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.tile.water.StormDrainTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class StormDrainBlock extends ContainerBlock {

    public StormDrainBlock() {
        super(AbstractBlock.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .noDrops()
                .strength(3.0F)
                .harvestTool(ToolType.PICKAXE));
    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader world) {
        return new StormDrainTileEntity();
    }

    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
