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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public
class BlockDeadlySpawner extends BlockContainer
{
	public static final String ID = "deadly_spawner";
	
	public
	BlockDeadlySpawner( )
	{
		super( Material.IRON, MapColor.STONE );
		setSoundType( SoundType.METAL );
		
		setHardness( Config.get( ).GENERAL.SPAWNER_HARDNESS );
		setResistance( Config.get( ).GENERAL.SPAWNER_RESISTANCE );
		setHarvestLevel( ModObjects.TOOL_NAME_PICKAXE, Config.get( ).GENERAL.SPAWNER_HARVEST_LEVEL );
		
		setDefaultState( blockState.getBaseState( ).withProperty( EnumSpawnerType.PROPERTY, EnumSpawnerType.DEFAULT ) );
	}
	
	public
	void initTileEntity( World world, BlockPos pos, IBlockState state, Config dimConfig, Random random )
	{
		TileEntity spawnerData = world.getTileEntity( pos );
		if( spawnerData instanceof TileEntityDeadlySpawner ) {
			((TileEntityDeadlySpawner) spawnerData).initializeSpawner( state.getValue( EnumSpawnerType.PROPERTY ), dimConfig, random );
		}
		else {
			DeadlyWorldMod.log( ).error( "Failed to fetch mob spawner tile entity at [{}]!", pos );
		}
	}
	
	@Override
	public
	TileEntity createNewTileEntity( World world, int meta ) { return new TileEntityDeadlySpawner( ); }
	
	@Override
	protected
	BlockStateContainer createBlockState( ) { return new BlockStateContainer( this, EnumSpawnerType.PROPERTY ); }
	
	// Will be removed in 1.13
	@SuppressWarnings( "deprecation" )
	@Override
	public
	IBlockState getStateFromMeta( int meta ) { return getDefaultState( ).withProperty( EnumSpawnerType.PROPERTY, EnumSpawnerType.byMetadata( meta ) ); }
	
	@Override
	public
	int getMetaFromState( IBlockState state ) { return state.getValue( EnumSpawnerType.PROPERTY ).getMetadata( ); }
	
	@Override
	public
	void getSubBlocks( CreativeTabs tab, NonNullList< ItemStack > items )
	{
		for( EnumSpawnerType type : EnumSpawnerType.values( ) ) {
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
	int damageDropped( IBlockState state ) { return state.getValue( EnumSpawnerType.PROPERTY ).getMetadata( ); }
	
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
		LootTable   loot  = world.getLootTableManager( ).getLootTableFromLocation( state.getValue( EnumSpawnerType.PROPERTY ).LOOT_TABLE_BLOCK );
		
		LootContext.Builder lootContext = new LootContext.Builder( world );
		if( harvesters.get( ) != null ) {
			lootContext.withLuck( harvesters.get( ).getLuck( ) ).withPlayer( harvesters.get( ) );
		}
		
		drops.addAll( loot.generateLootForPools( world.rand, lootContext.build( ) ) );
	}
	
	@Override
	@SuppressWarnings( "deprecation" )
	public
	boolean isOpaqueCube( IBlockState state ) { return false; }
	
	@Override
	public
	EnumBlockRenderType getRenderType( IBlockState state ) { return EnumBlockRenderType.MODEL; }
	
	@Override
	@SideOnly( Side.CLIENT )
	public
	BlockRenderLayer getBlockLayer( ) { return BlockRenderLayer.CUTOUT; }
}
