package fathertoast.deadlyworld.datagen.loot;

import fathertoast.crust.api.datagen.loot.LootEntryItemBuilder;
import fathertoast.crust.api.datagen.loot.LootTableBuilder;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import net.minecraft.data.loot.packs.VanillaChestLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

public class DWChestLootTables extends VanillaChestLoot { // Extending the vanilla class doesn't really do anything, but it's fun
    /*
     * For reference, the vanilla "simple dungeon" chest loot table has 3 pools:
     *
     * 1-3 x Rare items:
     *      1 Saddle, 1 Golden apple, 1 Epic golden apple, 1 Record - Otherside, 1 Record - 13, 1 Record - Cat,
     *      1 Name tag, 1 Gold horse armor, 1 Iron horse armor, 1 Diamond horse armor, 1 Enchanted book
     *
     * 1-4 x Basic items:
     *      1-4 Iron ingots, 1-4 Gold ingots, 1 Bread, 1-4 Wheat, 1 Bucket, 1-4 Redstone, 1-4 Coal,
     *      2-4 Melon seeds, 2-4 Pumpkin seeds, 2-4 Beetroot seeds
     *
     * 3 x Common mob loot:
     *      1-8 Bones, 1-8 Gunpowder, 1-8 Rotten flesh, 1-8 String
     */
    protected BiConsumer<ResourceLocation, LootTable.Builder> lootRegistry;
    
    /** Builds all loot tables for this provider. */
    @Override
    public void generate( BiConsumer<ResourceLocation, LootTable.Builder> registry ) {
        lootRegistry = registry;
        
        for( SpawnerType type : SpawnerType.values() ) {
            if( !type.isSubfeature() ) // Feature handles the chest loot
                add( type.getChestLootTable(), buildSpawnerChestLoot( type ) );
        }
        //        for( FloorTrapType type : FloorTrapType.values() ) {
        //            add( type.getChestLootTable(), buildFloorTrapChestLoot( type ) );
        //        }
        //        for( TowerType type : TowerType.values() ) {
        //            add( type.getChestLootTable(), buildTowerChestLoot( type ) );
        //        }
    }
    
    private LootTableBuilder buildSpawnerChestLoot( SpawnerType type ) {
        final LootTableBuilder loot = new LootTableBuilder();
        switch( type ) {
            case DEFAULT, STREAM -> loot.addPool( buildExplorationLootPool() );
            case SWARM -> loot.addPool( buildExplosivesLootPool() );
            case BRUTAL -> loot.addPool( buildValuableLootPool() );
            case NEST -> loot.addPool( buildBuggyLootPool() );
            case MINI -> loot.addPool( buildBrewingLootPool() );
            case DUNGEON ->
                    throw new IllegalArgumentException( "Subfeatures do not have chest loot! (spawner type \"" + type + "\")" );
            default ->
                    throw new IllegalArgumentException( "Spawner type \"" + type + "\" is missing chest loot table data gen code!" );
        }
        return loot.addLootTable( "base", BuiltInLootTables.SIMPLE_DUNGEON );
    }
    
    //    private LootTableBuilder buildFloorTrapChestLoot( FloorTrapType type ) {
    //        final LootTableBuilder loot = new LootTableBuilder();
    //        switch( type ) {
    //            case TNT -> { }
    //            default ->
    //                    throw new IllegalArgumentException( "Floor trap type \"" + type + "\" is missing chest loot table data gen code!" );
    //        }
    //        return loot;
    //    }
    
    //    private LootTableBuilder buildTowerChestLoot( TowerType type ) {
    //        final LootTableBuilder loot = new LootTableBuilder();
    //        switch( type ) {
    //            case SIMPLE -> { }
    //            default ->
    //                    throw new IllegalArgumentException( "Tower type \"" + type + "\" is missing chest loot table data gen code!" );
    //        }
    //        return loot;
    //    }
    
    protected void add( ResourceLocation name, LootTableBuilder builder ) {
        lootRegistry.accept( name, builder.toLootTable() );
    }
    
