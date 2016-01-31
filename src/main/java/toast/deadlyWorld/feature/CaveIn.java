package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.world.World;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld.TagBuilder;
import toast.deadlyWorld._DeadlyWorld;

public class CaveIn implements WorldFeature {
    /// This feature's items.
    public static final WorldFeatureItem[] items = CaveIn.buildItems();
    /// The total weight for this feature's items.
    public static final int totalWeight = WorldGenerator.getTotalWeight(CaveIn.items);

    /// The chance for this to appear in any given chunk. Determined by the properties file.
    public final double frequency;

    public CaveIn(double freq) {
        this.frequency = freq;
    }

    /// Attempts to generate this feature in the given chunk. Block position is given.
    @Override
    public void generate(World world, Random random, int x, int z) {
        if (this.frequency <= random.nextDouble())
            return;
        x += random.nextInt(16);
        z += random.nextInt(16);
        int y = random.nextInt(30) + 11;
        for (byte state = 0; y < 54; y++) {
            if (world.isBlockNormalCubeDefault(x, y, z, true)) {
                if (state == 0) {
                    if (this.canBePlaced(world, random, x, y, z)) {
                        this.place(world, random, x, y, z);
                        return;
                    }
                    state = -1;
                }
            }
            else if (world.isAirBlock(x, y, z)) {
                state = 0;
            }
        }
    }

    /// Returns true if this feature can be placed at the location.
    @Override
    public boolean canBePlaced(World world, Random random, int x, int y, int z) {
        return world.isAirBlock(x, y - 1, z) && world.isBlockNormalCubeDefault(x, y + 2, z, false);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        WorldGenerator.choose(random, CaveIn.totalWeight, CaveIn.items).place(world, random, x, y + 1, z);
    }

    /// Builds the list of items for this feature.
    private static WorldFeatureItem[] buildItems() {
        int length = _DeadlyWorld.CAVE_INS.length;
        WorldFeatureItem[] items = new WorldFeatureItem[length];
        for (int i = length; i-- > 0;) {
            items[i] = new SpawnerItem(TagBuilder.createCaveInSpawner(_DeadlyWorld.CAVE_INS[i]), Properties.getInt(Properties.CAVE_INS, _DeadlyWorld.CAVE_INS[i].toLowerCase()), true, false);
        }
        return items;
    }
}