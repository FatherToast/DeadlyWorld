package fathertoast.deadlyworld.oregen;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

class WorldGenDeadlySilverfish extends WorldGenDeadlyMinable
{
	@Override
	boolean generate( int veinSize, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		return super.generate( veinSize, Config.get( ).GENERAL.SILVERFISH_REPLACEABLE, world, random, position );
	}
	
	@Override
	protected
	boolean setBlock( World world, Random random, BlockPos position, IBlockState blockToReplace )
	{
		IBlockState infestedState =
			Config.get( ).GENERAL.SILVERFISH_AUTOGEN ?
			ModObjects.getInfestedVersionIfReplaceable( blockToReplace ) :
			Blocks.MONSTER_EGG.getDefaultState( ).withProperty( BlockSilverfish.VARIANT, BlockSilverfish.EnumType.STONE );
		
		if( infestedState != null ) {
			world.setBlockState( position, infestedState, FeatureGenerator.UPDATE_FLAGS );
			return true;
		}
		return false;
	}
}
