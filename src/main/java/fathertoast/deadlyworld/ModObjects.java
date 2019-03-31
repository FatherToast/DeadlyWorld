package fathertoast.deadlyworld;

import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.item.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers everything that needs to be registered with Forge and stores references to those objects.
 * <p>
 * "RegistryEvents are currently supported for the following types:
 * Block, Item, Potion, Biome, SoundEvent, PotionType, Enchantment, IRecipe, VillagerProfession, EntityEntry"
 * <p>
 * See: <url>https://mcforge.readthedocs.io/en/latest/concepts/registries/</url>
 */
@SuppressWarnings( { "SameParameterValue", "WeakerAccess" } )
public
class ModObjects
{
	public static final BlockDeadlySpawner DEADLY_SPAWNER = addInfo( "deadly_spawner", CreativeTabs.DECORATIONS, new BlockDeadlySpawner( ) );
	
	public static final Item FEATURE_TESTER = addInfo( "feature_tester", CreativeTabs.MISC, new ItemFeatureTester( ) );
	
	private static final List< BlockDeadlySilverfish >       SILVERFISH_ORDERED = new ArrayList<>( );
	private static final Map< Block, BlockDeadlySilverfish > SILVERFISH_LOOKUP  = new HashMap<>( );
	
	public static
	BlockDeadlySilverfish getInfestedVersion( Block block ) { return SILVERFISH_LOOKUP.get( block ); }
	
	public static
	IBlockState getInfestedVersion( IBlockState state )
	{
		BlockDeadlySilverfish infested = getInfestedVersion( state.getBlock( ) );
		if( infested != null ) {
			return infested.fromDisguise( state );
		}
		return null;
	}
	
	public static
	IBlockState getInfestedVersionIfReplaceable( IBlockState state )
	{
		if( Config.get( ).GENERAL.SILVERFISH_REPLACEABLE.matches( state ) ) {
			return getInfestedVersion( state );
		}
		return null;
	}
	
	// The list of infested silverfish blocks in the order they were registered.
	public static
	List< BlockDeadlySilverfish > getSilverfishBlocksOrdered( ) { return SILVERFISH_ORDERED; }
	
	private static
	< T extends Block > T addInfo( String name, CreativeTabs tab, T block )
	{
		block.setRegistryName( DeadlyWorldMod.MOD_ID, name ).setUnlocalizedName( DeadlyWorldMod.LANG_KEY + name ).setCreativeTab( tab );
		return block;
	}
	
	private static
	void buildSilverfishBlock( Block block )
	{
		if( SILVERFISH_LOOKUP.containsKey( block ) ) {
			return;
		}
		ResourceLocation disguiseRegName = block.getRegistryName( );
		if( disguiseRegName == null ) {
			DeadlyWorldMod.log( ).error( "Failed to find registry entry for block '{}'", block );
			return;
		}
		
		BlockDeadlySilverfish infestedBlock = BlockDeadlySilverfish.buildFor( block );
		ResourceLocation infestedRegName = new ResourceLocation(
			DeadlyWorldMod.MOD_ID,
			"infested_" + disguiseRegName.getResourceDomain( ) + "_" + disguiseRegName.getResourcePath( )
		);
		infestedBlock.setRegistryName( infestedRegName ).setUnlocalizedName( block.getUnlocalizedName( ).substring( "tile.".length( ) ) )
			.setCreativeTab( CreativeTabs.DECORATIONS );
		
		SILVERFISH_ORDERED.add( infestedBlock );
		SILVERFISH_LOOKUP.put( block, infestedBlock );
	}
	
	private static
	< T extends Item > T addInfo( String name, CreativeTabs tab, T item )
	{
		item.setRegistryName( DeadlyWorldMod.MOD_ID, name ).setUnlocalizedName( DeadlyWorldMod.LANG_KEY + name ).setCreativeTab( tab );
		return item;
	}
	
	// Used for now until there is a Forge RegistryEvent for the TileEntity registry.
	public static
	void registerTileEntities( )
	{
		GameRegistry.registerTileEntity( TileEntityDeadlySpawner.class, new ResourceLocation( DeadlyWorldMod.MOD_ID, "deadly_spawner" ) );
		GameRegistry.registerTileEntity( TileEntityDeadlyTrap.class, new ResourceLocation( DeadlyWorldMod.MOD_ID, "deadly_trap" ) );
	}
	
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public
	void registerBlocks( RegistryEvent.Register< Block > event )
	{
		event.getRegistry( ).registerAll(
			DEADLY_SPAWNER
		);
	}
	
	@SubscribeEvent( priority = EventPriority.LOWEST )
	public
	void registerBlocksLate( RegistryEvent.Register< Block > event )
	{
		SILVERFISH_ORDERED.clear( );
		SILVERFISH_LOOKUP.clear( );
		
		buildSilverfishBlock( Blocks.COBBLESTONE );
		buildSilverfishBlock( Blocks.MOSSY_COBBLESTONE );
		if( Config.get( ).GENERAL.SILVERFISH_AUTOGEN ) {
			List< Block > disguiseBlocks = Config.get( ).GENERAL.SILVERFISH_REPLACEABLE.getSortedBlocks( );
			for( Block block : disguiseBlocks ) {
				buildSilverfishBlock( block );
			}
		}
		
		event.getRegistry( ).registerAll(
			SILVERFISH_ORDERED.toArray( new BlockDeadlySilverfish[ 0 ] )
		);
	}
	
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public
	void registerItems( RegistryEvent.Register< Item > event )
	{
		event.getRegistry( ).registerAll(
			itemForBlock( DEADLY_SPAWNER, itemStack -> EnumSpawnerType.byMetadata( itemStack.getMetadata( ) ).getName( ) )
		);
		if( Config.get( ).GENERAL.FEATURE_TESTER ) {
			event.getRegistry( ).register( FEATURE_TESTER );
		}
		
		for( BlockDeadlySilverfish infestedBlock : SILVERFISH_ORDERED ) {
			event.getRegistry( ).register( new ItemBlockDeadlySilverfish( infestedBlock ).setRegistryName( infestedBlock.getRegistryName( ) ) );
		}
	}
	
	@SubscribeEvent( priority = EventPriority.NORMAL )
	public
	void registerModels( ModelRegistryEvent event )
	{
		DeadlyWorldMod.sidedProxy.registerModels( );
	}
	
	private
	Item itemForBlock( Block block )
	{
		return new ItemBlock( block ).setRegistryName( block.getRegistryName( ) );
	}
	
	private
	Item itemForBlock( Block block, ItemMultiTexture.Mapper mapper )
	{
		return new ItemMultiTexture( block, block, mapper ).setRegistryName( block.getRegistryName( ) );
	}
}
