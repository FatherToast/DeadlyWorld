package fathertoast.deadlyworld.datagen.loot.builder;

import fathertoast.deadlyworld.datagen.loot.DWLootTableProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.loot.functions.EnchantRandomly;
import net.minecraft.loot.functions.ExplorationMap;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.MapDecoration;

/**
 * Used to wrap the vanilla method for building loot pools and provide convenience methods for easier use.
 * <p>
 * At its core, a loot pool is a collection of loot entries. When a pool is used to generate loot, it picks a number of
 * entries (based on the pool's "rolls") to use at random (based on the entries' weights).
 * Loot pools may also include functions and/or conditions, which are applied to all item stacks produced by this pool.
 */
@SuppressWarnings( { "unused", "UnusedReturnValue" } )
public class LootPoolBuilder {
    
    private final LootPool.Builder wrapped;
    
    /** Creates a new loot pool builder with 1 roll and no loot entries, functions, or conditions. */
    public LootPoolBuilder( String name ) { this( name, 1.0F ); }
    
    /** Creates a new loot pool builder with a constant number of rolls and no loot entries, functions, or conditions. */
    public LootPoolBuilder( String name, float rolls ) { this( name, rolls, rolls ); }
    
    /** Creates a new loot pool builder with a min and max number of rolls and no loot entries, functions, or conditions. */
    public LootPoolBuilder( String name, float minRolls, float maxRolls ) {
        wrapped = LootPool.lootPool().name( name ).setRolls( new RandomValueRange( minRolls, maxRolls ) );
    }
    
    /** @return The underlying loot pool builder. */
    public LootPool.Builder build() { return wrapped; }
    
    /** @param conditions Any number of conditions to add to this loot pool. */
    public LootPoolBuilder addCondition( ILootCondition.IBuilder... conditions ) {
        for( ILootCondition.IBuilder condition : conditions ) wrapped.when( condition );
        return this;
    }
    
    /** Adds the standard conditions to this loot pool for 'uncommon' rarity. */
    public LootPoolBuilder addUncommonConditions() { return addCondition( RandomChance.randomChance( DWLootTableProvider.RARITY_UNCOMMON ) ); }
    
    /** Adds the standard conditions to this loot pool for 'rare' rarity. */
    public LootPoolBuilder addRareConditions() { return addCondition( RandomChance.randomChance( DWLootTableProvider.RARITY_RARE ) ); }
    
    /** @param functions Any number of functions to add to this loot pool. */
    public LootPoolBuilder addFunction( ILootFunction.IBuilder... functions ) {
        for( ILootFunction.IBuilder function : functions ) wrapped.apply( function );
        return this;
    }
    
    /** @param entries Any number of loot entries to add to this loot pool. */
    public LootPoolBuilder add( ItemLootEntryBuilder... entries ) {
        for( ItemLootEntryBuilder entry : entries ) wrapped.add( entry.build() );
        return this;
    }
    
    /** Adds a simple item loot entry. */
    public LootPoolBuilder addItem( IItemProvider item, int weight ) {
        return addItem( item, weight, 1, 1 );
    }
    
    /** Adds a simple item loot entry. */
    public LootPoolBuilder addItem( IItemProvider item, int weight, int count ) {
        return addItem( item, weight, count, count );
    }
    
    /** Adds a simple item loot entry. */
    public LootPoolBuilder addItem( IItemProvider item, int weight, int minCount, int maxCount ) {
        return add( new ItemLootEntryBuilder( item, weight ).setCount( minCount, maxCount ) );
    }
    
    /** Adds a simple item loot entry with a count of 1-4. */
    public LootPoolBuilder addItemClusterSmall( IItemProvider item, int weight ) {
        return addItem( item, weight, 1, 4 );
    }
    
    /** Adds a simple item loot entry with a count of 1-8. */
    public LootPoolBuilder addItemClusterLarge( IItemProvider item, int weight ) {
        return addItem( item, weight, 1, 8 );
    }
    
    /** Adds a simple enchanted book with a random enchantment. */
    public LootPoolBuilder addEnchantedBook( int weight ) {
        return add( new ItemLootEntryBuilder( Items.BOOK, weight ).applyOneRandomApplicableEnchant() );
    }
    
    /** Adds a simple enchanted book with a random enchantment. */
    public LootPoolBuilder addEnchantedBook( int weight, Enchantment... enchantments ) {
        return add( new ItemLootEntryBuilder( Items.BOOK, weight ).applyOneRandomEnchant( enchantments ) );
    }
    
    /** Adds a simple enchanted book. */
    public LootPoolBuilder addEnchantedBook( int weight, int level, boolean treasure ) {
        return add( new ItemLootEntryBuilder( Items.BOOK, weight ).enchant( level, treasure ) );
    }
    
    /** Adds a simple enchanted book. */
    public LootPoolBuilder addEnchantedBook( int weight, int levelMin, int levelMax, boolean treasure ) {
        return add( new ItemLootEntryBuilder( Items.BOOK, weight ).enchant( levelMin, levelMax, treasure ) );
    }
    
    /**
     * Adds an explorer map.
     * Note: TARGET_X (white X) and TARGET_POINT (red triangle) are unused. RED_X is used for buried treasure.
     */
    public LootPoolBuilder addExplorerMap( int weight, Structure<?> target, MapDecoration.Type marker ) {
        return add( new ItemLootEntryBuilder( Items.MAP, weight ).addFunction( ExplorationMap.makeExplorationMap()
                .setDestination( target ).setMapDecoration( marker ).setZoom( (byte) 1 ).setSkipKnownStructures( false ) ) );
    }
    
    /** Adds a loot table as a drop. */
    public LootPoolBuilder addTable( ResourceLocation lootTable, int weight, ILootCondition.IBuilder... conditions ) {
        return addTable( lootTable, weight, 0, conditions );
    }
    
    /** Adds a loot table as a drop. */
    public LootPoolBuilder addTable( ResourceLocation lootTable, int weight, int quality, ILootCondition.IBuilder... conditions ) {
        final LootEntry.Builder<?> entry = TableLootEntry.lootTableReference( lootTable ).setWeight( weight ).setQuality( quality );
        for( ILootCondition.IBuilder condition : conditions ) entry.when( condition );
        wrapped.add( entry );
        return this;
    }
    
    /** Adds an empty entry. */
    public LootPoolBuilder addEmpty( int weight, ILootCondition.IBuilder... conditions ) {
        return addEmpty( weight, 0, conditions );
    }
    
    /** Adds an empty entry. */
    public LootPoolBuilder addEmpty( int weight, int quality, ILootCondition.IBuilder... conditions ) {
        final LootEntry.Builder<?> entry = EmptyLootEntry.emptyItem().setWeight( weight ).setQuality( quality );
        for( ILootCondition.IBuilder condition : conditions ) entry.when( condition );
        wrapped.add( entry );
        return this;
    }
    
    // No need for this; also magic find deserves to die
    //    /** @param value The additional rolls for each level of looting. */
    //    public LootPoolBuilder setBonusRolls( float value ) {
    //        wrapped.bonusRolls( value, value );
    //        return this;
    //    }
    //
    //    /**
    //     * @param min Minimum additional rolls for this pool for each level of looting (inclusive).
    //     * @param max Maximum additional rolls for this pool for each level of looting (inclusive).
    //     */
    //    public LootPoolBuilder setBonusRolls( float min, float max ) {
    //        wrapped.bonusRolls( min, max );
    //        return this;
    //    }
}