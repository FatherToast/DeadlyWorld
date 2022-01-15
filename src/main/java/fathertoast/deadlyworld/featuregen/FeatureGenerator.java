package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public
class FeatureGenerator
{
	/** This flag causes a block update. */
	public static final int FLAG_BLOCK_UPDATE      = 0b00001;
	/** This flag sends the change to clients (server). */
	public static final int FLAG_NOTIFY_CLIENTS    = 0b00010;
	/** This flag prevents the block from being re-rendered (client). */
	@SuppressWarnings( "unused" )
	public static final int FLAG_NO_RERENDER       = 0b00100;
	/** This flag forces any re-renders to run on the main thread instead of the worker pool (client). */
	public static final int FLAG_PRIORITY_RERENDER = 0b01000;
	/** This flag prevents observers from seeing the change. */
	@SuppressWarnings( "WeakerAccess" )
	public static final int FLAG_NO_OBSERVER       = 0b10000;
	
	/** For world generation, we want to cause as few updates as possible, but the client does need to receive changes. */
	public static final int UPDATE_FLAGS = FLAG_NOTIFY_CLIENTS | FLAG_NO_OBSERVER;
	
	public static final String CHEST_LOOT_TABLE_PATH = "feature_chests/";
	
	// List of all feature generators in the mod.
	public static final List< WorldGenDeadlyWorldFeature > FEATURE_LIST = Arrays.asList(
		// Tower traps
		EnumTowerType.FIREBALL.makeWorldGen( ),
		EnumTowerType.GATLING.makeWorldGen( ),
		EnumTowerType.POTION.makeWorldGen( ),
		EnumTowerType.FIRE.makeWorldGen( ),
		EnumTowerType.DEFAULT.makeWorldGen( ),
		
		// Floor traps
		EnumFloorTrapType.TNT.makeWorldGen( ),
		EnumFloorTrapType.TNT_MOB.makeWorldGen( ),
		EnumFloorTrapType.POTION.makeWorldGen( ),
		
		// Spawners
		EnumSpawnerType.SILVERFISH_NEST.makeWorldGen( ),
		EnumSpawnerType.BRUTAL.makeWorldGen( ),
		EnumSpawnerType.SWARM.makeWorldGen( ),
		EnumSpawnerType.STREAM.makeWorldGen( ),
		EnumSpawnerType.DEFAULT.makeWorldGen( ),
		
		// Chests
		EnumChestType.VALUABLE.makeWorldGen( ),
		EnumChestType.MIMIC.makeWorldGen( ),
		EnumChestType.SURPRISE.makeWorldGen( ),
		EnumChestType.TNT_FLOOR_TRAP.makeWorldGen( ),
		EnumChestType.INFESTED.makeWorldGen( ),
		EnumChestType.DEFAULT.makeWorldGen( ),
		EnumChestType.TRAPPED.makeWorldGen( )
	);
	
	public static final WorldGenDeadlyDungeon DUNGEON_FEATURE = new WorldGenDeadlyDungeon( );
	
	@SubscribeEvent( priority = EventPriority.LOWEST )
	public
	void prePopulateChunk( PopulateChunkEvent.Pre event )
	{
		World  world  = event.getWorld( );
		Config config = Config.get( world );
		if( config != null ) {
			Random   random         = event.getRand( );
			BlockPos chunkCenterPos = new BlockPos( (event.getChunkX( ) << 4) + 8, 0, (event.getChunkZ( ) << 4) + 8 );
			
			for( WorldGenDeadlyWorldFeature feature : FEATURE_LIST ) {
				generateFeature( config, feature, world, random, chunkCenterPos );
			}
		}
	}
	
	@SubscribeEvent( priority = EventPriority.HIGHEST )
	public
	void populateChunkTerrain( PopulateChunkEvent.Populate event )
	{
		World  world  = event.getWorld( );
		Config config = Config.get( world );
		if( config != null ) {
			switch( event.getType( ) ) {
				case DUNGEON:
					if( config.DUNGEONS.DISABLE_VANILLA_DUNGEONS )
						event.setResult( Event.Result.DENY );
					break;
				case NETHER_LAVA2: // Vanilla lava veins
					if( config.VEINS.DISABLE_LAVA_VEINS )
						event.setResult( Event.Result.DENY );
					break;
			}
		}
	}
	