    private LootPool.Builder buildExplorationLootPool() {
        return new DWLootPoolBuilder( "exploration" ).setRolls( 1, 3 )
                // Weapons
                .addItem( Items.BOW, 10 )
                .addItem( Items.CROSSBOW, 5 )
                .addItemClusterLarge( Items.ARROW, 15 )
                .addItem( Items.STONE_SWORD, 10 )
                .addItem( Items.STONE_AXE, 10 )
                .addItem( Items.IRON_SWORD, 10 )
                .addItem( Items.DIAMOND_SWORD, 5 )
                .addItem( Items.SHIELD, 10 )
                .addItem( Items.FISHING_ROD, 10 )
                // Buckets
                .addItem( Items.WATER_BUCKET, 5 )
                .addItem( Items.LAVA_BUCKET, 5 )
                .addItem( Items.MILK_BUCKET, 5 )
                // Utilities
                .addItemClusterSmall( Items.ENDER_PEARL, 10 )
                .addItem( Items.CLOCK, 10 )
                .addItem( Items.COMPASS, 10 )
                .addItem( Items.RECOVERY_COMPASS, 5 )
                .addItem( Items.SPYGLASS, 5 )
                .addItem( Items.MAP, 10 )
                .addItem( Items.BRUSH, 10 )
                // Explorer maps
                .addExplorerMapBuriedTreasure( 1 )
                .addExplorerMapMansion( 1 )
                .addExplorerMapMonument( 1 )
                // Enchanted book
                .addEnchantedBook( 5, 10, 30, true )
                .toLootPool();
    }
    
    private LootPool.Builder buildValuableLootPool() {
        return new DWLootPoolBuilder( "valuable" ).setRolls( 1, 3 )
                // Materials
                .addItemClusterLarge( Items.IRON_INGOT, 15 )
                .addItemClusterLarge( Items.GOLD_INGOT, 10 )
                .addItemClusterLarge( Items.COPPER_INGOT, 5 )
                .addItemCluster( Items.DIAMOND, 5, 3 )
                .addItemCluster( Items.EMERALD, 5, 3 )
                .addItemCluster( Items.ECHO_SHARD, 5, 3 )
                // Food
                .addItem( Items.CAKE, 1 )
                .addItemClusterSmall( Items.GOLDEN_CARROT, 10 )
                .addItemClusterSmall( Items.GOLDEN_APPLE, 10 )
                .addItem( Items.ENCHANTED_GOLDEN_APPLE, 1 )
                // Iron equipment
                .addItem( Items.IRON_SWORD, 2 )
                .addItem( Items.IRON_PICKAXE, 1 )
                .addItem( Items.IRON_HELMET, 2 )
                .addItem( Items.IRON_CHESTPLATE, 2 )
                .addItem( Items.IRON_LEGGINGS, 2 )
                .addItem( Items.IRON_BOOTS, 2 )
                .addItem( Items.IRON_HORSE_ARMOR, 1 )
                // Diamond equipment
                .addItem( Items.DIAMOND_SWORD, 2 )
                .addItem( Items.DIAMOND_PICKAXE, 1 )
                .addItem( Items.DIAMOND_AXE, 1 )
                .addItem( Items.DIAMOND_SHOVEL, 2 )
                .addItem( Items.DIAMOND_HOE, 1 )
                .addItem( Items.DIAMOND_HELMET, 1 )
                .addItem( Items.DIAMOND_CHESTPLATE, 1 )
                .addItem( Items.DIAMOND_LEGGINGS, 1 )
                .addItem( Items.DIAMOND_BOOTS, 1 )
                .addItem( Items.DIAMOND_HORSE_ARMOR, 1 )
                // Explorer maps
                .addExplorerMapMansion( 2 )
                .addExplorerMapMonument( 2 )
                // Enchanted book
                .addEnchantedBook( 10, 30, true )
                .toLootPool();
    }
    
    private LootPool.Builder buildExplosivesLootPool() {
        return new DWLootPoolBuilder( "explosives" ).setRolls( 1, 3 )
                // Explosives
                .addItemClusterLarge( Items.GUNPOWDER, 20 )
                .addItemClusterSmall( Items.TNT, 10 )
                .addItem( Items.TNT_MINECART, 5 )
                // Fire starters
                .addEntry( new LootEntryItemBuilder( Items.BOW ).setWeight( 5 ).applyOneRandomEnchant( Enchantments.FLAMING_ARROWS ).toLootEntry() )
                .addItemClusterLarge( Items.FIRE_CHARGE, 10 )
                .addItem( Items.FLINT_AND_STEEL, 10 )
                // Tools
                .addItemClusterLarge( Items.REDSTONE, 10 )
                .addItemClusterSmall( Items.REPEATER, 5 )
                .addItemClusterSmall( Items.COMPARATOR, 5 )
                .addItemClusterSmall( Items.OBSERVER, 5 )
                .addItemClusterSmall( Items.DISPENSER, 5 )
                .addItemClusterSmall( Items.DAYLIGHT_DETECTOR, 5 )
                .addItem( Items.SHEARS, 5 )
                .addItemClusterLarge( Items.STRING, 10 )
                .addItem( Items.TRIPWIRE_HOOK, 5, 2 )
                // Enchanted book
                .addEnchantedBook( 5, Enchantments.BLAST_PROTECTION, Enchantments.SWEEPING_EDGE, Enchantments.FLAMING_ARROWS )
                .toLootPool();
    }
    
