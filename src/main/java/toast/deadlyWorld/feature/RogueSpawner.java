package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import toast.deadlyWorld.ChestBuilder;
import toast.deadlyWorld.Properties;

public class RogueSpawner implements WorldFeature {
    /// This feature's items.
    public static final WorldFeatureItem[] items = SpawnerItem.buildItems("spawner_rogue");
    /// The total weight for this feature's items.
    public static final int totalWeight = WorldGenerator.getTotalWeight(RogueSpawner.items);
    /// The chance for a spawner to be armored.
    public static final double armorChance = Properties.getDouble(Properties.SPAWNERS, "_armor_chance");
    /// The chance for a spawner to have a chest.
    public static final double chestChance = Properties.getDouble(Properties.SPAWNERS, "_chest_chance");
    /// The chance for an armored spawner to be a chest instead.
    public static final double trickChance = Properties.getDouble(Properties.SPAWNERS, "_trick_chance");

    /// The chance for this to appear in any given chunk. Determined by the properties file.
    public final double frequency;

    public RogueSpawner(double freq) {
        this.frequency = freq;
    }

    /// Attempts to generate this feature in the given chunk. Block position is given.
    @Override
    public void generate(World world, Random random, int x, int z) {
        if (this.frequency <= random.nextDouble())
            return;
        x += random.nextInt(16);
        z += random.nextInt(16);
        int y = random.nextInt(50) + 11;
        for (byte state = 0; y > 4; y--) {
            if (world.isBlockNormalCubeDefault(x, y, z, true)) {
                if (state == 0) {
                    if (this.canBePlaced(world, random, x, y + 1, z)) {
                        this.place(world, random, x, y + 1, z);
                        return;
                    }
                    state = -1;
                }
            }
            else {
                state = 0;
            }
        }
    }

    /// Returns true if this feature can be placed at the location.
    @Override
    public boolean canBePlaced(World world, Random random, int x, int y, int z) {
        return world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        if (random.nextDouble() < RogueSpawner.armorChance) {
            boolean trick = false;
            int[][] positions = { };
            if (random.nextDouble() < RogueSpawner.chestChance) {
                positions = new int[][] { { 0, 1, 0 }, { -1, 0, 0 }, { 1, 0, 0 }, { 0, 0, -1 }, { 0, 0, 1 }, { -1, -1, 0 }, { 1, -1, 0 }, { 0, -1, -1 }, { 0, -1, 1 }, { 0, -2, 0 } };
                ChestBuilder.place(world, random, x, y - 1, z, ChestBuilder.SPAWNER_ARMORED);
            }
            else {
                positions = new int[][] { { 0, 1, 0 }, { -1, 0, 0 }, { 1, 0, 0 }, { 0, 0, -1 }, { 0, 0, 1 }, { 0, -1, 0 } };
                trick = random.nextDouble() < RogueSpawner.trickChance;
            }
            for (int[] pos : positions) {
                world.setBlock(x + pos[0], y + pos[1], z + pos[2], Blocks.obsidian, 0, 2);
            }
            if (trick) {
                ChestBuilder.place(world, random, x, y, z, ChestBuilder.SPAWNER);
                return;
            }
        }
        else if (random.nextDouble() < RogueSpawner.chestChance && world.isBlockNormalCubeDefault(x, y - 2, z, false)) {
            ChestBuilder.place(world, random, x, y - 1, z, ChestBuilder.SPAWNER);
        }
        WorldGenerator.choose(random, RogueSpawner.totalWeight, RogueSpawner.items).place(world, random, x, y, z);
    }
}