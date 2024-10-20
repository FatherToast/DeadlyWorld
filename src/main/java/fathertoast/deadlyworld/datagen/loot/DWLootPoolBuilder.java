package fathertoast.deadlyworld.datagen.loot;

import fathertoast.crust.api.datagen.loot.LootEntryItemBuilder;
import fathertoast.crust.api.datagen.loot.LootPoolBuilder;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ExplorationMapFunction;
import net.minecraft.world.level.storage.loot.functions.SetStewEffectFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class DWLootPoolBuilder extends LootPoolBuilder {
    /** Creates a loot pool with the given unique name. */
    public DWLootPoolBuilder( String id ) { super( id ); }
    
    
    /** Adds a simple item loot entry. */
    public DWLootPoolBuilder addItem( ItemLike item, int weight ) {
        return addEntry( new LootEntryItemBuilder( item ).setWeight( weight ).toLootEntry() );
    }
    
    /** Adds a simple item loot entry. */
    public DWLootPoolBuilder addItem( ItemLike item, int weight, int count ) {
        return addEntry( new LootEntryItemBuilder( item ).setWeight( weight ).setCount( count ).toLootEntry() );
    }
    
    /** Adds a simple item loot entry with a count of 1-4. */
    public DWLootPoolBuilder addItemClusterSmall( ItemLike item, int weight ) {
        return addItemCluster( item, weight, 4 );
    }
    
    /** Adds a simple item loot entry with a count of 1-8. */
    public DWLootPoolBuilder addItemClusterLarge( ItemLike item, int weight ) {
        return addItemCluster( item, weight, 8 );
    }
    
    /** Adds a simple item loot entry with a count of 1-max. */
    public DWLootPoolBuilder addItemCluster( ItemLike item, int weight, int max ) {
        return addEntry( new LootEntryItemBuilder( item ).setWeight( weight ).setCount( 1, max ).toLootEntry() );
    }
    
    /** Adds a suspicious stew with a specific effect and a set duration (in seconds). */
    public DWLootPoolBuilder addSuspiciousStew( int weight, MobEffect effect, int duration ) {
        return addEntry( new LootEntryItemBuilder( Items.SUSPICIOUS_STEW ).setWeight( weight )
                .addFunction( SetStewEffectFunction.stewEffect().withEffect( effect, ConstantValue.exactly( duration ) ) )
                .toLootEntry() );
    }
    
    /** Adds a suspicious stew with a specific effect and a random duration (in seconds). */
    public DWLootPoolBuilder addSuspiciousStew( int weight, MobEffect effect, int durationMin, int durationMax ) {
        return addEntry( new LootEntryItemBuilder( Items.SUSPICIOUS_STEW ).setWeight( weight )
                .addFunction( SetStewEffectFunction.stewEffect().withEffect( effect, UniformGenerator.between( durationMin, durationMax ) ) )
                .toLootEntry() );
    }
    
    /** Adds a simple enchanted book with a random enchantment. */
    public DWLootPoolBuilder addEnchantedBook( int weight ) {
        return addEntry( new LootEntryItemBuilder( Items.BOOK ).setWeight( weight ).applyOneRandomApplicableEnchant().toLootEntry() );
    }
    
    /** Adds a simple enchanted book with a random enchantment. */
    public DWLootPoolBuilder addEnchantedBook( int weight, Enchantment... enchantments ) {
        return addEntry( new LootEntryItemBuilder( Items.BOOK ).setWeight( weight ).applyOneRandomEnchant( enchantments ).toLootEntry() );
    }
    
    /** Adds a simple enchanted book. */
    public DWLootPoolBuilder addEnchantedBook( int weight, int level, boolean treasure ) {
        return addEntry( new LootEntryItemBuilder( Items.BOOK ).setWeight( weight ).enchant( level, treasure ).toLootEntry() );
    }
    
    /** Adds a simple enchanted book. */
    public DWLootPoolBuilder addEnchantedBook( int weight, int levelMin, int levelMax, boolean treasure ) {
        return addEntry( new LootEntryItemBuilder( Items.BOOK ).setWeight( weight ).enchant( levelMin, levelMax, treasure ).toLootEntry() );
    }
    
    /** Adds a standard treasure map. */
    public DWLootPoolBuilder addExplorerMapBuriedTreasure( int weight ) {
        return addExplorerMap( weight, StructureTags.ON_TREASURE_MAPS, MapDecoration.Type.RED_X,
                1, 50, false );
    }
    
    /** Adds a standard woodland mansion explorer map. */
    public DWLootPoolBuilder addExplorerMapMansion( int weight ) {
        return addExplorerMap( weight, StructureTags.ON_WOODLAND_EXPLORER_MAPS, MapDecoration.Type.MANSION,
                2, 100, true );
    }
    
    /** Adds a standard ocean monument explorer map. */
    public DWLootPoolBuilder addExplorerMapMonument( int weight ) {
        return addExplorerMap( weight, StructureTags.ON_OCEAN_EXPLORER_MAPS, MapDecoration.Type.MONUMENT,
                2, 100, true );
    }
    
    /**
     * Adds an explorer map.
     * Notes: TARGET_X (white X) and TARGET_POINT (red triangle) are unused. RED_X is used for buried treasure.
     * <p>
     * When you use anything other than buried treasure, woodland mansion, or ocean monument, you need to add i18n support!
     * Due to the localization issue, making this method internal use only for now.
     */
    private DWLootPoolBuilder addExplorerMap( int weight, TagKey<Structure> target, MapDecoration.Type marker,
                                              int zoom, int searchRadius, boolean skipLoadedChunks ) {
        return addEntry( new LootEntryItemBuilder( Items.MAP ).setWeight( weight )
                .addFunction( ExplorationMapFunction.makeExplorationMap().setDestination( target ).setMapDecoration( marker )
                        .setZoom( (byte) zoom ).setSearchRadius( searchRadius ).setSkipKnownStructures( skipLoadedChunks ) )
                .toLootEntry() );
    }
    
    /** @param value The number of rolls for this pool. */
    @Override
    public DWLootPoolBuilder setRolls( float value ) {
        super.setRolls( value );
        return this;
    }
    
    /**
     * @param min Minimum rolls for this pool (inclusive).
     * @param max Maximum rolls for this pool (inclusive).
     */
    @Override
    public DWLootPoolBuilder setRolls( float min, float max ) {
        super.setRolls( min, max );
        return this;
    }
    
    /** @param value The additional rolls for each level of looting. */
    @Override
    public DWLootPoolBuilder setBonusRolls( float value ) {
        super.setBonusRolls( value );
        return this;
    }
    
    /**
     * @param min Minimum additional rolls for this pool for each level of looting (inclusive).
     * @param max Maximum additional rolls for this pool for each level of looting (inclusive).
     */
    @Override
    public DWLootPoolBuilder setBonusRolls( float min, float max ) {
        super.setBonusRolls( min, max );
        return this;
    }
    
    /** @param entry A loot entry to add to this builder. */
    @Override
    public DWLootPoolBuilder addEntry( LootPoolEntryContainer.Builder<?> entry ) {
        super.addEntry( entry );
        return this;
    }
}