package fathertoast.deadlyworld;

import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.item.*;
import fathertoast.deadlyworld.oregen.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

import java.io.File;

@SuppressWarnings( "WeakerAccess" )
@Mod( modid = DeadlyWorldMod.MOD_ID, name = DeadlyWorldMod.NAME, version = DeadlyWorldMod.VERSION )
public
class DeadlyWorldMod
{
	public static final String MOD_ID  = "deadlyworld";
	public static final String NAME    = "Deadly World";
	public static final String VERSION = "1.1.1_for_mc1.12.2";
	
	/* TODO LIST:
	 *  - testing & fine tuning
	 *      + overworld
	 *      + nether
	 *      ? end
	 *
	 * Primary features:
	 *  - chests
	 *      + mimic 2.0 (custom entity)
	 *      + cave-in (via surprise or combo)
	 *  - floor traps
	 *      + fire
	 *  - ceiling traps
	 *      + cave-in
	 *      + lava
	 *  - combo traps
	 *      + spider spawner & splash poison dispenser
	 *      + undead spawner & splash harm dispener
	 *      ? any spawner & fish hook dispenser (custom entity)
	 *      ? any floor trap & fish hook dispenser (custom entity)
	 *      ? fire immune spawner & fireball dispenser
	 *      ? creeper spawner & lightning dispenser
	 *  ? support for custom potions in towers/floor traps/events
	 *  ? wall traps
	 *      + arrow traps
	 *  - config tweaks
	 *      ? option to allow floor traps to trigger vs creative mode players, and vice-versa for other traps
	 *
	 * Utility features:
	 *  - modify vanilla structures?
	 *  ? add chance to fail replacing blocks in config (notably per silverfish replaceable block and per vein)
	 */
	
	/** Handles operations when different behaviors are needed between the client and server sides. */
	@SidedProxy( modId = DeadlyWorldMod.MOD_ID, clientSide = "fathertoast.deadlyworld.client.ClientProxy", serverSide = "fathertoast.deadlyworld.server.ServerProxy" )
	public static SidedModProxy sidedProxy;
	
	/** The translation key used by this mod. */
	public static final String LANG_KEY = DeadlyWorldMod.MOD_ID + ".";
	
	private File configDirectory;
	
	private static Logger logger;
	
	public static
	Logger log( ) { return logger; }
	
	public static
	void mark( World world, BlockPos pos )
	{
		DeadlyWorldMod.mark( world, pos, Blocks.GLASS.getDefaultState( ) );
	}
	
	public static
	void mark( World world, BlockPos pos, IBlockState block )
	{
		if( Config.get( ).GENERAL.DEBUG ) {
			for( int y = pos.getY( ) + 1; y < world.getHeight( ); y++ ) {
				world.setBlockState( new BlockPos( pos.getX( ), y, pos.getZ( ) ), block, FeatureGenerator.UPDATE_FLAGS );
			}
			DeadlyWorldMod.log( ).info( "Marked: {}", pos );
		}
	}
	
	@EventHandler
	public
	void preInit( FMLPreInitializationEvent event )
	{
		configDirectory = event.getModConfigurationDirectory( );
		logger = event.getModLog( );
		Config.load( logger, "Deadly_World", configDirectory );
		
		if( Config.get( ).GENERAL.DEBUG ) {
			DeadlyWorldMod.log( ).warn( "Loaded in debug mode!" );
		}
		
		MinecraftForge.EVENT_BUS.register( new ModObjects( ) );
		
		sidedProxy.preInit( );
	}
	
	@EventHandler
	public
	void init( FMLInitializationEvent event )
	{
		ModObjects.registerTileEntities( );
		
		MinecraftForge.EVENT_BUS.register( new DeadlyEventHandler( ) );
		
		FeatureGenerator featureGenerator = new FeatureGenerator( );
		MinecraftForge.EVENT_BUS.register( featureGenerator );
		MinecraftForge.TERRAIN_GEN_BUS.register( featureGenerator );
		
		OreGenerator oreGenerator = new OreGenerator( );
		MinecraftForge.EVENT_BUS.register( oreGenerator );
		MinecraftForge.ORE_GEN_BUS.register( oreGenerator );
		
		sidedProxy.init( );
		
		if( !ModObjects.getSilverfishBlocksOrdered( ).isEmpty( ) ) {
			MinecraftForge.EVENT_BUS.register( new SilverfishBlockEventHandler( ) );
		}
	}
	
	@EventHandler
	public
	void postInit( FMLPostInitializationEvent event )
	{
		boolean isInDev = (Boolean) Launch.blackboard.get( "fml.deobfuscatedEnvironment" );
		if( isInDev ) {
			/* This line updates the loot table asset files.
			 * Assets are copied to a temporary directory on run, so the game needs to be started a second time
			 * before the updated loot table assets can be tested in the game.
			 */
			fathertoast.deadlyworld.loot.LootTableHelper.generateBaseLootTables( configDirectory );
		}
		configDirectory = null; // No longer needed, clear ref
	}
}
