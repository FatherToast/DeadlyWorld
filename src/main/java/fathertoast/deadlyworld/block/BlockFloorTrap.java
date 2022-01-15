package fathertoast.deadlyworld.block;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

import java.util.Random;

public
class BlockFloorTrap extends BlockContainer
{
	public static final String ID = "floor_trap";
	
	public
	BlockFloorTrap( )
	{
		super( Material.ROCK, MapColor.STONE );
		setSoundType( SoundType.STONE );
		
		setHardness( Config.get( ).GENERAL.FLOOR_TRAP_HARDNESS );
		setResistance( Config.get( ).GENERAL.FLOOR_TRAP_RESISTANCE );
		setHarvestLevel( ModObjects.TOOL_NAME_PICKAXE, Config.get( ).GENERAL.FLOOR_TRAP_HARVEST_LEVEL );
		
		setDefaultState( blockState.getBaseState( ).withProperty( EnumFloorTrapType.PROPERTY, EnumFloorTrapType.TNT ) );
	}
	
	public
	void initTileEntity( World world, BlockPos pos, IBlockState state, Config dimConfig, Random random )
	{
		TileEntity spawnerData = world.getTileEntity( pos );
		if( spawnerData instanceof TileEntityFloorTrap ) {
			((TileEntityFloorTrap) spawnerData).initializeFloorTrap( state.getValue( EnumFloorTrapType.PROPERTY ), dimConfig, random );
		}
		else {
			DeadlyWorldMod.log( ).error( "Failed to fetch trap tile entity at [{}]!", pos );
		}
	}
	
	@Override
	public
	TileEntity createNewTileEntity( World world, int meta ) { return new TileEntityFloorTrap( ); }
	
	@Override
	protected
	BlockStateContainer createBlockState( ) { return new BlockStateContainer( this, EnumFloorTrapType.PROPERTY ); }
	
	// Will be removed in 1.13
	@SuppressWarnings( "deprecation" )
	@Override
	public
	IBlockState getStateFromMeta( int meta ) { return getDefaultState( ).withProperty( EnumFloorTrapType.PROPERTY, EnumFloorTrapType.byMetadata( meta ) ); }
	
	@Override
	public
	int getMetaFromState( IBlockState state ) { return state.getValue( EnumFloorTrapType.PROPERTY ).getMetadata( ); }
	
	@Override
	public
	void getSubBlocks( CreativeTabs tab, NonNullList< ItemStack > items )
	{
		for( EnumFloorTrapType type : EnumFloorTrapType.values( ) ) {
			items.add( new ItemStack( this, 1, type.getMetadata( ) ) );
		}
	}
	
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
			initTileEntity( world, pos, state, Config.getOrDefault( world ), world.rand );
		}
	}
	
	@Override
	public
	Item getItemDropped( IBlockState state, Random rand, int fortune ) { return Items.AIR; }
	
	@Override
	public
	int damageDropped( IBlockState state ) { return state.getValue( EnumFloorTrapType.PROPERTY ).getMetadata( ); }
	
	@Override
	public
	int quantityDropped( Random random ) { return 0; }
	
	@Override
	public
	void getDrops( NonNullList< ItemStack > drops, IBlockAccess blockAccess, BlockPos pos, IBlockState state, int fortune )
	{
		if( !(blockAccess instanceof WorldServer) )
			return;
		WorldServer world = (WorldServer) blockAccess;
		LootTable   loot  = world.getLootTableManager( ).getLootTableFromLocation( state.getValue( EnumFloorTrapType.PROPERTY ).LOOT_TABLE_BLOCK );
		
		LootContext.Builder lootContext = new LootContext.Builder( world );
		if( harvesters.get( ) != null ) {
			lootContext.withLuck( harvesters.get( ).getLuck( ) ).withPlayer( harvesters.get( ) );
		}
		
		drops.addAll( loot.generateLootForPools( world.rand, lootContext.build( ) ) );
	}
	
	@Override
	public
	EnumBlockRenderType getRenderType( IBlockState state ) { return EnumBlockRenderType.MODEL; }
}
