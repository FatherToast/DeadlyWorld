package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public
class WorldGenSpawner extends WorldGenFloorFeature
{
	private final EnumSpawnerType TYPE;
	private final IBlockState     BLOCK_STATE;
	
	public
	WorldGenSpawner( EnumSpawnerType spawnerType )
	{
		super( spawnerType.displayName );
		TYPE = spawnerType;
		BLOCK_STATE = ModObjects.DEADLY_SPAWNER.getDefaultState( ).withProperty( EnumSpawnerType.PROPERTY, spawnerType );
	}
	
	@Override
	public
	Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return TYPE.getFeatureConfig( dimConfig ); }
	
	@Override
	public
	BlockPos placeFeature( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		if( world.rand.nextFloat( ) < getFeatureConfig( dimConfig ).CHEST_CHANCE ) {
			FeatureGenerator.placeChest( position, world, random, TYPE.lootTable );
		}
		BlockPos spawnerPos = position.add( 0, 1, 0 );
		placeSpawner( spawnerPos, dimConfig, world, random );
		TYPE.decorateSpawner( this, spawnerPos, dimConfig, world, random );
		return spawnerPos;
	}
	
	@Override
	public
	boolean canBePlaced( World world, Random random, BlockPos position )
	{
		return !world.getBlockState( position.add( 0, 2, 0 ) ).isFullCube( )
		       && world.getBlockState( position.add( 0, -1, 0 ) ).isFullCube( );
	}
	
	private
	void placeSpawner( BlockPos pos, Config dimConfig, World world, Random random )
	{
		setBlock( world, random, pos, BLOCK_STATE );
		ModObjects.DEADLY_SPAWNER.initTileEntity( world, pos, BLOCK_STATE, dimConfig );
	}
}
