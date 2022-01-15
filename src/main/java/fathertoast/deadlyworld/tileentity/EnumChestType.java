package fathertoast.deadlyworld.tileentity;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.state.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.item.*;
import fathertoast.deadlyworld.loot.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

public
enum EnumChestType implements IStringSerializable
{
	DEFAULT( "simple" ) {
		@Override
		public
		Config.FeatureChest getFeatureConfig( Config dimConfig ) { return dimConfig.CHEST_DEFAULT; }
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			loot.addThemePoolExploration( );
			super.buildChestLootTable( loot );
		}
		
		@Override
		public
		void decorateChest( WorldGenChest generator, BlockPos chestPos, Config dimConfig, World world, Random random ) { }
	},
	
	VALUABLE( "valuable" ) {
		@Override
		public
		Config.FeatureChest getFeatureConfig( Config dimConfig ) { return dimConfig.CHEST_VALUABLE; }
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			loot.addThemePoolValuable( );
			super.buildChestLootTable( loot );
		}
		
		@Override
		public
		void decorateChest( WorldGenChest generator, BlockPos chestPos, Config dimConfig, World world, Random random )
		{
			// Pick the block to cover chest with
			IBlockState coverBlock = dimConfig.CHEST_VALUABLE.COVER_BLOCKS.TOTAL_WEIGHT > 0 ?
			                         dimConfig.CHEST_VALUABLE.COVER_BLOCKS.nextBlock( random ) : Blocks.OBSIDIAN.getDefaultState( );
			
			// Set all blocks adjacent to the chest to the cover block
			generator.setBlock( dimConfig, world, random, chestPos.add( 0, -1, 0 ), coverBlock );
			generator.setBlock( dimConfig, world, random, chestPos.add( 0, 1, 0 ), coverBlock );
			generator.setBlock( dimConfig, world, random, chestPos.add( -1, 0, 0 ), coverBlock );
			generator.setBlock( dimConfig, world, random, chestPos.add( 0, 0, -1 ), coverBlock );
			generator.setBlock( dimConfig, world, random, chestPos.add( 1, 0, 0 ), coverBlock );
			generator.setBlock( dimConfig, world, random, chestPos.add( 0, 0, 1 ), coverBlock );
		}
	},
	
	TRAPPED( "trapped" ) {
		@Override
		public
		Config.FeatureChest getFeatureConfig( Config dimConfig ) { return dimConfig.CHEST_TRAPPED; }
		
		@Override
		public
		boolean canTypeBePlaced( World world, BlockPos position )
		{
			// Make sure the TNT is generated above a full block
			return world.getBlockState( position.add( 0, -1, 0 ) ).isFullCube( ) && super.canTypeBePlaced( world, position );
		}
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			loot.addThemePoolExplosives( );
			super.buildChestLootTable( loot );
		}
		
		@Override
		public
		void decorateChest( WorldGenChest generator, BlockPos chestPos, Config dimConfig, World world, Random random )
		{
			// Place TNT under chest
			generator.setBlock( dimConfig, world, random, chestPos.add( 0, -1, 0 ), Blocks.TNT.getDefaultState( ) );
		}
	},
	
	TNT_FLOOR_TRAP( "tnt_floor_trapped" ) {
		@Override
		public
		Config.FeatureChest getFeatureConfig( Config dimConfig ) { return dimConfig.CHEST_TNT_FLOOR_TRAP; }
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			loot.addThemePoolExplosives( );
			super.buildChestLootTable( loot );
		}
		
		@Override
		public
		void decorateChest( WorldGenChest generator, BlockPos chestPos, Config dimConfig, World world, Random random )
		{
			// Place TNT floor trap under chest
			IBlockState blockState = ModObjects.FLOOR_TRAP.getDefaultState( ).withProperty( EnumFloorTrapType.PROPERTY, EnumFloorTrapType.TNT );
			BlockPos    trapPos    = chestPos.add( 0, -1, 0 );
			generator.setBlock( dimConfig, world, random, trapPos, blockState );
			ModObjects.FLOOR_TRAP.initTileEntity( world, trapPos, blockState, dimConfig, random );
		}
	},
	
	INFESTED( "infested" ) {
		@Override
		public
		Config.FeatureChest getFeatureConfig( Config dimConfig ) { return dimConfig.CHEST_INFESTED; }
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			// Events should always be the first entry
			loot.addLootTable( "event", "Spawn Silverfish", EnumDeadlyEventType.SILVERFISH.LOOT_TABLE );
			
			loot.addThemePoolBuggy( );
			super.buildChestLootTable( loot );
		}
		
		@Override
		public
		void decorateChest( WorldGenChest generator, BlockPos chestPos, Config dimConfig, World world, Random random ) { }
	},
	
	SURPRISE( "surprise" ) {
		@Override
		public
		Config.FeatureChest getFeatureConfig( Config dimConfig ) { return dimConfig.CHEST_SURPRISE; }
		
		@Override
		public
		ResourceLocation getLootTable( Config dimConfig, World world, Random random, BlockPos position )
		{
			return dimConfig.CHEST_SURPRISE.SURPRISE_TYPE_LIST.nextItem( random ).LOOT_TABLE_CHEST;
		}
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot ) { } // This one is broken out by type - no need for a loot table here
		
		@Override
		public
		void decorateChest( WorldGenChest generator, BlockPos chestPos, Config dimConfig, World world, Random random ) { }
	},
	
	MIMIC( "mimic" ) {
		@Override
		public
		Config.FeatureChest getFeatureConfig( Config dimConfig ) { return dimConfig.CHEST_MIMIC; }
		
		@Override
		public
		ResourceLocation getLootTable( Config dimConfig, World world, Random random, BlockPos position )
		{
			return EnumDeadlyEventType.SPAWN_MIMIC.LOOT_TABLE; // The actual loot table is applied during the spawn mimic event
		}
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			loot.addThemePoolValuable( );
			super.buildChestLootTable( loot );
		}
		
		@Override
		public
		void decorateChest( WorldGenChest generator, BlockPos chestPos, Config dimConfig, World world, Random random ) { }
	};
	
	public static final String FEATURE_PATH = "chests/";
	
	public final ResourceLocation LOOT_TABLE_CHEST;
	
	public final String NAME;
	public final String DISPLAY_NAME;
	
	EnumChestType( String name ) { this( name, name.replace( "_", " " ) + " chests" ); }
	
	EnumChestType( String name, String prettyName )
	{
		NAME = name;
		DISPLAY_NAME = prettyName;
		
		LOOT_TABLE_CHEST = new ResourceLocation(
			DeadlyWorldMod.MOD_ID, FeatureGenerator.CHEST_LOOT_TABLE_PATH + FEATURE_PATH + name );
		if( !"surprise".equals( name ) ) { // This one goes to a directory, not an actual loot table
			LootTableList.register( LOOT_TABLE_CHEST );
		}
	}
	
	public abstract
	Config.FeatureChest getFeatureConfig( Config dimConfig );
	
	public
	void buildChestLootTable( LootTableBuilder loot )
	{
		loot.addLootTable( "base", "Vanilla Chest", LootTableList.CHESTS_SIMPLE_DUNGEON );
	}
	
	public
	boolean canTypeBePlaced( World world, BlockPos position )
	{
		return !world.getBlockState( position.add( 0, 2, 0 ) ).isFullCube( ) &&
		       world.isAirBlock( position.add( 0, 1, 0 ) );
	}
	
	public
	ResourceLocation getLootTable( Config dimConfig, World world, Random random, BlockPos position ) { return LOOT_TABLE_CHEST; }
	
	public abstract
	void decorateChest( WorldGenChest generator, BlockPos chestPos, Config dimConfig, World world, Random random );
	
	public
	WorldGenChest makeWorldGen( ) { return new WorldGenChest( this ); }
	
	@Override
	public
	String toString( ) { return NAME; }
	
	@Override
	public
	String getName( ) { return NAME; }
}
