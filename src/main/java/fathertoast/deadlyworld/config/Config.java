package fathertoast.deadlyworld.config;

import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.item.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.*;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This helper class manages and stores references to user-defined configurations.
 * <p>
 * This special version provides a separate config per dimension and makes the configs in a subfolder.
 */
public
class Config
{
	/** @return The mod's main config. Also used as the main dimension's config. */
	public static
	Config get( ) { return Config.INSTANCE; }
	
	/** @return The config for the specified world's dimension. Null if the mod is not enabled for that dimension. */
	@Nullable
	public static
	Config get( World world )
	{
		int dimId = world.provider.getDimensionType( ) == null ? world.provider.getDimension( ) : world.provider.getDimensionType( ).getId( );
		if( dimId == MAIN_DIM.getId( ) ) {
			return Config.ENABLED_FOR_MAIN_DIM ? get( ) : null;
		}
		return Config.DIMENSION_INSTANCES.get( dimId );
	}
	
	/** @return The config for the specified world's dimension. Falls back to the main config if the mod is not enabled for that dimension. */
	public static
	Config getOrDefault( World world )
	{
		Config dimConfig = get( world );
		return dimConfig != null ? dimConfig : get( );
	}
	
	/** @return A list of all configs that are enabled. */
	public static
	List< Config > getAllEnabled( )
	{
		List< Config > configs = new ArrayList<>( );
		if( Config.ENABLED_FOR_MAIN_DIM ) {
			configs.add( Config.INSTANCE );
		}
		configs.addAll( Config.DIMENSION_INSTANCES.values( ) );
		return configs;
	}
	
	/** @return The most readable dimension name corresponding to the passed world, as it would appear when used in the config itself. */
	public static
	String getDimensionKey( World world )
	{
		return world.provider.getDimensionType( ) == null ? Integer.toString( world.provider.getDimension( ) ) : world.provider.getDimensionType( ).getName( );
	}
	
	/** Loads the configs for this mod. */
	public static
	void load( Logger logger, final String folderName, final File configDir )
	{
		Config.log = logger;
		Config.log.info( "Loading configs..." );
		long startTime = System.nanoTime( );
		
		// Set up config subfolder
		File modConfigDir = new File( configDir, folderName );
		modConfigDir.mkdir( );
		
		// Global mod config
		Config.dimensionLoading = MAIN_DIM.getId( );
		Config.configLoading = new Configuration( new File( modConfigDir, "_Main_Config_and_" + getDimemsionFileName( MAIN_DIM ) + FILE_EXT ) );
		Config.configLoading.load( );
		Config.INSTANCE = new Config( );
		Config.configLoading.save( );
		Config.configLoading = null;
		
		// Dimension-specific configs
		Config.loadDimensionFiles( modConfigDir );
		
		long estimatedTime = System.nanoTime( ) - startTime;
		Config.log.info( "Loaded configs in {} ms", estimatedTime / 1.0E6 );
	}
	
	/** Loads the config for a single dimension. */
	private static
	void loadDim( int dimId, String dimension, String fileName, final File modConfigDir )
	{
		if( DIMENSION_INSTANCES.containsKey( dimId ) ) {
			Config.log.warn( "Skipping duplicate dimension '{}'! Please review the list of dimensions you have enabled for this mod.", dimension );
			return;
		}
		Config.dimensionLoading = dimId;
		Config.configLoading = new Configuration( new File( modConfigDir, fileName + FILE_EXT ) );
		Config.configLoading.load( );
		DIMENSION_INSTANCES.put( dimId, new Config( ) );
		Config.configLoading.save( );
		Config.configLoading = null;
	}
	
	/** Loads the configs for all enabled dimensions and flags the main dimension as enabled or disabled. */
	private static
	void loadDimensionFiles( final File modConfigDir )
	{
		Config.ENABLED_FOR_MAIN_DIM = false;
		String[] dimensions = Config.INSTANCE.GENERAL.ENABLED_DIMENSIONS;
		Config.log.info( "Enabled for {} dimensions; loading additional dimension configs...", dimensions.length );
		
		for( String dimension : dimensions ) {
			// Determine the dimension key and file name
			int    dimId;
			String fileSuffix;
			try {
				DimensionType type = DimensionType.byName( dimension );
				dimId = type.getId( );
				fileSuffix = getDimemsionFileName( type );
			}
			catch( Exception ex ) {
				// Not a dimension name, silently try parsing as a numerical id
				try {
					dimId = Integer.parseInt( dimension );
					fileSuffix = dimension;
					
					// Check if this id could be replaced by a readable name
					try {
						DimensionType type = DimensionType.getById( dimId );
						if( type != null ) {
							// The id is replaceable, change file destination and alert the user
							fileSuffix = getDimemsionFileName( type );
							Config.log.warn(
								"Detected dimension id '{}' that can be replaced by a readable name! " +
								"Please change it to dimension name '{}' to avoid problems in the future.",
								dimension, type.getName( )
							);
						}
					}
					catch( Exception ignore ) { }
				}
				catch( NumberFormatException nfex ) {
					// Not a name or a number, not valid
					Config.log.error( "Skipping invalid dimension '{}'! Please review the list of dimensions you have enabled for this mod.", dimension );
					continue;
				}
			}
			
			if( dimId == MAIN_DIM.getId( ) ) {
				// Flag the main dimension as enabled; its config is already loaded
				Config.ENABLED_FOR_MAIN_DIM = true;
			}
			else {
				// Load the dimension's config
				loadDim( dimId, dimension, fileSuffix, modConfigDir );
			}
		}
	}
	
	/**
	 * @return A file-name-friendly version of the dimension name.
	 * Generally should do nothing, it's just here to prevent other mods from mucking things up.
	 */
	private static
	String getDimemsionFileName( DimensionType dimensionType ) { return dimensionType.getName( ).replace( " ", "_" ).toLowerCase( ); }
	
	// The file extension used by configs.
	private static final String FILE_EXT = ".cfg";
	
	// Logger used by the config-loading classes.
	static         Logger        log;
	// Dimension id for the config currently being loaded.
	private static int           dimensionLoading;
	// Config file currently being loaded. Null when not loading any file.
	private static Configuration configLoading;
	
	// The main config.
	private static Config INSTANCE;
	
	// Fields related to per-dimension configs.
	private static final DimensionType              MAIN_DIM            = DimensionType.OVERWORLD;
	private static       boolean                    ENABLED_FOR_MAIN_DIM;
	private static final HashMap< Integer, Config > DIMENSION_INSTANCES = new HashMap<>( );
	
	private
	Config( ) { }
	
	// General category applies to all dimensions.
	public final GENERAL GENERAL = Config.dimensionLoading == MAIN_DIM.getId( ) ? new GENERAL( ) : null;
	
	public
	class GENERAL extends PropertyCategory
	{
		@Override
		String name( ) { return "_general"; }
		
		@Override
		String comment( )
		{
			return "General and/or miscellaneous options not related to a specific dimension.";
		}
		
		public final boolean DEBUG = prop(
			"_debug_mode", false,
			"If true, the mod will start up in debug mode."
		);
		
		private final String[] ENABLED_DIMENSIONS = prop(
			"_enabled_dimensions", new String[] {
				DimensionType.OVERWORLD.getName( ),
				DimensionType.NETHER.getName( )
			},
			"The dimensions that Deadly World will alter terrain generation in.\n" +
			"This main config doubles as the overworld config file (dimension 0).\n" +
			"Other dimensions each have their own config file.",
			"dimension_name, dimension_id"
		);
		
		public final float FLOOR_TRAP_HARDNESS      = prop(
			"block_hardness_floor_trap", 5.0F,
			"How long it takes to break floor traps."
		);
		public final int   FLOOR_TRAP_HARVEST_LEVEL = prop(
			"block_harvest_level_floor_trap", 1,
			"The level of pickaxe required to break floor traps in a reasonable time.\n" +
			"For vanilla: Wood/Gold = 0, Stone = 1, Iron = 2, Diamond = 3."
		);
		public final float FLOOR_TRAP_RESISTANCE    = prop(
			"block_resistance_floor_trap", 25.0F,
			"How resistant floor traps are to being destroyed by explosions.\n" +
			"Typical explosion resistance is 5 times the hardness."
		);
		
		public final float SPAWNER_HARDNESS      = prop(
			"block_hardness_spawners", 10.0F,
			"How long it takes to break Deadly World spawners.\n" +
			"The default hardness is the same as vanilla mob spawners."
		);
		public final int   SPAWNER_HARVEST_LEVEL = prop(
			"block_harvest_level_spawners", 2,
			"The level of pickaxe required to break Deadly World spawners in a reasonable time.\n" +
			"For vanilla: Wood/Gold = 0, Stone = 1, Iron = 2, Diamond = 3."
		);
		public final float SPAWNER_RESISTANCE    = prop(
			"block_resistance_spawners", 2000.0F,
			"How resistant Deadly World spawners are to being destroyed by explosions.\n" +
			"The default explosion resistance is the same as vanilla obsidian."
		);
		
		public final float TOWER_DISPENSER_HARDNESS      = prop(
			"block_hardness_tower_dispenser", 10.0F,
			"How long it takes to break tower dispensers."
		);
		public final int   TOWER_DISPENSER_HARVEST_LEVEL = prop(
			"block_harvest_level_tower_dispenser", 2,
			"The level of pickaxe required to break tower dispensers in a reasonable time.\n" +
			"For vanilla: Wood/Gold = 0, Stone = 1, Iron = 2, Diamond = 3."
		);
		public final float TOWER_DISPENSER_RESISTANCE    = prop(
			"block_resistance_tower_dispenser", 50.0F,
			"How resistant tower dispensers are to being destroyed by explosions.\n" +
			"Typical explosion resistance is 5 times the hardness."
		);
		
		public final boolean FEATURE_TESTER = prop(
			"item_feature_tester", true,
			"Set this to false to disable the \'Feature Tester\' item."
		);
		
		public final boolean SILVERFISH_AUTOGEN = prop(
			"silverfish_blocks", true,
			"Set this to false to disable the blocks automatically built and registered to disguise themselves\n" +
			"as the blocks defined as \'replaceable\' below.\n" +
			"Does not disable the infested variants of cobblestone or the dungeon fill/variant blocks you have set in\n" +
			"the \"_terrain\" category of each dimension."
		);
		
		public final TargetBlock.TargetMap SILVERFISH_REPLACEABLE = prop(
			"silverfish_blocks_replaceable", new TargetBlock[] {
				// Natural overworld
				new TargetBlock( Blocks.STONE ), new TargetBlock( Blocks.DIRT ), new TargetBlock( Blocks.SANDSTONE ),
				new TargetBlock( Blocks.CLAY ), new TargetBlock( Blocks.STONEBRICK ), new TargetBlock( Blocks.RED_SANDSTONE ),
				// Structural overworld
				new TargetBlock( Blocks.COBBLESTONE ), new TargetBlock( Blocks.BOOKSHELF ), new TargetBlock( Blocks.MOSSY_COBBLESTONE ),
				// Ores overworld
				new TargetBlock( Blocks.GOLD_ORE ), new TargetBlock( Blocks.IRON_ORE ), new TargetBlock( Blocks.COAL_ORE ),
				new TargetBlock( Blocks.LAPIS_ORE ), new TargetBlock( Blocks.DIAMOND_ORE ), new TargetBlock( Blocks.REDSTONE_ORE ),
				new TargetBlock( Blocks.EMERALD_ORE ),
				// Nether
				new TargetBlock( Blocks.NETHERRACK ), new TargetBlock( Blocks.SOUL_SAND ), new TargetBlock( Blocks.NETHER_BRICK ),
				new TargetBlock( Blocks.RED_NETHER_BRICK ), new TargetBlock( Blocks.QUARTZ_ORE ),
				// The End
				new TargetBlock( Blocks.END_STONE ), new TargetBlock( Blocks.PURPUR_BLOCK ), new TargetBlock( Blocks.PURPUR_PILLAR ),
				new TargetBlock( Blocks.END_BRICKS )
			},
			"List of blockstates that can be replaced by silverfish blocks in ALL dimensions. Each block defined here\n" +
			"will have a corresponding infested version generated and registered. All valid blockstates of these blocks\n" +
			"can function and will appear in the creative menu (plus cobblestone and all dungeon fill/variant blocks), but\n" +
			"the generator will only replace blocks matching the state definitions in this list.\n" +
			" * Note that only full-cube blocks are supported. There is no reason this shouldn\'t work with any full-cube\n" +
			"blocks used in world generation from other mods."
		);
		
