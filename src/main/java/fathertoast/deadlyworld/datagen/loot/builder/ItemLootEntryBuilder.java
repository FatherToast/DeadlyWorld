package fathertoast.deadlyworld.datagen.loot.builder;

import fathertoast.deadlyworld.datagen.loot.DWLootTableProvider;
import net.minecraft.advancements.criterion.EntityFlagsPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.loot.functions.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;

/**
 * Used to wrap the vanilla method for building item loot entries and provide convenience methods for easier use.
 * Item loot entries are not the only kind of loot entry, they are just the only entry complicated enough to warrant a builder.
 * <p>
 * At its core, an item loot entry is an item stack of loot entries. When a pool is used to generate loot, it picks a number of
 * entries (based on the pool's "rolls") to use at random (based on the entries' weights).
 * Loot pools may also include functions and/or conditions, which are applied to all item stacks produced by this pool.
 */
@SuppressWarnings( { "unused", "UnusedReturnValue" } )
public class ItemLootEntryBuilder {
    
    private final StandaloneLootEntry.Builder<?> wrapped;
    
    /** Creates a new item loot entry builder with a specified weight and no functions or conditions. */
    public ItemLootEntryBuilder( IItemProvider item, int weight ) {
        wrapped = ItemLootEntry.lootTableItem( item ).setWeight( weight );
    }
    
    /** @return The underlying loot entry builder. */
    public LootEntry.Builder<?> build() { return wrapped; }
    
    /** @param value A new quality for this loot entry. Quality alters the weight of this entry based on luck level. */
    public ItemLootEntryBuilder setQuality( int value ) {
        wrapped.setQuality( value );
        return this;
    }
    
    /** @param conditions Any number of conditions to add to this loot entry. */
    public ItemLootEntryBuilder addCondition( ILootCondition.IBuilder... conditions ) {
        for( ILootCondition.IBuilder condition : conditions ) wrapped.when( condition );
        return this;
    }
    
    /** Adds the standard conditions to this loot entry for 'uncommon' rarity. */
    public ItemLootEntryBuilder addUncommonConditions() {
        return addCondition( RandomChance.randomChance( DWLootTableProvider.RARITY_UNCOMMON ) );
    }
    
    /** Adds the standard conditions to this loot entry for 'rare' rarity. */
    public ItemLootEntryBuilder addRareConditions() {
        return addCondition( RandomChance.randomChance( DWLootTableProvider.RARITY_RARE ) );
    }
    
    /** @param functions Any number of functions to add to this loot entry. */
    public ItemLootEntryBuilder addFunction( ILootFunction.IBuilder... functions ) {
        for( ILootFunction.IBuilder function : functions ) wrapped.apply( function );
        return this;
    }
    
    /** Adds a stack size function. */
    public ItemLootEntryBuilder setCount( int value ) { return setCount( value, value ); }
    
    /** Adds a stack size function. */
    public ItemLootEntryBuilder setCount( int min, int max ) {
        return addFunction( SetCount.setCount( new RandomValueRange( min, max ) ) );
    }
    
    /** Adds a looting enchant (luck) bonus function no limit. */
    public ItemLootEntryBuilder addLootingBonus( float value ) { return addLootingBonus( value, value, 0 ); }
    
    /** Adds a looting enchant (luck) bonus function with no limit. */
    public ItemLootEntryBuilder addLootingBonus( float min, float max ) { return addLootingBonus( min, max, 0 ); }
    
    /** Adds a looting enchant (luck) bonus function. */
    public ItemLootEntryBuilder addLootingBonus( float min, float max, int limit ) {
        return addFunction( LootingEnchantBonus.lootingMultiplier( new RandomValueRange( min, max ) ).setLimit( limit ) );
    }
    
    /** Adds a set damage function. */
    public ItemLootEntryBuilder setDamage( int value ) { return setDamage( value, value ); }
    
    /** Adds a set damage function. */
    public ItemLootEntryBuilder setDamage( int min, int max ) {
        return addFunction( SetDamage.setDamage( new RandomValueRange( min, max ) ) );
    }
    
    /** Adds an nbt tag compound function. */
    public ItemLootEntryBuilder setNBTTag( CompoundNBT tag ) { return addFunction( SetNBT.setTag( tag ) ); }
    
    /** Adds a smelt function with the EntityOnFire condition. */
    public ItemLootEntryBuilder smeltIfBurning() {
        return addFunction( Smelt.smelted().when( EntityHasProperty.hasProperties( LootContext.EntityTarget.THIS,
                EntityPredicate.Builder.entity().flags( EntityFlagsPredicate.Builder.flags().setOnFire( true ).build() ) ) ) );
    }
    
    /** Adds a random enchantment function. */
    public ItemLootEntryBuilder applyOneRandomApplicableEnchant() {
        return addFunction( EnchantRandomly.randomApplicableEnchantment() );
    }
    
    /** Adds a random enchantment function. */
    public ItemLootEntryBuilder applyOneRandomEnchant( Enchantment... enchantments ) {
        final EnchantRandomly.Builder builder = new EnchantRandomly.Builder();
        for( Enchantment enchantment : enchantments ) builder.withEnchantment( enchantment );
        return addFunction( builder );
    }
    
    /** Adds an enchanting function. */
    public ItemLootEntryBuilder enchant( int level, boolean treasure ) { return enchant( level, level, treasure ); }
    
    /** Adds an enchanting function. */
    public ItemLootEntryBuilder enchant( int levelMin, int levelMax, boolean treasure ) {
        final EnchantWithLevels.Builder builder = EnchantWithLevels.enchantWithLevels( new RandomValueRange( levelMin, levelMax ) );
        if( treasure ) builder.allowTreasure();
        return addFunction( builder );
    }
}