package fathertoast.deadlyworld.block;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public
class BlockDeadlyTrap extends BlockContainer
{
	public
	BlockDeadlyTrap( )
	{
		super( Material.IRON, MapColor.STONE );
		setSoundType( SoundType.METAL );
		disableStats( );
		setHardness( 5.0F );
	}
	
	public
	void initTileEntity( World world, BlockPos pos, IBlockState state, Config dimConfig )
	{
		TileEntity spawnerData = world.getTileEntity( pos );
		if( spawnerData instanceof TileEntityDeadlyTrap ) {
			//((TileEntityDeadlyTrap) spawnerData).initializeSpawner( dimConfig );
		}
		else {
			DeadlyWorldMod.log( ).error( "Failed to fetch trap tile entity at [{}]!", pos );
		}
	}
	
	@Override
	public
	TileEntity createNewTileEntity( World world, int meta ) { return new TileEntityDeadlyTrap( ); }
	
	@Override
	public
	int getExpDrop( IBlockState state, IBlockAccess world, BlockPos pos, int fortune )
	{
		return 15 + RANDOM.nextInt( 15 ) + RANDOM.nextInt( 15 );
	}
	
	@Override
	public
	void onBlockPlacedBy( World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack )
	{
		super.onBlockPlacedBy( world, pos, state, placer, stack );
		
		if( !world.isRemote ) {
			initTileEntity( world, pos, state, Config.getOrDefault( world ) );
		}
	}
	
	@Override
	public
	Item getItemDropped( IBlockState state, Random rand, int fortune ) { return Items.AIR; }
	
	@Override
	public
	int quantityDropped( Random random ) { return 0; }
	
	@Override
	public
	EnumBlockRenderType getRenderType( IBlockState state ) { return EnumBlockRenderType.MODEL; }
}
