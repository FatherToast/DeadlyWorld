package fathertoast.deadlyworld.common.block;

import fathertoast.deadlyworld.common.core.config.Config;
import fathertoast.deadlyworld.common.tile.spawner.DeadlySpawnerTileEntity;
import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import net.minecraft.block.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeadlySpawnerBlock extends ContainerBlock {
    
    private final SpawnerType spawnerType;
    
    public DeadlySpawnerBlock( @Nonnull SpawnerType type ) {
        super( Config.BLOCKS.get( SpawnerType.CATEGORY, type.toString() ).adjustBlockProperties( AbstractBlock.Properties.copy( Blocks.SPAWNER ) )
                .requiresCorrectToolForDrops()
                .strength( 5.0F )
        );
        spawnerType = type;

        // TODO - Yes, this is no longer needed. Each spawner comes with its own block that stores the spawner type.
        /* These are all separate blocks now, right?  Is this needed?
		setDefaultState( blockState.getBaseState( ).withProperty( EnumSpawnerType.PROPERTY, EnumSpawnerType.LONE ) );
         */
    }
    
    @Nullable
    @Override
    public TileEntity newBlockEntity( IBlockReader world ) { return new DeadlySpawnerTileEntity(); }
    
    public SpawnerType getSpawnerType() { return spawnerType; }
    
    @Override
    public int getExpDrop( BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silkTouch ) {
        return silkTouch > 0 ? 0 : (15 + RANDOM.nextInt( 15 ) + RANDOM.nextInt( 15 ));
    }
    
    @Override
    public BlockRenderType getRenderShape( BlockState state ) { return BlockRenderType.MODEL; }
}