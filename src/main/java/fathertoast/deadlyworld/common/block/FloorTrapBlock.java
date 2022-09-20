package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapTileEntity;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapType;
import net.minecraft.block.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FloorTrapBlock extends Block {

    private final FloorTrapType trapType;

    public FloorTrapBlock( @Nonnull FloorTrapType trapType ) {
        super( Config.BLOCKS.get( trapType ).adjustBlockProperties( AbstractBlock.Properties.copy( Blocks.SPAWNER ) ) );
        this.trapType = trapType;;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new FloorTrapTileEntity();
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silkTouch ) {
        return 15 + RANDOM.nextInt( 15 ) + RANDOM.nextInt( 15 );
    }


    @Override
    public BlockRenderType getRenderShape(BlockState state ) { return BlockRenderType.INVISIBLE; }

    public final FloorTrapType getTrapType() {
        return trapType;
    }
}
