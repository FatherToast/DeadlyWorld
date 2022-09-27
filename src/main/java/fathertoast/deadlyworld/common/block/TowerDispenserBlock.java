package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapTileEntity;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapType;
import fathertoast.deadlyworld.common.tile.tower.TowerDispenserTileEntity;
import fathertoast.deadlyworld.common.tile.tower.TowerType;
import net.minecraft.block.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public class TowerDispenserBlock extends Block {

    private final TowerType towerType;

    public TowerDispenserBlock( TowerType type ) {
        super( Config.BLOCKS.get( type ).adjustBlockProperties( AbstractBlock.Properties.copy( Blocks.SPAWNER ) ) );
        this.towerType = type;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TowerDispenserTileEntity();
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silkTouch ) {
        return 15 + RANDOM.nextInt( 15 ) + RANDOM.nextInt( 15 );
    }


    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderShape(BlockState state ) { return BlockRenderType.MODEL; }

    public final TowerType getTowerType() {
        return towerType;
    }
}
