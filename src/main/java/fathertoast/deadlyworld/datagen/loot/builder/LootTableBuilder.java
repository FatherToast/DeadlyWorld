package fathertoast.deadlyworld.datagen.loot.builder;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.MapDecoration;

/**
 * Used to wrap the vanilla method for building loot tables and provide convenience methods for easier use.
 * <p>
 * At its core, a loot table is a collection of loot pools. When a table is used to generate loot, it uses each of its pools.
 * Loot tables may also include functions, which are applied to all item stacks produced by this table and its pools.
 */
@SuppressWarnings( { "UnusedReturnValue", "unused" } )
public class LootTableBuilder {
    
    private final LootTable.Builder wrapped;
    
    /** Creates a new loot table builder with no loot pools or functions. */
    public LootTableBuilder() { wrapped = LootTable.lootTable(); }
    
    /** @return The underlying loot table builder. */
    public LootTable.Builder build() { return wrapped; }
    
    /** @param pools Any number of loot pools to add to this loot table. */
    public LootTableBuilder add( LootPoolBuilder... pools ) {
        for( LootPoolBuilder pool : pools ) wrapped.withPool( pool.build() );
        return this;
    }
    
    /** Adds a loot pool containing another loot table. */
    public LootTableBuilder addTable( String name, ResourceLocation lootTable ) {
        return add( new LootPoolBuilder( name, 1 ).addTable( lootTable, 1 ) );
    }
    
    /** Adds a pool with a single item drop. */
    public LootTableBuilder addGuaranteedDrop( String name, IItemProvider item, int count ) {
        return add( new LootPoolBuilder( name )
                .add( new ItemLootEntryBuilder( item, 1 ).setCount( count ) ) );
    }
    
    /** Adds a pool with an item drop of 0-2 + (0-1 * luck). */
    public LootTableBuilder addCommonDrop( String name, IItemProvider item ) {
        return addCommonDrop( name, item, 2 );
    }
    
    /** Adds a pool with an item drop of 0-max + (0-1 * luck). */
    public LootTableBuilder addCommonDrop( String name, IItemProvider item, int max ) {
        return addCommonDrop( name, item, 0, max );
    }
    
    /** Adds a pool with an item drop of min-max + (0-1 * luck). */
    public LootTableBuilder addCommonDrop( String name, IItemProvider item, int min, int max ) {
        return add( new LootPoolBuilder( name )
                .add( new ItemLootEntryBuilder( item, 1 ).setCount( min, max ).addLootingBonus( 0, 1 ) ) );
    }
    
    /** Adds a pool with an item drop of (-1)-1 + (0-1 * luck). */
    public LootTableBuilder addSemicommonDrop( String name, IItemProvider item ) {
        return add( new LootPoolBuilder( name )
                .add( new ItemLootEntryBuilder( item, 1 ).setCount( -1, 1 ).addLootingBonus( 0, 1 ) ) );
    }
    
    /** Adds a pool with an item drop of 1-8 + (0-2 * luck) and chance of 25% + (10% * luck). */
    public LootTableBuilder addClusterDrop( String name, IItemProvider item ) {
        return addClusterDrop( name, item, 8 );
    }
    
    /** Adds a pool with an item drop of 1-max + (0-(max/4) * luck) and chance of 25% + (10% * luck). */
    public LootTableBuilder addClusterDrop( String name, IItemProvider item, int max ) {
        return add( new LootPoolBuilder( name ).addUncommonConditions()
                .add( new ItemLootEntryBuilder( item, 1 ).setCount( 1, max ).addLootingBonus( 0.0F, max / 4.0F ) ) );
    }
    
    /** Adds a pool with a single item drop (from a list) and chance of 25% + (10% * luck). */
    public LootTableBuilder addUncommonDrop( String name, IItemProvider... items ) {
        final LootPoolBuilder pool = new LootPoolBuilder( name ).addUncommonConditions();
        for( IItemProvider item : items ) pool.add( new ItemLootEntryBuilder( item, 1 ) );
        return add( pool );
    }
    
    /**
     * Adds a pool with a single item drop (from a list) and chance of 25% + (10% * luck).
     * Item stack size is used as the weight of each item.
     */
    public LootTableBuilder addUncommonDrop( String name, ItemStack... items ) {
        final LootPoolBuilder pool = new LootPoolBuilder( name ).addUncommonConditions();
        for( ItemStack item : items ) pool.add( new ItemLootEntryBuilder( item.getItem(), item.getCount() ) );
        return add( pool );
    }
    