	@SubscribeEvent( priority = EventPriority.HIGHEST )
	public
	void postPopulateChunk( PopulateChunkEvent.Post event )
	{
		World  world  = event.getWorld( );
		Config config = Config.get( world );
		if( config != null ) {
			Random   random         = event.getRand( );
			BlockPos chunkCenterPos = new BlockPos( (event.getChunkX( ) << 4) + 8, 0, (event.getChunkZ( ) << 4) + 8 );
			
			generateMultiFeature( config, DUNGEON_FEATURE, world, random, chunkCenterPos );
		}
	}
	
	private
	void generateFeature( Config dimConfig, WorldGenDeadlyWorldFeature generator, World world, Random random, BlockPos chunkCenterPos )
	{
		Config.FeatureConfig featureConfig   = generator.getFeatureConfig( dimConfig );
		float                placementChance = featureConfig.getPlacementChance( world, chunkCenterPos );
		if( placementChance > 0.0F && random.nextFloat( ) < placementChance ) {
			BlockPos featurePos = chunkCenterPos.add( random.nextInt( 16 ), nextHeight( featureConfig, random ), random.nextInt( 16 ) );
			
			featurePos = generator.generate( dimConfig, dimConfig.TERRAIN.REPLACEABLE_BLOCKS, world, random, featurePos );
			
			if( featurePos != null && featureConfig.DEBUG_MARKER ) {
				DeadlyWorldMod.mark( world, featurePos.up( 5 ) );
			}
		}
	}
	
	@SuppressWarnings( "SameParameterValue" )
	private
	void generateMultiFeature( Config dimConfig, WorldGenDeadlyWorldFeature generator, World world, Random random, BlockPos chunkCenterPos )
	{
		Config.FeatureMulti featureConfig = (Config.FeatureMulti) generator.getFeatureConfig( dimConfig );
		for( float count = featureConfig.getPlacementChance( world, chunkCenterPos ); count >= 1.0F || count > 0.0F && count > random.nextFloat( ); count-- ) {
			BlockPos featurePos = chunkCenterPos.add( random.nextInt( 16 ), nextHeight( featureConfig, random ), random.nextInt( 16 ) );
			
			featurePos = generator.generate( dimConfig, dimConfig.TERRAIN.REPLACEABLE_BLOCKS, world, random, featurePos );
			
			if( featurePos != null && featureConfig.DEBUG_MARKER ) {
				DeadlyWorldMod.mark( world, featurePos.up( 5 ) );
			}
		}
	}
	
	private static
	int nextHeight( Config.FeatureConfig featureConfig, Random random )
	{
		if( featureConfig.HEIGHTS[ 0 ] >= featureConfig.HEIGHTS[ 1 ] )
			return featureConfig.HEIGHTS[ 0 ];
		return random.nextInt( featureConfig.HEIGHTS[ 1 ] - featureConfig.HEIGHTS[ 0 ] ) + featureConfig.HEIGHTS[ 0 ];
	}
	
	// Helper methods below
	
	static
	void placeChest( BlockPos chestPos, World world, Random random, ResourceLocation lootTable, boolean trapped )
	{
		IBlockState chestState = trapped ? Blocks.TRAPPED_CHEST.getDefaultState( ) : Blocks.CHEST.getDefaultState( );
		chestState = chestState.withProperty( BlockChest.FACING, EnumFacing.Plane.HORIZONTAL.random( random ) );
		world.setBlockState( chestPos, Blocks.CHEST.correctFacing( world, chestPos, chestState ), FeatureGenerator.UPDATE_FLAGS );
		
		TileEntity chestInventory = world.getTileEntity( chestPos );
		if( chestInventory instanceof TileEntityChest ) {
			((TileEntityChest) chestInventory).setLootTable( lootTable, random.nextLong( ) );
		}
		else {
			DeadlyWorldMod.log( ).error( "Failed to fetch chest tile entity at [{}]! Expect an empty chest. :(", chestPos );
		}
	}
}
