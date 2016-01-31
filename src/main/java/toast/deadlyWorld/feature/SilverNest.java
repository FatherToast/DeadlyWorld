package toast.deadlyWorld.feature;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import toast.deadlyWorld.ChestBuilder;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld._DeadlyWorld;

public class SilverNest implements WorldFeature {
    /// This feature's items.
    public static final WorldFeatureItem item = SpawnerItem.buildSpawner("silver_nest", 0);
    public static final WorldFeatureItem angryItem = SpawnerItem.buildSpawner("silver_nest", 1);
    public static final WorldFeatureItem surpriseItem = SpawnerItem.buildSpawner("silver_nest", 2);
    /// The weights for each nest type.
    public static final int[] weights = WorldGenerator.getWeights(Properties.NESTS, _DeadlyWorld.NESTS);
    /// The total weight for nest types.
    public static final int totalWeight = WorldGenerator.getTotalWeight(SilverNest.weights);
    /// The chance for a nest spawner to be aggressive.
    public static final double angeredChance = Properties.getDouble(Properties.NESTS, "_angered_chance");

    /// The chance for this to appear in any given chunk. Determined by the properties file.
    public final double frequency;

    public SilverNest(double freq) {
        this.frequency = freq;
    }

    /// Attempts to generate this feature in the given chunk. Block position is given.
    @Override
    public void generate(World world, Random random, int x, int z) {
        if (this.frequency <= random.nextDouble())
            return;
        x += random.nextInt(16);
        z += random.nextInt(16);
        int y = 62;
        ArrayList<Integer> yValues = new ArrayList<Integer>(15);
        for (byte state = 3; y > 5; y--) {
            if (world.isBlockNormalCubeDefault(x, y, z, true)) {
                if (state <= 0) {
                    if (this.canBePlaced(world, random, x, y + 1, z)) {
                        yValues.add(Integer.valueOf(y + 1));
                    }
                    state = 3;
                }
            }
            else {
                state--;
            }
        }
        if (yValues.size() > 0) {
            this.place(world, random, x, yValues.get(random.nextInt(yValues.size())).intValue(), z);
        }
    }

    /// Returns true if this feature can be placed at the location.
    @Override
    public boolean canBePlaced(World world, Random random, int x, int y, int z) {
        return world.isAirBlock(x, y, z);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        for (int x1 = -1; x1 <= 1; x1++) {
            for (int y1 = -2; y1 <= 1; y1++) {
                for (int z1 = -1; z1 <= 1; z1++) {
                    if (x1 == 0 || z1 == 0 || y1 == -1 || y1 == 0)
                        if (x1 != 0 || z1 != 0 || y1 < -1 || y1 > 0) {
                            world.setBlock(x + x1, y + y1, z + z1, Blocks.monster_egg, 1, 2);
                        }
                }
            }
        }
        if (random.nextDouble() < SilverNest.angeredChance) {
            SilverNest.angryItem.place(world, random, x, y, z);
        }
        else {
            SilverNest.item.place(world, random, x, y, z);
        }

        String type = WorldGenerator.choose(random, SilverNest.totalWeight, _DeadlyWorld.NESTS, SilverNest.weights);
        if (type == "surprise") {
            SilverNest.surpriseItem.place(world, random, x, y - 1, z);
        }
        else if (type == "chest") {
            ChestBuilder.place(world, random, x, y - 1, z, ChestBuilder.SILVER_NEST);
        }
        else {
            Block block;
            if (type == "redstone") {
                block = Blocks.redstone_ore;
            }
            else if (type == "lapis") {
                block = Blocks.lapis_ore;
            }
            else if (type == "gold") {
                block = Blocks.gold_ore;
            }
            else if (type == "emerald") {
                block = Blocks.emerald_ore;
            }
            else if (type == "diamond") {
                block = Blocks.diamond_ore;
            }
            else if (type == "party") {
                block = Blocks.cake;
            }
            else {
                block = Blocks.redstone_ore;
                _DeadlyWorld.console("Error choosing silvernest type! Unknown type: " + (type == null ? "null" : type));
            }
            world.setBlock(x, y - 1, z, block, 0, 2);
        }
    }
}