		public final double PROGRESSIVE_RECOVERY = prop(
			"spawner_progressive_recovery", 0.0025,
			" * Progressive spawn delay:\n" +
			"By default, spawners added by this mod use a mechanic called \"progressive spawn delay\". Unlike vanilla\n" +
			"spawners that have a completely random delay chosen anywhere from 10 to 40 seconds (what awful variance!),\n" +
			"Deadly World spawners will start from a 10 second delay and slowly increase up to 40 seconds delay as you\n" +
			"continue to stand close to them (with the same vanilla delay limits of 200-800 ticks).\n" +
			"A spawner's \"delay buildup\" starts at its minimum delay and increases by its progressive delay (+/- 10%)\n" +
			"with each successful spawn, up to its maximum delay.\n" +
			"The progressive delay of each spawner is determined by its configs when generated and can then be\n" +
			"overwritten by nbt editing.\n" +
			" * Now, for the actual config option:\n" +
			"The rate at which the progressive spawn delay on spawners recovers while no players are within range.\n" +
			"Inactive spawners\' \"delay buildups\" are reduced by (progressive delay * this value) each tick."
		);
	}
	
	public final TERRAIN TERRAIN = new TERRAIN( );
	
	public
	class TERRAIN extends PropertyCategory
	{
		@Override
		String name( ) { return "_terrain"; }
		
		@Override
		String comment( )
		{
			return "Options related to the dimension's terrain.";
		}
		
		public final TargetBlock.TargetMap REPLACEABLE_BLOCKS = prop(
			"_replaceable_blocks",
			Config.dimensionLoading == DimensionType.NETHER.getId( ) ? new TargetBlock[] { new TargetBlock( Blocks.NETHERRACK ) } :
			Config.dimensionLoading == DimensionType.THE_END.getId( ) ? new TargetBlock[] { new TargetBlock( Blocks.END_STONE ) } :
			buildNaturalStoneTargets( ),
			"The block(s) that can be replaced in this dimension by terrain generation."
		);
		
		public final float SILVERFISH_AGGRO_CHANCE = prop(
			"silverfish_aggressive_chance", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? 0.1F : 0.3F,
			"The chance for silverfish emerging from this mod's silverfish blocks in this dimension to spawn\n" +
			"already calling for reinforcements, if any players are within eyesight. Be warned this can cascade.",
			R_FLT_ONE
		);
		
		public final WeightedBlockConfig.BlockList FLOOR_TRAP_COVERS = prop(
			"floor_trap_covers", makeDefaultFloorTrapCovers( ),
			"A weighted list of blocks to pick from when covering a floor trap in this dimension."
		);
		
		private
		WeightedBlockConfig[] makeDefaultFloorTrapCovers( )
		{
			if( Config.dimensionLoading == DimensionType.NETHER.getId( ) ) {
				return new WeightedBlockConfig[] {
					// Carpets
					new WeightedBlockConfig( Blocks.CARPET.getDefaultState( ).withProperty( BlockColored.COLOR, EnumDyeColor.BROWN ), 50 ),
					new WeightedBlockConfig( Blocks.CARPET.getDefaultState( ).withProperty( BlockColored.COLOR, EnumDyeColor.RED ), 50 ),
					new WeightedBlockConfig( Blocks.CARPET.getDefaultState( ).withProperty( BlockColored.COLOR, EnumDyeColor.ORANGE ), 50 ),
					// Slabs
					new WeightedBlockConfig( Blocks.STONE_SLAB.getDefaultState( ).withProperty(
						BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.NETHERBRICK ), 150 ),
					// Decor
					new WeightedBlockConfig( Blocks.PUMPKIN.getDefaultState( ).withProperty( BlockHorizontal.FACING, EnumFacing.NORTH ), 10 ),
					new WeightedBlockConfig( Blocks.PUMPKIN.getDefaultState( ).withProperty( BlockHorizontal.FACING, EnumFacing.SOUTH ), 10 ),
					new WeightedBlockConfig( Blocks.PUMPKIN.getDefaultState( ).withProperty( BlockHorizontal.FACING, EnumFacing.EAST ), 10 ),
					new WeightedBlockConfig( Blocks.PUMPKIN.getDefaultState( ).withProperty( BlockHorizontal.FACING, EnumFacing.WEST ), 10 ),
					new WeightedBlockConfig( Blocks.RED_MUSHROOM, 50 ),
					new WeightedBlockConfig( Blocks.BROWN_MUSHROOM, 50 ),
					// Ores
					new WeightedBlockConfig( Blocks.QUARTZ_ORE, 20 ),
					// Other
					new WeightedBlockConfig( Blocks.CAKE, 1 )
				};
			}
			if( Config.dimensionLoading == DimensionType.THE_END.getId( ) ) {
				return new WeightedBlockConfig[] {
					// Pressure plates
					new WeightedBlockConfig( Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 100 ),
					// Slabs
					new WeightedBlockConfig( Blocks.STONE_SLAB.getDefaultState( ).withProperty(
						BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SAND ), 200 ),
					// Other
					new WeightedBlockConfig( Blocks.END_BRICKS, 100 ),
					new WeightedBlockConfig( Blocks.CAKE, 1 )
				};
			}
			// For the overworld, as well as any dimensions added by mods
			return new WeightedBlockConfig[] {
				// Pressure plates
				new WeightedBlockConfig( Blocks.STONE_PRESSURE_PLATE, 100 ),
				// Carpets
				new WeightedBlockConfig( Blocks.CARPET.getDefaultState( ).withProperty( BlockColored.COLOR, EnumDyeColor.SILVER ), 34 ),
				new WeightedBlockConfig( Blocks.CARPET.getDefaultState( ).withProperty( BlockColored.COLOR, EnumDyeColor.GRAY ), 33 ),
				new WeightedBlockConfig( Blocks.CARPET.getDefaultState( ).withProperty( BlockColored.COLOR, EnumDyeColor.BROWN ), 33 ),
				// Slabs
				new WeightedBlockConfig( Blocks.STONE_SLAB.getDefaultState( ).withProperty(
					BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.STONE ), 34 ),
				new WeightedBlockConfig( Blocks.STONE_SLAB.getDefaultState( ).withProperty(
					BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SMOOTHBRICK ), 33 ),
				new WeightedBlockConfig( Blocks.STONE_SLAB.getDefaultState( ).withProperty(
					BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.COBBLESTONE ), 33 ),
				// Decor
				new WeightedBlockConfig( Blocks.RED_MUSHROOM, 20 ),
				new WeightedBlockConfig( Blocks.BROWN_MUSHROOM, 20 ),
				// Ores
				new WeightedBlockConfig( Blocks.GOLD_ORE, 10 ),
				// Other
				new WeightedBlockConfig( Blocks.CAKE, 1 )
			};
		}
		
		private
		TargetBlock[] buildNaturalStoneTargets( )
		{
			ArrayList< TargetBlock > naturalStone = new ArrayList<>( );
			for( BlockStone.EnumType stoneType : BlockStone.EnumType.values( ) ) {
				if( stoneType.isNatural( ) ) {
					naturalStone.add( new TargetBlock( Blocks.STONE.getDefaultState( ).withProperty( BlockStone.VARIANT, stoneType ) ) );
				}
			}
			return naturalStone.toArray( new TargetBlock[ 0 ] );
		}
	}
	
	//////// Ore Generation ////////
	
	public final VEINS VEINS = new VEINS( );
	
	public
	class VEINS extends PropertyCategory
	{
		@Override
		String name( ) { return "veins"; }
		
		@Override
		String comment( )
		{
			return "Options related to material 'vein' generation in general.";
		}
		
		public final int USER_DEFINED_VEIN_COUNT = prop(
			"_user_defined_veins", 0,
			"Number of additional vein types to generate.\n" +
			"Reload the game after setting this option for their config categories to be auto-generated.\n" +
			"There will be one config category per user-defined vein."
		);
		
		public final VeinUserDefined[] USER_DEFINED_VEINS;
		
		{
			VeinUserDefined[] customVeins = new VeinUserDefined[ USER_DEFINED_VEIN_COUNT ];
			for( int i = 0; i < USER_DEFINED_VEIN_COUNT; i++ ) {
				customVeins[ i ] = new VeinUserDefined( i );
			}
			USER_DEFINED_VEINS = customVeins;
		}
		
		public final boolean DISABLE_COAL_VEINS = prop(
			"disable_coal_veins", false,
			"Suppresses coal ore generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_DIAMOND_VEINS = prop(
			"disable_diamond_veins", false,
			"Suppresses diamond ore generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_DIRT_VEINS = prop(
			"disable_dirt_veins", false,
			"Suppresses dirt vein generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_GOLD_VEINS = prop(
			"disable_gold_veins", false,
			"Suppresses gold ore generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_GRAVEL_VEINS = prop(
			"disable_gravel_veins", false,
			"Suppresses gravel vein generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_IRON_VEINS = prop(
			"disable_iron_veins", false,
			"Suppresses iron ore generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_LAPIS_VEINS = prop(
			"disable_lapis_veins", false,
			"Suppresses lapis lazuli ore generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_REDSTONE_VEINS = prop(
			"disable_redstone_veins", false,
			"Suppresses redstone ore generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_QUARTZ_VEINS = prop(
			"disable_quartz_veins", false,
			"Suppresses quartz ore generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_DIORITE_VEINS = prop(
			"disable_diorite_veins", false,
			"Suppresses diorite vein generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_GRANITE_VEINS = prop(
			"disable_granite_veins", false,
			"Suppresses granite vein generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_ANDESITE_VEINS = prop(
			"disable_andesite_veins", false,
			"Suppresses andesite vein generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_EMERALD_VEINS = prop(
			"disable_emerald_veins", false,
			"Suppresses emerald ore generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_SILVERFISH_VEINS = prop(
			"disable_silverfish_veins", true,
			"Suppresses silverfish vein generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final boolean DISABLE_LAVA_VEINS = prop(
			"disable_lava_veins", true,
			"Suppresses lava vein generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
	}
	
	public final VeinConfig VEIN_LAVA       = new VeinConfig(
		"lava", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? 4.0F : 16.0F,
		0, Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? 32 : 128, 3
	);
	public final VeinConfig VEIN_SAND       = new VeinConfig(
		"sand", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? 0.25F : 0.0F,
		0, 62, 33
	);
	public final VeinConfig VEIN_SILVERFISH = new VeinConfig(
		"silverfish", 10.0F,
		5, 256, 25
	);
	public final VeinConfig VEIN_WATER      = new VeinConfig(
		"water", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? 6.0F : 0.0F,
		0, 62, 7
	);
	
	public final VeinConfig VEIN_DIRT     = new VeinReplacement( "dirt", 10, 0, 256, 33 );
	public final VeinConfig VEIN_GRAVEL   = new VeinReplacement( "gravel", 8, 0, 256, 33 );
	public final VeinConfig VEIN_DIORITE  = new VeinReplacement( "diorite", 10, 0, 80, 33 );
	public final VeinConfig VEIN_GRANITE  = new VeinReplacement( "granite", 10, 0, 80, 33 );
	public final VeinConfig VEIN_ANDESITE = new VeinReplacement( "andesite", 10, 0, 80, 33 );
	
