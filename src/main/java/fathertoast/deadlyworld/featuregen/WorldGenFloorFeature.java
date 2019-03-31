package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.config.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public abstract
class WorldGenFloorFeature extends WorldGenDeadlyFeature
{
	public
	WorldGenFloorFeature( String name ) { super( name ); }
	
	// Generates this feature. Returns true if successful.
	@Override
	public
	BlockPos generate( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		boolean  inWall     = true;
		BlockPos currentPos = position;
		
		IBlockState block;
		while( currentPos.getY( ) > 5 ) {
			block = world.getBlockState( currentPos );
			if( block.isFullCube( ) ) {
				if( !inWall ) {
					// Just hit a floor block, check if the spot is valid for placement
					if( canBePlaced( world, random, currentPos ) ) {
						return placeFeature( dimConfig, replaceableBlocks, world, random, currentPos );
					}
					inWall = true;
				}
			}
			else if( inWall ) {
				// Hit an open space, start looking for a floor block
				inWall = false;
			}
			
			currentPos = new BlockPos( currentPos.getX( ), currentPos.getY( ) - 1, currentPos.getZ( ) );
		}
		return null;
	}
}
