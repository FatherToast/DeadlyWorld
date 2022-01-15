package fathertoast.deadlyworld.featuregen;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.loot.*;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

public
class WorldGenDeadlyDungeon extends WorldGenDeadlyWorldFeature
{
	private final ResourceLocation LOOT_TABLE_CHEST;
	
	WorldGenDeadlyDungeon( )
	{
		super( "dungeon" );
		LOOT_TABLE_CHEST = LootTableList.register( new ResourceLocation(
			DeadlyWorldMod.MOD_ID, FeatureGenerator.CHEST_LOOT_TABLE_PATH + NAME ) );
	}
	
	@Override
	public
	Config.FeatureConfig getFeatureConfig( Config dimensionConfig )
	{
		return dimensionConfig.DUNGEONS;
	}
	
	public
	void buildChestLootTable( LootTableBuilder loot )
	{
		loot.addThemePoolExploration( );
		loot.addLootTable( "base", "Vanilla Chest", LootTableList.CHESTS_SIMPLE_DUNGEON );
	}
	
	@Override
	public
	BlockPos placeFeature( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		return placeFeature( dimConfig, replaceableBlocks, world, random, position, new BoundingBox( dimConfig, random ) );
	}
	
	private
	BlockPos placeFeature( Config dimConfig, @SuppressWarnings( "unused" ) TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position, BoundingBox dungeonBB )
	{
		// Prepare silverfish blocks
		boolean silverfishEnabled = Config.get( ).GENERAL.SILVERFISH_AUTOGEN && dimConfig.DUNGEONS.SILVERFISH_CHANCE > 0.0F;
		
		// Build the structure
		for( int y = 4; y >= 0; y-- ) {
			WeightedBlockConfig.BlockList blocks = y > 0 ? dimConfig.DUNGEONS.WALL_BLOCKS : dimConfig.DUNGEONS.FLOOR_BLOCKS;
			
			for( int x = dungeonBB.MIN_X; x <= dungeonBB.MAX_X; x++ ) {
				for( int z = dungeonBB.MIN_Z; z <= dungeonBB.MAX_Z; z++ ) {
					BlockPos blockPos = position.add( x, y, z );
					
					IBlockState block = world.getBlockState( blockPos );
					if( !(block.getBlock( ) instanceof BlockChest) ) {
						if( y != 0 && x != dungeonBB.MIN_X && x != dungeonBB.MAX_X && z != dungeonBB.MIN_Z && z != dungeonBB.MAX_Z ) {
							// Clear out inside of feature
							world.setBlockToAir( blockPos );
						}
						else if( !world.getBlockState( blockPos.down( ) ).getMaterial( ).isSolid( ) ) {
							// Make all openings 1 block taller
							world.setBlockToAir( blockPos );
						}
						else if( block.getMaterial( ).isSolid( ) ) {
							// Decorate walls and floor
							if( silverfishEnabled && random.nextFloat( ) < dimConfig.DUNGEONS.SILVERFISH_CHANCE ) {
								// Place silverfish block
								setBlock( dimConfig, world, random, blockPos, ModObjects.infest( blocks.nextBlock( random, Blocks.COBBLESTONE ) ) );
							}
							else {
								// Place regular block
								setBlock( dimConfig, world, random, blockPos, blocks, Blocks.COBBLESTONE );
							}
						}
					}
				}
			}
		}
		
		// Place chests
		int varX = dungeonBB.RADIUS_X * 2 + 1;
		int varZ = dungeonBB.RADIUS_Z * 2 + 1;
		for( int chests = random.nextInt( dimConfig.DUNGEONS.CHESTS_VARIANCE ) + dimConfig.DUNGEONS.CHESTS_MIN; chests > 0; chests-- ) {
			for( int attempts = 0; attempts < 3; attempts++ ) {
				// Pick a random position along the wall inside the dungeon
				BlockPos chestPos;
				if( random.nextBoolean( ) ) {
					chestPos = position.add(
						random.nextInt( varX ) - dungeonBB.RADIUS_X,
						1,
						random.nextBoolean( ) ? dungeonBB.RADIUS_Z : -dungeonBB.RADIUS_Z
					);
				}
				else {
					chestPos = position.add(
						random.nextBoolean( ) ? dungeonBB.RADIUS_X : -dungeonBB.RADIUS_X,
						1,
						random.nextInt( varZ ) - dungeonBB.RADIUS_Z
					);
				}
				
				// The spot must be open and adjacent to a wall, but not in a corner
				if( world.isAirBlock( chestPos ) ) {
					int adjSolidBlocks = 0;
					for( EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL ) {
						if( world.getBlockState( chestPos.offset( enumfacing ) ).getMaterial( ).isSolid( ) ) {
							adjSolidBlocks++;
						}
					}
					if( adjSolidBlocks == 1 ) {
						FeatureGenerator.placeChest( chestPos, world, random, LOOT_TABLE_CHEST, false );
						break;
					}
				}
			}
		}
		
		// Choose and place central subfeature
		dimConfig.DUNGEONS.SUBFEATURE_LIST.nextItem( random ).getWorldGen( ).placeFeature( dimConfig, replaceableBlocks, world, random, position );
		
		return position;
	}
	