	public final VeinConfig VEIN_COAL     = new VeinReplacement( "coal", 20, 0, 128, 17 );
	public final VeinConfig VEIN_QUARTZ   = new VeinReplacement( "quartz", 16, 10, 118, 14 );
	public final VeinConfig VEIN_IRON     = new VeinReplacement( "iron", 20, 0, 64, 9 );
	public final VeinConfig VEIN_GOLD     = new VeinReplacement( "gold", 2, 0, 32, 9 );
	public final VeinConfig VEIN_REDSTONE = new VeinReplacement( "redstone", 8, 0, 16, 8 );
	public final VeinConfig VEIN_DIAMOND  = new VeinReplacement( "diamond", 1, 0, 16, 8 );
	public final VeinConfig VEIN_LAPIS    = new VeinReplacement( "lapis", 1, 0, 32, 7 );
	public final VeinConfig VEIN_EMERALD  = new VeinReplacement( "emerald", 4, 4, 32, 1 );
	
	// Contains the properties common to all veins.
	public static
	class VeinConfig extends PropertyCategory
	{
		@Override
		String name( ) { return "veins_" + KEY; }
		
		@Override
		String comment( )
		{
			return "Options related to " + KEY + " 'vein' generation.";
		}
		
		private final float                 PLACEMENTS;
		private final EnvironmentListConfig PLACEMENTS_EXCEPTIONS;
		
		
		public final int[]   HEIGHTS;
		public final int[]   SIZES;
		public final boolean DEBUG_MARKER;
		
		VeinConfig( String key, float placements, int minHeight, int maxHeight, int size )
		{
			this( key, placements, minHeight, maxHeight, size, size );
		}
		
		VeinConfig( String key, float placements, int minHeight, int maxHeight, int minSize, int maxSize )
		{
			super( key );
			
			PLACEMENTS = prop(
				"_count", placements,
				"The number of placement attempts for this vein type.\n" +
				"A decimal represents a chance for a placement attempt (e.g., 0.3 means 30% chance for one attempt)."
			);
			PLACEMENTS_EXCEPTIONS = prop(
				"_count_exceptions", new TargetEnvironment[ 0 ],
				"The number of placement attempts when generating in particular locations.\n" +
				"More specific locations take priority over others (biome < biome* < global setting)."
			);
			
			HEIGHTS = new int[] {
				prop(
					"height_min", minHeight,
					"The minimum height to generate this vein type at."
				),
				prop(
					"height_max", maxHeight,
					"The maximum height to generate this vein type at."
				)
			};
			SIZES = new int[] {
				prop(
					"size_min", minSize,
					"The minimum size for this vein type."
				),
				prop(
					"size_max", maxSize,
					"The maximum size for this vein type."
				)
			};
			DEBUG_MARKER = prop(
				"testing_marker", false,
				"When set to true, places a 1x1 column of this ore to the height limit from each generated vein.\n" +
				"This is game-breaking and laggy. You must also enable debug mode in the main mod config.\n" +
				"Consider using a tool to strip away all stone/dirt/etc. for more intensive testing."
			);
		}
		
		public
		float getPlacements( World world, BlockPos pos )
		{
			return PLACEMENTS_EXCEPTIONS.getValueForLocation( world, pos, PLACEMENTS );
		}
	}
	
	public static
	class VeinReplacement extends VeinConfig
	{
		private final int VANILLA_PLACEMENTS;
		
		@Override
		String name( ) { return "veins_xtra_" + KEY; }
		
		@Override
		String comment( )
		{
			return "Options related to additional " + KEY + " vein generation.\n" +
			       "This ignores the 'disabled' vein settings, allowing you to replace normal vein generation.\n" +
			       "Defaults are equivalent to the vanilla values except for count (vanilla count is " + VANILLA_PLACEMENTS + ").";
		}
		
		VeinReplacement( String key, int vanillaCount, int minHeight, int maxHeight, int size )
		{
			super( key, 0.0F, minHeight, maxHeight, size );
			VANILLA_PLACEMENTS = vanillaCount;
		}
	}
	
	public static
	class VeinUserDefined extends VeinConfig
	{
		@Override
		String name( ) { return "veins_xtra_userdefined_" + KEY; }
		
		@Override
		String comment( )
		{
			return "Options related to a user-defined vein (index=" + KEY + ").\n" +
			       "These custom veins are generated in order of their indexes (i.e. 0, 1, 2, ...).";
		}
		
		VeinUserDefined( int index )
		{
			super( String.valueOf( index ), 0.0F, 0, 62, 9 );
		}
		
		public final IBlockState FILL_BLOCK = prop(
			"_block", Blocks.BRICK_BLOCK.getDefaultState( ),
			"The block this user-defined vein generates."
		);
		
		public final boolean COVERED = prop(
			"covered", false,
			"When set to true, this vein will not generate any exposed blocks (like water/lava veins).\n" +
			"I only recommend using this to add new fluid veins."
		);
	}
	
	//////// Feature - Dungeons ////////
	
	public final DUNGEONS DUNGEONS = new DUNGEONS( "dungeons", 8.0F, 0, 256 );
	
	public static
	class DUNGEONS extends FeatureMulti
	{
		public final WeightedBlockConfig.BlockList WALL_BLOCKS;
		public final WeightedBlockConfig.BlockList FLOOR_BLOCKS;
		
		DUNGEONS( String key, float placements, int minHeight, int maxHeight )
		{
			super( key, placements, minHeight, maxHeight );
			
			final WeightedBlockConfig[] wallBlocks;
			final WeightedBlockConfig[] floorBlocks;
			if( Config.dimensionLoading == DimensionType.NETHER.getId( ) ) {
				wallBlocks = new WeightedBlockConfig[] {
					new WeightedBlockConfig( Blocks.NETHER_BRICK, 100 ),
					new WeightedBlockConfig( Blocks.RED_NETHER_BRICK, 30 )
				};
				floorBlocks = new WeightedBlockConfig[] {
					new WeightedBlockConfig( Blocks.NETHER_BRICK, 100 ),
					new WeightedBlockConfig( Blocks.RED_NETHER_BRICK, 100 )
				};
			}
			else if( Config.dimensionLoading == DimensionType.THE_END.getId( ) ) {
				wallBlocks = new WeightedBlockConfig[] { new WeightedBlockConfig( Blocks.END_BRICKS, 70457 ) };
				floorBlocks = new WeightedBlockConfig[] {
					new WeightedBlockConfig( Blocks.END_BRICKS, 100 ),
					new WeightedBlockConfig( Blocks.END_STONE, 50 )
				};
			}
			else {
				// Overworld and mod-added dimensions
				wallBlocks = new WeightedBlockConfig[] {
					new WeightedBlockConfig( Blocks.COBBLESTONE, 100 ),
					new WeightedBlockConfig( Blocks.MOSSY_COBBLESTONE, 35 )
				};
				floorBlocks = new WeightedBlockConfig[] {
					new WeightedBlockConfig( Blocks.COBBLESTONE, 100 ),
					new WeightedBlockConfig( Blocks.MOSSY_COBBLESTONE, 70 )
				};
			}
			
			WALL_BLOCKS = prop(
				"_wall_blocks", wallBlocks,
				"A weighted list of blocks to pick from when generating the walls of dungeons.\n" +
				"For a vanilla dungeon look, set this to only cobblestone."
			);
			FLOOR_BLOCKS = prop(
				"_floor_blocks", floorBlocks,
				"A weighted list of blocks to pick from when generating the floor of dungeons.\n" +
				"For a vanilla dungeon look, set cobblestone to 150 and mossy cobblestone to 50."
			);
		}
		
		public final boolean DISABLE_VANILLA_DUNGEONS = prop(
			"_disable_vanilla_dungeons", true,
			"Suppresses dungeon generation events when set to true.\n" +
			"Does not disable generation added by this mod."
		);
		
		public final float SILVERFISH_CHANCE = prop(
			"_silverfish_chance", 0.2F,
			"The chance for each wall and floor block in the dungeon to be infested with silverfish.\n" +
			"For this to function, both the fill and variant blocks must be silverfish replaceable and silverfish autogen must be enabled.\n" +
			"Vanilla dungeons do not generate any silverfish blocks.",
			R_FLT_ONE
		);
		
		public final int CHESTS_MIN = prop(
			"_chest_count_min", 2,
			"The minimum number of chests to generate in each dungeon.\n" +
			"Note that occasionally chests will fail to generate, resulting in fewer chests than the minimum.\n" +
			"Chests are more likely to fail in vanilla dungeons, so maybe set this to 1 for a more vanilla experience."
		);
		public final int CHESTS_VARIANCE = Math.max( prop(
			"_chest_count_max", 2,
			"The maximum number of chests to generate in each dungeon. The default is the same as vanilla."
		) - CHESTS_MIN + 1, 1 ); // Convert from max to just the random interval
		
		public final int OPEN_WALLS_MIN = prop(
			"_open_walls_min", 1,
			"The minimum number of open wall spaces a prospective spawn attempt must have to not be canceled.\n" +
			"The default is the same as vanilla. Setting this to 0 allows dungeons to generate completely unconnected to any caves, ravines, etc."
		);
		public final int OPEN_WALLS_MAX = prop(
			"_open_walls_max", 10,
			"The maximum number of open wall spaces a prospective spawn attempt can have without being canceled.\n" +
			"Loosening open wall restrictions allows more dungeons to spawn in the world, and affects how open/closed-off they feel.\n" +
			"For vanilla dungeons, this value is 5."
		);
		
		// Note these are actually half-width (pseudo radius)
		public final int WIDTH_MIN      = prop(
			"_width_min", 5,
			"The minimum width of dungeons. This refers to the open space, not including walls or anything inside.\n" +
			"Note that dungeons can only generate in odd widths, and each axis is rolled separately. The default is the same as vanilla.",
			3, Integer.MAX_VALUE
		) >> 1; // Convert to half-width, note this effectively rounds up to the nearest odd number
		public final int WIDTH_VARIANCE = Math.max( (prop(
			"_width_max", 11,
			"The maximum width of dungeons. This refers to the open space, not including walls or anything inside.\n" +
			"Note that dungeons can only generate in odd widths, and each axis is rolled separately. Vanilla dungeons are max 7 wide.",
			3, Integer.MAX_VALUE
		) >> 1) - WIDTH_MIN + 1, 1 ); // Convert from max to just the random interval
		
		public final WeightedEnumConfig< EnumDungeonSubfeature > SUBFEATURE_LIST = prop(
			"subfeature_weight", EnumDungeonSubfeature.values( ),
			"Weight for the ", " to be generated in dungeons."
		);
	}
	
	public final SPAWNER_DUNGEON SPAWNER_DUNGEON = new SPAWNER_DUNGEON(
		EnumSpawnerType.DUNGEON, 16.0F, false, 200, 800, 40, 4, 4.0F
	);
	
	public static
	class SPAWNER_DUNGEON extends FeatureSpawner implements ISubgenFeature
	{
		@Override
		String name( ) { return "features_dungeons_spawner"; }
		
		@Override
		String comment( )
		{
			return "Options related to the generation of dungeon spawners. These are only generated as a subfeature of dungeons.";
		}
		
		SPAWNER_DUNGEON( EnumSpawnerType type,
		                float actRange, boolean checkSight, int minDelay, int maxDelay, int prgrDelay, int spawnCount, float spawnRange )
		{
			super( type, 0.0F, 0.0F, 0, 0, actRange, checkSight, minDelay, maxDelay, prgrDelay, spawnCount, spawnRange, null );
		}
	}
	
	//////// Feature - Chests ////////
	
	private static final float PLACEMENTS_CHESTS_COMMON = 0.1F;
	private static final float PLACEMENTS_CHESTS_UNCOMMON = 0.04F;
	private static final float PLACEMENTS_CHESTS_RARE = 0.02F;
	
	public final FeatureChest CHEST_DEFAULT = new FeatureChest(
		EnumChestType.DEFAULT, PLACEMENTS_CHESTS_COMMON, 12, 52, 0.1F
	);
	
	public final CHEST_VALUABLE CHEST_VALUABLE = new CHEST_VALUABLE(
		EnumChestType.VALUABLE, PLACEMENTS_CHESTS_RARE, 12, 32, 0.0F
	);
	
	public final FeatureChest CHEST_TRAPPED = new FeatureChest(
		EnumChestType.TRAPPED, 0.0F, 12, 52, Float.NaN
	);
	
