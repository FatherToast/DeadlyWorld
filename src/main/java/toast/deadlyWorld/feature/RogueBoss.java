package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.world.World;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld._DeadlyWorld;

public class RogueBoss implements WorldFeature {
    /// This feature's items.
    public static final BossItem[] items = RogueBoss.buildItems();
    /// The total weight for this feature's items.
    public static final int totalWeight = WorldGenerator.getTotalWeight(RogueBoss.items);

    /// The chance for this to appear in any given chunk. Determined by the properties file.
    public final double frequency;

    public RogueBoss(double freq) {
        this.frequency = freq;
    }

    /// Attempts to generate this feature in the given chunk. Block position is given.
    @Override
    public void generate(World world, Random random, int x, int z) {
        if (this.frequency <= random.nextDouble())
            return;
        x += random.nextInt(16);
        z += random.nextInt(16);
        int y = random.nextInt(30) + 12;
        BossItem item = (BossItem) WorldGenerator.choose(random, RogueBoss.totalWeight, RogueBoss.items);
        if (item.type == 1) {
            for (byte state = 0; y > 5; y--) {
                if (world.isBlockNormalCubeDefault(x, y, z, true) || world.isBlockNormalCubeDefault(x + 1, y, z, true) || world.isBlockNormalCubeDefault(x, y, z + 1, true) || world.isBlockNormalCubeDefault(x + 1, y, z + 1, true)) {
                    if (state == 0) {
                        if (this.canBePlaced(world, random, x, y + 1, z, item)) {
                            this.place(world, random, x, y + 1, z, item);
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
        else {
            for (byte state = 0; y > 5; y--) {
                if (world.isBlockNormalCubeDefault(x, y, z, true)) {
                    if (state == 0) {
                        if (this.canBePlaced(world, random, x, y + 1, z, item)) {
                            this.place(world, random, x, y + 1, z, item);
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
    }

    /// Returns true if this feature can be placed at the location.
    @Override
    public boolean canBePlaced(World world, Random random, int x, int y, int z) {
        return this.canBePlaced(world, random, x, y, z, (BossItem) WorldGenerator.choose(random, RogueBoss.totalWeight, RogueBoss.items));
    }

    public boolean canBePlaced(World world, Random random, int x, int y, int z, BossItem item) {
        return item.type == 1 ? world.isAirBlock(x, y, z) && world.isAirBlock(x + 1, y, z) && world.isAirBlock(x, y, z + 1) && world.isAirBlock(x + 1, y, z + 1) : item.type == 0 ? world.isAirBlock(x, y, z) : world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        this.place(world, random, x, y, z, (BossItem) WorldGenerator.choose(random, RogueBoss.totalWeight, RogueBoss.items));
    }

    public void place(World world, Random random, int x, int y, int z, BossItem item) {
        item.place(world, random, x, y, z);
    }

    /// Builds the list of items for this feature.
    private static BossItem[] buildItems() {
        int length = _DeadlyWorld.MOBS.length;
        BossItem[] items = new BossItem[length];
        for (int i = length; i-- > 0;) {
            items[i] = new BossItem(_DeadlyWorld.MOBS[i], Properties.getInt(Properties.BOSSES_ROGUE, _DeadlyWorld.MOBS[i].toLowerCase()));
        }
        return items;
    }
}