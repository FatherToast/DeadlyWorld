package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import toast.deadlyWorld.ChestBuilder;

public class SwarmSpawner implements WorldFeature {
    /// This feature's items.
    public static final WorldFeatureItem[] items = SpawnerItem.buildItems("spawner_swarm");
    /// The total weight for this feature's items.
    public static final int totalWeight = WorldGenerator.getTotalWeight(SwarmSpawner.items);

    /// The chance for this to appear in any given chunk. Determined by the properties file.
    public final double frequency;

    public SwarmSpawner(double freq) {
        this.frequency = freq;
    }

    /// Attempts to generate this feature in the given chunk. Block position is given.
    @Override
    public void generate(World world, Random random, int x, int z) {
        if (this.frequency <= random.nextDouble())
            return;
        x += random.nextInt(16);
        z += random.nextInt(16);
        int y = random.nextInt(40) + 11;
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
        ChestBuilder.place(world, random, x, y - 1, z, ChestBuilder.SPAWNER_SWARM);
        WorldGenerator.choose(random, SwarmSpawner.totalWeight, SwarmSpawner.items).place(world, random, x, y, z);
        world.setBlock(x, y + 1, z, Blocks.sandstone, 1, 2);
    }
}