	public final FeatureChest CHEST_TNT_FLOOR_TRAP = new FeatureChest(
		EnumChestType.TNT_FLOOR_TRAP, PLACEMENTS_CHESTS_UNCOMMON, 12, 52, 0.0F
	);
	
	public final CHEST_INFESTED CHEST_INFESTED = new CHEST_INFESTED(
		EnumChestType.INFESTED, PLACEMENTS_CHESTS_UNCOMMON, 12, 60, 1.0F
	);
	
	public final CHEST_SURPRISE CHEST_SURPRISE = new CHEST_SURPRISE(
		EnumChestType.SURPRISE, PLACEMENTS_CHESTS_UNCOMMON, 12, 52, 1.0F
	);
	
	public final CHEST_MIMIC CHEST_MIMIC = new CHEST_MIMIC(
		EnumChestType.MIMIC, PLACEMENTS_CHESTS_RARE, 12, 52, 0.1F
	);
	
	public static
	class CHEST_VALUABLE extends FeatureChest
	{
		CHEST_VALUABLE( EnumChestType type, float placements, int minHeight, int maxHeight, float trappedChance )
		{
			super( type, placements, minHeight, maxHeight, trappedChance );
		}
		
		public WeightedBlockConfig.BlockList COVER_BLOCKS = prop(
			"cover_block_list", makeDefaultCoverMaterials( ),
			"A weighted list of blocks to pick from to make up the blocks that surround this chest."
		);
		
		private
		WeightedBlockConfig[] makeDefaultCoverMaterials( )
		{
			if( Config.dimensionLoading == DimensionType.NETHER.getId( ) ) {
				return new WeightedBlockConfig[] {
					// Nethery
					new WeightedBlockConfig( Blocks.MAGMA, 100 ),
					new WeightedBlockConfig( Blocks.NETHER_BRICK, 100 ),
					new WeightedBlockConfig( Blocks.RED_NETHER_BRICK, 100 ),
					new WeightedBlockConfig( Blocks.SOUL_SAND, 50 ),
					new WeightedBlockConfig( Blocks.GLOWSTONE, 50 ),
					new WeightedBlockConfig( Blocks.NETHERRACK, 10 ),
					// Other
					new WeightedBlockConfig( Blocks.GRAVEL, 50 ),
					new WeightedBlockConfig( Blocks.OBSIDIAN, 20 )
				};
			}
			if( Config.dimensionLoading == DimensionType.THE_END.getId( ) ) {
				return new WeightedBlockConfig[] {
					new WeightedBlockConfig( Blocks.OBSIDIAN, 100 ),
					new WeightedBlockConfig( Blocks.END_STONE, 20 )
				};
			}
			// For the overworld, as well as any dimensions added by mods
			return new WeightedBlockConfig[] {
				// Stones
				new WeightedBlockConfig( Blocks.COBBLESTONE, 50 ),
				new WeightedBlockConfig( Blocks.MOSSY_COBBLESTONE, 20 ),
				new WeightedBlockConfig( Blocks.STONE, 20 ),
				new WeightedBlockConfig( Blocks.STONE.getDefaultState( ).withProperty( BlockStone.VARIANT, BlockStone.EnumType.ANDESITE ), 50 ),
				new WeightedBlockConfig( Blocks.STONE.getDefaultState( ).withProperty( BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH ), 50 ),
				// Soils
				new WeightedBlockConfig( Blocks.DIRT, 50 ),
				new WeightedBlockConfig( Blocks.DIRT.getDefaultState().withProperty( BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT ), 50 ),
				new WeightedBlockConfig( Blocks.CLAY, 50 ),
				new WeightedBlockConfig( Blocks.GRAVEL, 20 ),
				// Other
				new WeightedBlockConfig( Blocks.WOOL.getDefaultState().withProperty( BlockColored.COLOR, EnumDyeColor.SILVER ), 50 ),
				new WeightedBlockConfig( Blocks.OBSIDIAN, 20 )
			};
		}
	}
	
	public static
	class CHEST_INFESTED extends FeatureChest
	{
		CHEST_INFESTED( EnumChestType type, float placements, int minHeight, int maxHeight, float trappedChance )
		{
			super( type, placements, minHeight, maxHeight, trappedChance );
		}
		
		public final float LAUNCH_SPEED = prop(
			"launch_speed", 0.2F,
			"The maximum horizontal speed spawned silverfish are launched at."
		);
		
		public final int SILVERFISH_COUNT = prop(
			"silverfish_count", 6,
			"The number of silverfish spawned when the silverfish event triggers."
		);
	}
	
	public static
	class CHEST_SURPRISE extends FeatureChest
	{
		CHEST_SURPRISE( EnumChestType type, float placements, int minHeight, int maxHeight, float trappedChance )
		{
			super( type, placements, minHeight, maxHeight, trappedChance );
		}
		
		public final int TNT_FUSE_TIME_MIN = prop(
			"tnt_fuse_time_min", 40,
			"The minimum delay before spawned tnt explodes, in ticks. (20 ticks = 1 second)"
		);
		public final int TNT_FUSE_TIME_MAX = prop(
			"tnt_fuse_time_max", 60,
			"The maximum delay before spawned tnt explodes, in ticks. (20 ticks = 1 second)"
		);
		
		public final float TNT_LAUNCH_SPEED = prop(
			"tnt_launch_speed", 0.0F,
			"The maximum horizontal speed spawned tnt is launched at."
		) / 0.02F; // Divide out tnt's base speed
		
		public final int TNT_COUNT = prop(
			"tnt_count", 1,
			"The number of primed tnt spawned when the tnt event triggers.\n" +
			"If you make this more than 1, all items in the chest will probably get destroyed when the tnt goes off."
		);
		
		public final int GAS_DURATION_DELAY = prop(
			"gas_delay", 20,
			"The delay before the poison gas cloud starts spreading, in ticks. (20 ticks = 1 second)"
		);
		public final int GAS_DURATION = prop(
			"gas_duration", 40,
			"The duration (after its initial delay) until the poison gas cloud reaches max size and disappears, in ticks."
		);
		
		public final float GAS_MAX_RADIUS = prop(
			"gas_max_radius", 12.0F,
			"The maximum distance, in blocks, the poison gas cloud spreads from its origin.\n" +
			"Note the cloud starts at 0.5 radius and linearly increases to max radius at exactly its max duration."
		);
		
		public final int GAS_POISON_DURATION = prop(
			"gas_type_poison_duration", 200,
			"Duration of the poison effect applied by poison gas clouds, in ticks."
		);
		public final int GAS_POISON_POTENCY  = prop(
			"gas_type_poison_potency", 0,
			"Potency of the poison effect applied by poison gas clouds."
		);
		
		public final int GAS_WITHER_DURATION = prop(
			"gas_type_wither_duration", 200,
			"Duration of the wither effect applied by \"poison\" gas clouds, in ticks."
		);
		public final int GAS_WITHER_POTENCY  = prop(
			"gas_type_wither_potency", 0,
			"Potency of the wither effect applied by \"poison\" gas clouds."
		);
		
		public final int GAS_HARM_POTENCY = prop(
			"gas_type_harm_potency", 1,
			"Potency of the instant damage effect applied by \"poison\" gas clouds."
		);
		
		public final WeightedEnumConfig< EnumPotionCloudType > GAS_POTION_TYPE_LIST = prop(
			"gas_type_weight", EnumPotionCloudType.values( ),
			"Weight for the ", " potion type to be used for \"poison\" gas."
		);
		
		public final WeightedEnumConfig< EnumSurpriseChestType > SURPRISE_TYPE_LIST = prop(
			"_event_type_weight", EnumSurpriseChestType.values( ),
			"Weight for the ", " surprise event type."
		);
	}
	
	public static
	class CHEST_MIMIC extends FeatureChest
	{
		CHEST_MIMIC( EnumChestType type, float placements, int minHeight, int maxHeight, float trappedChance )
		{
			super( type, placements, minHeight, maxHeight, trappedChance );
		}
		
		public final float MULTIPLIER_DAMAGE = prop(
			"attrib_mult_damage", 2.0F,
			"Multiplier applied to the spawned mimic\'s base damage attribute."
		);
		public final float MULTIPLIER_HEALTH = prop(
			"attrib_mult_health", 2.0F,
			"Multiplier applied to the spawned mimic\'s base health attribute."
		);
		public final float MULTIPLIER_SPEED  = prop(
			"attrib_mult_speed", 1.1F,
			"Multiplier applied to the spawned mimic\'s base movement speed attribute."
		);
		
		public final float BABY_CHANCE = prop(
			"baby_chance", 1.0F,
			"Chance for the spawned mimic to be a baby (only works for Zombies, Pig Zombies, and Animals).",
			R_FLT_ONE
		);
		
		public final WeightedRandomConfig SPAWN_LIST = prop(
			"spawn_list", makeDefaultSpawnList( ),
			"Weighted list of mobs that can be spawned as \"mimics\". One of these is chosen\n" +
			"at random when the spawn mimic event is triggered."
		);
		
		private
		WeightedRandomConfig.Item[] makeDefaultSpawnList( )
		{
			return new WeightedRandomConfig.Item[] { new WeightedRandomConfig.Item( EntityZombie.class, 70457 ) };
		}
	}
	
	public static
	class FeatureChest extends FeatureSubtyped
	{
		@Override
		String subKey( ) { return "chests"; }
		
		public final float TRAPPED_CHANCE;
		
		FeatureChest( EnumChestType type, float placements, int minHeight, int maxHeight, float trappedChance )
		{
			super( type.getName( ), placements, minHeight, maxHeight );
			
			TRAPPED_CHANCE = type == EnumChestType.TRAPPED ? 1.0F : prop(
				"_trapped_chance", trappedChance,
				"The chance for " + type.DISPLAY_NAME + " to use the \'trapped chest\' block instead of a normal chest block.\n" +
				"For reference, the loot table for these chests is \'" + type.LOOT_TABLE_CHEST.toString( ) + "\'.",
				R_FLT_ONE
			);
			
		}
	}
	
	//////// Feature - Spawners ////////
	
	private static final float PLACEMENTS_SPAWNERS_COMMON = 0.16F;
	private static final float PLACEMENTS_SPAWNERS_UNCOMMON = 0.04F;
	private static final float PLACEMENTS_SPAWNERS_RARE = 0.02F;
	
	public final FeatureSpawner SPAWNER_DEFAULT = new FeatureSpawner(
		EnumSpawnerType.DEFAULT, 0.33F, PLACEMENTS_SPAWNERS_COMMON, 12, 52,
		16.0F, false, 200, 800, 40, 4, 4.0F, null
	);
	
	public final FeatureSpawner SPAWNER_STREAM = new FeatureSpawner(
		EnumSpawnerType.STREAM, 1.0F, PLACEMENTS_SPAWNERS_UNCOMMON, 12, 42,
		16.0F, true, 0, 400, 10, 1, 2.0F,
		Blocks.RED_SANDSTONE.getDefaultState( ).withProperty( BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.CHISELED )
	);
	
	public final FeatureSpawner SPAWNER_SWARM = new FeatureSpawner(
		EnumSpawnerType.SWARM, 1.0F, PLACEMENTS_SPAWNERS_RARE, 12, 32,
		20.0F, true, 400, 2400, 100, 12, 8.0F,
		Blocks.SANDSTONE.getDefaultState( ).withProperty( BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED )
	);
	
	public final SPAWNER_BRUTAL SPAWNER_BRUTAL = new SPAWNER_BRUTAL(
		EnumSpawnerType.BRUTAL, 1.0F, PLACEMENTS_SPAWNERS_RARE, 12, 32,
		16.0F, true, 200, 800, 100, 2, 3.0F,
		Blocks.STONEBRICK.getDefaultState( ).withProperty( BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED )
	);
	
	public final SILVERFISH_NEST SILVERFISH_NEST = new SILVERFISH_NEST(
		EnumSpawnerType.SILVERFISH_NEST, 0.33F, PLACEMENTS_SPAWNERS_COMMON, 12, 62,
		16.0F, false, 100, 400, 20, 6, 6.0F
	);
	
