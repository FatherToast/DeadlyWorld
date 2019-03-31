package fathertoast.deadlyworld.config;

import fathertoast.deadlyworld.block.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.monster.*;
import net.minecraft.init.Blocks;
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
	
	// General category is specific to the main config (dimension 0).
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
		
		public final boolean FEATURE_TESTER = prop(
			"item_feature_tester", true,
			"Set this to false to disable the \'Feature Tester\' item."
		);
		
		public final boolean SILVERFISH_AUTOGEN = prop(
			"silverfish_blocks", true,
			"Set this to false to disable the blocks automatically built and registered to disguise themselves\n" +
			"as the blocks defined as \"replaceable\" below.\n" +
			"Does not disable the infested variants of cobblestone and mossy cobblestone (for use in various features)."
		);
		
		public final boolean SILVERFISH_DISGUISE_MOD = prop(
			"silverfish_blocks_disguise_mod", true,
			"When true, silverfish blocks will be considered \'added by\' the mod of the block they are disguised as.\n" +
			"Not much of an effect outside of tooltips (notably, block tooltips that would give away the block)."
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
			"List of blockstates that can be replaced by silverfish blocks. Each block defined here will have a\n" +
			"corresponding infested version generated and registered. All valid blockstates of any block here will\n" +
			"function and appear in the creative menu (plus cobblestone and mossy cobblestone), but the generator will\n" +
			"only replace blocks matching the state definitions in this list.\n" +
			" * Note that only full-cube blocks are supported. There is no reason this shouldn\'t work with any full-cube\n" +
			"blocks used in world generation from other mods. If it doesn\'t, try to get the author of the incompatible\n" +
			"mod in contact with me so I can help them fix it.\n" +
			"This mod\'s silverfish blocks must be enabled for this to have any effect."
		);
		
		public final float SPAWNER_HARDNESS = prop(
			"spawner_hardness", 5.0F,
			"How long it takes to break Deadly World spawners.\n" +
			"The default hardness is the same as vanilla mob spawners."
		);
		
		public final float SPAWNER_RESISTANCE = prop(
			"spawner_explosion_resist", 2000.0F,
			"How resistant Deadly World spawners are to being destroyed by explosions.\n" +
			"The default explosion resistance is the same as vanilla obsidian."
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
			"silverfish_aggressive_chance", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? 0.15F : 0.3F,
			"The chance for silverfish emerging from this mod's silverfish blocks in this dimension to spawn\n" +
			"already calling for reinforcements, if any players are within eyesight."
		);
		
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
	}
	
	public final Vein VEIN_LAVA       = new Vein(
		"lava", 4.0,
		0, 32, 3
	);
	public final Vein VEIN_SAND       = new Vein(
		"sand", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? 0.25 : 0.0,
		0, 62, 33
	);
	public final Vein VEIN_SILVERFISH = new Vein(
		"silverfish", 10.0,
		5, 255, 25
	);
	public final Vein VEIN_WATER      = new Vein(
		"water", Config.dimensionLoading == DimensionType.OVERWORLD.getId( ) ? 6.0 : 0.0,
		0, 62, 7
	);
	
	public final Vein VEIN_DIRT     = new VeinReplacement( "dirt", 0, 256, 33 );
	public final Vein VEIN_GRAVEL   = new VeinReplacement( "gravel", 0, 256, 33 );
	public final Vein VEIN_DIORITE  = new VeinReplacement( "diorite", 0, 80, 33 );
	public final Vein VEIN_GRANITE  = new VeinReplacement( "granite", 0, 80, 33 );
	public final Vein VEIN_ANDESITE = new VeinReplacement( "andesite", 0, 80, 33 );
	
	public final Vein VEIN_COAL     = new VeinReplacement( "coal", 0, 128, 17 );
	public final Vein VEIN_QUARTZ   = new VeinReplacement( "quartz", 10, 118, 14 );
	public final Vein VEIN_IRON     = new VeinReplacement( "iron", 0, 64, 9 );
	public final Vein VEIN_GOLD     = new VeinReplacement( "gold", 0, 32, 9 );
	public final Vein VEIN_REDSTONE = new VeinReplacement( "redstone", 0, 16, 8 );
	public final Vein VEIN_DIAMOND  = new VeinReplacement( "diamond", 0, 16, 8 );
	public final Vein VEIN_LAPIS    = new VeinReplacement( "lapis", 0, 32, 7 );
	public final Vein VEIN_EMERALD  = new VeinReplacement( "emerald", 4, 32, 1 );
	
	public static
	class Vein extends PropertyCategory
	{
		@Override
		String name( ) { return "veins_" + KEY; }
		
		@Override
		String comment( )
		{
			return "Options related to " + KEY + " 'vein' generation.";
		}
		
		public final double  PLACEMENTS;
		public final int[]   HEIGHTS;
		public final int[]   SIZES;
		public final boolean DEBUG_MARKER;
		
		Vein( String key, double placements, int minHeight, int maxHeight, int size )
		{
			this( key, placements, minHeight, maxHeight, size, size );
		}
		
		Vein( String key, double placements, int minHeight, int maxHeight, int minSize, int maxSize )
		{
			super( key );
			
			PLACEMENTS = prop(
				"_count", placements,
				"The number of placement attempts for this vein type.\n" +
				"A decimal represents a chance for a placement attempt (e.g., 0.3 means 30% chance for one attempt)."
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
	}
	
	public static
	class VeinReplacement extends Vein
	{
		@Override
		String name( ) { return "veins_xtra_" + KEY; }
		
		@Override
		String comment( )
		{
			return "Options related to additional " + KEY + " vein generation.\n" +
			       "This ignores the 'disabled' vein settings, allowing you to replace normal vein generation.";
		}
		
		VeinReplacement( String key, int minHeight, int maxHeight, int size )
		{
			super( key, 0.0, minHeight, maxHeight, size );
		}
	}
	
	public static
	class VeinUserDefined extends Vein
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
			super( String.valueOf( index ), 0.0, 0, 62, 9 );
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
	
	public final FeatureChest FEATURE_CHEST = new FeatureChest( "lone", 0.1, 12, 52 );
	
	public static
	class FeatureChest extends FeatureConfig
	{
		private static final String SUBKEY = "_chests";
		
		@Override
		String comment( )
		{
			return "Options related to the generation of " + KEY.substring( 0, KEY.length( ) - SUBKEY.length( ) ) + " chests.";
		}
		
		FeatureChest( String key, double placement, int minHeight, int maxHeight )
		{
			super( key + SUBKEY, placement, minHeight, maxHeight );
		}
	}
	
	public final FeatureSpawner SPAWNER_LONE = new FeatureSpawner(
		EnumSpawnerType.LONE, 0.3F, 0.16, 12, 52
	);
	
	public final FeatureSpawner SPAWNER_STREAM = new FeatureSpawner(
		EnumSpawnerType.STREAM, 1.0F, 0.04, 12, 42,
		16.0F, true, 0, 400, 10, 1, 2.0F
	);
	
	public final FeatureSpawner SPAWNER_SWARM = new FeatureSpawner(
		EnumSpawnerType.SWARM, 1.0F, 0.04, 12, 32,
		20.0F, true, 400, 2400, 100, 12, 8.0F
	);
	
	public final SPAWNER_BRUTAL SPAWNER_BRUTAL = new SPAWNER_BRUTAL( );
	
	public static
	class SPAWNER_BRUTAL extends FeatureSpawner
	{
		SPAWNER_BRUTAL( )
		{
			super(
				EnumSpawnerType.BRUTAL, 1.0F, 0.04, 12, 32,
				16.0F, true, 200, 800, 100, 2, 3.0F
			);
		}
		
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
	
	public final SPAWNER_SILVERFISH_NEST SPAWNER_SILVERFISH_NEST = new SPAWNER_SILVERFISH_NEST( );
	
	public static
	class SPAWNER_SILVERFISH_NEST extends FeatureSpawner
	{
		SPAWNER_SILVERFISH_NEST( )
		{
			super(
				EnumSpawnerType.SILVERFISH_NEST, 0.3F, 0.16, 12, 62,
				16.0F, false, 100, 400, 20, 6, 6.0F
			);
		}
		
		@Override
		protected
		WeightedRandomConfig.Item[] makeDefaultSpawnList( )
		{
			return new WeightedRandomConfig.Item[] { new WeightedRandomConfig.Item( EntitySilverfish.class, 100 ) };
		}
	}
	
	public static
	class FeatureSpawner extends FeatureConfig
	{
		private static final String SUBKEY = "_spawners";
		
		@Override
		String comment( )
		{
			return "Options related to the generation of " + KEY.substring( 0, KEY.length( ) - SUBKEY.length( ) ) + " spawners.";
		}
		
		public final float CHEST_CHANCE;
		
		public final float                DYNAMIC_CHANCE;
		public final WeightedRandomConfig SPAWN_LIST;
		
		public final float   ACTIVATION_RANGE;
		public final boolean CHECK_SIGHT;
		
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
		
		FeatureSpawner( EnumSpawnerType type, float chestChance, double placement, int minHeight, int maxHeight )
		{
			this(
				type, chestChance, placement, minHeight, maxHeight,
				16.0F, false, 200, 800, 40, 4, 4.0F
			);
		}
		
		FeatureSpawner( EnumSpawnerType type, float chestChance, double placement, int minHeight, int maxHeight,
		                float actRange, boolean checkSight, int minDelay, int maxDelay, int prgrDelay, int spawnCount, float spawnRange )
		{
			super( type.getName( ) + SUBKEY, placement, minHeight, maxHeight );
			
			CHEST_CHANCE = prop(
				"_chest_chance", chestChance,
				"The chance for a chest to generate beneath " + type.displayName + " spawners.\n" +
				"For reference, the loot table for these chests is \'" + type.lootTable.toString( ) + "\'.",
				R_FLT_ONE
			);
			
			DYNAMIC_CHANCE = prop(
				"_dynamic_chance", type == EnumSpawnerType.STREAM ? 1.0F : type == EnumSpawnerType.SILVERFISH_NEST ? 0.0F : 0.2F,
				"The chance for a " + type.displayName + " to generate as \'dynamic\'.\n" +
				"Dynamic spawners pick a new mob to spawn after each spawn.",
				R_FLT_ONE
			);
			SPAWN_LIST = prop(
				"_spawn_list", makeDefaultSpawnList( ),
				"Weighted list of mobs that can be spawned by " + type.displayName + "s. One of these is chosen\n" +
				"at random when the spawner is generated. Spawners that are generated as \'dynamic\' will pick again\n" +
				"between each spawn."
			);
			
			ACTIVATION_RANGE = prop(
				"activation_range", actRange,
				"The spawner is active as long as a player is within this distance (spherical distance)."
			);
			CHECK_SIGHT = prop(
				"activation_sight_check", checkSight,
				"When the sight check is enabled, " + type.displayName + " will only spawn when they have direct\n" +
				"line-of-sight to a player within activation range. The spawner\'s delay will continue to tick down,\n" +
				"but it will wait to actually spawn until it has line-of-sight."
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
				"attrib_add_armor", type == EnumSpawnerType.BRUTAL ? 15.0F : 0.0F,
				"Bonus added to spawned entites\' base armor attribute.",
				0.0F, 30.0F
			);
			ADDED_ARMOR_TOUGHNESS = prop(
				"attrib_add_armor_toughness", type == EnumSpawnerType.BRUTAL ? 8.0F : 0.0F,
				"Bonus added to spawned entites\' base armor toughness attribute.",
				0.0F, 20.0F
			);
			ADDED_KNOCKBACK_RESIST = prop(
				"attrib_add_knockback_resist", type == EnumSpawnerType.BRUTAL ? 0.2F : 0.0F,
				"Bonus added to spawned entites\' base knockback resistance attribute (1.00 = 100% chance to resist).",
				R_FLT_ONE
			);
			
			MULTIPLIER_DAMAGE = prop(
				"attrib_mult_damage", type == EnumSpawnerType.BRUTAL ? 1.5F : 1.0F,
				"Multiplier applied to spawned entites\' base attack damage attribute."
			);
			MULTIPLIER_HEALTH = prop(
				"attrib_mult_health", type == EnumSpawnerType.BRUTAL ? 1.5F : 1.0F,
				"Multiplier applied to spawned entites\' base health attribute."
			);
			MULTIPLIER_SPEED = prop(
				"attrib_mult_speed", type == EnumSpawnerType.BRUTAL ? 1.2F : 1.0F,
				"Multiplier applied to spawned entites\' base movement speed attribute."
			);
			
			SPAWN_COUNT = prop(
				"spawn_count", spawnCount,
				"The number of mobs to attempt creating with each spawn. May spawn fewer depending on nearby obstructions."
			);
			SPAWN_RANGE = prop(
				"spawn_range", spawnRange,
				"The maximum horizontal range to spawn mobs in."
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
	
	// Contains the properties common to all world features.
	public static abstract
	class FeatureConfig extends PropertyCategory
	{
		@Override
		String name( ) { return "features_" + KEY; }
		
		public final double  PLACEMENT_CHANCE;
		public final int[]   HEIGHTS;
		public final boolean DEBUG_MARKER;
		
		FeatureConfig( String key, double placement, int minHeight, int maxHeight )
		{
			super( key );
			
			PLACEMENT_CHANCE = prop(
				"_" + KEY + "_chance", placement,
				"The ratio of chunks to place this feature in.\n" +
				"This represents a chance for a placement attempt in each chunk from 0 to 1\n" +
				"(e.g., 0.1 means 10% chance per chunk).",
				PropertyCategory.R_DBL_ONE
			);
			HEIGHTS = new int[] {
				prop(
					"_" + KEY + "_height_min", minHeight,
					"The minimum height to generate this feature at."
				),
				prop(
					"_" + KEY + "_height_max", Config.dimensionLoading == DimensionType.NETHER.getId( ) ? 100 : maxHeight,
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
	}
	
	// Contains basic implementations for all config option types, along with some useful constants.
	@SuppressWarnings( { "SameParameterValue", "unused", "WeakerAccess" } )
	private static abstract
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
		
		IBlockState prop( String key, IBlockState defaultValue, String comment )
		{
			String      target     = cprop( key, defaultValue, comment ).getString( );
			IBlockState blockState = TargetBlock.parseStateForMatch( target );
			
			// Fall back to old style
			if( blockState.getBlock( ) == Blocks.AIR ) {
				String[] pair = target.split( " ", 2 );
				if( pair.length > 1 ) {
					Block block = TargetBlock.getStringAsBlock( pair[ 0 ] );
					//noinspection deprecation // Meta will be replaced by block states in the future. Ignore this for now.
					return block.getStateFromMeta( Integer.parseInt( pair[ 1 ].trim( ) ) );
				}
			}
			return blockState;
		}
		
		Property cprop( String key, IBlockState defaultValue, String comment )
		{
			String defaultId = Block.REGISTRY.getNameForObject( defaultValue.getBlock( ) ).toString( )
			                   + " " + defaultValue.getBlock( ).getMetaFromState( defaultValue );
			comment = amendComment( comment, "Block", defaultId, "mod_id:block_id, mod_id:block_id[<properties>], mod_id:block_id meta" );
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
			comment = amendComment( comment, "Block_Array", defaultIds, "mod_id:block_id, mod_id:block_id[<properties>], mod_id:*" );
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
			comment = amendComment( comment, "Entity_Array", defaultIds, "mod_id:entity_id <extra_data>, ~mod_id:entity_id <extra_data>" );
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
