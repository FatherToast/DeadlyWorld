package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public
class StateMapperDeadlySilverfish extends StateMapperBase
{
	@Override
	protected
	ModelResourceLocation getModelResourceLocation( IBlockState state )
	{
		IBlockState      disguiseState = ((BlockDeadlySilverfish) state.getBlock( )).toDisguise( state );
		ResourceLocation regName       = Block.REGISTRY.getNameForObject( disguiseState.getBlock( ) );
		return new ModelResourceLocation(
			new ResourceLocation( DeadlyWorldMod.MOD_ID, ModelLoaderDeadlySilverfish.PREFIX + regName.toString( ) ),
			getPropertyString( disguiseState.getProperties( ) )
		);
	}
}
