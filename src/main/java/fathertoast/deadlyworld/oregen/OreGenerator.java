package fathertoast.deadlyworld.oregen;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public
class OreGenerator
{
	private static final IBlockState DIORITE  = Blocks.STONE.getDefaultState( ).withProperty( BlockStone.VARIANT, BlockStone.EnumType.DIORITE );
	private static final IBlockState GRANITE  = Blocks.STONE.getDefaultState( ).withProperty( BlockStone.VARIANT, BlockStone.EnumType.GRANITE );
	private static final IBlockState ANDESITE = Blocks.STONE.getDefaultState( ).withProperty( BlockStone.VARIANT, BlockStone.EnumType.ANDESITE );
	
	// The same generators are used for each dimension; only their driving-configs are swapped.
	private WorldGenDeadlyMinable genLava       = new WorldGenDeadlyMinable( ).setFill( Blocks.LAVA.getDefaultState( ) ).setCovered( );
	private WorldGenDeadlyMinable genWater      = new WorldGenDeadlyMinable( ).setFill( Blocks.WATER.getDefaultState( ) ).setCovered( );
	// Silverfish veins will get better logic at some point, to handle more block disguises.
	private WorldGenDeadlyMinable genSilverfish = new WorldGenDeadlyMinable( ).setFill( Blocks.MONSTER_EGG.getDefaultState( ) );
	
	private WorldGenDeadlyMinable genUserDefinedOre = new WorldGenDeadlyMinable( );
	private WorldGenDeadlyMinable genExtraOre       = new WorldGenDeadlyMinable( );
	
