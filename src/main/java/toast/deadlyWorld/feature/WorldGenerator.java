package toast.deadlyWorld.feature;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld._DeadlyWorld;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;

public class WorldGenerator implements IWorldGenerator {
    /// The list of registered features.
    public static final WorldFeature[] features = WorldGenerator.buildFeaturesArray();

    public WorldGenerator() {
        GameRegistry.registerWorldGenerator(this, 0xff);
    }

    /**
     * Generates some world
     * chunkGenerator = the IChunkProvider that is generating.
     * chunkProvider = the IChunkProvider that is requesting the world generation.
     */
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (!_DeadlyWorld.generation || world.isRemote)
            return;
        switch (world.provider.dimensionId) {
            case 0:
                WorldGenerator.run(world, random, chunkX << 4, chunkZ << 4);
                break;
            case -1:
                /// Nether generation.
                break;
            case 1:
                /// End generation.
                break;
        }
    }

    /// Called for each chunk to run the feature generation. Block position is given.
    public static void run(World world, Random random, int x, int z) {
        for (WorldFeature feature : WorldGenerator.features) {
            feature.generate(world, random, x, z);
        }

        //** Testing zone
        x += random.nextInt(16); // Starting x.
        z += random.nextInt(16); // Starting z.
        int y = 63; // Sea level.
        while (!world.isAirBlock(x, y, z)) {
            y++; // y will be the first air block at or above starting height.
            //*/
        }
    }

    /// Builds a glass pillar from the given location up to layer 127.
    public static void mark(World world, int x, int y, int z) {
        _DeadlyWorld.console("Marking! " + x + "," + z);
        while (y < 127) {
            world.setBlock(x, ++y, z, Blocks.glass, 0, 2);
        }
    }

    /// Gets the total weight for an array of world feature items.
    public static int getTotalWeight(WorldFeatureItem... items) {
        int totalWeight = 0;
        for (WorldFeatureItem item : items) {
            totalWeight += item.getWeight();
        }
        return totalWeight;
    }

    /// Gets the value of each given property as an int and returns them in order.
    public static int[] getWeights(String category, String... properties) {
        int length = properties.length;
        int[] weights = new int[length];
        for (int i = length; i-- > 0;) {
            weights[i] = Properties.getInt(category, properties[i].toLowerCase());
        }
        return weights;
    }

    /// Returns the sum of all ints in the array.
    public static int getTotalWeight(int... weights) {
        int weight = 0;
        for (int i : weights) {
            weight += i;
        }
        return weight;
    }

    /// Chooses one of the given items at random.
    public static WorldFeatureItem choose(Random random, int totalWeight, WorldFeatureItem... items) {
        if (totalWeight > 0) {
            totalWeight = random.nextInt(totalWeight);
            for (WorldFeatureItem item : items)
                if ( (totalWeight -= item.getWeight()) < 0)
                    return item;
        }
        return items[0];
    }

    public static String choose(Random random, int totalWeight, String[] items, int[] weights) {
        if (totalWeight > 0) {
            totalWeight = random.nextInt(totalWeight);
            int length = items.length;
            for (int i = length; i-- > 0;)
                if ( (totalWeight -= weights[i]) < 0)
                    return items[i];
        }
        return items[0];
    }

    /// Builds the list of features that have not been disabled by the config file.
    private static WorldFeature[] buildFeaturesArray() {
        ArrayList<WorldFeature> featuresList = new ArrayList<WorldFeature>();
        double property;

        property = Properties.getDouble(Properties.DUNGEONS, "_place_attempts");
        if (property > 0.0) {
            featuresList.add(new DeadlyWorldDungeon(property));
        }
        property = Properties.getDouble(Properties.FREQUENCY, "silverfish_nest");
        if (property > 0.0) {
            featuresList.add(new SilverNest(property));
        }

        property = Properties.getDouble(Properties.FREQUENCY, "brutal_spawner");
        if (property > 0.0) {
            featuresList.add(new BrutalSpawner(property));
        }
        property = Properties.getDouble(Properties.FREQUENCY, "swarm_spawner");
        if (property > 0.0) {
            featuresList.add(new SwarmSpawner(property));
        }
        property = Properties.getDouble(Properties.FREQUENCY, "spawner");
        if (property > 0.0) {
            featuresList.add(new RogueSpawner(property));
        }

        property = Properties.getDouble(Properties.FREQUENCY, "fire_trap");
        if (property > 0.0) {
            featuresList.add(new FireTrap(property));
        }
        property = Properties.getDouble(Properties.FREQUENCY, "mine");
        if (property > 0.0) {
            featuresList.add(new Mine(property));
        }
        property = Properties.getDouble(Properties.FREQUENCY, "potion_trap");
        if (property > 0.0) {
            featuresList.add(new PotionTrap(property));
        }
        property = Properties.getDouble(Properties.FREQUENCY, "spawner_trap");
        if (property > 0.0) {
            featuresList.add(new SpawnerTrap(property));
        }

        property = Properties.getDouble(Properties.FREQUENCY, "tower");
        if (property > 0.0) {
            featuresList.add(new Tower(property));
        }
        property = Properties.getDouble(Properties.FREQUENCY, "chest");
        if (property > 0.0) {
            featuresList.add(new RogueChest(property));
            /**
             * NYI
             * property = Properties.getDouble(Properties.FREQUENCY, "cave_in");
             * if (property > 0.0)
             * featuresList.add(new CaveIn(property));
             */
        }

        property = Properties.getDouble(Properties.VEINS, "sand_count");
        if (property > 0.0) {
            featuresList.add(new VeinFeature(property, Blocks.sand, Properties.getInt(Properties.VEINS, "sand_size"), Properties.getInt(Properties.VEINS, "sand_min_height"), Properties.getInt(Properties.VEINS, "sand_max_height")));
        }
        property = Properties.getDouble(Properties.VEINS, "lava_count");
        if (property > 0.0) {
            featuresList.add(new VeinLiquid(property, Blocks.lava, Properties.getInt(Properties.VEINS, "lava_size"), Properties.getInt(Properties.VEINS, "lava_min_height"), Properties.getInt(Properties.VEINS, "lava_max_height")));
        }
        property = Properties.getDouble(Properties.VEINS, "water_count");
        if (property > 0.0) {
            featuresList.add(new VeinLiquid(property, Blocks.water, Properties.getInt(Properties.VEINS, "water_size"), Properties.getInt(Properties.VEINS, "water_min_height"), Properties.getInt(Properties.VEINS, "water_max_height")));
        }
        property = Properties.getDouble(Properties.VEINS, "spawner_count");
        if (property > 0.0) {
            featuresList.add(new VeinSpawner(property));
        }
        property = Properties.getDouble(Properties.VEINS, "silverfish_count");
        if (property > 0.0) {
            featuresList.add(new VeinFeature(property, Blocks.monster_egg, Properties.getInt(Properties.VEINS, "silverfish_size"), Properties.getInt(Properties.VEINS, "silverfish_min_height"), Properties.getInt(Properties.VEINS, "silverfish_max_height")));
        }

        property = Properties.getDouble(Properties.FREQUENCY, "boss");
        if (property > 0.0) {
            featuresList.add(new RogueBoss(property));
        }

        featuresList.trimToSize();
        _DeadlyWorld.console("Loaded " + featuresList.size() + " features!");
        return featuresList.toArray(new WorldFeature[0]);
    }
}