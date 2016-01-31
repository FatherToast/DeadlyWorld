package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class PotionTrap implements WorldFeature {
    /// This feature's items.
    public static final WorldFeatureItem[] items = SpawnerItem.buildItems("potion");
    /// The total weight for this feature's items.
    public static final int totalWeight = WorldGenerator.getTotalWeight(PotionTrap.items);

    /// The chance for this to appear in any given chunk. Determined by the properties file.
    public final double frequency;

    public PotionTrap(double freq) {
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
        if (world.isAirBlock(x - 1, y, z) || world.isAirBlock(x + 1, y, z) || world.isAirBlock(x, y, z - 1) || world.isAirBlock(x, y, z + 1)) {
            y--;
        }
        if (!world.isBlockNormalCubeDefault(x - 1, y, z, false) || !world.isBlockNormalCubeDefault(x + 1, y, z, false) || !world.isBlockNormalCubeDefault(x, y, z - 1, false) || !world.isBlockNormalCubeDefault(x, y, z + 1, false))
            return false;
        return world.isAirBlock(x, y + 2, z) && world.isBlockNormalCubeDefault(x, y - 1, z, false);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        if (world.isAirBlock(x - 1, y, z) || world.isAirBlock(x + 1, y, z) || world.isAirBlock(x, y, z - 1) || world.isAirBlock(x, y, z + 1)) {
            world.setBlock(x, y, z, Blocks.air, 0, 2);
            y--;
        }
        WorldGenerator.choose(random, PotionTrap.totalWeight, PotionTrap.items).place(world, random, x, y, z);
        Mine.coverTrap(world, random, x, y, z);
    }
}