	@SubscribeEvent( priority = EventPriority.LOWEST )
	public
	void preOreGen( OreGenEvent.Pre event )
	{
		World  world  = event.getWorld( );
		Config config = Config.get( world );
		if( config != null ) {
			Random   random         = event.getRand( );
			BlockPos chunkCenterPos = event.getPos( ).add( 8, 0, 8 );
			
			generateOre( config, config.VEIN_LAVA, genLava, world, random, chunkCenterPos );
			generateOre( config, config.VEIN_WATER, genWater, world, random, chunkCenterPos );
			
			int userDefinedVeinCount = config.VEINS.USER_DEFINED_VEIN_COUNT;
			for( int i = 0; i < userDefinedVeinCount; i++ ) {
				Config.VeinUserDefined veinConfig = config.VEINS.USER_DEFINED_VEINS[ i ];
				generateOre( config, veinConfig, genUserDefinedOre.setFill( veinConfig.FILL_BLOCK ).setCovered( veinConfig.COVERED ),
				             world, random, chunkCenterPos );
			}
			
			generateOre( config, config.VEIN_DIRT, genExtraOre.setFill( Blocks.DIRT.getDefaultState( ) ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_GRAVEL, genExtraOre.setFill( Blocks.GRAVEL.getDefaultState( ) ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_SAND, genExtraOre.setFill( Blocks.SAND.getDefaultState( ) ), world, random, chunkCenterPos );
			
			generateOre( config, config.VEIN_DIORITE, genExtraOre.setFill( DIORITE ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_GRANITE, genExtraOre.setFill( GRANITE ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_ANDESITE, genExtraOre.setFill( ANDESITE ), world, random, chunkCenterPos );
		}
	}
	
	@SubscribeEvent( priority = EventPriority.HIGHEST )
	public
	void postOreGen( OreGenEvent.Post event )
	{
		World  world  = event.getWorld( );
		Config config = Config.get( world );
		if( config != null ) {
			Random   random         = event.getRand( );
			BlockPos chunkCenterPos = event.getPos( ).add( 8, 0, 8 );
			
			generateOre( config, config.VEIN_COAL, genExtraOre.setFill( Blocks.COAL_ORE.getDefaultState( ) ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_QUARTZ, genExtraOre.setFill( Blocks.QUARTZ_ORE.getDefaultState( ) ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_IRON, genExtraOre.setFill( Blocks.IRON_ORE.getDefaultState( ) ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_GOLD, genExtraOre.setFill( Blocks.GOLD_ORE.getDefaultState( ) ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_REDSTONE, genExtraOre.setFill( Blocks.REDSTONE_ORE.getDefaultState( ) ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_DIAMOND, genExtraOre.setFill( Blocks.DIAMOND_ORE.getDefaultState( ) ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_LAPIS, genExtraOre.setFill( Blocks.LAPIS_ORE.getDefaultState( ) ), world, random, chunkCenterPos );
			generateOre( config, config.VEIN_EMERALD, genExtraOre.setFill( Blocks.EMERALD_ORE.getDefaultState( ) ), world, random, chunkCenterPos );
			
			generateOre( config, config.VEIN_SILVERFISH, genSilverfish, world, random, chunkCenterPos );
		}
	}
	
	@SubscribeEvent( priority = EventPriority.HIGHEST )
	public
	void oreGen( OreGenEvent.GenerateMinable event )
	{
		World  world  = event.getWorld( );
		Config config = Config.get( world );
		if( config != null ) {
			switch( event.getType( ) ) {
				case COAL:
					if( config.VEINS.DISABLE_COAL_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case DIAMOND:
					if( config.VEINS.DISABLE_DIAMOND_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case DIRT:
					if( config.VEINS.DISABLE_DIRT_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case GOLD:
					if( config.VEINS.DISABLE_GOLD_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case GRAVEL:
					if( config.VEINS.DISABLE_GRAVEL_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case IRON:
					if( config.VEINS.DISABLE_IRON_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case LAPIS:
					if( config.VEINS.DISABLE_LAPIS_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case REDSTONE:
					if( config.VEINS.DISABLE_REDSTONE_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case QUARTZ:
					if( config.VEINS.DISABLE_QUARTZ_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case DIORITE:
					if( config.VEINS.DISABLE_DIORITE_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case GRANITE:
					if( config.VEINS.DISABLE_GRANITE_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case ANDESITE:
					if( config.VEINS.DISABLE_ANDESITE_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case EMERALD:
					if( config.VEINS.DISABLE_EMERALD_VEINS )
						event.setResult( Event.Result.DENY );
					break;
				case SILVERFISH:
					if( config.VEINS.DISABLE_SILVERFISH_VEINS )
						event.setResult( Event.Result.DENY );
					break;
			}
		}
	}
	
	private
	void generateOre( Config config, Config.Vein veinConfig, WorldGenDeadlyMinable generator, World world, Random random, BlockPos chunkCenterPos )
	{
		for( double count = veinConfig.PLACEMENTS; count >= 1.0 || count > 0.0 && count > random.nextDouble( ); count-- ) {
			BlockPos veinPos = chunkCenterPos.add( random.nextInt( 16 ), nextHeight( veinConfig, random ), random.nextInt( 16 ) );
			
			boolean gen = generator.generate( nextSize( veinConfig, random ), config.TERRAIN.REPLACEABLE_BLOCKS, world, random, veinPos );
			
			if( gen && veinConfig.DEBUG_MARKER ) {
				DeadlyWorldMod.mark( world, veinPos, generator.getFill( ) );
			}
		}
	}
	
	private static
	int nextSize( Config.Vein veinConfig, Random random )
	{
		if( veinConfig.SIZES[ 0 ] >= veinConfig.SIZES[ 1 ] )
			return veinConfig.SIZES[ 0 ];
		return random.nextInt( veinConfig.SIZES[ 1 ] - veinConfig.SIZES[ 0 ] ) + veinConfig.SIZES[ 0 ];
	}
	
	private static
	int nextHeight( Config.Vein veinConfig, Random random )
	{
		if( veinConfig.HEIGHTS[ 0 ] >= veinConfig.HEIGHTS[ 1 ] )
			return veinConfig.HEIGHTS[ 0 ];
		return random.nextInt( veinConfig.HEIGHTS[ 1 ] - veinConfig.HEIGHTS[ 0 ] ) + veinConfig.HEIGHTS[ 0 ];
	}
}
