package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.config.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

public
class WorldGenChest extends WorldGenFloorFeature
{
	public
	WorldGenChest( String name ) { super( name ); }
	
	@Override
	public
	Config.FeatureConfig getFeatureConfig( Config dimConfig ) { return dimConfig.FEATURE_CHEST; }
	
	@Override
	public
	BlockPos placeFeature( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		BlockPos chestPos = position.add( 0, 1, 0 );
		FeatureGenerator.placeChest( chestPos, world, random, LootTableList.CHESTS_SIMPLE_DUNGEON );
		return chestPos;
	}
	
	@Override
	public
	boolean canBePlaced( World world, Random random, BlockPos position )
	{
		return !world.getBlockState( position.add( 0, 2, 0 ) ).isFullCube( );
	}
}