    /** Adds a pool with a single item drop (from a list) and chance of 2.5% + (1% * luck). */
    public LootTableBuilder addRareDrop( String name, IItemProvider... items ) {
        final LootPoolBuilder pool = new LootPoolBuilder( name ).addRareConditions();
        for( IItemProvider item : items ) pool.add( new ItemLootEntryBuilder( item, 1 ) );
        return add( pool );
    }
    
    /**
     * Adds a pool with a single item drop (from a list) and chance of 2.5% + (1% * luck).
     * Item stack size is used as the weight of each item.
     */
    public LootTableBuilder addRareDrop( String name, ItemStack... items ) {
        final LootPoolBuilder pool = new LootPoolBuilder( name ).addRareConditions();
        for( ItemStack item : items ) pool.add( new ItemLootEntryBuilder( item.getItem(), item.getCount() ) );
        return add( pool );
    }
    
    public LootTableBuilder addThemePoolExploration() {
        return add( new LootPoolBuilder( "exploration", 1, 3 )
                // Weapons
                .addItem( Items.BOW, 10 )
                .addItemClusterLarge( Items.ARROW, 15 )
                .addItem( Items.STONE_SWORD, 10 )
                .addItem( Items.IRON_SWORD, 10 )
                .addItem( Items.DIAMOND_SWORD, 5 )
                .addItem( Items.SHIELD, 10 )
                // Buckets
                .addItem( Items.WATER_BUCKET, 5 )
                .addItem( Items.LAVA_BUCKET, 5 )
                .addItem( Items.MILK_BUCKET, 5 )
                // Utilities
                .addItemClusterSmall( Items.ENDER_PEARL, 10 )
                .addItem( Items.CLOCK, 10 )
                .addItem( Items.COMPASS, 10 )
                .addItem( Items.MAP, 10 )
                // Explorer maps
                .addExplorerMapBuriedTreasure( 1 )
                .addExplorerMapMansion( 1 )
                .addExplorerMapMonument( 1 )
                // Enchanted book
                .addEnchantedBook( 5, 10, 30, true )
        );
    }
    
    public LootTableBuilder addThemePoolValuable() {
        return add( new LootPoolBuilder( "valuable", 1, 3 )
                // Materials
                .addItemClusterLarge( Items.IRON_INGOT, 15 )
                .addItemClusterLarge( Items.GOLD_INGOT, 10 )
                .addItem( Items.DIAMOND, 5, 1, 3 )
                .addItem( Items.EMERALD, 5, 1, 3 )
                // Food
                .addItem( Items.CAKE, 1 )
                .addItemClusterSmall( Items.GOLDEN_CARROT, 10 )
                .addItemClusterSmall( Items.GOLDEN_APPLE, 10 )
                .addItem( Items.ENCHANTED_GOLDEN_APPLE, 1 )
                // Iron equipment
                .addItem( Items.IRON_HELMET, 3 )
                .addItem( Items.IRON_CHESTPLATE, 3 )
                .addItem( Items.IRON_LEGGINGS, 3 )
                .addItem( Items.IRON_BOOTS, 3 )
                // Diamond equipment
                .addItem( Items.DIAMOND_SWORD, 2 )
                .addItem( Items.DIAMOND_PICKAXE, 1 )
                .addItem( Items.DIAMOND_AXE, 1 )
                .addItem( Items.DIAMOND_SHOVEL, 2 )
                .addItem( Items.DIAMOND_HELMET, 1 )
                .addItem( Items.DIAMOND_CHESTPLATE, 1 )
                .addItem( Items.DIAMOND_LEGGINGS, 1 )
                .addItem( Items.DIAMOND_BOOTS, 1 )
                // Explorer maps
                .addExplorerMapMansion( 2 )
                .addExplorerMapMonument( 2 )
                // Enchanted book
                .addEnchantedBook( 10, 30, true )
        );
    }
    
    public LootTableBuilder addThemePoolExplosives() {
        return add( new LootPoolBuilder( "explosives", 1, 3 )
                // Explosives
                .addItemClusterLarge( Items.GUNPOWDER, 20 )
                .addItemClusterSmall( Items.TNT, 10 )
                .addItem( Items.TNT_MINECART, 5 )
                // Fire starters
                .add( new ItemLootEntryBuilder( Items.BOW, 5 ).applyOneRandomEnchant( Enchantments.FLAMING_ARROWS ) )
                .addItemClusterLarge( Items.FIRE_CHARGE, 10 )
                .addItem( Items.FLINT_AND_STEEL, 10 )
                // Tools
                .addItemClusterLarge( Items.REDSTONE, 10 )
                .addItemClusterSmall( Items.REPEATER, 5 )
                .addItemClusterSmall( Items.COMPARATOR, 5 )
                .addItemClusterSmall( Items.OBSERVER, 5 )
                .addItemClusterSmall( Items.DAYLIGHT_DETECTOR, 5 )
                .addItem( Items.SHEARS, 10 )
                .addItemClusterLarge( Items.STRING, 10 )
                .addItem( Items.TRIPWIRE_HOOK, 5, 2 )
                // Enchanted book
                .addEnchantedBook( 5,
                        Enchantments.BLAST_PROTECTION, Enchantments.SWEEPING_EDGE, Enchantments.FLAMING_ARROWS )
        );
    }
    