	public static
	class SPAWNER_BRUTAL extends FeatureSpawner
	{
		SPAWNER_BRUTAL( EnumSpawnerType type, float chestChance, float placements, int minHeight, int maxHeight,
		                float actRange, boolean checkSight, int minDelay, int maxDelay, int prgrDelay, int spawnCount, float spawnRange,
		                IBlockState topperBlock)
		{
			super( type, chestChance, placements, minHeight, maxHeight, actRange, checkSight, minDelay, maxDelay, prgrDelay, spawnCount, spawnRange, topperBlock );
		}
		
		public final float VINES_CHANCE = prop(
			"vines_chance", 0.4F,
			"Chance to place a vines block for decoration in each adjacent air block."
		);
		
		public final boolean AMBIENT_FX      = prop(
			"brutal_ambient_fx", false,
			"If true, the potion effects applied to spawned mobs will not spawn potion fx particles."
		);
		public final boolean FIRE_RESISTANCE = prop(
			"brutal_fire_resistance", true,
			"If true, non-creeper mobs spawned by brutal spawners will have the \'fire resistance\' potion effect."
		);
		public final boolean WATER_BREATHING = prop(
			"brutal_water_breathing", true,
			"If true, non-creeper mobs spawned by brutal spawners will have the \'water breathing\' potion effect."
		);
	}
	
	public static
	class SILVERFISH_NEST extends FeatureSpawner
	{
		SILVERFISH_NEST( EnumSpawnerType type, float chestChance, float placements, int minHeight, int maxHeight,
		                      float actRange, boolean checkSight, int minDelay, int maxDelay, int prgrDelay, int spawnCount, float spawnRange )
		{
			super( type, chestChance, placements, minHeight, maxHeight, actRange, checkSight, minDelay, maxDelay, prgrDelay, spawnCount, spawnRange, null );
		}
		
		public WeightedBlockConfig.BlockList NEST_BLOCKS = prop(
			"nest_block_list", makeDefaultNestMaterials( ),
			"A weighted list of blocks to pick from to make up the entire nest. All blocks will be replaced with\n" +
			"silverfish-infested versions, limited by your silverfish replaceable/autogen settings."
		);
		
		@Override
		protected
		WeightedRandomConfig.Item[] makeDefaultSpawnList( )
		{
			return new WeightedRandomConfig.Item[] { new WeightedRandomConfig.Item( EntitySilverfish.class, 70457 ) };
		}
		
		private
		WeightedBlockConfig[] makeDefaultNestMaterials( )
		{
			if( Config.dimensionLoading == DimensionType.NETHER.getId( ) ) {
				return new WeightedBlockConfig[] {
					new WeightedBlockConfig( Blocks.SOUL_SAND, 100 ),
					new WeightedBlockConfig( Blocks.QUARTZ_ORE, 10 )
				};
			}
			if( Config.dimensionLoading == DimensionType.THE_END.getId( ) ) {
				return new WeightedBlockConfig[] { new WeightedBlockConfig( Blocks.OBSIDIAN, 70457 ) };
			}
			// For the overworld, as well as any dimensions added by mods
			return new WeightedBlockConfig[] {
				// Building blocks
				new WeightedBlockConfig( Blocks.COBBLESTONE, 300 ),
				new WeightedBlockConfig( Blocks.MOSSY_COBBLESTONE, 20 ),
				new WeightedBlockConfig( Blocks.CLAY, 20 ),
				// Ores
				new WeightedBlockConfig( Blocks.GOLD_ORE, 5 ),
				new WeightedBlockConfig( Blocks.LAPIS_ORE, 5 ),
				new WeightedBlockConfig( Blocks.DIAMOND_ORE, 5 ),
				new WeightedBlockConfig( Blocks.EMERALD_ORE, 5 )
			};
		}
	}
	
	public static
	class FeatureSpawner extends FeatureTrap
	{
		@Override
		String subKey( ) { return "spawners"; }
		
		public final float CHEST_CHANCE;
		
		public final float                DYNAMIC_CHANCE;
		public final WeightedRandomConfig SPAWN_LIST;
		
		public final int DELAY_MIN;
		public final int DELAY_MAX;
		public final int DELAY_PROGRESSIVE;
		
		public final float ADDED_ARMOR;
		public final float ADDED_ARMOR_TOUGHNESS;
		public final float ADDED_KNOCKBACK_RESIST;
		public final float MULTIPLIER_DAMAGE;
		public final float MULTIPLIER_HEALTH;
		public final float MULTIPLIER_SPEED;
		
		public final int   SPAWN_COUNT;
		public final float SPAWN_RANGE;
		
		public final WeightedBlockConfig.BlockList TOPPER_BLOCKS;
		
		FeatureSpawner( EnumSpawnerType type, float chestChance, float placements, int minHeight, int maxHeight,
		                float actRange, boolean checkSight, int minDelay, int maxDelay, int prgrDelay, int spawnCount, float spawnRange,
		                IBlockState topper )
		{
			super( type.getName( ), type.DISPLAY_NAME, placements, minHeight, maxHeight, actRange, checkSight );
			
			CHEST_CHANCE = this instanceof ISubgenFeature ? 0.0F : prop(
				"_chest_chance", chestChance,
				"The chance for a chest to generate beneath " + type.DISPLAY_NAME + ".\n" +
				"For reference, the loot table for these chests is \'" + type.LOOT_TABLE_CHEST.toString( ) + "\'.",
				R_FLT_ONE
			);
			
			DYNAMIC_CHANCE = prop(
				"_dynamic_chance", type == EnumSpawnerType.STREAM ? 1.0F : type == EnumSpawnerType.SILVERFISH_NEST ? 0.0F : 0.08F,
				"The chance for " + type.DISPLAY_NAME + " to generate as \'dynamic\'.\n" +
				"Dynamic spawners pick a new mob to spawn after each spawn.",
				R_FLT_ONE
			);
			SPAWN_LIST = prop(
				"_spawn_list", makeDefaultSpawnList( ),
				"Weighted list of mobs that can be spawned by " + type.DISPLAY_NAME + ". One of these is chosen\n" +
				"at random when the spawner is generated. Spawners that are generated as \'dynamic\' will pick again\n" +
				"between each spawn."
			);
			
			DELAY_MIN = prop(
				"delay_min", minDelay,
				"The minimum delay between spawns, in ticks. (20 ticks = 1 second)"
			);
			DELAY_MAX = prop(
				"delay_max", maxDelay,
				"The maximumm delay between spawns, in ticks. (20 ticks = 1 second)"
			);
			DELAY_PROGRESSIVE = prop(
				"delay_progressive", prgrDelay,
				"Each spawn increases the spawner\'s delay buildup by this many ticks (+/- 10%). Set this to 0 to\n" +
				"revert to the lame vanilla spawner behavior (simple random between min and max).\n" +
				" * See the main config for a more in-depth description of progressive spawn delay, as well as\n" +
				"   the global recovery rate option."
			);
			
			ADDED_ARMOR = prop(
				"attrib_add_armor", type == EnumSpawnerType.BRUTAL || type == EnumSpawnerType.DUNGEON ? 15.0F : 0.0F,
				"Bonus added to spawned entities\' base armor attributes.",
				0.0F, 30.0F
			);
			ADDED_ARMOR_TOUGHNESS = prop(
				"attrib_add_armor_toughness", type == EnumSpawnerType.BRUTAL || type == EnumSpawnerType.DUNGEON ? 8.0F : 0.0F,
				"Bonus added to spawned entities\' base armor toughness attributes.",
				0.0F, 20.0F
			);
			ADDED_KNOCKBACK_RESIST = prop(
				"attrib_add_knockback_resist", type == EnumSpawnerType.BRUTAL ? 0.2F : 0.0F,
				"Bonus added to spawned entities\' base knockback resistance attributes (1.00 = 100% chance to resist).",
				R_FLT_ONE
			);
			
			MULTIPLIER_DAMAGE = prop(
				"attrib_mult_damage", type == EnumSpawnerType.BRUTAL || type == EnumSpawnerType.DUNGEON ? 1.5F : 1.0F,
				"Multiplier applied to spawned entities\' base attack damage attributes."
			);
			MULTIPLIER_HEALTH = prop(
				"attrib_mult_health", type == EnumSpawnerType.BRUTAL ? 1.5F : 1.0F,
				"Multiplier applied to spawned entities\' base health attributes."
			);
			MULTIPLIER_SPEED = prop(
				"attrib_mult_speed", type == EnumSpawnerType.BRUTAL ? 1.2F : 1.0F,
				"Multiplier applied to spawned entities\' base movement speed attributes."
			);
			
			SPAWN_COUNT = prop(
				"spawn_count", spawnCount,
				"The number of mobs to attempt creating with each spawn. May spawn fewer depending on nearby obstructions."
			);
			SPAWN_RANGE = prop(
				"spawn_range", spawnRange,
				"The maximum horizontal range to spawn mobs in."
			);
			
			final WeightedBlockConfig[] topperBlocks;
			if( type == EnumSpawnerType.DEFAULT ) {
				if( Config.dimensionLoading == DimensionType.NETHER.getId( ) ) {
					topperBlocks = new WeightedBlockConfig[] {
						new WeightedBlockConfig( Blocks.NETHER_BRICK, 100 ),
						new WeightedBlockConfig( Blocks.RED_NETHER_BRICK, 100 )
					};
				}
				else if( Config.dimensionLoading == DimensionType.THE_END.getId( ) ) {
					topperBlocks = new WeightedBlockConfig[] { new WeightedBlockConfig( Blocks.END_BRICKS, 70457 ) };
				}
				else {
					// Overworld and mod-added dimensions
					topperBlocks = new WeightedBlockConfig[] {
						new WeightedBlockConfig( Blocks.COBBLESTONE, 100 ),
						new WeightedBlockConfig( Blocks.MOSSY_COBBLESTONE, 100 )
					};
				}
			}
			else if( topper == null ) {
				topperBlocks = null;
			}
			else {
				topperBlocks = new WeightedBlockConfig[] { new WeightedBlockConfig( topper, 70457 ) };
			}
			
			TOPPER_BLOCKS = topperBlocks == null ? null : prop(
				"topper_blocks", topperBlocks,
				"A weighted list of blocks to pick from when placing the decoration block on top of " + type.DISPLAY_NAME + "."
			);
		}
		
		protected
		WeightedRandomConfig.Item[] makeDefaultSpawnList( )
		{
			if( Config.dimensionLoading == DimensionType.NETHER.getId( ) ) {
				return new WeightedRandomConfig.Item[] {
					new WeightedRandomConfig.Item( EntityWitherSkeleton.class, 200 ),
					new WeightedRandomConfig.Item( EntityHusk.class, 100 ),
					new WeightedRandomConfig.Item( EntityBlaze.class, 100 ),
					new WeightedRandomConfig.Item( EntityCaveSpider.class, 10 ),
					new WeightedRandomConfig.Item( EntityCreeper.class, 10 ),
					new WeightedRandomConfig.Item( EntityMagmaCube.class, 10 )
				};
			}
			if( Config.dimensionLoading == DimensionType.THE_END.getId( ) ) {
				return new WeightedRandomConfig.Item[] {
					new WeightedRandomConfig.Item( EntityEnderman.class, 200 ),
					new WeightedRandomConfig.Item( EntityCreeper.class, 10 )
				};
			}
			// For the overworld, as well as any dimensions added by mods
			return new WeightedRandomConfig.Item[] {
				// Vanilla dungeon mobs
				new WeightedRandomConfig.Item( EntityZombie.class, 200 ),
				new WeightedRandomConfig.Item( EntitySkeleton.class, 100 ),
				new WeightedRandomConfig.Item( EntitySpider.class, 100 ),
				// Extras
				new WeightedRandomConfig.Item( EntityCaveSpider.class, 10 ),
				new WeightedRandomConfig.Item( EntityCreeper.class, 10 ),
				new WeightedRandomConfig.Item( EntitySilverfish.class, 10 )
			};
		}
	}
	
	//////// Feature - Floor Traps ////////
	
