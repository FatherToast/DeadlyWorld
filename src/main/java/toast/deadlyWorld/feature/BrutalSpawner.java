package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import toast.deadlyWorld.ChestBuilder;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld.TagBuilder;

public class BrutalSpawner implements WorldFeature {
    /// The tag holding the base stats for brutal mobs.
    public static final NBTTagCompound BASE_TAG = new NBTTagCompound();
    static {
        Potion[] potions = { Potion.fireResistance, Potion.regeneration, Potion.resistance, Potion.damageBoost, Potion.moveSpeed, Potion.waterBreathing };
        int[] amplifiers = { Properties.getBoolean(Properties.BRUTAL_MOBS, "_fire_resistance") ? 0 : -1, Properties.getInt(Properties.BRUTAL_MOBS, "_regeneration"), Properties.getInt(Properties.BRUTAL_MOBS, "_resistance"), Properties.getInt(Properties.BRUTAL_MOBS, "_strength"), Properties.getInt(Properties.BRUTAL_MOBS, "_swiftness"), Properties.getBoolean(Properties.BRUTAL_MOBS, "_water_breathing") ? 0 : -1 };
        for (int i = potions.length; i-- > 0;)
            if (amplifiers[i] >= 0) {
                TagBuilder.addPotionEffect(BrutalSpawner.BASE_TAG, potions[i], amplifiers[i]);
            }
    }
    /// This feature's items.
    public static final WorldFeatureItem[] items = SpawnerItem.buildItems("spawner_brutal");
    /// The total weight for this feature's items.
    public static final int totalWeight = WorldGenerator.getTotalWeight(BrutalSpawner.items);

    /// The chance for this to appear in any given chunk. Determined by the properties file.
    public final double frequency;

    public BrutalSpawner(double freq) {
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
        ChestBuilder.place(world, random, x, y - 1, z, ChestBuilder.SPAWNER_BRUTAL);
        WorldGenerator.choose(random, BrutalSpawner.totalWeight, BrutalSpawner.items).place(world, random, x, y, z);
        world.setBlock(x, y + 1, z, Blocks.stonebrick, 3, 2);
        for (int y1 = 0; y1 < 2; y1++) {
            if (random.nextInt(4) == 0 && world.isAirBlock(x - 1, y + y1, z)) {
                world.setBlock(x - 1, y + y1, z, Blocks.vine, 8, 2);
            }
            if (random.nextInt(4) == 0 && world.isAirBlock(x + 1, y + y1, z)) {
                world.setBlock(x + 1, y + y1, z, Blocks.vine, 2, 2);
            }
            if (random.nextInt(4) == 0 && world.isAirBlock(x, y + y1, z - 1)) {
                world.setBlock(x, y + y1, z - 1, Blocks.vine, 1, 2);
            }
            if (random.nextInt(4) == 0 && world.isAirBlock(x, y + y1, z + 1)) {
                world.setBlock(x, y + y1, z + 1, Blocks.vine, 4, 2);
            }
        }
    }
}