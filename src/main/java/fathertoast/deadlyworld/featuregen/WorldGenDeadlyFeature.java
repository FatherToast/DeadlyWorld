package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.config.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public abstract
class WorldGenDeadlyFeature
{
	public final String NAME;
	
	public
	WorldGenDeadlyFeature( String name ) { NAME = name; }
	
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
		if( canBePlaced( world, random, position ) ) {
			return placeFeature( dimConfig, replaceableBlocks, world, random, position );
		}
		return null;
	}
	
	public
	boolean canBePlaced( World world, Random random, BlockPos position )
	{
		return true;
	}
	
	public
	boolean setBlock( World world, Random random, BlockPos position, IBlockState block )
	{
		world.setBlockState( position, block, FeatureGenerator.UPDATE_FLAGS );
		return true;
	}
}
