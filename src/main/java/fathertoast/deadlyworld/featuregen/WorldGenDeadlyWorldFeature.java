package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.config.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings( "UnusedReturnValue" )
public abstract
class WorldGenDeadlyWorldFeature
{
	public final String NAME;
	
	WorldGenDeadlyWorldFeature( String name ) { NAME = name; }
	
	// Returns this feature's specific configuration.
	public abstract
	Config.FeatureConfig getFeatureConfig( Config dimensionConfig );
	
	// Generates this feature's structure at a position.
	public abstract
	BlockPos placeFeature( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position );
	
	// Generates this feature. Returns true if successful.
	public
	BlockPos generate( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		if( canBePlaced( dimConfig, world, position ) ) {
			return placeFeature( dimConfig, replaceableBlocks, world, random, position );
		}
		return null;
	}
	
	public
	boolean canBePlaced( Config dimConfig, World world, BlockPos position )
	{
		return true;
	}
	
	public
	boolean setBlock( Config dimConfig, World world, Random random, BlockPos position, IBlockState block )
	{
		world.setBlockState( position, block, FeatureGenerator.UPDATE_FLAGS );
		return true;
	}
	
	public
	boolean setBlock( Config dimConfig, World world, Random random, BlockPos position, WeightedBlockConfig.BlockList blocks, Block defaultBlock )
	{
		setBlock( dimConfig, world, random, position, blocks.nextBlock( random, defaultBlock ) );
		return true;
	}
	public
	boolean setBlock( Config dimConfig, World world, Random random, BlockPos position, WeightedBlockConfig.BlockList blocks, IBlockState defaultBlock )
	{
		setBlock( dimConfig, world, random, position, blocks.nextBlock( random, defaultBlock ) );
		return true;
	}
}
