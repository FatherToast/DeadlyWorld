package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public
class WorldGenChest extends WorldGenFloorFeature
{
	private final EnumChestType TYPE;
	
	public
	WorldGenChest( EnumChestType chestType )
	{
		super( "chest." + chestType.NAME );
		TYPE = chestType;
	}
	
	@Override
	public
	Config.FeatureConfig getFeatureConfig( Config dimConfig ) { return TYPE.getFeatureConfig( dimConfig ); }
	
	@Override
	public
	BlockPos placeFeature( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		BlockPos         chestPos  = position.add( 0, 1, 0 );
		ResourceLocation lootTable = TYPE.getLootTable( dimConfig, world, random, position );
		FeatureGenerator.placeChest(
			chestPos, world, random, lootTable, random.nextFloat( ) < TYPE.getFeatureConfig( dimConfig ).TRAPPED_CHANCE
		);
		TYPE.decorateChest( this, chestPos, dimConfig, world, random );
		return chestPos;
	}
	
	@Override
	public
	boolean canBePlaced( Config dimConfig, World world, BlockPos position )
	{
		BlockPos trapPos;
		if( TrapHelper.isAnySideOpen( world, position ) ) {
			trapPos = position.add( 0, -1, 0 );
			if( TrapHelper.isAnySideOpen( world, trapPos ) ) {
				return false;
			}
		}
		else {
			trapPos = position;
		}
		return TYPE.canTypeBePlaced( world, trapPos );
	}
}
