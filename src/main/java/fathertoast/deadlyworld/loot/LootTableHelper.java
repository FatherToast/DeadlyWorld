package fathertoast.deadlyworld.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.item.*;
import fathertoast.deadlyworld.tileentity.*;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@ParametersAreNonnullByDefault
public
class LootTableHelper
{
	/** The file extension for loot table files. */
	private static final String FILE_EXT = ".json";
	
	/** The Gson format to generate files with. */
	private static final Gson GSON_LOOT_TABLES =
		new GsonBuilder( )
			.registerTypeAdapter( RandomValueRange.class, new RandomValueRange.Serializer( ) )
			.registerTypeAdapter( LootPool.class, new LootPool.Serializer( ) )
			.registerTypeAdapter( LootTable.class, new LootTable.Serializer( ) )
			.registerTypeHierarchyAdapter( LootEntry.class, new LootEntry.Serializer( ) )
			.registerTypeHierarchyAdapter( LootFunction.class, new LootFunctionManager.Serializer( ) )
			.registerTypeHierarchyAdapter( LootCondition.class, new LootConditionManager.Serializer( ) )
			.registerTypeHierarchyAdapter( LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer( ) )
			.setPrettyPrinting( ).create( );
	
	/**
	 * Generates the base loot tables to be included in this mod's resources.
	 */
	public static
	void generateBaseLootTables( File configDir )
	{
		// Extra safeguards to help prevent running this code outside of the development environment
		File runDir = configDir.getParentFile( );
		if( !"run".equals( runDir.getName( ) ) ) {
			return; // Silently ignore this call
		}
		File projectDir    = runDir.getParentFile( );
		File lootTablesDir = new File( projectDir, "src/main/resources/assets/" + DeadlyWorldMod.MOD_ID + "/loot_tables/" );
		if( !lootTablesDir.exists( ) ) {
			return; // Silently ignore this call
		}
		
		DeadlyWorldMod.log( ).warn( "Generating base loot tables..." );
		long startTime = System.nanoTime( );
		
		DeadlyWorldMod.log( ).warn( "Loot table directory: '{}'", lootTablesDir.getAbsolutePath( ) );
		deleteAllLootTables( lootTablesDir );
		createAllLootTables( lootTablesDir );
		
		long estimatedTime = System.nanoTime( ) - startTime;
		DeadlyWorldMod.log( ).warn( "Generated base loot tables in {} ms", estimatedTime / 1.0E6 );
	}
	
	/**
	 * Recursively destroys all files in a directory.
	 */
	private static
	void deleteAllLootTables( File directory )
	{
		File[] files = directory.listFiles( );
		if( files != null ) {
			for( File file : files ) {
				if( file.isDirectory( ) ) {
					deleteAllLootTables( file );
				}
				if( !file.delete( ) ) {
					DeadlyWorldMod.log( ).error( "Failed to delete file: '{}'", file.getPath( ) );
				}
			}
		}
	}
	
	/**
	 * Creates all base loot table files.
	 */
	private static
	void createAllLootTables( File lootTablesDir )
	{
		lootTablesDir.mkdirs( );
		
		createUtilityLootTables( new File( lootTablesDir, EnumDeadlyEventType.PATH ) );
		createBlockLootTables( new File( lootTablesDir, ModObjects.BLOCK_LOOT_TABLE_PATH ) );
		createFeatureLootTables( new File( lootTablesDir, FeatureGenerator.CHEST_LOOT_TABLE_PATH ) );
	}
	
	/**
	 * Creates base utility loot table files.
	 */
	private static
	void createUtilityLootTables( File subDir )
	{
		subDir.mkdirs( );
		
		for( EnumDeadlyEventType event : EnumDeadlyEventType.values( ) ) {
			LootTableBuilder loot = new LootTableBuilder( );
			event.buildLootTable( loot );
			generateFile( subDir, event.NAME, loot );
		}
	}
	
