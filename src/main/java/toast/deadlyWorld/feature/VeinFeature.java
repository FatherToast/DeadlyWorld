package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class VeinFeature implements WorldFeature {
    /**
     * Vanilla Defaults
     * 
     * ore = count, size, min - max
     * 
     * dirt = 20, 32, 0 - 128
     * gravel = 10, 32, 0 - 128
     * coal = 20, 16, 0 - 128
     * iron = 20, 8, 0 - 64
     * gold = 2, 8, 0 - 32
     * redstone = 8, 7, 0 - 16
     * diamond = 1, 7, 0 - 16
     **/
    /// The number of veins to be generated. Determined by the properties file.
    public final double veinCount;
    /// The block ID to be placed.
    public final Block block;
    /// The metadata for the blocks to be placed.
    public int blockMeta = 0;
    /// The number of blocks to generate.
    public final int veinSize;
    /// The blocks to generate in.
    public Block blockReplace = Blocks.stone;
    /// The limits for vein height.
    public final int heightMin, heightMax;

    public VeinFeature(double count, Block block, int size, int min, int max) {
        if (size < 1) {
            size = 1;
        }
        if (min < 0) {
            min = 0;
        }
        else if (min > 255) {
            min = 255;
        }
        if (max < min) {
            max = min;
        }
        else if (max > 255) {
            max = 255;
        }
        this.veinCount = count;
        this.block = block;
        this.veinSize = size;
        this.heightMin = min;
        this.heightMax = max;
    }

    /// Sets the variable and returns this for ease in construction.
    public VeinFeature setMetadata(int meta) {
        this.blockMeta = meta;
        return this;
    }

    public VeinFeature setBlockReplace(Block block) {
        this.blockReplace = block;
        return this;
    }

    /// Attempts to generate this feature in the given chunk. Block position is given.
    @Override
    public void generate(World world, Random random, int x, int z) {
        for (double count = this.veinCount; count >= 1.0 || count > 0.0 && count > random.nextDouble(); count--) {
            this.place(world, random, x + random.nextInt(16), random.nextInt(this.heightMax - this.heightMin) + this.heightMin, z + random.nextInt(16));
        }
    }

    /// Returns true if this feature can be placed at the location.
    @Override
    public boolean canBePlaced(World world, Random random, int x, int y, int z) {
        return true;
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        float rotation = random.nextFloat() * (float) Math.PI;
        double xiMin = x + 8.0 + Math.sin(rotation) * this.veinSize / 8.0;
        double xiMax = x + 8.0 - Math.sin(rotation) * this.veinSize / 8.0;
        double ziMin = z + 8.0 + Math.cos(rotation) * this.veinSize / 8.0;
        double ziMax = z + 8.0 - Math.cos(rotation) * this.veinSize / 8.0;
        double yiMin = y + random.nextInt(3) - 2;
        double yiMax = y + random.nextInt(3) - 2;
        for (int i = 0; i <= this.veinSize; i++) {
            double dxi = xiMin + (xiMax - xiMin) * i / this.veinSize;
            double dyi = yiMin + (yiMax - yiMin) * i / this.veinSize;
            double dzi = ziMin + (ziMax - ziMin) * i / this.veinSize;
            double rad = (Math.sin(i * Math.PI / this.veinSize) + 1.0) * random.nextDouble() * this.veinSize / 16.0 + 1.0;
            int xMin = (int) Math.floor(dxi - rad / 2.0);
            int yMin = (int) Math.floor(dyi - rad / 2.0);
            int zMin = (int) Math.floor(dzi - rad / 2.0);
            int xMax = (int) Math.floor(dxi + rad / 2.0);
            int yMax = (int) Math.floor(dyi + rad / 2.0);
            int zMax = (int) Math.floor(dzi + rad / 2.0);
            for (x = xMin; x <= xMax; x++) {
                double dx = (x + 0.5 - dxi) / (rad / 2.0);
                if (dx * dx < 1.0) {
                    for (y = yMin; y <= yMax; y++) {
                        double dy = (y + 0.5 - dyi) / (rad / 2.0);
                        if (dx * dx + dy * dy < 1.0) {
                            for (z = zMin; z <= zMax; z++) {
                                double dz = (z + 0.5 - dzi) / (rad / 2.0);
                                Block block = world.getBlock(x, y, z);
                                if (dx * dx + dy * dy + dz * dz < 1.0 && this.canBePlaced(world, random, x, y, z) && block != null && block.isReplaceableOreGen(world, x, y, z, this.blockReplace)) {
                                    world.setBlock(x, y, z, this.block, this.blockMeta, 2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}