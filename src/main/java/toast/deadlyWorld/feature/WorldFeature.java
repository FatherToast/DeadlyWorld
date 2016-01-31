package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.world.World;

public interface WorldFeature {
    /// Attempts to generate this feature in the given chunk. Block position is given.
    public void generate(World world, Random random, int x, int z);

    /// Returns true if this feature can be placed at the location.
    public boolean canBePlaced(World world, Random random, int x, int y, int z);

    /// Places this feature at the location.
    public void place(World world, Random random, int x, int y, int z);
}