	/**
	 * Creates base loot table files for block drops.
	 */
	private static
	void createBlockLootTables( File subDir )
	{
		File blockDir;
		subDir.mkdirs( );
		
		// Floor traps
		blockDir = new File( subDir, BlockFloorTrap.ID + "/" );
		blockDir.mkdirs( );
		for( EnumFloorTrapType type : EnumFloorTrapType.values( ) ) {
			LootTableBuilder loot = new LootTableBuilder( );
			type.buildBlockLootTable( loot );
			generateFile( blockDir, type.NAME, loot );
		}
		
		// Deadly spawners
		blockDir = new File( subDir, BlockDeadlySpawner.ID + "/" );
		blockDir.mkdirs( );
		for( EnumSpawnerType type : EnumSpawnerType.values( ) ) {
			LootTableBuilder loot = new LootTableBuilder( );
			type.buildBlockLootTable( loot );
			generateFile( blockDir, type.NAME, loot );
		}
		
		// Tower dispensers
		blockDir = new File( subDir, BlockTowerDispenser.ID + "/" );
		blockDir.mkdirs( );
		for( EnumTowerType type : EnumTowerType.values( ) ) {
			LootTableBuilder loot = new LootTableBuilder( );
			type.buildBlockLootTable( loot );
			generateFile( blockDir, type.NAME, loot );
		}
	}
	
	/**
	 * Creates base loot table files for features that generate chests.
	 */
	private static
	void createFeatureLootTables( File subDir )
	{
		/* For Reference, vanilla dungeon chests have 3 loot pools:
		 *
		 * 1-3 x Rare items:
		 *      Saddle, Golden apple, Epic golden apple, Record - 13, Record - Cat,
		 *      Name tag, Gold horse armor, Iron horse armor, Diamond horse armor,
		 *      Enchanted book
		 *
		 * 1-4 x Basic items:
		 *      Iron ingots, Gold ingots, Bread, Wheat, Bucket, Redstone, Coal,
		 *      Melon seeds, Pumpkin seeds, Beetroot seeds
		 *
		 * 3 x Common mob loot:
		 *      Bones, Gunpowder, Rotten flesh, String
		 */
		
		File featureDir;
		subDir.mkdirs( );
		
		// Chests
		featureDir = new File( subDir, EnumChestType.FEATURE_PATH );
		featureDir.mkdirs( );
		for( EnumChestType type : EnumChestType.values( ) ) {
			if( type != EnumChestType.SURPRISE ) {
				LootTableBuilder loot = new LootTableBuilder( );
				type.buildChestLootTable( loot );
				generateFile( featureDir, type.NAME, loot );
			}
		}
		// Surprise chests
		featureDir = new File( subDir, EnumChestType.FEATURE_PATH + EnumChestType.SURPRISE.NAME + "/" );
		featureDir.mkdirs( );
		for( EnumSurpriseChestType type : EnumSurpriseChestType.values( ) ) {
			LootTableBuilder loot = new LootTableBuilder( );
			type.buildChestLootTable( loot );
			generateFile( featureDir, type.NAME, loot );
		}
		
		// Spawners
		featureDir = new File( subDir, EnumSpawnerType.FEATURE_PATH );
		featureDir.mkdirs( );
		for( EnumSpawnerType type : EnumSpawnerType.values( ) ) {
			if( type != EnumSpawnerType.DUNGEON ) { // Handled below
				LootTableBuilder loot = new LootTableBuilder( );
				type.buildChestLootTable( loot );
				generateFile( featureDir, type.NAME, loot );
			}
		}
		
		// Dungeons
		LootTableBuilder loot = new LootTableBuilder( );
		FeatureGenerator.DUNGEON_FEATURE.buildChestLootTable( loot );
		generateFile( subDir, FeatureGenerator.DUNGEON_FEATURE.NAME, loot );
	}
	
	private static void generateFile( File directory, String path, LootTableBuilder loot )
	{
		File lootTableFile = new File( directory, path.toLowerCase( ) + FILE_EXT );
		try {
			lootTableFile.createNewFile( );
			FileWriter out = new FileWriter( lootTableFile );
			GSON_LOOT_TABLES.toJson( loot.toLootTable( ), LootTable.class, out );
			out.close( );
		}
		catch( IOException ex ) {
			DeadlyWorldMod.log( ).error( "Failed to generate loot table: '{}'", lootTableFile.getPath( ), ex );
		}
	}
}
