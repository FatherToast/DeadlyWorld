package fathertoast.deadlyworld.block.state;

import fathertoast.deadlyworld.*;
import fathertoast.deadlyworld.block.*;
import fathertoast.deadlyworld.config.*;
import fathertoast.deadlyworld.featuregen.*;
import fathertoast.deadlyworld.loot.*;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.*;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Random;

public
enum EnumSpawnerType implements IExclusiveMetaProvider
{
	DEFAULT( "simple" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_DEFAULT; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Lapis Lazuli",
			                    new ItemStack( Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage( ) ), 1, 8 );
			loot.addSemicommonDrop( "semicommon", "Iron Ingot", Items.IRON_INGOT );
			loot.addRareDrop( "rare", "Gem", Items.DIAMOND, Items.EMERALD );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK )
						           .applyOneRandomApplicableEnchant( ).toLootEntry( ) )
					.toLootPool( )
			);
		}
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			loot.addThemePoolExploration( );
			super.buildChestLootTable( loot );
		}
	},
	
	STREAM( "stream" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_STREAM; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Lapis Lazuli",
			                    new ItemStack( Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage( ) ), 1, 8 );
			loot.addSemicommonDrop( "semicommon", "Iron Ingot", Items.IRON_INGOT );
			loot.addUncommonDrop( "uncommon", "Gold Ingot", Items.GOLD_INGOT );
			loot.addRareDrop( "rare", "Gem", Items.DIAMOND, Items.EMERALD );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK )
						           .applyOneRandomApplicableEnchant( ).toLootEntry( ) )
					.toLootPool( )
			);
		}
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			loot.addThemePoolExploration( );
			super.buildChestLootTable( loot );
		}
	},
	
	SWARM( "swarm" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_SWARM; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Lapis Lazuli",
			                    new ItemStack( Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage( ) ), 1, 8 );
			loot.addSemicommonDrop( "semicommon", "Iron Ingot", Items.IRON_INGOT );
			loot.addClusterDrop( "cluster", "Gold Ingot", Items.GOLD_INGOT, 4 );
			loot.addRareDrop( "rare", "Gem", Items.DIAMOND, Items.EMERALD );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK )
						           .applyOneRandomApplicableEnchant( ).toLootEntry( ) )
					.toLootPool( )
			);
		}
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			loot.addThemePoolExplosives( );
			super.buildChestLootTable( loot );
		}
	},
	
	BRUTAL( "brutal" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_BRUTAL; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Lapis Lazuli",
			                    new ItemStack( Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage( ) ), 1, 8 );
			loot.addSemicommonDrop( "semicommon", "Iron Ingot", Items.IRON_INGOT );
			loot.addClusterDrop( "cluster", "Flint", Items.FLINT );
			loot.addRareDrop( "rare", "Gem", Items.DIAMOND, Items.EMERALD );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK )
						           .applyOneRandomApplicableEnchant( ).toLootEntry( ) )
					.toLootPool( )
			);
			
			// Potion Buffs
			loot.addUncommonDrop( "uncommon", "Potion",
			                      PotionUtils.addPotionToItemStack( new ItemStack( Items.POTIONITEM ), PotionTypes.FIRE_RESISTANCE ),
			                      PotionUtils.addPotionToItemStack( new ItemStack( Items.POTIONITEM ), PotionTypes.WATER_BREATHING ),
			                      PotionUtils.addPotionToItemStack( new ItemStack( Items.POTIONITEM ), PotionTypes.STRENGTH )
			                      );
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
		void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random )
		{
			// Topper block
			super.decorateSpawner( generator, spawnerPos, dimConfig, world, random );
			
			// Place vines around outside
			if( dimConfig.SPAWNER_BRUTAL.VINES_CHANCE > 0.0F ) {
				for( int y = 0; y <= 1; y++ ) {
					for( EnumFacing facing : EnumFacing.HORIZONTALS ) {
						BlockPos pos = spawnerPos.add( 0, y, 0 ).offset( facing );
						if( random.nextFloat( ) < dimConfig.SPAWNER_BRUTAL.VINES_CHANCE && world.isAirBlock( pos ) ) {
							generator.setBlock(
								dimConfig, world, random, pos,
								Blocks.VINE.getDefaultState( ).withProperty( BlockVine.getPropertyFor( facing.getOpposite( ) ), true )
							);
						}
					}
				}
			}
		}
		
		@Override
		public
		void initializeEntity( EntityLiving entity, Config dimConfig, World world, BlockPos pos )
		{
			super.initializeEntity( entity, dimConfig, world, pos );
			
			// Apply potion effects
			if( !(entity instanceof EntityCreeper) ) {
				boolean hide = dimConfig.SPAWNER_BRUTAL.AMBIENT_FX;
				if( dimConfig.SPAWNER_BRUTAL.FIRE_RESISTANCE ) {
					entity.addPotionEffect( new PotionEffect( MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, hide, !hide ) );
				}
				if( dimConfig.SPAWNER_BRUTAL.WATER_BREATHING ) {
					entity.addPotionEffect( new PotionEffect( MobEffects.WATER_BREATHING, Integer.MAX_VALUE, 0, hide, !hide ) );
				}
			}
		}
	},
	
	SILVERFISH_NEST( "nest", "silverfish nests" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SILVERFISH_NEST; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Gold Nugget", Items.GOLD_NUGGET, 1, 9 );
			loot.addClusterDrop( "cluster", "Iron Nugget", Items.IRON_NUGGET, 9 );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).applyOneRandomEnchant(
						Enchantments.FORTUNE, Enchantments.LOOTING, Enchantments.LUCK_OF_THE_SEA,
						Enchantments.BANE_OF_ARTHROPODS, Enchantments.SILK_TOUCH
					).toLootEntry( ) )
					.toLootPool( )
			);
			
			// Buggy
			ResourceLocation name     = EntityList.getKey( EntitySilverfish.class );
			ItemStack        spawnEgg = new ItemStack( Items.SPAWN_EGG );
			ItemMonsterPlacer.applyEntityIdToItemStack( spawnEgg, name );
			loot.addSemicommonDrop( "semicommon", "Silverfish Egg", spawnEgg );
			loot.addUncommonDrop( "uncommon", "Seeds", Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.WHEAT_SEEDS );
			loot.addRareDrop( "rare", "Special", Items.EMERALD, Item.getItemFromBlock( Blocks.CAKE ) );
		}
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot )
		{
			loot.addThemePoolBuggy( );
			super.buildChestLootTable( loot );
		}
		
		@Override
		public
		void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random )
		{
			// Decide the nest block to place
			IBlockState nestBlock = ModObjects.infest( dimConfig.SILVERFISH_NEST.NEST_BLOCKS.nextBlock( random, Blocks.COBBLESTONE ) );
			
			// Place a nest block under the spawner if no chest was generated
			BlockPos chestPos = spawnerPos.add( 0, -1, 0 );
			if( world.getBlockState( chestPos ).getBlock( ) != Blocks.CHEST ) {
				generator.setBlock( dimConfig, world, random, chestPos, nestBlock );
			}
			
			// Place the nest block all around spawner
			generator.setBlock( dimConfig, world, random, spawnerPos.add( 0, 1, 0 ), nestBlock );
			for( int y = -1; y <= 1; y++ ) {
				generator.setBlock( dimConfig, world, random, spawnerPos.add( -1, y, 0 ), nestBlock );
				generator.setBlock( dimConfig, world, random, spawnerPos.add( 0, y, -1 ), nestBlock );
				generator.setBlock( dimConfig, world, random, spawnerPos.add( 1, y, 0 ), nestBlock );
				generator.setBlock( dimConfig, world, random, spawnerPos.add( 0, y, 1 ), nestBlock );
			}
			generator.setBlock( dimConfig, world, random, spawnerPos.add( -1, 0, -1 ), nestBlock );
			generator.setBlock( dimConfig, world, random, spawnerPos.add( -1, 0, 1 ), nestBlock );
			generator.setBlock( dimConfig, world, random, spawnerPos.add( 1, 0, -1 ), nestBlock );
			generator.setBlock( dimConfig, world, random, spawnerPos.add( 1, 0, 1 ), nestBlock );
		}
	},
	
	DUNGEON( "dungeon" ) {
		@Override
		public
		Config.FeatureSpawner getFeatureConfig( Config dimConfig ) { return dimConfig.SPAWNER_DUNGEON; }
		
		@Override
		public
		void buildBlockLootTable( LootTableBuilder loot )
		{
			// Basic
			loot.addCommonDrop( "common", "Lapis Lazuli",
			                    new ItemStack( Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage( ) ), 1, 8 );
			loot.addSemicommonDrop( "semicommon", "Iron Ingot", Items.IRON_INGOT );
			loot.addRareDrop( "rare", "Gem", Items.DIAMOND, Items.EMERALD );
			
			loot.addPool(
				new LootPoolBuilder( "enchant" )
					.addConditions( LootPoolBuilder.RARE_CONDITIONS )
					.addEntry( new LootEntryItemBuilder( "Enchants", Items.BOOK ).enchant( 5, 30, true ).toLootEntry( ) )
					.toLootPool( )
			);
		}
		
		@Override
		public
		void buildChestLootTable( LootTableBuilder loot ) { } // The chests are part of the dungeon feature, not the spawner
		
		@Override
		public
		void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random ) { }
	};
	
	public static final String FEATURE_PATH = "deadly_spawners/";
	
	public static final PropertyEnum< EnumSpawnerType > PROPERTY = PropertyEnum.create( "type", EnumSpawnerType.class );
	
	public final ResourceLocation LOOT_TABLE_BLOCK;
	public final ResourceLocation LOOT_TABLE_CHEST;
	
	public final String NAME;
	public final String DISPLAY_NAME;
	
	EnumSpawnerType( String name ) { this( name, name.replace( "_", " " ) + " spawners" ); }
	
	EnumSpawnerType( String name, String prettyName )
	{
		NAME = name;
		DISPLAY_NAME = prettyName;
		
		LOOT_TABLE_BLOCK = LootTableList.register( new ResourceLocation(
			DeadlyWorldMod.MOD_ID, ModObjects.BLOCK_LOOT_TABLE_PATH + BlockDeadlySpawner.ID + "/" + name ) );
		LOOT_TABLE_CHEST = new ResourceLocation(
			DeadlyWorldMod.MOD_ID, FeatureGenerator.CHEST_LOOT_TABLE_PATH + FEATURE_PATH + name );
		if( !"dungeon".equals( name ) ) { // This one has its loot table handled elsewhere
			LootTableList.register( LOOT_TABLE_CHEST );
		}
	}
	
	public abstract
	Config.FeatureSpawner getFeatureConfig( Config dimConfig );
	
	public abstract
	void buildBlockLootTable( LootTableBuilder loot );
	
	public
	void buildChestLootTable( LootTableBuilder loot )
	{
		loot.addLootTable( "base", "Vanilla Chest", LootTableList.CHESTS_SIMPLE_DUNGEON );
	}
	
	public
	boolean canTypeBePlaced( World world, BlockPos position )
	{
		return !world.getBlockState( position.add( 0, 2, 0 ) ).isFullCube( ) &&
		       world.isAirBlock( position.add( 0, 1, 0 ) ) &&
		       world.getBlockState( position.add( 0, -1, 0 ) ).isFullCube( );
	}
	
	public
	void decorateSpawner( WorldGenSpawner generator, BlockPos spawnerPos, Config dimConfig, World world, Random random )
	{
		// Place topper block based on config
		WeightedBlockConfig.BlockList toppers = getFeatureConfig( dimConfig ).TOPPER_BLOCKS;
		if( toppers != null && toppers.TOTAL_WEIGHT > 0 ) {
			generator.setBlock( dimConfig, world, random, spawnerPos.add( 0, 1, 0 ), toppers.nextBlock( random ) );
		}
	}
	
	public
	void initializeEntity( EntityLiving entity, Config dimConfig, World world, BlockPos pos )
	{
		Config.FeatureSpawner config = getFeatureConfig( dimConfig );
		
		// Flat boosts
		if( config.ADDED_ARMOR > 0.0F ) {
			addAttribute( entity, SharedMonsterAttributes.ARMOR, config.ADDED_ARMOR );
		}
		if( config.ADDED_KNOCKBACK_RESIST > 0.0F ) {
			addAttribute( entity, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, config.ADDED_KNOCKBACK_RESIST );
		}
		if( config.ADDED_ARMOR_TOUGHNESS > 0.0F ) {
			addAttribute( entity, SharedMonsterAttributes.ARMOR_TOUGHNESS, config.ADDED_ARMOR_TOUGHNESS );
		}
		
		// Multipliers
		if( config.MULTIPLIER_DAMAGE != 1.0F ) {
			multAttribute( entity, SharedMonsterAttributes.ATTACK_DAMAGE, config.MULTIPLIER_DAMAGE );
		}
		if( config.MULTIPLIER_HEALTH != 1.0F ) {
			multAttribute( entity, SharedMonsterAttributes.MAX_HEALTH, config.MULTIPLIER_HEALTH );
		}
		if( config.MULTIPLIER_SPEED != 1.0F ) {
			multAttribute( entity, SharedMonsterAttributes.MOVEMENT_SPEED, config.MULTIPLIER_SPEED );
		}
		entity.setHealth( entity.getMaxHealth( ) );
	}
	
	public
	WorldGenSpawner makeWorldGen( ) { return new WorldGenSpawner( this ); }
	
	@Override
	public
	String toString( ) { return NAME; }
	
	@Override
	public
	String getName( ) { return NAME; }
	
	@Override
	public
	int getMetadata( ) { return ordinal( ); }
	
	public static
	EnumSpawnerType byMetadata( int meta )
	{
		if( meta < 0 || meta >= values( ).length ) {
			DeadlyWorldMod.log( ).warn( "Attempted to load invalid spawner type with metadata '{}'", meta );
			return DEFAULT;
		}
		return values( )[ meta ];
	}
	
	private static
	void addAttribute( EntityLivingBase entity, IAttribute attribute, double amount )
	{
		try {
			entity.getEntityAttribute( attribute ).setBaseValue( entity.getEntityAttribute( attribute ).getBaseValue( ) + amount );
		}
		catch( Exception ex ) {
			// This is fine, entity just doesn't have the attribute
		}
	}
	
	private static
	void multAttribute( EntityLivingBase entity, IAttribute attribute, double amount )
	{
		try {
			entity.getEntityAttribute( attribute ).setBaseValue( entity.getEntityAttribute( attribute ).getBaseValue( ) * amount );
		}
		catch( Exception ex ) {
			// This is fine, entity just doesn't have the attribute
		}
	}
}