	private static final float PLACEMENTS_FLOOR_TRAPS_COMMON = 0.2F;
	private static final float PLACEMENTS_FLOOR_TRAPS_UNCOMMON = 0.1F;
	
	public final FLOOR_TRAP_TNT FLOOR_TRAP_TNT = new FLOOR_TRAP_TNT(
		EnumFloorTrapType.TNT, PLACEMENTS_FLOOR_TRAPS_COMMON, 12, 60
	);
	
	public final FLOOR_TRAP_TNT_MOB FLOOR_TRAP_TNT_MOB = new FLOOR_TRAP_TNT_MOB(
		EnumFloorTrapType.TNT_MOB, PLACEMENTS_FLOOR_TRAPS_UNCOMMON, 12, 60
	);
	
	public final FLOOR_TRAP_POTION FLOOR_TRAP_POTION = new FLOOR_TRAP_POTION(
		EnumFloorTrapType.POTION, PLACEMENTS_FLOOR_TRAPS_COMMON, 12, 60
	);
	
	public static
	class FLOOR_TRAP_TNT extends FeatureFloorTrap
	{
		FLOOR_TRAP_TNT( EnumFloorTrapType type, float placement, int minHeight, int maxHeight )
		{
			super( type, placement, minHeight, maxHeight );
		}
		
		public final int FUSE_TIME_MIN = prop(
			"fuse_time_min", 40,
			"The minimum delay before spawned tnt explodes, in ticks. (20 ticks = 1 second)"
		);
		public final int FUSE_TIME_MAX = prop(
			"fuse_time_max", 50,
			"The maximum delay before spawned tnt explodes, in ticks. (20 ticks = 1 second)"
		);
		
		public final float LAUNCH_SPEED = prop(
			"launch_speed", 0.3F,
			"The maximum horizontal speed spawned tnt is launched at."
		) / 0.02F; // Divide out tnt's base speed
		
		public final int TNT_COUNT = prop(
			"tnt_count", 4,
			"The number of primed tnt spawned when the trap triggers."
		);
	}
	
	public static
	class FLOOR_TRAP_TNT_MOB extends FeatureFloorTrap
	{
		FLOOR_TRAP_TNT_MOB( EnumFloorTrapType type, float placement, int minHeight, int maxHeight )
		{
			super( type, placement, minHeight, maxHeight );
		}
		
		public final float MULTIPLIER_HEALTH = prop(
			"attrib_mult_health", 0.5F,
			"Multiplier applied to the spawned entity\'s base health attribute."
		);
		public final float MULTIPLIER_SPEED  = prop(
			"attrib_mult_speed", 1.3F,
			"Multiplier applied to the spawned entity\'s base movement speed attribute."
		);
		
		public final int FUSE_TIME_MIN = prop(
			"fuse_time_min", 70,
			"The minimum delay before the tnt \"hat\" explodes, in ticks. (20 ticks = 1 second)"
		);
		public final int FUSE_TIME_MAX = prop(
			"fuse_time_max", 80,
			"The maximum delay before the tnt \"hat\" explodes, in ticks. (20 ticks = 1 second)"
		);
		
		public final WeightedRandomConfig SPAWN_LIST = prop(
			"spawn_list", makeDefaultSpawnList( ),
			"Weighted list of mobs that can be spawned by tnt mob traps. One of these is chosen\n" +
			"at random when the trap is triggered."
		);
		
		private
		WeightedRandomConfig.Item[] makeDefaultSpawnList( )
		{
			if( Config.dimensionLoading == DimensionType.NETHER.getId( ) ) {
				return new WeightedRandomConfig.Item[] {
					new WeightedRandomConfig.Item( EntityWitherSkeleton.class, 200 ),
					new WeightedRandomConfig.Item( EntityHusk.class, 100 ),
					new WeightedRandomConfig.Item( EntityBlaze.class, 100 ),
					new WeightedRandomConfig.Item( EntityCaveSpider.class, 10 ),
					new WeightedRandomConfig.Item( EntityMagmaCube.class, 10 )
				};
			}
			if( Config.dimensionLoading == DimensionType.THE_END.getId( ) ) {
				return new WeightedRandomConfig.Item[] {
					new WeightedRandomConfig.Item( EntityEnderman.class, 200 )
				};
			}
			// For the overworld, as well as any dimensions added by mods
			return new WeightedRandomConfig.Item[] {
				// Vanilla dungeon mobs
				new WeightedRandomConfig.Item( EntityZombie.class, 200 ),
				new WeightedRandomConfig.Item( EntitySkeleton.class, 100 ),
				new WeightedRandomConfig.Item( EntitySpider.class, 100 ),
				// Extras
				new WeightedRandomConfig.Item( EntityCaveSpider.class, 10 ),
				new WeightedRandomConfig.Item( EntitySilverfish.class, 10 )
			};
		}
	}
	
	public static
	class FLOOR_TRAP_POTION extends FeatureFloorTrap
	{
		FLOOR_TRAP_POTION( EnumFloorTrapType type, float placement, int minHeight, int maxHeight )
		{
			super( type, placement, minHeight, maxHeight );
		}
		
		public final int RESET_TIME_MIN = prop(
			"reset_time_min", 20,
			"The minimum delay before potion traps can be tripped again, in ticks. (20 ticks = 1 second)"
		);
		public final int RESET_TIME_MAX = prop(
			"reset_time_max", 40,
			"The maximum delay before potion traps can be tripped again, in ticks. (20 ticks = 1 second)"
		);
		
		public final int HARM_POTENCY = prop(
			"type_harm_potency", 1,
			"Potency of the instant damage effect applied by harm potion traps."
		);
		
		public final int POISON_DURATION = prop(
			"type_poison_duration", 1600,
			"Duration of the poison effect applied by poison potion traps, in ticks (affected by proximity to the splash)."
		);
		public final int POISON_POTENCY  = prop(
			"type_poison_potency", 0,
			"Potency of the poison effect applied by poison potion traps."
		);
		
		public final int HUNGER_DURATION = prop(
			"type_hunger_duration", 2000,
			"Duration of the hunger effect applied by hunger potion traps, in ticks (affected by proximity to the splash)."
		);
		public final int HUNGER_POTENCY  = prop(
			"type_hunger_potency", 0,
			"Potency of the hunger effect applied by hunger potion traps."
		);
		
		public final int DAZE_DURATION = prop(
			"type_daze_duration", 2000,
			"Duration of the effects applied by daze potion traps, in ticks (affected by proximity to the splash)."
		);
		public final int DAZE_POTENCY  = prop(
			"type_daze_potency", 0,
			"Potency of the weakness, fatigue, and slowness effects applied by daze potion traps."
		);
		
		public final int LEVITATION_DURATION = prop(
			"type_levitation_duration", 200,
			"Duration of the levitation effect applied by levitation potion traps, in ticks (affected by proximity to the splash)."
		);
		public final int LEVITATION_POTENCY  = prop(
			"type_levitation_potency", 1,
			"Potency of the levitation effect applied by levitation potion traps."
		);
		
		public final WeightedEnumConfig< EnumPotionTrapType > POTION_TYPE_LIST = prop(
			"type_weight", EnumPotionTrapType.values( ),
			"Weight for the ", " potion trap type."
		);
	}
	
	public static
	class FeatureFloorTrap extends FeatureTrap
	{
		@Override
		String subKey( ) { return "floor_traps"; }
		
		public final float COVER_CHANCE;
		
		@Override
		String comment( )
		{
			return "Options related to the generation of " + KEY + " traps in floors.";
		}
		
		FeatureFloorTrap( EnumFloorTrapType type, float placement, int minHeight, int maxHeight )
		{
			this( type, placement, minHeight, maxHeight, 3.3F, true );
		}
		
		FeatureFloorTrap( EnumFloorTrapType type, float placement, int minHeight, int maxHeight, float actRange, boolean checkSight )
		{
			super( type.NAME, type.DISPLAY_NAME, placement, minHeight, maxHeight, actRange, checkSight );
			
			COVER_CHANCE = prop(
				"_cover_chance", 0.5F,
				"The chance for " + type.DISPLAY_NAME + " to generate with a \'cover\' block placed on top.\n" +
				"The possible cover blocks are determined in the dimension\'s terrain config section.",
				R_FLT_ONE
			);
		}
	}
	
	//////// Feature - Towers ////////
	
	private static final float PLACEMENTS_TOWERS_COMMON = 0.16F;
	private static final float PLACEMENTS_TOWERS_UNCOMMON = 0.04F;
	private static final float PLACEMENTS_TOWERS_RARE = 0.02F;
	
	public final FeatureTower TOWER_DEFAULT = new FeatureTower(
		EnumTowerType.DEFAULT, PLACEMENTS_TOWERS_COMMON, 12, 60,
		8.0F, 1.5F, 6.0F, 20, 60
	);
	
	public final FeatureTower TOWER_FIRE = new FeatureTower(
		EnumTowerType.FIRE, PLACEMENTS_TOWERS_UNCOMMON, 12, 52,
		6.0F, 1.5F, 6.0F, 20, 60
	);
	
	public final TOWER_POTION TOWER_POTION = new TOWER_POTION(
		EnumTowerType.POTION, PLACEMENTS_TOWERS_UNCOMMON, 12, 42,
		6.0F, 1.0F, 6.0F, 20, 60
	);
	
	public final FeatureTower TOWER_GATLING = new FeatureTower(
		EnumTowerType.GATLING, PLACEMENTS_TOWERS_RARE, 12, 32,
		4.0F, 1.0F, 18.0F, 11, 22
	);
	
	public final FeatureTower TOWER_FIREBALL = new FeatureTower(
		EnumTowerType.FIREBALL, PLACEMENTS_TOWERS_RARE, 12, 32,
		3.0F, 1.0F, 8.0F, 20, 40
	);
	
	public static
	class TOWER_POTION extends FeatureTower
	{
		TOWER_POTION( EnumTowerType type, float placement, int minHeight, int maxHeight,
		              float damage, float projSpeed, float projVariance, int minDelay, int maxDelay )
		{
			super( type, placement, minHeight, maxHeight, damage, projSpeed, projVariance, minDelay, maxDelay );
		}
		
		public final int SLOWNESS_DURATION = prop(
			"type_slowness_duration", 600,
			"Duration of the slowness effect applied by slowness arrows, in ticks. Default is equivalent to stray skeleton arrows."
		);
		public final int SLOWNESS_POTENCY  = prop(
			"type_slowness_potency", 0,
			"Potency of the slowness effect applied by slowness arrows. Default is equivalent to stray skeleton arrows."
		);
		
		public final int POISON_DURATION = prop(
			"type_poison_duration", 200,
			"Duration of the poison effect applied by poison arrows, in ticks."
		);
		public final int POISON_POTENCY  = prop(
			"type_poison_potency", 0,
			"Potency of the poison effect applied by poison arrows."
		);
		
		public final int WITHER_DURATION = prop(
			"type_wither_duration", 200,
			"Duration of the wither effect applied by wither arrows, in ticks."
		);
		public final int WITHER_POTENCY  = prop(
			"type_wither_potency", 0,
			"Potency of the wither effect applied by wither arrows."
		);
		
		public final int HARM_POTENCY = prop(
			"type_harm_potency", 1,
			"Potency of the instant damage effect applied by harm arrows."
		);
		
		public final int HUNGER_DURATION = prop(
			"type_hunger_duration", 400,
			"Duration of the hunger effect applied by hunger arrows, in ticks."
		);
		public final int HUNGER_POTENCY  = prop(
			"type_hunger_potency", 0,
			"Potency of the hunger effect applied by hunger arrows."
		);
		
		public final int BLINDNESS_DURATION = prop(
			"type_blindness_duration", 400,
			"Duration of the blindness effect applied by blindness arrows, in ticks."
		);
		
		public final int WEAKNESS_DURATION = prop(
			"type_weakness_duration", 600,
			"Duration of the weakness and fatigue effects applied by weakness arrows, in ticks."
		);
		public final int WEAKNESS_POTENCY  = prop(
			"type_weakness_potency", 0,
			"Potency of the weakness and fatigue effects applied by weakness arrows."
		);
		
