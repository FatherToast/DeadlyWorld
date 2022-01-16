package fathertoast.deadlyworld.datagen.loot;

import net.minecraft.block.Blocks;
import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.functions.EnchantRandomly;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.ResourceLocation;

import java.util.function.BiConsumer;

public class DWChestLootTables extends ChestLootTables {

    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        // Vanilla abandoned mineshaft chest loot table, left for example
        biConsumer.accept(LootTables.ABANDONED_MINESHAFT, LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1))
                        .add(ItemLootEntry.lootTableItem(Items.GOLDEN_APPLE).setWeight(20))
                        .add(ItemLootEntry.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE))
                        .add(ItemLootEntry.lootTableItem(Items.NAME_TAG).setWeight(30))
                        .add(ItemLootEntry.lootTableItem(Items.BOOK).setWeight(10).apply(EnchantRandomly.randomApplicableEnchantment()))
                        .add(ItemLootEntry.lootTableItem(Items.IRON_PICKAXE).setWeight(5))
                        // This is basically for making empty container slots
                        .add(EmptyLootEntry.emptyItem().setWeight(5))).withPool(LootPool.lootPool().setRolls(RandomValueRange.between(2.0F, 4.0F))
                        .add(ItemLootEntry.lootTableItem(Items.IRON_INGOT).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(1.0F, 5.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.GOLD_INGOT).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(1.0F, 3.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.REDSTONE).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(4.0F, 9.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.LAPIS_LAZULI).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(4.0F, 9.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.DIAMOND).setWeight(3).apply(SetCount.setCount(RandomValueRange.between(1.0F, 2.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.COAL).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(3.0F, 8.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.BREAD).setWeight(15).apply(SetCount.setCount(RandomValueRange.between(1.0F, 3.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.MELON_SEEDS).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.PUMPKIN_SEEDS).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F))))
                        .add(ItemLootEntry.lootTableItem(Items.BEETROOT_SEEDS).setWeight(10).apply(SetCount.setCount(RandomValueRange.between(2.0F, 4.0F))))).withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(3))
                        .add(ItemLootEntry.lootTableItem(Blocks.RAIL).setWeight(20).apply(SetCount.setCount(RandomValueRange.between(4.0F, 8.0F))))
                        .add(ItemLootEntry.lootTableItem(Blocks.POWERED_RAIL).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(1.0F, 4.0F))))
                        .add(ItemLootEntry.lootTableItem(Blocks.DETECTOR_RAIL).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(1.0F, 4.0F))))
                        .add(ItemLootEntry.lootTableItem(Blocks.ACTIVATOR_RAIL).setWeight(5).apply(SetCount.setCount(RandomValueRange.between(1.0F, 4.0F))))
                        .add(ItemLootEntry.lootTableItem(Blocks.TORCH).setWeight(15).apply(SetCount.setCount(RandomValueRange.between(1.0F, 16.0F))))));
    }
}
