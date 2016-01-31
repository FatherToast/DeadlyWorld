package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.world.World;

public class FireTrap implements WorldFeature {
    /// This feature's item.
    public static final WorldFeatureItem item = SpawnerItem.buildSpawner("fire", 0);

    /// The chance for this to appear in any given chunk. Determined by the properties file.
    public final double frequency;

    public FireTrap(double freq) {
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
        for (byte state = 0; y > 5; y--) {
            if (world.isBlockNormalCubeDefault(x, y, z, true)) {
                if (state == 0) {
                    if (this.canBePlaced(world, random, x, y, z)) {
                        this.place(world, random, x, y, z);
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
        y++;
        for (int x1 = -1; x1 < 2; x1++) {
            for (int z1 = -1; z1 < 2; z1++) {
                if (world.isBlockNormalCubeDefault(x + x1, y, z + z1, true))
                    return false;
            }
        }
        y--;
        return world.isAirBlock(x, y + 2, z) && world.isBlockNormalCubeDefault(x, y - 1, z, false) && world.isBlockNormalCubeDefault(x - 1, y, z, false) && world.isBlockNormalCubeDefault(x + 1, y, z, false) && world.isBlockNormalCubeDefault(x, y, z - 1, false) && world.isBlockNormalCubeDefault(x, y, z + 1, false);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        FireTrap.item.place(world, random, x, y, z);
        Mine.coverTrap(world, random, x, y, z);
    }
}