    private LootPool.Builder buildFieryLootPool() {
        return new DWLootPoolBuilder( "fiery" ).setRolls( 1, 3 )
                // Weapons
                .addEntry( new LootEntryItemBuilder( Items.IRON_SWORD ).setWeight( 5 ).applyOneRandomEnchant( Enchantments.FIRE_ASPECT ).toLootEntry() )
                .addEntry( new LootEntryItemBuilder( Items.BOW ).setWeight( 5 ).applyOneRandomEnchant( Enchantments.FLAMING_ARROWS ).toLootEntry() )
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
                .addEnchantedBook( 5, Enchantments.FIRE_PROTECTION, Enchantments.FIRE_ASPECT, Enchantments.FLAMING_ARROWS )
                .toLootPool();
    }
    
    private LootPool.Builder buildBrewingLootPool() {
        return new DWLootPoolBuilder( "brewing" ).setRolls( 1, 3 )
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
                .addItemClusterSmall( Items.NETHER_WART, 5 )
                .addItemClusterSmall( Items.GLISTERING_MELON_SLICE, 5 )
                .addItemClusterSmall( Items.GOLDEN_CARROT, 5 )
                .addItemClusterSmall( Items.RABBIT_FOOT, 5 )
                .addItemClusterSmall( Items.PUFFERFISH, 5 )
                .addItemClusterSmall( Items.BLAZE_POWDER, 5 )
                .addItemClusterSmall( Items.MAGMA_CREAM, 5 )
                .addItemClusterSmall( Items.GHAST_TEAR, 5 )
                .addItemClusterSmall( Items.SCUTE, 5 )
                .addItemClusterSmall( Items.PHANTOM_MEMBRANE, 5 )
                // Very rare brewing
                .addItem( Items.DRAGON_BREATH, 1 )
                // Suspicious stews
                .addSuspiciousStew( 2, MobEffects.BLINDNESS, 8 ) // Azure bluet
                .addSuspiciousStew( 2, MobEffects.POISON, 12 ) // Lily of the valley
                .addSuspiciousStew( 2, MobEffects.REGENERATION, 8 ) // Oxeye daisy
                .addSuspiciousStew( 2, MobEffects.NIGHT_VISION, 5 ) // Poppy / torchflower
                .addSuspiciousStew( 2, MobEffects.WEAKNESS, 9 ) // Tulips
                // Explorer maps
                .addExplorerMapMansion( 2 )
                // Enchanted book
                .addEnchantedBook( 5, 10, 30, true )
                .toLootPool();
    }
    
    private LootPool.Builder buildBuggyLootPool() {
        return new DWLootPoolBuilder( "buggy" ).setRolls( 4 )
                // Silverfish eggs
                .addItemClusterLarge( Items.SILVERFISH_SPAWN_EGG, 10 )
                // Seeds
                .addItemClusterSmall( Items.BEETROOT_SEEDS, 15 )
                .addItemClusterSmall( Items.MELON_SEEDS, 15 )
                .addItemClusterSmall( Items.PITCHER_POD, 2 )
                .addItemClusterSmall( Items.PUMPKIN_SEEDS, 15 )
                .addItemClusterSmall( Items.WHEAT_SEEDS, 15 )
                .addItemClusterSmall( Items.TORCHFLOWER_SEEDS, 2 )
                // Nuggets
                .addItemCluster( Items.IRON_NUGGET, 10, 9 )
                .addItemCluster( Items.GOLD_NUGGET, 10, 9 )
                // Gems
                .addItem( Items.DIAMOND, 1 )
                .addItem( Items.EMERALD, 1 )
                .addItem( Items.AMETHYST_SHARD, 1 )
                .addItem( Items.CAKE, 1 )
                // Suspicious stews
                .addSuspiciousStew( 1, MobEffects.HEAL, 0 )
                .addSuspiciousStew( 1, MobEffects.HARM, 0 )
                .addSuspiciousStew( 1, MobEffects.POISON, 7, 10 )
                .addSuspiciousStew( 1, MobEffects.REGENERATION, 7, 10 )
                .addSuspiciousStew( 1, MobEffects.WEAKNESS, 7, 10 )
                .addSuspiciousStew( 1, MobEffects.DAMAGE_BOOST, 7, 10 )
                // Explorer maps
                .addExplorerMapBuriedTreasure( 2 )
                // Enchanted book
                .addEnchantedBook( 2,
                        Enchantments.BLOCK_FORTUNE, Enchantments.MOB_LOOTING, Enchantments.FISHING_LUCK,
                        Enchantments.BANE_OF_ARTHROPODS, Enchantments.SILK_TOUCH )
                .toLootPool();
    }
}