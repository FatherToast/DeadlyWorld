package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public
class WorldGenTowerTrap extends WorldGenFloorFeature
{
	private final EnumTowerType TYPE;
	private final IBlockState   BLOCK_STATE;
	
	public
	WorldGenTowerTrap( EnumTowerType towerType )
	{
		super( "tower." + towerType.NAME );
		TYPE = towerType;
		BLOCK_STATE = ModObjects.TOWER_DISPENSER.getDefaultState( ).withProperty( EnumTowerType.PROPERTY, towerType );
	}
	
	@Override
	public
	Config.FeatureTower getFeatureConfig( Config dimConfig ) { return dimConfig.TOWER_DEFAULT; }
	
	@Override
	public
	BlockPos placeFeature( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		BlockPos basePos = getBasePos( dimConfig, world, position );
		if( basePos == null ) {
			basePos = position.add( 0, 1, 0 );
		}
		for( int y = basePos.getY( ) - position.getY( ); y > 0; y-- ) {
			setBlock( dimConfig, world, random, position.add( 0, y, 0 ), TYPE.getFeatureConfig( dimConfig ).PILLAR_BLOCKS, Blocks.LOG );
		}
		BlockPos dispenserPos = basePos.add( 0, 1, 0 );
		setBlock( dimConfig, world, random, dispenserPos, BLOCK_STATE );
		ModObjects.TOWER_DISPENSER.initTileEntity( world, dispenserPos, BLOCK_STATE, dimConfig, random );
		return dispenserPos;
	}
	
	@Override
	public
	boolean canBePlaced( Config dimConfig, World world, BlockPos position )
	{
		BlockPos basePos = getBasePos( dimConfig, world, position );
		if( basePos == null ) {
			return false;
		}
		BlockPos dispenserPos = basePos.add( 0, 1, 0 );
		for( int x = -1; x <= 1; x++ ) {
			for( int z = -1; z <= 1; z++ ) {
				if( !world.isAirBlock( dispenserPos.add( x, 0, z ) ) ) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Gets the block position for the base of the tower. The dispenser goes directly above the base.
	@Nullable
	private
	BlockPos getBasePos( Config dimConfig, World world, BlockPos position )
	{
		BlockPos currentPos;
		for( int y = 1; y <= dimConfig.TOWER_DEFAULT.MAX_TOWER_HEIGHT; y++ ) {
			currentPos = position.add( 0, y, 0 );
			if( world.isAirBlock( currentPos ) ) {
				return currentPos;
			}
		}
		return null;
	}
}
