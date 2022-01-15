package fathertoast.deadlyworld.loot;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( { "unused", "WeakerAccess", "UnusedReturnValue" } )
public
class LootTableBuilder
{
	private final List< LootPool > pools = new ArrayList<>( );
	
	/** @return A new loot table object reflecting the current state of this builder. */
	public
	LootTable toLootTable( ) { return new LootTable( pools.toArray( new LootPool[ 0 ] ) ); }
	
	public
	LootTableBuilder addThemePoolExploration( )
	{
		LootPoolBuilder pool = new LootPoolBuilder( "exploration" ).setRolls( 1.0F, 3.0F );
		// Weapons
		pool.addEntry( new LootEntryItemBuilder( "Bow", Items.BOW ).setWeight( 10 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Arrow", Items.ARROW ).setWeight( 15 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Stone Sword", Items.STONE_SWORD ).setWeight( 10 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Iron Sword", Items.IRON_SWORD ).setWeight( 10 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Diamond Sword", Items.DIAMOND_SWORD ).setWeight( 5 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Shield", Items.SHIELD ).setWeight( 10 ).toLootEntry( ) );
		// Buckets
		pool.addEntry( new LootEntryItemBuilder( "Water Bucket", Items.WATER_BUCKET ).setWeight( 5 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Lava Bucket", Items.LAVA_BUCKET ).setWeight( 5 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Milk Bucket", Items.MILK_BUCKET ).setWeight( 5 ).toLootEntry( ) );
		// Utilities
		pool.addEntry( new LootEntryItemBuilder( "Ender Pearl", Items.ENDER_PEARL ).setWeight( 10 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Clock", Items.CLOCK ).setWeight( 10 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Compass", Items.COMPASS ).setWeight( 10 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Map", Items.MAP ).setWeight( 10 ).toLootEntry( ) );
		// Enchanted book
		pool.addEntry( new LootEntryItemBuilder( "Enchanted Book", Items.BOOK ).setWeight( 5 )
			               .enchant( 10, 30, true ).toLootEntry( ) );
		
		pools.add( pool.toLootPool( ) );
		return this;
	}
	
	public
	LootTableBuilder addThemePoolValuable( )
	{
		LootPoolBuilder pool = new LootPoolBuilder( "valuable" ).setRolls( 1.0F, 3.0F );
		// Materials
		pool.addEntry( new LootEntryItemBuilder( "Iron Ingot", Items.IRON_INGOT ).setWeight( 15 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Gold Ingot", Items.GOLD_INGOT ).setWeight( 10 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Diamond", Items.DIAMOND ).setWeight( 5 ).setCount( 1, 3 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Emerald", Items.EMERALD ).setWeight( 5 ).setCount( 1, 3 ).toLootEntry( ) );
		// Food
		pool.addEntry( new LootEntryItemBuilder( "Cake", Blocks.CAKE ).setWeight( 1 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Gold Carrot", Items.GOLDEN_CARROT ).setWeight( 10 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Gold Apple", Items.GOLDEN_APPLE ).setWeight( 10 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Epic Apple", new ItemStack( Items.GOLDEN_APPLE, 1, 1 ) ).setWeight( 2 ).toLootEntry( ) );
		// Iron equipment
		pool.addEntry( new LootEntryItemBuilder( "Iron Helmet", Items.IRON_HELMET ).setWeight( 3 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Iron Chestplate", Items.IRON_CHESTPLATE ).setWeight( 3 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Iron Leggings", Items.IRON_LEGGINGS ).setWeight( 3 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Iron Boots", Items.IRON_BOOTS ).setWeight( 3 ).toLootEntry( ) );
		// Diamond equipment
		pool.addEntry( new LootEntryItemBuilder( "Diamond Sword", Items.DIAMOND_SWORD ).setWeight( 2 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Diamond Pick", Items.DIAMOND_PICKAXE ).setWeight( 1 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Diamond Axe", Items.DIAMOND_AXE ).setWeight( 1 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Diamond Shovel", Items.DIAMOND_SHOVEL ).setWeight( 2 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Diamond Helmet", Items.DIAMOND_HELMET ).setWeight( 1 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Diamond Chestplate", Items.DIAMOND_CHESTPLATE ).setWeight( 1 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Diamond Leggings", Items.DIAMOND_LEGGINGS ).setWeight( 1 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Diamond Boots", Items.DIAMOND_BOOTS ).setWeight( 1 ).toLootEntry( ) );
		// Enchanted book
		pool.addEntry( new LootEntryItemBuilder( "Enchanted Book", Items.BOOK ).setWeight( 10 )
			               .enchant( 30, true ).toLootEntry( ) );
		
		pools.add( pool.toLootPool( ) );
		return this;
	}
	
	public
	LootTableBuilder addThemePoolExplosives( )
	{
		LootPoolBuilder pool = new LootPoolBuilder( "explosives" ).setRolls( 1.0F, 3.0F );
		// Explosives
		pool.addEntry( new LootEntryItemBuilder( "Gunpowder", Items.GUNPOWDER ).setWeight( 20 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "TNT", Blocks.TNT ).setWeight( 10 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "TNT Minecart", Items.TNT_MINECART ).setWeight( 5 ).toLootEntry( ) );
		// Igniters
		pool.addEntry( new LootEntryItemBuilder( "Flame Bow", Items.BOW )
			               .applyOneRandomEnchant( Enchantments.FLAME ).setWeight( 5 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Fire Charge", Items.FIRE_CHARGE ).setWeight( 10 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Flint & Steel", Items.FLINT_AND_STEEL ).setWeight( 10 ).toLootEntry( ) );
		// Tools
		pool.addEntry( new LootEntryItemBuilder( "Redstone", Items.REDSTONE ).setWeight( 10 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Repeater", Items.REPEATER ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Comparator", Items.COMPARATOR ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Observer", Blocks.OBSERVER ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Daylight Detector", Blocks.DAYLIGHT_DETECTOR ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Shears", Items.SHEARS ).setWeight( 10 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "String", Items.STRING ).setWeight( 10 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Tripwire", Blocks.TRIPWIRE_HOOK ).setWeight( 5 ).setCount( 2 ).toLootEntry( ) );
		// Enchanted book
		pool.addEntry( new LootEntryItemBuilder( "Enchanted Book", Items.BOOK ).setWeight( 5 )
			               .applyOneRandomEnchant( Enchantments.BLAST_PROTECTION, Enchantments.SWEEPING, Enchantments.FLAME ).toLootEntry( ) );
		
		pools.add( pool.toLootPool( ) );
		return this;
	}
	
	public
	LootTableBuilder addThemePoolFire( )
	{
		LootPoolBuilder pool = new LootPoolBuilder( "fiery" ).setRolls( 1.0F, 3.0F );
		// Weapons
		pool.addEntry( new LootEntryItemBuilder( "Flame Bow", Items.BOW )
			               .applyOneRandomEnchant( Enchantments.FLAME ).setWeight( 5 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Arrow", Items.ARROW ).setWeight( 15 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Fire Sword", Items.IRON_SWORD )
			               .applyOneRandomEnchant( Enchantments.FIRE_ASPECT ).setWeight( 5 ).toLootEntry( ) );
		// Utilities
		pool.addEntry( new LootEntryItemBuilder( "Torch", Blocks.TORCH ).setWeight( 15 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Fire Charge", Items.FIRE_CHARGE ).setWeight( 15 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Flint & Steel", Items.FLINT_AND_STEEL ).setWeight( 10 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Lava Bucket", Items.LAVA_BUCKET ).setWeight( 10 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Magma", Blocks.MAGMA ).setWeight( 10 ).setCount( 1, 4 ).toLootEntry( ) );
		// Crafting
		pool.addEntry( new LootEntryItemBuilder( "Glowstone Dust", Items.GLOWSTONE_DUST ).setWeight( 10 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Blaze Powder", Items.BLAZE_POWDER ).setWeight( 10 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Magma Cream", Items.MAGMA_CREAM ).setWeight( 10 ).setCount( 1, 4 ).toLootEntry( ) );
		// Enchanted book
		pool.addEntry( new LootEntryItemBuilder( "Enchanted Book", Items.BOOK ).setWeight( 5 )
			               .applyOneRandomEnchant( Enchantments.FIRE_PROTECTION, Enchantments.FIRE_ASPECT, Enchantments.FLAME ).toLootEntry( ) );
		
		pools.add( pool.toLootPool( ) );
		return this;
	}
	
	public
	LootTableBuilder addThemePoolBrewing( )
	{
		LootPoolBuilder pool = new LootPoolBuilder( "brewing" ).setRolls( 1.0F, 3.0F );
		// Basic brewing
		pool.addEntry( new LootEntryItemBuilder( "Glass Bottle", Items.GLASS_BOTTLE ).setWeight( 15 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Glowstone Dust", Items.GLOWSTONE_DUST ).setWeight( 15 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Redstone Dust", Items.REDSTONE ).setWeight( 15 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Gunpowder", Items.GUNPOWDER ).setWeight( 15 ).setCount( 1, 8 ).toLootEntry( ) );
		// Common brewing
		pool.addEntry( new LootEntryItemBuilder( "Fermented Eye", Items.FERMENTED_SPIDER_EYE ).setWeight( 10 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Spider Eye", Items.SPIDER_EYE ).setWeight( 10 ).setCount( 1, 8 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Sugar", Items.SUGAR ).setWeight( 10 ).setCount( 1, 8 ).toLootEntry( ) );
		// Rare brewing
		pool.addEntry( new LootEntryItemBuilder( "Gold Melon", Items.SPECKLED_MELON ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Gold Carrot", Items.GOLDEN_CARROT ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Rabbit Foot", Items.RABBIT_FOOT ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Pufferfish", new ItemStack( Items.FISH, 1, ItemFishFood.FishType.PUFFERFISH.getMetadata( ) ) )
			               .setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Blaze Powder", Items.BLAZE_POWDER ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Magma Cream", Items.MAGMA_CREAM ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Ghast Tear", Items.GHAST_TEAR ).setWeight( 5 ).setCount( 1, 4 ).toLootEntry( ) );
		// Enchanted book
		pool.addEntry( new LootEntryItemBuilder( "Enchanted Book", Items.BOOK ).setWeight( 5 )
			               .enchant( 10, 30, true ).toLootEntry( ) );
		
		pools.add( pool.toLootPool( ) );
		return this;
	}
	
	public
	LootTableBuilder addThemePoolBuggy( )
	{
		LootPoolBuilder  pool     = new LootPoolBuilder( "buggy" ).setRolls( 4.0F );
		// Silverfish eggs
		ResourceLocation name     = EntityList.getKey( EntitySilverfish.class );
		ItemStack        spawnEgg = new ItemStack( Items.SPAWN_EGG );
		ItemMonsterPlacer.applyEntityIdToItemStack( spawnEgg, name );
		pool.addEntry( new LootEntryItemBuilder( "Silverfish Egg", spawnEgg ).setWeight( 10 ).setCount( 1, 8 ).toLootEntry( ) );
		// Seeds
		pool.addEntry( new LootEntryItemBuilder( "Beetroot Seeds", Items.BEETROOT_SEEDS ).setWeight( 15 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Melon Seeds", Items.MELON_SEEDS ).setWeight( 15 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Pumpkin Seeds", Items.PUMPKIN_SEEDS ).setWeight( 15 ).setCount( 1, 4 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Wheat Seeds", Items.WHEAT_SEEDS ).setWeight( 15 ).setCount( 1, 4 ).toLootEntry( ) );
		// Nuggets
		pool.addEntry( new LootEntryItemBuilder( "Iron Nugget", Items.IRON_NUGGET ).setWeight( 10 ).setCount( 1, 9 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Gold Nugget", Items.GOLD_NUGGET ).setWeight( 10 ).setCount( 1, 9 ).toLootEntry( ) );
		// Gems
		pool.addEntry( new LootEntryItemBuilder( "Diamond", Items.DIAMOND ).setWeight( 1 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Emerald", Items.EMERALD ).setWeight( 1 ).toLootEntry( ) );
		pool.addEntry( new LootEntryItemBuilder( "Cake", Blocks.CAKE ).setWeight( 1 ).toLootEntry( ) );
		// Enchanted book
		pool.addEntry( new LootEntryItemBuilder( "Enchanted Book", Items.BOOK ).setWeight( 2 ).applyOneRandomEnchant(
			Enchantments.FORTUNE, Enchantments.LOOTING, Enchantments.LUCK_OF_THE_SEA,
			Enchantments.BANE_OF_ARTHROPODS, Enchantments.SILK_TOUCH
		).toLootEntry( ) );
		
		pools.add( pool.toLootPool( ) );
		return this;
	}
	
	/** @param pool A loot pool to add to this builder. */
	public
	LootTableBuilder addPool( LootPool pool )
	{
		pools.add( pool );
		return this;
	}
	
	/** Adds a pool referencing a loot table. */
	public
	LootTableBuilder addLootTable( String id, String name, ResourceLocation lootTable )
	{
		return addPool( new LootPoolBuilder( id ).addEntryTable( name, lootTable ).toLootPool( ) );
	}
	
	/** Adds a pool with a single item drop. */
	public
	LootTableBuilder addGuaranteedDrop( String id, String name, Block block, int count )
	{
		return addGuaranteedDrop( id, name, Item.getItemFromBlock( block ), count );
	}
	
	/** Adds a pool with a single item drop. */
	public
	LootTableBuilder addGuaranteedDrop( String id, String name, Item item, int count )
	{
		return addPool(
			new LootPoolBuilder( id )
				.addEntry( new LootEntryItemBuilder( name, item ).setCount( count ).toLootEntry( ) )
				.toLootPool( )
		);
	}
	
	/** Adds a pool with an item drop of 0-2 + (0-1 * luck). */
	public
	LootTableBuilder addCommonDrop( String id, String name, Block block )
	{
		return addCommonDrop( id, name, Item.getItemFromBlock( block ) );
	}
	
	/** Adds a pool with an item drop of 0-2 + (0-1 * luck). */
	public
	LootTableBuilder addCommonDrop( String id, String name, Item item )
	{
		return addCommonDrop( id, name, item, 2 );
	}
	
	/** Adds a pool with an item drop of 0-2 + (0-1 * luck). */
	public
	LootTableBuilder addCommonDrop( String id, String name, ItemStack item )
	{
		return addCommonDrop( id, name, item, 2 );
	}
	
	/** Adds a pool with an item drop of 0-max + (0-1 * luck). */
	public
	LootTableBuilder addCommonDrop( String id, String name, Block block, int max )
	{
		return addCommonDrop( id, name, Item.getItemFromBlock( block ), max );
	}
	
	/** Adds a pool with an item drop of 0-max + (0-1 * luck). */
	public
	LootTableBuilder addCommonDrop( String id, String name, Item item, int max )
	{
		return addCommonDrop( id, name, item, 0, max );
	}
	
	/** Adds a pool with an item drop of 0-max + (0-1 * luck). */
	public
	LootTableBuilder addCommonDrop( String id, String name, ItemStack item, int max )
	{
		return addCommonDrop( id, name, item, 0, max );
	}
	
	/** Adds a pool with an item drop of min-max + (0-1 * luck). */
	public
	LootTableBuilder addCommonDrop( String id, String name, Block block, int min, int max )
	{
		return addCommonDrop( id, name, Item.getItemFromBlock( block ), min, max );
	}
	
	/** Adds a pool with an item drop of min-max + (0-1 * luck). */
	public
	LootTableBuilder addCommonDrop( String id, String name, Item item, int min, int max )
	{
		return addPool(
			new LootPoolBuilder( id )
				.addEntry( new LootEntryItemBuilder( name, item ).setCount( min, max ).addLootingBonus( 0, 1 ).toLootEntry( ) )
				.toLootPool( )
		);
	}
	
	/** Adds a pool with an item drop of min-max + (0-1 * luck). */
	public
	LootTableBuilder addCommonDrop( String id, String name, ItemStack item, int min, int max )
	{
		return addPool(
			new LootPoolBuilder( id )
				.addEntry( new LootEntryItemBuilder( name, item ).setCount( min, max ).addLootingBonus( 0, 1 ).toLootEntry( ) )
				.toLootPool( )
		);
	}
	
	/** Adds a pool with an item drop of (-1)-1 + (0-1 * luck). */
	public
	LootTableBuilder addSemicommonDrop( String id, String name, Block block )
	{
		return addSemicommonDrop( id, name, Item.getItemFromBlock( block ) );
	}
	
	/** Adds a pool with an item drop of (-1)-1 + (0-1 * luck). */
	public
	LootTableBuilder addSemicommonDrop( String id, String name, Item item )
	{
		return addPool(
			new LootPoolBuilder( id )
				//.addConditions( LootPoolBuilder.KILLED_BY_PLAYER_CONDITION )
				.addEntry( new LootEntryItemBuilder( name, item ).setCount( -1, 1 ).addLootingBonus( 0, 1 ).toLootEntry( ) )
				.toLootPool( )
		);
	}
	
	/** Adds a pool with an item drop of (-1)-1 + (0-1 * luck). */
	public
	LootTableBuilder addSemicommonDrop( String id, String name, ItemStack item )
	{
		return addPool(
			new LootPoolBuilder( id )
				//.addConditions( LootPoolBuilder.KILLED_BY_PLAYER_CONDITION )
				.addEntry( new LootEntryItemBuilder( name, item ).setCount( -1, 1 ).addLootingBonus( 0, 1 ).toLootEntry( ) )
				.toLootPool( )
		);
	}
	
	/** Adds a pool with an item drop of 1-8 + (0-2 * luck) and chance of 25% + (10% * luck). */
	public
	LootTableBuilder addClusterDrop( String id, String name, Block block )
	{
		return addClusterDrop( id, name, Item.getItemFromBlock( block ) );
	}
	
	/** Adds a pool with an item drop of 1-8 + (0-2 * luck) and chance of 25% + (10% * luck). */
	public
	LootTableBuilder addClusterDrop( String id, String name, Item item )
	{
		return addClusterDrop( id, name, item, 8 );
	}
	
	/** Adds a pool with an item drop of 1-8 + (0-2 * luck) and chance of 25% + (10% * luck). */
	public
	LootTableBuilder addClusterDrop( String id, String name, ItemStack item )
	{
		return addClusterDrop( id, name, item, 8 );
	}
	
	/** Adds a pool with an item drop of 1-max + (0-(max/4) * luck) and chance of 25% + (10% * luck). */
	public
	LootTableBuilder addClusterDrop( String id, String name, Block block, int max )
	{
		return addClusterDrop( id, name, Item.getItemFromBlock( block ), max );
	}
	
	/** Adds a pool with an item drop of 1-max + (0-(max/4) * luck) and chance of 25% + (10% * luck). */
	public
	LootTableBuilder addClusterDrop( String id, String name, Item item, int max )
	{
		return addPool(
			new LootPoolBuilder( id )
				.addConditions( LootPoolBuilder.UNCOMMON_CONDITIONS )
				.addEntry( new LootEntryItemBuilder( name, item ).setCount( 1, max ).addLootingBonus( 0, max / 4.0F ).toLootEntry( ) )
				.toLootPool( )
		);
	}
	
	/** Adds a pool with an item drop of 1-max + (0-(max/4) * luck) and chance of 25% + (10% * luck). */
	public
	LootTableBuilder addClusterDrop( String id, String name, ItemStack item, int max )
	{
		return addPool(
			new LootPoolBuilder( id )
				.addConditions( LootPoolBuilder.UNCOMMON_CONDITIONS )
				.addEntry( new LootEntryItemBuilder( name, item ).setCount( 1, max ).addLootingBonus( 0, max / 4.0F ).toLootEntry( ) )
				.toLootPool( )
		);
	}
	
	/** Adds a pool with a single item drop (from a list) and chance of 25% + (10% * luck). */
	public
	LootTableBuilder addUncommonDrop( String id, String name, Block... blocks )
	{
		return addUncommonDrop( id, name, toItemArray( blocks ) );
	}
	
	/** Adds a pool with a single item drop (from a list) and chance of 25% + (10% * luck). */
	public
	LootTableBuilder addUncommonDrop( String id, String name, Item... items )
	{
		LootPoolBuilder pool = new LootPoolBuilder( id ).addConditions( LootPoolBuilder.UNCOMMON_CONDITIONS );
		for( int i = 0; i < items.length; i++ ) {
			pool.addEntry( new LootEntryItemBuilder( name + " " + (i + 1), items[ i ] ).toLootEntry( ) );
		}
		return addPool( pool.toLootPool( ) );
	}
	
	/** Adds a pool with a single item drop (from a list) and chance of 25% + (10% * luck). Item stack size is used as the weight of each item. */
	public
	LootTableBuilder addUncommonDrop( String id, String name, ItemStack... items )
	{
		LootPoolBuilder pool = new LootPoolBuilder( id ).addConditions( LootPoolBuilder.UNCOMMON_CONDITIONS );
		for( int i = 0; i < items.length; i++ ) {
			pool.addEntry( new LootEntryItemBuilder( name + " " + (i + 1), items[ i ] ).setWeight( items[ i ].getCount( ) ).toLootEntry( ) );
		}
		return addPool( pool.toLootPool( ) );
	}
	
	/** Adds a pool with a single item drop (from a list) and chance of 2.5% + (1% * luck). */
	public
	LootTableBuilder addRareDrop( String id, String name, Block... blocks )
	{
		return addRareDrop( id, name, toItemArray( blocks ) );
	}
	
	/** Adds a pool with a single item drop (from a list) and chance of 2.5% + (1% * luck). */
	public
	LootTableBuilder addRareDrop( String id, String name, Item... items )
	{
		LootPoolBuilder pool = new LootPoolBuilder( id ).addConditions( LootPoolBuilder.RARE_CONDITIONS );
		for( int i = 0; i < items.length; i++ ) {
			pool.addEntry( new LootEntryItemBuilder( name + " " + (i + 1), items[ i ] ).toLootEntry( ) );
		}
		return addPool( pool.toLootPool( ) );
	}
	
	/** Adds a pool with a single item drop (from a list) and chance of 2.5% + (1% * luck). Item stack size is used as the weight of each item. */
	public
	LootTableBuilder addRareDrop( String id, String name, ItemStack... items )
	{
		LootPoolBuilder pool = new LootPoolBuilder( id ).addConditions( LootPoolBuilder.RARE_CONDITIONS );
		for( int i = 0; i < items.length; i++ ) {
			pool.addEntry( new LootEntryItemBuilder( name + " " + (i + 1), items[ i ] ).setWeight( items[ i ].getCount( ) ).toLootEntry( ) );
		}
		return addPool( pool.toLootPool( ) );
	}
	
	/** Adds a pool with a single item drop (from a list) and chance of 0% + (1.5% * luck). */
	public
	LootTableBuilder addEpicDrop( String id, String name, Block... blocks )
	{
		return addEpicDrop( id, name, toItemArray( blocks ) );
	}
	
	/** Adds a pool with a single item drop (from a list) and chance of 0% + (1.5% * luck). */
	public
	LootTableBuilder addEpicDrop( String id, String name, Item... items )
	{
		LootPoolBuilder pool = new LootPoolBuilder( id ).addConditions( LootPoolBuilder.EPIC_CONDITIONS );
		for( int i = 0; i < items.length; i++ ) {
			pool.addEntry( new LootEntryItemBuilder( name + " " + (i + 1), items[ i ] ).toLootEntry( ) );
		}
		return addPool( pool.toLootPool( ) );
	}
	
	/** Adds a pool with a single item drop (from a list) and chance of 0% + (1.5% * luck). Item stack size is used as the weight of each item. */
	public
	LootTableBuilder addEpicDrop( String id, String name, ItemStack... items )
	{
		LootPoolBuilder pool = new LootPoolBuilder( id ).addConditions( LootPoolBuilder.EPIC_CONDITIONS );
		for( int i = 0; i < items.length; i++ ) {
			pool.addEntry( new LootEntryItemBuilder( name + " " + (i + 1), items[ i ] ).setWeight( items[ i ].getCount( ) ).toLootEntry( ) );
		}
		return addPool( pool.toLootPool( ) );
	}
	
	private static
	Item[] toItemArray( Block[] blocks )
	{
		Item[] items = new Item[ blocks.length ];
		for( int i = 0; i < blocks.length; i++ ) {
			items[ i ] = Item.getItemFromBlock( blocks[ i ] );
		}
		return items;
	}
}
