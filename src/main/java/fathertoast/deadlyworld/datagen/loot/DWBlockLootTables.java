package fathertoast.deadlyworld.datagen.loot;

import fathertoast.crust.api.datagen.loot.LootEntryItemBuilder;
import fathertoast.crust.api.datagen.loot.LootTableBuilder;
import fathertoast.deadlyworld.common.block.floortrap.FloorTrapType;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DWBlockLootTables extends VanillaBlockLoot { // Extending vanilla block loot to copy all "explosion resistant" items
    protected DWBlockLootTables() { super(); }
    
    /** Builds all loot tables for this provider. */
    @Override
    public void generate() {
        for( SpawnerType type : SpawnerType.values() ) {
            add( DWBlocks.spawner( type ), buildSpawnerLoot( type ) );
        }
        //        for( FloorTrapType type : FloorTrapType.values() ) {
        //            add( DWBlocks.floorTrap( type ), buildFloorTrapLoot( type ) );
        //        }
        //        for( TowerType type : TowerType.values() ) {
        //            add( DWBlocks.towerDispenser( type ), buildTowerDispenserLoot( type ) );
        //        }
    }
    
    @Override
    protected Iterable<Block> getKnownBlocks() {
        // This is basically pulled straight from the forge docs on data gen for block/entity loot tables
        return DWBlocks.REGISTRY.getEntries().stream().flatMap( RegistryObject::stream ).collect( Collectors.toList() );
    }
    
    private LootTableBuilder buildSpawnerLoot( SpawnerType type ) {
        final LootTableBuilder loot = new LootTableBuilder();
        switch( type ) {
            case DEFAULT, MINI, DUNGEON -> { } // No extras
            case STREAM -> loot.addPool( buildExplorationLootPool() );
            case SWARM -> loot.addPool( buildExplosivesLootPool() );
            case BRUTAL -> loot.addPool( buildValuableLootPool() );
            case NEST -> { return loot.addPool( buildBuggyLootPool() ); } // Skip base loot
            default ->
                    throw new IllegalArgumentException( "Spawner type \"" + type + "\" is missing block loot table data gen code!" );
        }
        return loot.addPool( buildBasicSpawnerLootPool() );
    }
    
    private LootTableBuilder buildFloorTrapLoot( FloorTrapType type ) {
        final LootTableBuilder loot = new LootTableBuilder();
        switch( type ) { //TODO
            case TNT -> { }
            default ->
                    throw new IllegalArgumentException( "Floor trap type \"" + type + "\" is missing block loot table data gen code!" );
        }
        return loot;
    }
    
    private LootTableBuilder buildTowerDispenserLoot( TowerType type ) {
        final LootTableBuilder loot = new LootTableBuilder();
        switch( type ) { //TODO
            case SIMPLE -> { }
            default ->
                    throw new IllegalArgumentException( "Tower type \"" + type + "\" is missing block loot table data gen code!" );
        }
        return loot;
    }
    
    protected <T extends Block> void add( Supplier<T> block, LootTableBuilder builder ) {
        add( block.get(), builder.toLootTable() );
    }
    
    private LootPool.Builder buildBasicSpawnerLootPool() {
        return new DWLootPoolBuilder( "spawner" ).setRolls( 4 )
                // Zombie
                .addItemClusterLarge( Items.ROTTEN_FLESH, 15 )
                .addItemClusterSmall( Items.IRON_INGOT, 5 )
                .addItem( Items.CARROT, 2 )
                .addItem( Items.POTATO, 2 )
                // Skeleton
                .addItemClusterLarge( Items.BONE, 10 )
                .addItemClusterLarge( Items.ARROW, 15 )
                .addItem( Items.BOW, 5 )
                // Spider
                .addItemClusterLarge( Items.STRING, 10 )
                .addItemClusterSmall( Items.SPIDER_EYE, 5 )
                // Creeper
                .addItemClusterLarge( Items.GUNPOWDER, 10 )
                // Utilities
                .addItem( Items.EXPERIENCE_BOTTLE, 5 )
                .addEnchantedBook( 1, 10, 30, true )
                .toLootPool();
    }
    
    private LootPool.Builder buildExplorationLootPool() {
        return new DWLootPoolBuilder( "exploration" ).setRolls( 1 )
                // Weapons
                .addItem( Items.BOW, 10 )
                .addItem( Items.CROSSBOW, 5 )
                .addItemClusterLarge( Items.ARROW, 10 )
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
                .addItem( Items.CLOCK, 5 )
                .addItem( Items.COMPASS, 5 )
                .addItem( Items.RECOVERY_COMPASS, 5 )
                .addItem( Items.SPYGLASS, 5 )
                .addItem( Items.MAP, 5 )
                .addItem( Items.BRUSH, 5 )
                // Explorer maps
                .addExplorerMapBuriedTreasure( 1 )
                .addExplorerMapMansion( 1 )
                .addExplorerMapMonument( 1 )
                .toLootPool();
    }
    
    private LootPool.Builder buildValuableLootPool() {
        return new DWLootPoolBuilder( "valuable" ).setRolls( 1, 2 )
                // Materials
                .addItemClusterLarge( Items.RAW_IRON, 15 )
                .addItemClusterLarge( Items.RAW_GOLD, 10 )
                .addItemClusterLarge( Items.RAW_COPPER, 5 )
                .addItemClusterSmall( Items.IRON_INGOT, 15 )
                .addItemClusterSmall( Items.GOLD_INGOT, 10 )
                .addItemClusterSmall( Items.COPPER_INGOT, 5 )
                .addItemCluster( Items.DIAMOND, 5, 3 )
                .addItemCluster( Items.EMERALD, 5, 3 )
                .addItem( Items.ECHO_SHARD, 5 )
                // Food
                .addItemClusterSmall( Items.GOLDEN_CARROT, 10 )
                .addItemClusterSmall( Items.GOLDEN_APPLE, 10 )
                // Iron equipment
                .addItem( Items.IRON_SWORD, 5 )
                .addItem( Items.IRON_HELMET, 2 )
                .addItem( Items.IRON_CHESTPLATE, 2 )
                .addItem( Items.IRON_LEGGINGS, 2 )
                .addItem( Items.IRON_BOOTS, 2 )
                // Diamond equipment
                .addItem( Items.DIAMOND_SWORD, 5 )
                .addItem( Items.DIAMOND_HELMET, 1 )
                .addItem( Items.DIAMOND_CHESTPLATE, 1 )
                .addItem( Items.DIAMOND_LEGGINGS, 1 )
                .addItem( Items.DIAMOND_BOOTS, 1 )
                // Explorer maps
                .addExplorerMapMansion( 2 )
                .addExplorerMapMonument( 2 )
                // Enchanted book
                .addEnchantedBook( 10, 30, true )
                .toLootPool();
    }
    
    private LootPool.Builder buildExplosivesLootPool() {
        return new DWLootPoolBuilder( "explosives" ).setRolls( 1 )
                // Explosives
                .addItemClusterLarge( Items.GUNPOWDER, 20 )
                .addItemClusterSmall( Items.TNT, 10 )
                // Fire starters
                .addEntry( new LootEntryItemBuilder( Items.BOW ).setWeight( 5 ).applyOneRandomEnchant( Enchantments.FLAMING_ARROWS ).toLootEntry() )
                .addItemClusterLarge( Items.FIRE_CHARGE, 15 )
                .addItem( Items.FLINT_AND_STEEL, 5 )
                // Tools
                .addItem( Items.SHEARS, 5 )
                .addItemClusterLarge( Items.STRING, 15 )
                .addItem( Items.TRIPWIRE_HOOK, 10, 2 )
                // Enchanted book
                .addEnchantedBook( 5, Enchantments.BLAST_PROTECTION, Enchantments.SWEEPING_EDGE, Enchantments.FLAMING_ARROWS )
                .toLootPool();
    }
    
    private LootPool.Builder buildFieryLootPool() {
        return new DWLootPoolBuilder( "fiery" ).setRolls( 1 )
                // Weapons
                .addEntry( new LootEntryItemBuilder( Items.IRON_SWORD ).setWeight( 5 ).applyOneRandomEnchant( Enchantments.FIRE_ASPECT ).toLootEntry() )
                .addEntry( new LootEntryItemBuilder( Items.BOW ).setWeight( 5 ).applyOneRandomEnchant( Enchantments.FLAMING_ARROWS ).toLootEntry() )
                .addItemClusterLarge( Items.ARROW, 15 )
                // Utilities
                .addItemClusterLarge( Items.TORCH, 20 )
                .addItemClusterLarge( Items.FIRE_CHARGE, 15 )
                .addItem( Items.FLINT_AND_STEEL, 5 )
                .addItem( Items.LAVA_BUCKET, 5 )
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
        return new DWLootPoolBuilder( "brewing" ).setRolls( 1, 2 )
                // Basic brewing
                .addItemClusterSmall( Items.GLASS_BOTTLE, 20 )
                .addItemClusterSmall( Items.GLOWSTONE_DUST, 20 )
                .addItemClusterSmall( Items.REDSTONE, 20 )
                .addItemClusterSmall( Items.GUNPOWDER, 20 )
                // Common brewing
                .addItemClusterSmall( Items.FERMENTED_SPIDER_EYE, 10 )
                .addItemClusterSmall( Items.SPIDER_EYE, 10 )
                .addItemClusterSmall( Items.SUGAR, 10 )
                // Rare brewing
                .addItem( Items.NETHER_WART, 5 )
                .addItem( Items.GLISTERING_MELON_SLICE, 5 )
                .addItem( Items.GOLDEN_CARROT, 5 )
                .addItem( Items.RABBIT_FOOT, 5 )
                .addItem( Items.PUFFERFISH, 5 )
                .addItem( Items.BLAZE_POWDER, 5 )
                .addItem( Items.MAGMA_CREAM, 5 )
                .addItem( Items.GHAST_TEAR, 5 )
                .addItem( Items.SCUTE, 5 )
                .addItem( Items.PHANTOM_MEMBRANE, 5 )
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
        return new DWLootPoolBuilder( "buggy" ).setRolls( 2 )
                // Silverfish eggs
                .addItem( Items.SILVERFISH_SPAWN_EGG, 10 )
                // Seeds
                .addItemClusterSmall( Items.BEETROOT_SEEDS, 15 )
                .addItemClusterSmall( Items.MELON_SEEDS, 15 )
                .addItemClusterSmall( Items.PITCHER_POD, 1 )
                .addItemClusterSmall( Items.PUMPKIN_SEEDS, 15 )
                .addItemClusterSmall( Items.WHEAT_SEEDS, 15 )
                .addItemClusterSmall( Items.TORCHFLOWER_SEEDS, 1 )
                // Nuggets
                .addItemCluster( Items.IRON_NUGGET, 10, 9 )
                .addItemCluster( Items.GOLD_NUGGET, 10, 9 )
                // Gems
                .addItem( Items.DIAMOND, 1 )
                .addItem( Items.EMERALD, 1 )
                .addItem( Items.AMETHYST_SHARD, 1 )
                // Suspicious stews
                .addSuspiciousStew( 1, MobEffects.HEAL, 0 )
                .addSuspiciousStew( 1, MobEffects.HARM, 0 )
                .addSuspiciousStew( 1, MobEffects.POISON, 7, 10 )
                .addSuspiciousStew( 1, MobEffects.REGENERATION, 7, 10 )
                .addSuspiciousStew( 1, MobEffects.WEAKNESS, 7, 10 )
                .addSuspiciousStew( 1, MobEffects.DAMAGE_BOOST, 7, 10 )
                // Utilities
                .addItem( Items.EXPERIENCE_BOTTLE, 5 )
                .addEnchantedBook( 2, Enchantments.BLOCK_FORTUNE, Enchantments.MOB_LOOTING,
                        Enchantments.FISHING_LUCK, Enchantments.BANE_OF_ARTHROPODS, Enchantments.SILK_TOUCH )
                .toLootPool();
    }
}