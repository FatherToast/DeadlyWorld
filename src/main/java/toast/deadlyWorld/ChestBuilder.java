package toast.deadlyWorld;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;

public class ChestBuilder {
    /// Default loot categories for various features.
    public static final String DUNGEON = ChestGenHooks.DUNGEON_CHEST;
    public static final String TOWER = "DeadlyWorld.towerChest";
    public static final String SILVER_NEST = "DeadlyWorld.silverfishNestChest";

    public static final String SPAWNER = "DeadlyWorld.spawnerChest";
    public static final String SPAWNER_ARMORED = "DeadlyWorld.spawnerChestArmored";
    public static final String SPAWNER_BRUTAL = "DeadlyWorld.spawnerChestBrutal";
    public static final String SPAWNER_SWARM = "DeadlyWorld.spawnerChestSwarm";
    public static final String SPAWNER_TRAP = "DeadlyWorld.spawnerTrap";

    public static final String CHEST_VALUABLE = "DeadlyWorld.chestRogueValuable";
    public static final String[] CHEST_ROGUE = new String[5];

    /// Shortcut to create a new weighted chest Items.
    public static WeightedRandomChestContent loot(Item item, int damage, int min, int max, int weight) {
        return new WeightedRandomChestContent(item, damage, min, max, weight);
    }

    public static WeightedRandomChestContent loot(Block block, int damage, int min, int max, int weight) {
        return ChestBuilder.loot(new ItemStack(block, 1, damage), min, max, weight);
    }

    public static WeightedRandomChestContent loot(ItemStack item, int min, int max, int weight) {
        return new WeightedRandomChestContent(item, min, max, weight);
    }

    /// Sets the min and max and adds chest loot to the category.
    public static void setCategoryStats(ChestGenHooks category, int min, int max, WeightedRandomChestContent[] contents) {
        category.setMin(min);
        category.setMax(max);
        for (WeightedRandomChestContent item : contents) {
            category.addItem(item);
        }
    }

    /// Returns a loot category based on the given height.
    public static String getRogueChestByHeight(int y) {
        if (y < 20)
            return ChestBuilder.CHEST_ROGUE[4];
        else if (y < 28)
            return ChestBuilder.CHEST_ROGUE[3];
        else if (y < 38)
            return ChestBuilder.CHEST_ROGUE[2];
        else if (y < 48)
            return ChestBuilder.CHEST_ROGUE[1];
        else
            return ChestBuilder.CHEST_ROGUE[0];
    }

    /// Automatically builds and places a chest.
    public static void place(World world, Random random, int x, int y, int z, String loot) {
        world.setBlock(x, y, z, Blocks.chest, 0, 2);
        ChestBuilder chest = new ChestBuilder(world, random, x, y, z);
        if (chest.isValid) {
            chest.fill(loot);
        }
        world.markBlockForUpdate(x, y, z);
    }

    public static void placeTrapped(World world, Random random, int x, int y, int z, String loot) {
        world.setBlock(x, y, z, Blocks.trapped_chest, 0, 2);
        ChestBuilder chest = new ChestBuilder(world, random, x, y, z);
        if (chest.isValid) {
            chest.fill(loot);
        }
        world.markBlockForUpdate(x, y, z);
    }

    /// Initializes this class.
    public static void init() {
        // Do nothing.
    }

    /// The RNG being used to generate the world.
    public Random random;
    /// The mob spawner entity being edited.
    public TileEntityChest chest;
    /// Returns true if this chest builder should be used.
    public boolean isValid;

