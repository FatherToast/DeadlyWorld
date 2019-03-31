package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public
class FeatureGenerator
{
	public static final int UPDATE_FLAGS = 18;
	
	// List of all feature generators in the mod.
	public static final List< WorldGenDeadlyFeature > FEATURE_LIST = Arrays.asList(
		EnumSpawnerType.SILVERFISH_NEST.makeWorldGen( ),
		EnumSpawnerType.BRUTAL.makeWorldGen( ),
		EnumSpawnerType.SWARM.makeWorldGen( ),
		EnumSpawnerType.STREAM.makeWorldGen( ),
		EnumSpawnerType.LONE.makeWorldGen( ),
		new WorldGenChest( "lone chest" )
	);
	
	@SubscribeEvent( priority = EventPriority.LOWEST )
	public
	void prePopulateChunk( PopulateChunkEvent.Pre event )
	{
		World  world  = event.getWorld( );
		Config config = Config.get( world );
		if( config != null ) {
			Random   random         = event.getRand( );
			BlockPos chunkCenterPos = new BlockPos( (event.getChunkX( ) << 4) + 8, 0, (event.getChunkZ( ) << 4) + 8 );
			
			for( WorldGenDeadlyFeature feature : FEATURE_LIST ) {
				generateFeature( config, feature, world, random, chunkCenterPos );
			}
		}
	}
	
	private
	void generateFeature( Config dimConfig, WorldGenDeadlyFeature generator, World world, Random random, BlockPos chunkCenterPos )
	{
		Config.FeatureConfig featureConfig = generator.getFeatureConfig( dimConfig );
		if( featureConfig.PLACEMENT_CHANCE > 0.0 && random.nextDouble( ) < featureConfig.PLACEMENT_CHANCE ) {
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
	void placeChest( BlockPos chestPos, World world, Random random, ResourceLocation lootTable )
	{
		world.setBlockState( chestPos, Blocks.CHEST.correctFacing( world, chestPos, Blocks.CHEST.getDefaultState( ) ), FeatureGenerator.UPDATE_FLAGS );
		
		TileEntity chestInventory = world.getTileEntity( chestPos );
		if( chestInventory instanceof TileEntityChest ) {
			((TileEntityChest) chestInventory).setLootTable( lootTable, random.nextLong( ) );
		}
		else {
			DeadlyWorldMod.log( ).error( "Failed to fetch chest tile entity at [{}]! Expect an empty chest. :(", chestPos );
		}
	}
}
