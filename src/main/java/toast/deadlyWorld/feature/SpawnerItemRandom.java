package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld.SpawnBuilder;
import toast.deadlyWorld._DeadlyWorld;

public class SpawnerItemRandom extends SpawnerItem {
    /// The weights for each random spawner type.
    public static final int[] weights = WorldGenerator.getWeights(Properties.RANDOM_SPAWNERS, _DeadlyWorld.MOBS);
    /// The total weight for random spawner types.
    public static final int totalWeight = WorldGenerator.getTotalWeight(SpawnerItemRandom.weights);

    public SpawnerItemRandom(NBTTagCompound tag, int wt, boolean rel, boolean brute) {
        super(tag, wt, rel, brute);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        world.setBlock(x, y, z, Blocks.mob_spawner, 0, 2);
        SpawnBuilder spawner = new SpawnBuilder(world, random, x, y, z);
        if (spawner.isValid) {
            this.copySpawnTag(this.itemTag, spawner.spawnerTag, x, y, z);
            NBTTagCompound data = this.chooseStartingPotential(random, spawner.spawnerTag);
            spawner.setType(data.getString("Type"));
            spawner.setSpawnData((NBTTagCompound) data.copy());
            spawner.write();
        }
        world.markBlockForUpdate(x, y, z);
    }

    /// Chooses a random potential to start with.
    private NBTTagCompound chooseStartingPotential(Random random, NBTTagCompound spawnerTag) {
        int choice = random.nextInt(SpawnerItemRandom.totalWeight);
        NBTTagList potentials = spawnerTag.getTagList("SpawnPotentials", new NBTTagCompound().getId());
        for (int i = SpawnerItemRandom.weights.length; i-- > 0;)
            if ( (choice -= SpawnerItemRandom.weights[i]) < 0)
                return (NBTTagCompound) potentials.getCompoundTagAt(i).copy();
        _DeadlyWorld.console("Error choosing starting mob for random spawner! " + SpawnerItemRandom.totalWeight);
        return new NBTTagCompound();
    }
}