package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.world.World;

public interface WorldFeatureItem {
    /// Places this feature at the location.
    public void place(World world, Random random, int x, int y, int z);

    /// Returns the weight of this item.
    public int getWeight();
}