		public final int LEVITATION_DURATION = prop(
			"type_levitation_duration", 100,
			"Duration of the levitation effect applied by levitation arrows, in ticks."
		);
		public final int LEVITATION_POTENCY  = prop(
			"type_levitation_potency", 1,
			"Potency of the levitation effect applied by levitation arrows."
		);
		
		public final WeightedEnumConfig< EnumPotionArrowType > POTION_TYPE_LIST = prop(
			"type_weight", EnumPotionArrowType.values( ),
			"Weight for the ", " potion arrow type."
		);
	}
	
	public static
	class FeatureTower extends FeatureTrap
	{
		public final float ATTACK_DAMAGE;
		
		public final float PROJECTILE_SPEED;
		public final float PROJECTILE_VARIANCE;
		
		public final int DELAY_MIN;
		public final int DELAY_MAX;
		
		public final int MAX_TOWER_HEIGHT;
		
		public final WeightedBlockConfig.BlockList PILLAR_BLOCKS;
		
		@Override
		String subKey( ) { return "towers"; }
		
		@Override
		String comment( )
		{
			return "Options related to the generation of " + KEY + " tower traps.";
		}
		
		FeatureTower( EnumTowerType type, float placement, int minHeight, int maxHeight,
		              float damage, float projSpeed, float projVariance, int minDelay, int maxDelay )
		{
			this( type, placement, minHeight, maxHeight, damage, projSpeed, projVariance, minDelay, maxDelay, 10.0F, true );
		}
		
		FeatureTower( EnumTowerType type, float placement, int minHeight, int maxHeight,
		              float damage, float projSpeed, float projVariance, int minDelay, int maxDelay, float actRange, boolean checkSight )
		{
			super( type.NAME, type.DISPLAY_NAME, placement, minHeight, maxHeight, actRange, checkSight );
			
			ATTACK_DAMAGE = type != EnumTowerType.FIREBALL ? prop(
				"attack_damage", damage,
				"Damage dealt by " + type.DISPLAY_NAME + "\' attacks. This translates roughly into half-hearts of damage."
			) :
			prop(
				"attack_shots", damage,
				"Number of fireballs shot by " + type.DISPLAY_NAME + "\' attacks. Fireballs deal a fixed 5 damage (half-hearts)."
			);
			
			PROJECTILE_SPEED = prop(
				"projectile_speed", projSpeed,
				"Multiplier for how fast projectiles fired by " + type.DISPLAY_NAME + " move through the air."
			);
			PROJECTILE_VARIANCE = prop(
				"projectile_variance", projVariance,
				"The higher this value, the less accurate projectiles fired by " + type.DISPLAY_NAME + " are."
			);
			
			DELAY_MIN = prop(
				"attack_delay_min", minDelay,
				"The minimum delay between attacks, in ticks. (20 ticks = 1 second)"
			);
			DELAY_MAX = prop(
				"attack_delay_max", maxDelay,
				"The maximumm delay between attack, in ticks. (20 ticks = 1 second)"
			);
			
			MAX_TOWER_HEIGHT = prop(
				"max_tower_height", Config.dimensionLoading == DimensionType.NETHER.getId( ) ? 2 : 4,
				"The maximum height the tower can generate to reach out of non-solid blocks (like lava).\n" +
				"Note that this allows the tower to stretch above the \"_" + KEY + "_height_max\" setting."
			);
			
			final WeightedBlockConfig[] pillarBlocks;
			if( Config.dimensionLoading == DimensionType.NETHER.getId( ) ) {
				pillarBlocks = new WeightedBlockConfig[] {
					new WeightedBlockConfig( Blocks.NETHER_BRICK, 100 ),
					new WeightedBlockConfig( Blocks.RED_NETHER_BRICK, 60 )
				};
			}
			else if( Config.dimensionLoading == DimensionType.THE_END.getId( ) ) {
				pillarBlocks = new WeightedBlockConfig[] { new WeightedBlockConfig( Blocks.OBSIDIAN, 70457 ) };
			}
			else {
				// Overworld and mod-added dimensions
				pillarBlocks = new WeightedBlockConfig[] {
					new WeightedBlockConfig( Blocks.COBBLESTONE, 100 ),
					new WeightedBlockConfig( Blocks.MOSSY_COBBLESTONE, 50 )
				};
			}
			
			PILLAR_BLOCKS = prop(
				"pillar_blocks", pillarBlocks,
				"A weighted list of blocks to pick from when generating the bottom portion of " + type.DISPLAY_NAME + "."
			);
		}
	}
	
	// Properties for a subtyped world feature that has an activation range (optionally checking line of sight).
	public static abstract
	class FeatureTrap extends FeatureSubtyped
	{
		public final float   ACTIVATION_RANGE;
		public final boolean CHECK_SIGHT;
		
		FeatureTrap( String key, String displayName, float placement, int minHeight, int maxHeight, float actRange, boolean checkSight )
		{
			super( key, placement, minHeight, maxHeight );
			
			ACTIVATION_RANGE = prop(
				"activation_range", actRange,
				"The trap will be triggered once a player comes within this distance (spherical distance)."
			);
			CHECK_SIGHT = prop(
				"activation_sight_check", checkSight,
				"When the sight check is enabled, " + displayName + " will only trigger when they have direct\n" +
				"line-of-sight to a player within activation range."
			);
		}
	}
	
	// Properties for a world feature that has subtypes.
	@SuppressWarnings( { "WeakerAccess" } )
	public static abstract
	class FeatureSubtyped extends FeatureConfig
	{
		abstract
		String subKey( );
		
		String typeKey( ) { return subKey( ) + "_" + KEY; }
		
		@Override
		String name( ) { return "features_" + typeKey( ); }
		
		@Override
		String comment( )
		{
			return "Options related to the generation of " + KEY + " " + subKey( ) + ".";
		}
		
		FeatureSubtyped( String key, float placement, int minHeight, int maxHeight )
		{
			super( key, placement, minHeight, maxHeight );
		}
	}
	
	// Properties for a world feature that has subtypes.
	public static abstract
	class FeatureMulti extends FeatureConfig
	{
		FeatureMulti( String key, float placement, int minHeight, int maxHeight )
		{
			super( key, placement, minHeight, maxHeight );
		}
	}
	
	// Contains the properties common to all world features.
	public static abstract
	class FeatureConfig extends PropertyCategory
	{
		@Override
		String name( ) { return "features_" + KEY; }
		
		@Override
		String comment( )
		{
			return "Options related to the generation of " + KEY + ".";
		}
		
		private final float                 PLACEMENT_CHANCE;
		private final EnvironmentListConfig PLACEMENT_CHANCE_EXCEPTIONS;
		
		public final int[]   HEIGHTS;
		public final boolean DEBUG_MARKER;
		
		FeatureConfig( String key, float placement, int minHeight, int maxHeight )
		{
			super( key );
			
			if( this instanceof ISubgenFeature ) {
				// Feature does not naturally generate, these config options apply
				PLACEMENT_CHANCE = 0.0F;
				PLACEMENT_CHANCE_EXCEPTIONS = null;
				HEIGHTS = new int[2];
				DEBUG_MARKER = false;
				return;
			}
			
			if( this instanceof FeatureMulti ) {
				// Feature allows a placement count instead of a single chance
				PLACEMENT_CHANCE = prop(
					"_" + KEY + "_count", placement,
					"The number of placement attempts for this feature type.\n" +
					"A decimal represents a chance for a placement attempt (e.g., 0.3 means 30% chance for one attempt)."
				);
				PLACEMENT_CHANCE_EXCEPTIONS = prop(
					"_" + KEY + "_count_exceptions", new TargetEnvironment[ 0 ],
					"The number of placement attempts when generating in particular locations.\n" +
					"More specific locations take priority over others (biome < biome* < global setting)."
				);
			}
			else {
				// Normal feature behavior
				PLACEMENT_CHANCE = prop(
					"_" + KEY + "_chance", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? placement : placement * 0.5F,
					"The ratio of chunks to place this feature in.\n" +
					"This represents a chance for a placement attempt in each chunk from 0 to 1\n" +
					"(e.g., 0.1 means 10% chance per chunk).",
					PropertyCategory.R_FLT_ONE
				);
				PLACEMENT_CHANCE_EXCEPTIONS = prop(
					"_" + KEY + "_chance_exceptions", new TargetEnvironment[ 0 ],
					"The chance for a placement attempt when generating in particular locations.\n" +
					"More specific locations take priority over others (biome < biome* < global setting)."
				);
			}
			
			HEIGHTS = new int[] {
				prop(
					"_" + KEY + "_height_min", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? minHeight : 10,
					"The minimum height to generate this feature at."
				),
				prop(
					"_" + KEY + "_height_max", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? maxHeight : 100,
					"The maximum height to generate this feature at."
				)
			};
			DEBUG_MARKER = prop(
				KEY + "_testing_marker", false,
				"When set to true, places a 1x1 column of glass to the height limit from a few blocks above each generated feature.\n" +
				"This is game-breaking and laggy. You must also enable debug mode in the main mod config.\n" +
				"Consider using a tool to strip away all stone/dirt/etc. for more intensive testing."
			);
		}
		
		public
		float getPlacementChance( World world, BlockPos pos )
		{
			return PLACEMENT_CHANCE_EXCEPTIONS.getValueForLocation( world, pos, PLACEMENT_CHANCE );
		}
	}
	
	// Used to denote features that are strictly generated as part of another feature.
	// Implementing this causes all placement-related options to be ignored in the config.
	interface ISubgenFeature { }
	
	// Contains basic implementations for all config option types, along with some useful constants.
	@SuppressWarnings( { "SameParameterValue", "unused", "WeakerAccess" } )
	static abstract
	class PropertyCategory
	{
		/** Range: { -INF, INF } */
		static final double[] R_DBL_ALL = { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };
		/** Range: { 0.0, INF } */
		static final double[] R_DBL_POS = { 0.0, Double.POSITIVE_INFINITY };
		/** Range: { 0.0, 1.0 } */
		static final double[] R_DBL_ONE = { 0.0, 1.0 };
		
		/** Range: { -INF, INF } */
		static final float[] R_FLT_ALL = { Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY };
		/** Range: { 0.0, INF } */
		static final float[] R_FLT_POS = { 0.0F, Float.POSITIVE_INFINITY };
		/** Range: { 0.0, 1.0 } */
		static final float[] R_FLT_ONE = { 0.0F, 1.0F };
		
		/** Range: { MIN, MAX } */
		static final int[] R_INT_ALL       = { Integer.MIN_VALUE, Integer.MAX_VALUE };
		/** Range: { -1, MAX } */
		static final int[] R_INT_TOKEN_NEG = { -1, Integer.MAX_VALUE };
		/** Range: { 0, MAX } */
		static final int[] R_INT_POS0      = { 0, Integer.MAX_VALUE };
		/** Range: { 1, MAX } */
		static final int[] R_INT_POS1      = { 1, Integer.MAX_VALUE };
		/** Range: { 0, SRT } */
		static final int[] R_INT_SRT_POS   = { 0, Short.MAX_VALUE };
		/** Range: { 0, 255 } */
		static final int[] R_INT_BYT_UNS   = { 0, 0xff };
		/** Range: { 0, 127 } */
		static final int[] R_INT_BYT_POS   = { 0, Byte.MAX_VALUE };
		
		// Support for dynamically generated config categories.
		protected final String KEY;
		
		PropertyCategory( String key )
		{
			KEY = key;
			Config.configLoading.addCustomCategoryComment( name( ), comment( ) );
		}
		
		PropertyCategory( )
		{
			this( null );
		}
		
		abstract
		String name( );
		
		abstract
		String comment( );
		
		double[] defaultDblRange( )
		{
			return PropertyCategory.R_DBL_POS;
		}
		
		float[] defaultFltRange( )
		{
			return PropertyCategory.R_FLT_POS;
		}
		
		int[] defaultIntRange( )
		{
			return PropertyCategory.R_INT_POS0;
		}
		
