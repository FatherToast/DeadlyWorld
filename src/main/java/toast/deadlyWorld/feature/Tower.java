package toast.deadlyWorld.feature;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import toast.deadlyWorld.ChestBuilder;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld._DeadlyWorld;

public class Tower implements WorldFeature {
    /// This feature's items.
    public static final WorldFeatureItem[] items = { SpawnerItem.buildSpawner("arrow", 0), SpawnerItem.buildSpawner("arrow", 1) };
    /// The weights for each nest type.
    public static final int[] weights = WorldGenerator.getWeights(Properties.TOWERS, _DeadlyWorld.TOWERS);
    /// The total weight for nest types.
    public static final int totalWeight = WorldGenerator.getTotalWeight(Tower.weights);

    /// The chance for this to appear in any given chunk. Determined by the properties file.
    public final double frequency;

    public Tower(double freq) {
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
            if (!world.isAirBlock(x, y, z)) {
                if (state <= 0) {
                    if (this.canBePlaced(world, random, x, y + 2, z)) {
                        yValues.add(Integer.valueOf(y + 2));
                    }
                }
                state = 3;
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
        for (int x1 = -1; x1 < 2; x1++) {
            for (int z1 = -1; z1 < 2; z1++) {
                if (!world.isAirBlock(x + x1, y, z + z1))
                    return false;
            }
        }
        return world.isAirBlock(x, y - 1, z);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        Tower.placeTower(world, random, x, y, z);
        for (y -= 2; y > 4 && !world.isBlockNormalCubeDefault(x, y, z, true); y--) {
            if (random.nextInt(4) == 0) {
                world.setBlock(x, y, z, Blocks.cobblestone, 0, 2);
            }
            else {
                world.setBlock(x, y, z, Blocks.mossy_cobblestone, 0, 2);
            }
            world.markBlockForUpdate(x, y, z);
        }
    }

    /// Places a tower at the given location.
    public static void placeTower(World world, Random random, int x, int y, int z) {
        String type = WorldGenerator.choose(random, Tower.totalWeight, _DeadlyWorld.TOWERS, Tower.weights);
        byte onFire = (byte) (type.endsWith("fire") ? 1 : 0);
        if (type.startsWith("spawner")) {
            WorldGenerator.choose(random, RogueSpawner.totalWeight, RogueSpawner.items).place(world, random, x, y - 1, z);
        }
        else if (type.startsWith("double")) {
            Tower.items[onFire].place(world, random, x, y - 1, z);
        }
        else if (type.startsWith("chest")) {
            ChestBuilder.place(world, random, x, y - 1, z, ChestBuilder.TOWER);
        }
        else if (random.nextInt(4) == 0) {
            world.setBlock(x, y - 1, z, Blocks.cobblestone, 0, 2);
        }
        else {
            world.setBlock(x, y - 1, z, Blocks.mossy_cobblestone, 0, 2);
        }
        Tower.items[onFire].place(world, random, x, y, z);
    }
}