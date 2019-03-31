package fathertoast.deadlyworld;

import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.oregen.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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

@SuppressWarnings( "WeakerAccess" )
@Mod( modid = DeadlyWorldMod.MOD_ID, name = DeadlyWorldMod.NAME, version = DeadlyWorldMod.VERSION )
public
class DeadlyWorldMod
{
	public static final String MOD_ID  = "deadlyworld";
	public static final String NAME    = "Deadly World";
	public static final String VERSION = "1.0.0_for_mc1.12.2";
	
	/* TODO LIST:
	 *
	 * Primary features:
	 *  - feature variants & weighting
	 *      + chest vars (normal, trapped, mimic, cave-in)
	 *      + spawner vars (normal, armored)
	 *  - spawner customizing, incl. user-defined mob ids
	 *  - mimic chests
	 *  - universal silverfish disguise block(s)
	 *  - floor traps & covers
	 *      + mines
	 *      + splash potions (harm, poison, daze)
	 *      + fire
	 *      + towers?
	 *  - ceiling traps
	 *      + cave-ins
	 *      + lava?
	 *  - spawner traps
	 *      + swarm
	 *      + brutal
	 *      + boss
	 *  - wall traps?
	 *      + arrow traps
	 *
	 * Utility features:
	 *  - biome restriction (for all features and veins)
	 *  - replace vanilla dungeons
	 */
	
	/** Handles operations when different behaviors are needed between the client and server sides. */
	@SidedProxy( modId = DeadlyWorldMod.MOD_ID, clientSide = "fathertoast.deadlyworld.client.ClientProxy", serverSide = "fathertoast.deadlyworld.server.ServerProxy" )
	public static SidedModProxy sidedProxy;
	
	/** The translation key used by this mod. */
	public static final String LANG_KEY = DeadlyWorldMod.MOD_ID + ".";
	
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
		logger = event.getModLog( );
		Config.load( logger, "Deadly_World", event.getModConfigurationDirectory( ) );
		
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
		
		MinecraftForge.EVENT_BUS.register( new FeatureGenerator( ) );
		MinecraftForge.ORE_GEN_BUS.register( new OreGenerator( ) );
		
		sidedProxy.init( );
	}
	
	@EventHandler
	public
	void postInit( FMLPostInitializationEvent event ) { }
}