		WeightedRandomConfig prop( String key, WeightedRandomConfig.Item[] defaultValues, String comment )
		{
			return new WeightedRandomConfig( cprop( key, defaultValues, comment ).getStringList( ) );
		}
		
		Property cprop( String key, WeightedRandomConfig.Item[] defaultValues, String comment )
		{
			String[] defaultIds = new String[ defaultValues.length ];
			for( int i = 0; i < defaultIds.length; i++ ) {
				defaultIds[ i ] = defaultValues[ i ].toString( );
			}
			comment = amendComment( comment, "Weighted_Array", defaultIds, "mod_id:registry_name weight" );
			return Config.configLoading.get( name( ), key, defaultIds, comment );
		}
		
		< T extends Enum< T > & WeightedEnumConfig.Meta > WeightedEnumConfig< T > prop( String key, T[] validValues, String commentPart1, String commentPart2 )
		{
			List< WeightedEnumConfig.Item< T > > items = new ArrayList<>( );
			for( T value : validValues ) {
				String name = value.toString( ).toLowerCase( );
				items.add( new WeightedEnumConfig.Item<>( value, prop(
					key + "_" + name, value.defaultWeight( ),
					commentPart1 + name.replace( "_", " " ) + commentPart2,
					R_INT_POS0
				) ) );
			}
			return new WeightedEnumConfig<>( items );
		}
		
		IBlockState prop( String key, IBlockState defaultValue, String comment )
		{
			String      target     = cprop( key, defaultValue, comment ).getString( );
			IBlockState blockState = TargetBlock.parseStateForMatch( target );
			
			// Fall back to old style
			if( blockState.getBlock( ) == Blocks.AIR ) {
				String[] pair = target.split( " ", 2 );
				if( pair.length > 1 ) {
					Block block = TargetBlock.parseBlock( pair[ 0 ] );
					//noinspection deprecation // Meta will be replaced by block states in the future. Ignore this for now.
					return block.getStateFromMeta( Integer.parseInt( pair[ 1 ].trim( ) ) );
				}
			}
			return blockState;
		}
		
		Property cprop( String key, IBlockState defaultValue, String comment )
		{
			String defaultId = new TargetBlock( defaultValue ).toString( );
			comment = amendComment( comment, "Block", defaultId, "mod_id:block_id, mod_id:block_id[<properties>]" );
			return Config.configLoading.get( name( ), key, defaultId, comment );
		}
		
		TargetBlock.TargetMap prop( String key, TargetBlock[] defaultValues, String comment )
		{
			return TargetBlock.newTargetDefinition( cprop( key, defaultValues, comment ).getStringList( ) );
		}
		
		Property cprop( String key, TargetBlock[] defaultValues, String comment )
		{
			String[] defaultIds = new String[ defaultValues.length ];
			for( int i = 0; i < defaultIds.length; i++ ) {
				defaultIds[ i ] = defaultValues[ i ].toString( );
			}
			comment = amendComment( comment, "Target_Block_Array", defaultIds, "mod_id:block_id, mod_id:block_id[<properties>], mod_id:*" );
			return Config.configLoading.get( name( ), key, defaultIds, comment );
		}
		
		WeightedBlockConfig.BlockList prop( String key, WeightedBlockConfig[] defaultValues, String comment )
		{
			return WeightedBlockConfig.newTargetDefinition( cprop( key, defaultValues, comment ).getStringList( ) );
		}
		
		Property cprop( String key, WeightedBlockConfig[] defaultValues, String comment )
		{
			String[] defaultIds = new String[ defaultValues.length ];
			for( int i = 0; i < defaultIds.length; i++ ) {
				defaultIds[ i ] = defaultValues[ i ].toString( );
			}
			comment = amendComment( comment, "Block_Array", defaultIds, "mod_id:block_id <value>, mod_id:block_id[<properties>] <value>" );
			return Config.configLoading.get( name( ), key, defaultIds, comment );
		}
		
		EntityListConfig prop( String key, EntryEntity[] defaultValues, String comment )
		{
			return new EntityListConfig( cprop( key, defaultValues, comment ).getStringList( ) );
		}
		
		Property cprop( String key, EntryEntity[] defaultValues, String comment )
		{
			String[] defaultIds = new String[ defaultValues.length ];
			for( int i = 0; i < defaultIds.length; i++ ) {
				defaultIds[ i ] = defaultValues[ i ].toString( );
			}
			comment = amendComment( comment, "Entity_Array", defaultIds, "mod_id:entity_id <value>, ~mod_id:entity_id <value>" );
			return Config.configLoading.get( name( ), key, defaultIds, comment );
		}
		
		EnvironmentListConfig prop( String key, TargetEnvironment[] defaultValues, String comment )
		{
			return new EnvironmentListConfig( cprop( key, defaultValues, comment ).getStringList( ) );
		}
		
		Property cprop( String key, TargetEnvironment[] defaultValues, String comment )
		{
			String[] defaultIds = new String[ defaultValues.length ];
			for( int i = 0; i < defaultIds.length; i++ ) {
				defaultIds[ i ] = defaultValues[ i ].toString( );
			}
			comment = amendComment( comment, "Environment_Array", defaultIds, "biome/mod_id:biome_id=value, biome/mod_id:prefix*=value" );
			return Config.configLoading.get( name( ), key, defaultIds, comment );
		}
		
		boolean prop( String key, boolean defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment ).getBoolean( );
		}
		
		Property cprop( String key, boolean defaultValue, String comment )
		{
			comment = amendComment( comment, "Boolean", defaultValue, new Object[] { true, false } );
			return Config.configLoading.get( name( ), key, defaultValue, comment );
		}
		
		boolean[] prop( String key, boolean[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment ).getBooleanList( );
		}
		
		Property cprop( String key, boolean[] defaultValues, String comment )
		{
			comment = amendComment( comment, "Boolean_Array", ArrayUtils.toObject( defaultValues ), new Object[] { true, false } );
			return Config.configLoading.get( name( ), key, defaultValues, comment );
		}
		
		int prop( String key, int defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment ).getInt( );
		}
		
		int prop( String key, int defaultValue, String comment, int... range )
		{
			return cprop( key, defaultValue, comment, range ).getInt( );
		}
		
		Property cprop( String key, int defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment, defaultIntRange( ) );
		}
		
		Property cprop( String key, int defaultValue, String comment, int... range )
		{
			comment = amendComment( comment, "Integer", defaultValue, range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, defaultValue, comment, range[ 0 ], range[ 1 ] );
		}
		
		int[] prop( String key, int[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment ).getIntList( );
		}
		
		int[] prop( String key, int[] defaultValues, String comment, int... range )
		{
			return cprop( key, defaultValues, comment, range ).getIntList( );
		}
		
		Property cprop( String key, int[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment, defaultIntRange( ) );
		}
		
		Property cprop( String key, int[] defaultValues, String comment, int... range )
		{
			comment = amendComment( comment, "Integer_Array", ArrayUtils.toObject( defaultValues ), range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, defaultValues, comment, range[ 0 ], range[ 1 ] );
		}
		
		float prop( String key, float defaultValue, String comment )
		{
			return (float) cprop( key, defaultValue, comment ).getDouble( );
		}
		
		float prop( String key, float defaultValue, String comment, float... range )
		{
			return (float) cprop( key, defaultValue, comment, range ).getDouble( );
		}
		
		Property cprop( String key, float defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment, defaultFltRange( ) );
		}
		
		Property cprop( String key, float defaultValue, String comment, float... range )
		{
			comment = amendComment( comment, "Float", defaultValue, range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, prettyFloatToDouble( defaultValue ), comment, prettyFloatToDouble( range[ 0 ] ), prettyFloatToDouble( range[ 1 ] ) );
		}
		
		double prop( String key, double defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment ).getDouble( );
		}
		
		double prop( String key, double defaultValue, String comment, double... range )
		{
			return cprop( key, defaultValue, comment, range ).getDouble( );
		}
		
		Property cprop( String key, double defaultValue, String comment )
		{
			return cprop( key, defaultValue, comment, defaultDblRange( ) );
		}
		
		Property cprop( String key, double defaultValue, String comment, double... range )
		{
			comment = amendComment( comment, "Double", defaultValue, range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, defaultValue, comment, range[ 0 ], range[ 1 ] );
		}
		
		double[] prop( String key, double[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment ).getDoubleList( );
		}
		
		double[] prop( String key, double[] defaultValues, String comment, double... range )
		{
			return cprop( key, defaultValues, comment, range ).getDoubleList( );
		}
		
		Property cprop( String key, double[] defaultValues, String comment )
		{
			return cprop( key, defaultValues, comment, defaultDblRange( ) );
		}
		
		Property cprop( String key, double[] defaultValues, String comment, double... range )
		{
			comment = amendComment( comment, "Double_Array", ArrayUtils.toObject( defaultValues ), range[ 0 ], range[ 1 ] );
			return Config.configLoading.get( name( ), key, defaultValues, comment, range[ 0 ], range[ 1 ] );
		}
		
		String prop( String key, String defaultValue, String comment, String valueDescription )
		{
			return cprop( key, defaultValue, comment, valueDescription ).getString( );
		}
		
		String prop( String key, String defaultValue, String comment, String... validValues )
		{
			return cprop( key, defaultValue, comment, validValues ).getString( );
		}
		
		Property cprop( String key, String defaultValue, String comment, String valueDescription )
		{
			comment = amendComment( comment, "String", defaultValue, valueDescription );
			return Config.configLoading.get( name( ), key, defaultValue, comment, new String[ 0 ] );
		}
		
		Property cprop( String key, String defaultValue, String comment, String... validValues )
		{
			comment = amendComment( comment, "String", defaultValue, validValues );
			return Config.configLoading.get( name( ), key, defaultValue, comment, validValues );
		}
		
		String[] prop( String key, String[] defaultValues, String comment, String valueDescription )
		{
			return cprop( key, defaultValues, comment, valueDescription ).getStringList( );
		}
		
		Property cprop( String key, String[] defaultValues, String comment, String valueDescription )
		{
			comment = amendComment( comment, "String_Array", defaultValues, valueDescription );
			return Config.configLoading.get( name( ), key, defaultValues, comment );
		}
		
		private
		String amendComment( String comment, String type, Object[] defaultValues, String description )
		{
			return amendComment( comment, type, "{ " + toReadable( defaultValues ) + " }", description );
		}
		
		private
		String amendComment( String comment, String type, Object[] defaultValues, Object min, Object max )
		{
			return amendComment( comment, type, "{ " + toReadable( defaultValues ) + " }", min, max );
		}
		
		private
		String amendComment( String comment, String type, Object[] defaultValues, Object[] validValues )
		{
			return amendComment( comment, type, "{ " + toReadable( defaultValues ) + " }", validValues );
		}
		
		private
		String amendComment( String comment, String type, Object defaultValue, String description )
		{
			return comment + "\n   >> " + type + ":[ " + "Value={ " + description + " }, Default=" + defaultValue + " ]";
		}
		
		private
		String amendComment( String comment, String type, Object defaultValue, Object min, Object max )
		{
			return comment + "\n   >> " + type + ":[ " + "Range={ " + min + ", " + max + " }, Default=" + defaultValue + " ]";
		}
		
		private
		String amendComment( String comment, String type, Object defaultValue, Object[] validValues )
		{
			if( validValues.length < 2 )
				throw new IllegalArgumentException( "Attempted to create config with no options!" );
			
			return comment + "\n   >> " + type + ":[ " + "Valid_Values={ " + toReadable( validValues ) + " }, Default=" + defaultValue + " ]";
		}
		
		private
		double prettyFloatToDouble( float f )
		{
			return Double.parseDouble( Float.toString( f ) );
		}
		
		private
		String toReadable( Object[] array )
		{
			if( array.length <= 0 )
				return "";
			
			StringBuilder commentBuilder = new StringBuilder( );
			for( Object value : array ) {
				commentBuilder.append( value ).append( ", " );
			}
			return commentBuilder.substring( 0, commentBuilder.length( ) - 2 );
		}
	}
}
