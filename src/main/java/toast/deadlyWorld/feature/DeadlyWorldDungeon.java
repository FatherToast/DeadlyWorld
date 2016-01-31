package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import toast.deadlyWorld.ChestBuilder;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld._DeadlyWorld;

public class DeadlyWorldDungeon implements WorldFeature {
    /// This feature's items.
    public static final WorldFeatureItem[] items = SpawnerItem.buildItems("dungeon");
    /// The total weight for this feature's items.
    public static final int totalWeight = WorldGenerator.getTotalWeight(DeadlyWorldDungeon.items);
    /// The weights for each subtype for this feature.
    public static final int[] featureWeights = WorldGenerator.getWeights(Properties.DUNGEON_TYPES, _DeadlyWorld.DUNGEON_FEATURES);
    /// The total weight for this feature's subtypes.
    public static final int totalFeatureWeight = WorldGenerator.getTotalWeight(DeadlyWorldDungeon.featureWeights);
    /// The chance a dungeon spawner will be armored. Determined by the properties file.
    public static final double armorChance = Properties.getDouble(Properties.DUNGEONS, "_armor_chance");
    /// The chance a cobblestone block will contain a silverfish. Determined by the properties file.
    public static final double silverfishChance = Properties.getDouble(Properties.DUNGEONS, "_silverfish_chance");

    /// The number of veins to be generated. Determined by the properties file.
    public final double placeAttempts;

    public DeadlyWorldDungeon(double count) {
        this.placeAttempts = count;
    }

    /// Attempts to generate this feature in the given chunk. Block position is given.
    @Override
    public void generate(World world, Random random, int x, int z) {
        int chunkX = x;
        int chunkZ = z;
        int y;
        for (double count = this.placeAttempts; count >= 1.0 || count > 0.0 && count > random.nextDouble(); count--) {
            x = chunkX + random.nextInt(16) + 8;
            y = random.nextInt(128);
            z = chunkZ + random.nextInt(16) + 8;
            this.place(world, random, x, y, z);
        }
    }

    /// Returns true if this feature can be placed at the location.
    @Override
    public boolean canBePlaced(World world, Random random, int x, int y, int z) {
        return true;
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        byte radX = (byte) (random.nextInt(2) + 2);
        byte radY = 3;
        byte radZ = (byte) (random.nextInt(2) + 2);
        int exposedBlocks = 0;
        int X, Y, Z;
        for (X = x - radX - 1; X <= x + radX + 1; X++) {
            for (Y = y - 1; Y <= y + radY + 1; Y++) {
                for (Z = z - radZ - 1; Z <= z + radZ + 1; Z++) {
                    if ( (Y == y - 1 || Y == y + radY + 1) && !world.getBlock(X, Y, Z).getMaterial().isSolid())
                        return;
                    if ( (X == x - radX - 1 || X == x + radX + 1 || Z == z - radZ - 1 || Z == z + radZ + 1) && Y == y && world.isAirBlock(X, Y, Z) && world.isAirBlock(X, Y + 1, Z)) {
                        exposedBlocks++;
                    }
                }
            }
        }
        if (exposedBlocks < 1 || exposedBlocks > 5)
            return;
        for (X = x - radX - 1; X <= x + radX + 1; X++) {
            for (Y = y + radY; Y >= y - 1; Y--) {
                for (Z = z - radZ - 1; Z <= z + radZ + 1; Z++) {
                    if (X != x - radX - 1 && Y != y - 1 && Z != z - radZ - 1 && X != x + radX + 1 && Y != y + radY + 1 && Z != z + radZ + 1) {
                        world.setBlockToAir(X, Y, Z);
                    }
                    else if (Y >= 0 && !world.getBlock(X, Y - 1, Z).getMaterial().isSolid()) {
                        world.setBlockToAir(X, Y, Z);
                    }
                    else if (world.getBlock(X, Y, Z).getMaterial().isSolid()) {
                        if (Y == y - 1 && random.nextInt(4) != 0) {
                            world.setBlock(X, Y, Z, Blocks.mossy_cobblestone, 0, 2);
                        }
                        else if (random.nextDouble() < DeadlyWorldDungeon.silverfishChance) {
                            world.setBlock(X, Y, Z, Blocks.monster_egg, 1, 2);
                        }
                        else {
                            world.setBlock(X, Y, Z, Blocks.cobblestone, 0, 2);
                        }
                    }
                }
            }
        }
        byte chestCount = 0;
        while (chestCount < 2) {
            byte chestAttempts = 0;
            while (true) {
                if (chestAttempts < 3) {
                    placeChest: {
                        X = x + random.nextInt(radX * 2 + 1) - radX;
                        Z = z + random.nextInt(radZ * 2 + 1) - radZ;
                        if (world.isAirBlock(X, y, Z)) {
                            exposedBlocks = 0;
                            if (world.getBlock(X - 1, y, Z).getMaterial().isSolid()) {
                                exposedBlocks++;
                            }
                            if (world.getBlock(X + 1, y, Z).getMaterial().isSolid()) {
                                exposedBlocks++;
                            }
                            if (world.getBlock(X, y, Z - 1).getMaterial().isSolid()) {
                                exposedBlocks++;
                            }
                            if (world.getBlock(X, y, Z + 1).getMaterial().isSolid()) {
                                exposedBlocks++;
                            }
                            if (exposedBlocks == 1) {
                                ChestBuilder.place(world, random, X, y, Z, ChestBuilder.DUNGEON);
                                break placeChest;
                            }
                        }
                        chestAttempts++;
                        continue;
                    }
                }
                chestCount++;
                break;
            }
        }
        String type = WorldGenerator.choose(random, DeadlyWorldDungeon.totalFeatureWeight, _DeadlyWorld.DUNGEON_FEATURES, DeadlyWorldDungeon.featureWeights);
        if (type == "spawner") {
            if (random.nextDouble() < DeadlyWorldDungeon.armorChance) {
                world.setBlock(x - 1, y, z, Blocks.obsidian, 0, 2);
                world.setBlock(x + 1, y, z, Blocks.obsidian, 0, 2);
                world.setBlock(x, y - 1, z, Blocks.obsidian, 0, 2);
                world.setBlock(x, y + 1, z, Blocks.obsidian, 0, 2);
                world.setBlock(x, y, z - 1, Blocks.obsidian, 0, 2);
                world.setBlock(x, y, z + 1, Blocks.obsidian, 0, 2);
            }
            WorldGenerator.choose(random, DeadlyWorldDungeon.totalWeight, DeadlyWorldDungeon.items).place(world, random, x, y, z);
        }
        else if (type == "tower") {
            Tower.placeTower(world, random, x, y + 1, z);
            if (world.getBlock(x, y, z) == Blocks.cobblestone && random.nextDouble() < DeadlyWorldDungeon.silverfishChance) {
                world.setBlock(x, y, z, Blocks.monster_egg, 1, 2);
            }
        }
        else if (type == "brutal_spawner") {
            new BrutalSpawner(0.0).place(world, random, x, y, z);
        }
        else if (type == "swarm_spawner") {
            new SwarmSpawner(0.0).place(world, random, x, y, z);
        }
        else {
            _DeadlyWorld.console("Error choosing dungeon type! Unknown type: " + (type == null ? "null" : type));
        }
    }
}