	// Generates this feature. Returns true if successful.
	@Override
	public
	BlockPos generate( Config dimConfig, TargetBlock.TargetMap replaceableBlocks, World world, Random random, BlockPos position )
	{
		// Use the same randomized bounding box for the check and the placement
		BoundingBox dungeonBB = new BoundingBox( dimConfig, random );
		if( canBePlaced( dimConfig, world, position, dungeonBB ) ) {
			return placeFeature( dimConfig, replaceableBlocks, world, random, position, dungeonBB );
		}
		return null;
	}
	
	@Override
	public
	boolean canBePlaced( Config dimConfig, World world, BlockPos position )
	{
		// Don't use this one, we need a dungeon position or random source
		return false; //this.canBePlaced( dimConfig, world, position, new Position( random ) );
	}
	
	private
	boolean canBePlaced( Config dimConfig, World world, BlockPos position, BoundingBox dungeonPos )
	{
		// Valid placement spots will have 1-5 openings in the would-be wall that are 2 blocks high, and have a solid floor and ceiling
		int openWallBlocks = 0;
		for( DungeonLevel level : DungeonLevel.values( ) ) {
			for( int x = dungeonPos.MIN_X; x <= dungeonPos.MAX_X; x++ ) {
				for( int z = dungeonPos.MIN_Z; z <= dungeonPos.MAX_Z; z++ ) {
					BlockPos blockPos = position.add( x, level.Y, z );
					
					switch( level ) {
						case FLOOR: case CEILING:
							if( !world.getBlockState( blockPos ).getMaterial( ).isSolid( ) )
								return false; // Floor and ceiling must all be solid blocks
							break;
						case FOOT:
							if( (x == dungeonPos.MIN_X || x == dungeonPos.MAX_X || z == dungeonPos.MIN_Z || z == dungeonPos.MAX_Z) &&
							    world.isAirBlock( blockPos ) && world.isAirBlock( blockPos.up( ) ) ) {
								openWallBlocks++; // Count the number of open wall spaces
								if( openWallBlocks > dimConfig.DUNGEONS.OPEN_WALLS_MAX )
									return false;
							}
							break;
					}
				}
			}
		}
		return openWallBlocks >= dimConfig.DUNGEONS.OPEN_WALLS_MIN;
	}
	
	private
	enum DungeonLevel
	{
		FLOOR(0), CEILING(5), FOOT(1);
		
		public final int Y;
		DungeonLevel( int y ) { Y = y; }
	}
	
	private
	class BoundingBox
	{
		final int RADIUS_X;
		final int RADIUS_Z;
		
		final int MIN_X;
		final int MAX_X;
		
		final int MIN_Z;
		final int MAX_Z;
		
		BoundingBox( Config dimConfig, Random random )
		{
			this(
				random.nextInt( dimConfig.DUNGEONS.WIDTH_VARIANCE ) + dimConfig.DUNGEONS.WIDTH_MIN,
				random.nextInt( dimConfig.DUNGEONS.WIDTH_VARIANCE ) + dimConfig.DUNGEONS.WIDTH_MIN
			);
		}
		
		BoundingBox( int radiusX, int radiusZ )
		{
			RADIUS_X = radiusX;
			RADIUS_Z = radiusZ;
			
			MIN_X = -radiusX - 1;
			MAX_X = radiusX + 1;
			
			MIN_Z = -radiusZ - 1;
			MAX_Z = radiusZ + 1;
		}
	}
}