    public LootTableBuilder addThemePoolFire() {
        return add( new LootPoolBuilder( "fiery", 1, 3 )
                // Weapons
                .add( new ItemLootEntryBuilder( Items.IRON_SWORD, 5 ).applyOneRandomEnchant( Enchantments.FIRE_ASPECT ) )
                .add( new ItemLootEntryBuilder( Items.BOW, 5 ).applyOneRandomEnchant( Enchantments.FLAMING_ARROWS ) )
                .addItemClusterLarge( Items.ARROW, 15 )
                // Utilities
                .addItemClusterLarge( Items.TORCH, 15 )
                .addItemClusterLarge( Items.FIRE_CHARGE, 15 )
                .addItem( Items.FLINT_AND_STEEL, 10 )
                .addItem( Items.LAVA_BUCKET, 10 )
                .addItemClusterSmall( Items.MAGMA_BLOCK, 10 )
                // Crafting
                .addItemClusterSmall( Items.GLOWSTONE_DUST, 10 )
                .addItemClusterSmall( Items.BLAZE_POWDER, 10 )
                .addItemClusterSmall( Items.MAGMA_CREAM, 10 )
                // Enchanted book
                .addEnchantedBook( 5,
                        Enchantments.FIRE_PROTECTION, Enchantments.FIRE_ASPECT, Enchantments.FLAMING_ARROWS )
        );
    }
    
    public LootTableBuilder addThemePoolBrewing() {
        return add( new LootPoolBuilder( "brewing", 1, 3 )
                // Basic brewing
                .addItemClusterLarge( Items.GLASS_BOTTLE, 15 )
                .addItemClusterLarge( Items.GLOWSTONE_DUST, 15 )
                .addItemClusterLarge( Items.REDSTONE, 15 )
                .addItemClusterLarge( Items.GUNPOWDER, 15 )
                // Common brewing
                .addItemClusterLarge( Items.FERMENTED_SPIDER_EYE, 10 )
                .addItemClusterLarge( Items.SPIDER_EYE, 10 )
                .addItemClusterLarge( Items.SUGAR, 10 )
                // Rare brewing
                .addItemClusterSmall( Items.GLISTERING_MELON_SLICE, 5 )
                .addItemClusterSmall( Items.GOLDEN_CARROT, 5 )
                .addItemClusterSmall( Items.RABBIT_FOOT, 5 )
                .addItemClusterSmall( Items.PUFFERFISH, 5 )
                .addItemClusterSmall( Items.BLAZE_POWDER, 5 )
                .addItemClusterSmall( Items.MAGMA_CREAM, 5 )
                .addItemClusterSmall( Items.GHAST_TEAR, 5 )
                // Explorer maps
                .addExplorerMapMansion( 2 )
                // Enchanted book
                .addEnchantedBook( 5, 10, 30, true )
        );
    }
    
    public LootTableBuilder addThemePoolBuggy() {
        return add( new LootPoolBuilder( "buggy", 4 )
                // Silverfish eggs
                .addItemClusterLarge( Items.SILVERFISH_SPAWN_EGG, 10 )
                // Seeds
                .addItemClusterSmall( Items.BEETROOT_SEEDS, 15 )
                .addItemClusterSmall( Items.MELON_SEEDS, 15 )
                .addItemClusterSmall( Items.PUMPKIN_SEEDS, 15 )
                .addItemClusterSmall( Items.WHEAT_SEEDS, 15 )
                // Nuggets
                .addItem( Items.IRON_NUGGET, 10, 1, 9 )
                .addItem( Items.GOLD_NUGGET, 10, 1, 9 )
                // Gems
                .addItem( Items.DIAMOND, 1 )
                .addItem( Items.EMERALD, 1 )
                .addItem( Items.CAKE, 1 )
                // Explorer maps
                .addExplorerMapBuriedTreasure( 2 )
                // Enchanted book
                .addEnchantedBook( 2,
                        Enchantments.BLOCK_FORTUNE, Enchantments.MOB_LOOTING, Enchantments.FISHING_LUCK,
                        Enchantments.BANE_OF_ARTHROPODS, Enchantments.SILK_TOUCH )
        );
    }
}