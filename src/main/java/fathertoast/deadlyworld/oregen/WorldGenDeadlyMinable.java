package fathertoast.deadlyworld.oregen;

import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

class WorldGenDeadlyMinable
{
	private static final BlockPos[] COVERED_TEST_POS = {
		new BlockPos( 0, 1, 0 ), new BlockPos( 0, -1, 0 ),
		new BlockPos( 1, 0, 0 ), new BlockPos( -1, 0, 0 ),
		new BlockPos( 0, 0, 1 ), new BlockPos( 0, 0, -1 )
	};
	
	private IBlockState veinBlock;
	private boolean     veinCovered = false;
	
	IBlockState getFill( )
	{
		return veinBlock;
	}
	
	WorldGenDeadlyMinable setFill( IBlockState block )
	{
		veinBlock = block;
		return this;
	}
	
	WorldGenDeadlyMinable setCovered( )
	{
		veinCovered = true;
		return this;
	}
	
	WorldGenDeadlyMinable setCovered( boolean covered )
	{
		veinCovered = covered;
		return this;
	}
	
	// Generates this feature. Returns true if successful.
	boolean generate( int veinSize, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		int generated = 0;
		
		float  orientation = random.nextFloat( ) * (float) Math.PI;
		double xiMin       = (double) (position.getX( ) + MathHelper.sin( orientation ) * (float) veinSize / 8.0F);
		double xiMax       = (double) (position.getX( ) - MathHelper.sin( orientation ) * (float) veinSize / 8.0F);
		double yiMin       = (double) (position.getY( ) + random.nextInt( 3 ) - 2);
		double yiMax       = (double) (position.getY( ) + random.nextInt( 3 ) - 2);
		double ziMin       = (double) (position.getZ( ) + MathHelper.cos( orientation ) * (float) veinSize / 8.0F);
		double ziMax       = (double) (position.getZ( ) - MathHelper.cos( orientation ) * (float) veinSize / 8.0F);
		
		for( int blockNumber = 0; blockNumber < veinSize; blockNumber++ ) {
			float  ratio  = (float) blockNumber / (float) veinSize;
			double xi     = xiMin + (xiMax - xiMin) * (double) ratio;
			double yi     = yiMin + (yiMax - yiMin) * (double) ratio;
			double zi     = ziMin + (ziMax - ziMin) * (double) ratio;
			double mgntd  = random.nextDouble( ) * (double) veinSize / 16.0;
			double radius = ((double) (MathHelper.sin( (float) Math.PI * ratio ) + 1.0F) * mgntd + 1.0) / 2.0;
			int    xMin   = MathHelper.floor( xi - radius );
			int    yMin   = MathHelper.floor( yi - radius );
			int    zMin   = MathHelper.floor( zi - radius );
			int    xMax   = MathHelper.floor( xi + radius );
			int    yMax   = MathHelper.floor( yi + radius );
			int    zMax   = MathHelper.floor( zi + radius );
			
			for( int x = xMin; x <= xMax; x++ ) {
				double dx = ((double) x + 0.5 - xi) / radius;
				if( dx * dx < 1.0 ) {
					
					for( int y = yMin; y <= yMax; y++ ) {
						double dy = ((double) y + 0.5 - yi) / radius;
						if( dx * dx + dy * dy < 1.0 ) {
							
							for( int z = zMin; z <= zMax; z++ ) {
								double dz = ((double) z + 0.5 - zi) / radius;
								if( dx * dx + dy * dy + dz * dz < 1.0 ) {
									
									BlockPos    placementPos   = new BlockPos( x, y, z );
									IBlockState blockToReplace = world.getBlockState( placementPos );
									if( replaceableBlocks.matches( blockToReplace )
									    && setBlock( world, random, placementPos, blockToReplace ) ) {
										
										generated++;
									}
								}
							}
						}
					}
				}
			}
		}
		
		return generated > 0;
	}
	
	protected
	boolean setBlock( World world, Random random, BlockPos position, IBlockState blockToReplace )
	{
		if( veinCovered ) {
			IBlockState testBlock;
			for( BlockPos testPos : COVERED_TEST_POS ) {
				testBlock = world.getBlockState( position.add( testPos ) );
				if( !testBlock.isFullCube( ) && !testBlock.getBlock( ).equals( veinBlock.getBlock( ) ) ) {
					return false;
				}
			}
		}
		
		world.setBlockState( position, veinBlock, FeatureGenerator.UPDATE_FLAGS );
		return true;
	}
}