    public ChestBuilder(World world, Random rand, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityChest) {
            this.random = rand;
            this.chest = (TileEntityChest) tileEntity;
            this.isValid = true;
        }
    }

    public ChestBuilder(TileEntityChest tileEntity, Random rand) {
        this.chest = tileEntity;
        if (this.chest != null) {
            this.random = rand;
            this.isValid = true;
        }
    }

    /// Generates the chest's loot.
    public void fill(String loot) {
        ChestGenHooks info = ChestGenHooks.getInfo(loot);
        WeightedRandomChestContent.generateChestContents(this.random, info.getItems(this.random), this.chest, info.getCount(this.random));
    }

    static {
        // Define all the rogue chest ids.
        for (int i = ChestBuilder.CHEST_ROGUE.length; i-- > 0;) {
            ChestBuilder.CHEST_ROGUE[i] = "DeadlyWorld.chestRogueTier" + Integer.toString(i);
        }
        // List used to temporarily store the loot for each chest.
        ArrayList<WeightedRandomChestContent> list = new ArrayList<WeightedRandomChestContent>();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 3, 5));
        list.add(ChestBuilder.loot(Items.gold_nugget, 0, 4, 9, 5));
        list.add(ChestBuilder.loot(Items.flint, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.feather, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.bread, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.arrow, 0, 4, 12, 20));
        list.add(ChestBuilder.loot(Items.bow, 0, 1, 1, 2));
        list.add(ChestBuilder.loot(Items.fire_charge, 0, 1, 5, 5));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 1));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 1, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.TOWER), 2, 5, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.wheat_seeds, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.pumpkin_seeds, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.melon_seeds, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.dye, 3, 1, 3, 3)); // cocoa beans
        list.add(ChestBuilder.loot(Items.gold_nugget, 0, 4, 9, 5));
        list.add(ChestBuilder.loot(Items.bone, 0, 4, 6, 15));
        list.add(ChestBuilder.loot(Items.dye, 15, 8, 14, 20)); // bonemeal
        list.add(ChestBuilder.loot(Items.rotten_flesh, 0, 3, 7, 20));
        list.add(ChestBuilder.loot(Items.string, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.gunpowder, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 1));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 1, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.SILVER_NEST), 1, 6, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.diamond, 0, 1, 2, 3));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.redstone, 0, 4, 9, 5));
        list.add(ChestBuilder.loot(Items.bone, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.arrow, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.rotten_flesh, 0, 3, 7, 10));
        list.add(ChestBuilder.loot(Items.string, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.gunpowder, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.ender_pearl, 0, 1, 1, 10));
        list.add(ChestBuilder.loot(Items.bread, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.golden_apple, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.cake, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.name_tag, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.iron_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_pickaxe, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_sword, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_chestplate, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_helmet, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_leggings, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_boots, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 1));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 1, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.SPAWNER), 4, 4, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.experience_bottle, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.diamond, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.emerald, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.redstone, 0, 4, 9, 5));
        list.add(ChestBuilder.loot(Items.dye, 4, 4, 9, 5)); // lapis lazuli
        list.add(ChestBuilder.loot(Items.coal, 0, 3, 8, 10));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.golden_apple, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.name_tag, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.iron_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.golden_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_pickaxe, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_sword, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_chestplate, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_helmet, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_leggings, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_boots, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 3, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.SPAWNER_ARMORED), 6, 6, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.experience_bottle, 0, 2, 4, 10));
        list.add(ChestBuilder.loot(Items.skull, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.skull, 1, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.skull, 2, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.skull, 3, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.skull, 4, 1, 1, 1));
        list.add(ChestBuilder.loot(Blocks.tnt, 0, 1, 1, 2));
        list.add(ChestBuilder.loot(Items.diamond, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.emerald, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.golden_apple, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.name_tag, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.iron_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.golden_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 3, 3));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.SPAWNER_BRUTAL), 5, 5, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.experience_bottle, 0, 2, 4, 10));
        list.add(ChestBuilder.loot(Items.spawn_egg, 50, 1, 3, 1)); // creeper
        list.add(ChestBuilder.loot(Items.spawn_egg, 51, 1, 3, 1)); // skeleton
        list.add(ChestBuilder.loot(Items.spawn_egg, 52, 1, 3, 1)); // spider
        list.add(ChestBuilder.loot(Items.spawn_egg, 54, 1, 3, 1)); // zombie
        list.add(ChestBuilder.loot(Items.spawn_egg, 55, 1, 3, 1)); // slime
        list.add(ChestBuilder.loot(Items.spawn_egg, 58, 1, 3, 1)); // enderman
        list.add(ChestBuilder.loot(Items.spawn_egg, 59, 1, 3, 1)); // cave spider
        list.add(ChestBuilder.loot(Items.spawn_egg, 60, 1, 3, 1)); // silverfish
        list.add(ChestBuilder.loot(Items.diamond, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.emerald, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.golden_apple, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.name_tag, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.iron_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.golden_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 3, 3));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.SPAWNER_SWARM), 5, 5, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Blocks.tripwire_hook, 0, 2, 2, 2));
        list.add(ChestBuilder.loot(Blocks.daylight_detector, 0, 1, 1, 2));
        list.add(ChestBuilder.loot(Blocks.redstone_torch, 0, 3, 7, 5));
        list.add(ChestBuilder.loot(Items.repeater, 0, 1, 5, 3));
        list.add(ChestBuilder.loot(Items.comparator, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Blocks.tnt, 0, 1, 1, 2));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 5));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 3, 5));
        list.add(ChestBuilder.loot(Items.redstone, 0, 4, 9, 10));
        list.add(ChestBuilder.loot(Items.bone, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.arrow, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.rotten_flesh, 0, 3, 7, 10));
        list.add(ChestBuilder.loot(Items.string, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.gunpowder, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.ender_pearl, 0, 1, 1, 10));
        list.add(ChestBuilder.loot(Items.bread, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.golden_apple, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.cake, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.name_tag, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.iron_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_pickaxe, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_sword, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_chestplate, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_helmet, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_leggings, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_boots, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 1));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 1, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.SPAWNER_TRAP), 4, 4, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.experience_bottle, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.potionitem, 8225, 1, 1, 1)); // regeneration II
        list.add(ChestBuilder.loot(Items.potionitem, 8226, 1, 1, 1)); // swiftness II
        list.add(ChestBuilder.loot(Items.potionitem, 8259, 1, 1, 1)); // fire resistance (ext)
        list.add(ChestBuilder.loot(Items.potionitem, 8229, 1, 1, 1)); // healing II
        list.add(ChestBuilder.loot(Items.potionitem, 8262, 1, 1, 1)); // night vision (ext)
        list.add(ChestBuilder.loot(Items.potionitem, 8265, 1, 1, 1)); // strength (ext)
        list.add(ChestBuilder.loot(Items.potionitem, 8270, 1, 1, 1)); // invisibility (ext)
        list.add(ChestBuilder.loot(Items.potionitem, 8269, 1, 1, 1)); // water breathing (ext)
        list.add(ChestBuilder.loot(Items.diamond, 0, 1, 3, 5));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.coal, 0, 3, 8, 10));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.golden_apple, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.name_tag, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.iron_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.golden_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.iron_pickaxe, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_sword, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_chestplate, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_helmet, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_leggings, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_boots, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 3, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.CHEST_VALUABLE), 10, 10, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Blocks.cobblestone, 0, 8, 24, 5));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.gold_nugget, 0, 4, 9, 3));
        list.add(ChestBuilder.loot(Items.coal, 0, 3, 8, 5));
        list.add(ChestBuilder.loot(Items.bone, 0, 4, 6, 15));
        list.add(ChestBuilder.loot(Items.rotten_flesh, 0, 3, 7, 15));
        list.add(ChestBuilder.loot(Items.arrow, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.bread, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.wooden_pickaxe, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.wooden_sword, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.leather_chestplate, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.leather_helmet, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.leather_leggings, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.leather_boots, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 1));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 1, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.CHEST_ROGUE[0]), 2, 4, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.potionitem, 8193, 1, 1, 1)); // regeneration
        list.add(ChestBuilder.loot(Items.potionitem, 8194, 1, 1, 1)); // swiftness
        list.add(ChestBuilder.loot(Items.potionitem, 8195, 1, 1, 1)); // fire resistance
        list.add(ChestBuilder.loot(Items.potionitem, 8197, 1, 1, 1)); // healing
        list.add(ChestBuilder.loot(Items.potionitem, 8198, 1, 1, 1)); // night vision
        list.add(ChestBuilder.loot(Items.potionitem, 8201, 1, 1, 1)); // strength
        list.add(ChestBuilder.loot(Items.potionitem, 8206, 1, 1, 1)); // invisibility
        list.add(ChestBuilder.loot(Items.potionitem, 8237, 1, 1, 1)); // water breathing
        list.add(ChestBuilder.loot(Blocks.cobblestone, 0, 8, 24, 3));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 5));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 3, 5));
        list.add(ChestBuilder.loot(Items.gold_nugget, 0, 4, 9, 5));
        list.add(ChestBuilder.loot(Items.redstone, 0, 4, 9, 5));
        list.add(ChestBuilder.loot(Items.coal, 0, 3, 8, 10));
        list.add(ChestBuilder.loot(Items.bone, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.rotten_flesh, 0, 3, 7, 10));
        list.add(ChestBuilder.loot(Items.arrow, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.bread, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.iron_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_pickaxe, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_sword, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_chestplate, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_helmet, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_leggings, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_boots, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.painting, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 1));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 1, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.CHEST_ROGUE[1]), 2, 4, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Blocks.torch, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Blocks.cobblestone, 0, 8, 24, 1));
        list.add(ChestBuilder.loot(Items.potionitem, 8193, 1, 1, 1)); // regeneration
        list.add(ChestBuilder.loot(Items.potionitem, 8194, 1, 1, 1)); // swiftness
        list.add(ChestBuilder.loot(Items.potionitem, 8195, 1, 1, 1)); // fire resistance
        list.add(ChestBuilder.loot(Items.potionitem, 8197, 1, 1, 1)); // healing
        list.add(ChestBuilder.loot(Items.potionitem, 8198, 1, 1, 1)); // night vision
        list.add(ChestBuilder.loot(Items.potionitem, 8201, 1, 1, 1)); // strength
        list.add(ChestBuilder.loot(Items.potionitem, 8206, 1, 1, 1)); // invisibility
        list.add(ChestBuilder.loot(Items.potionitem, 8237, 1, 1, 1)); // water breathing
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 3, 5));
        list.add(ChestBuilder.loot(Items.redstone, 0, 4, 9, 5));
        list.add(ChestBuilder.loot(Items.coal, 0, 3, 8, 10));
        list.add(ChestBuilder.loot(Items.arrow, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.bread, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.golden_apple, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.name_tag, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.iron_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.golden_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.stone_pickaxe, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.stone_sword, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.chainmail_chestplate, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.chainmail_helmet, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.chainmail_leggings, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.chainmail_boots, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.painting, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 1, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.CHEST_ROGUE[2]), 3, 5, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Items.potionitem, 8193, 1, 1, 1)); // regeneration
        list.add(ChestBuilder.loot(Items.potionitem, 8194, 1, 1, 1)); // swiftness
        list.add(ChestBuilder.loot(Items.potionitem, 8195, 1, 1, 1)); // fire resistance
        list.add(ChestBuilder.loot(Items.potionitem, 8197, 1, 1, 1)); // healing
        list.add(ChestBuilder.loot(Items.potionitem, 8198, 1, 1, 1)); // night vision
        list.add(ChestBuilder.loot(Items.potionitem, 8201, 1, 1, 1)); // strength
        list.add(ChestBuilder.loot(Items.potionitem, 8206, 1, 1, 1)); // invisibility
        list.add(ChestBuilder.loot(Items.potionitem, 8237, 1, 1, 1)); // water breathing
        list.add(ChestBuilder.loot(Items.diamond, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 3, 10));
        list.add(ChestBuilder.loot(Items.redstone, 0, 4, 9, 5));
        list.add(ChestBuilder.loot(Items.coal, 0, 3, 8, 10));
        list.add(ChestBuilder.loot(Items.arrow, 0, 4, 12, 10));
        list.add(ChestBuilder.loot(Items.carrot, 0, 1, 3, 5));
        list.add(ChestBuilder.loot(Items.potato, 0, 1, 3, 5));
        list.add(ChestBuilder.loot(Items.bread, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.golden_apple, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.name_tag, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.golden_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.diamond_horse_armor, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_pickaxe, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_sword, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_chestplate, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_helmet, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_leggings, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.iron_boots, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.painting, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 1, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.CHEST_ROGUE[3]), 4, 6, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();

        list.add(ChestBuilder.loot(Items.potionitem, 8193, 1, 1, 1)); // regeneration
        list.add(ChestBuilder.loot(Items.potionitem, 8194, 1, 1, 1)); // swiftness
        list.add(ChestBuilder.loot(Items.potionitem, 8195, 1, 1, 1)); // fire resistance
        list.add(ChestBuilder.loot(Items.potionitem, 8197, 1, 1, 1)); // healing
        list.add(ChestBuilder.loot(Items.potionitem, 8198, 1, 1, 1)); // night vision
        list.add(ChestBuilder.loot(Items.potionitem, 8201, 1, 1, 1)); // strength
        list.add(ChestBuilder.loot(Items.potionitem, 8206, 1, 1, 1)); // invisibility
        list.add(ChestBuilder.loot(Items.potionitem, 8237, 1, 1, 1)); // water breathing
        list.add(ChestBuilder.loot(Items.iron_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.gold_ingot, 0, 1, 5, 10));
        list.add(ChestBuilder.loot(Items.bone, 0, 4, 6, 10));
        list.add(ChestBuilder.loot(Items.rotten_flesh, 0, 3, 7, 10));
        list.add(ChestBuilder.loot(Items.ender_pearl, 0, 1, 1, 10));
        list.add(ChestBuilder.loot(Items.bread, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.apple, 0, 1, 3, 15));
        list.add(ChestBuilder.loot(Items.golden_apple, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.cake, 0, 1, 1, 3));
        list.add(ChestBuilder.loot(Items.name_tag, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.lead, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.saddle, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.flint_and_steel, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_pickaxe, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_sword, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_chestplate, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_helmet, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_leggings, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.golden_boots, 0, 1, 1, 5));
        list.add(ChestBuilder.loot(Items.painting, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.slime_ball, 0, 1, 3, 3));
        list.add(ChestBuilder.loot(Items.bucket, 0, 1, 1, 1));
        list.add(ChestBuilder.loot(Items.enchanted_book, 0, 1, 1, 1));
        ChestBuilder.setCategoryStats(ChestGenHooks.getInfo(ChestBuilder.CHEST_ROGUE[4]), 2, 4, list.toArray(new WeightedRandomChestContent[0]));
        list.clear();
    }
}