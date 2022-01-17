package fathertoast.deadlyworld.datagen.loot;

import fathertoast.deadlyworld.common.tile.spawner.SpawnerType;
import fathertoast.deadlyworld.datagen.loot.builder.LootTableBuilder;
import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
public class DWChestLootTables extends ChestLootTables {
    
    @Override
    public void accept( BiConsumer<ResourceLocation, LootTable.Builder> gen ) {
        /* For reference, the vanilla dungeon chest loot table has 3 pools:
         *
         * 1-3 x Rare items:
         *      1 Saddle, 1 Golden apple, 1 Epic golden apple, 1 Record - 13, 1 Record - Cat,
         *      1 Name tag, 1 Gold horse armor, 1 Iron horse armor, 1 Diamond horse armor,
         *      1 Enchanted book
         *
         * 1-4 x Basic items:
         *      1-4 Iron ingots, 1-4 Gold ingots, 1 Bread, 1-4 Wheat, 1 Bucket, 1-4 Redstone, 1-4 Coal,
         *      2-4 Melon seeds, 2-4 Pumpkin seeds, 2-4 Beetroot seeds
         *
         * 3 x Common mob loot:
         *      1-8 Bones, 1-8 Gunpowder, 1-8 Rotten flesh, 1-8 String
         */
        
        for( SpawnerType spawnerType : SpawnerType.values() ) {
            if( !spawnerType.isSubfeature() ) { // Chests are handled by the primary feature
                gen.accept( spawnerType.getChestLootTable(), buildSpawnerChestLoot( spawnerType ) );
            }
        }
    }
    
    private LootTable.Builder buildSpawnerChestLoot( SpawnerType type ) {
        final LootTableBuilder loot = new LootTableBuilder();
        
        switch( type ) {
            case DEFAULT:
            case STREAM:
                loot.addThemePoolExploration();
                break;
            case SWARM:
                loot.addThemePoolExplosives();
                break;
            case BRUTAL:
                loot.addThemePoolValuable();
                break;
            case NEST:
                loot.addThemePoolBuggy();
                break;
            default:
                throw new IllegalArgumentException( "Spawner type \"" + type + "\" is missing loot table data gen code!" );
        }
        
        return loot.addTable( "base", LootTables.SIMPLE_DUNGEON ).build();
    }
}