package toast.deadlyWorld.feature;

import java.util.Collection;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld.SpawnBuilder;

public class SpawnerItemCreeper extends SpawnerItem {
    /// The percent chance that a creeper should spawn charged.
    public static final int CHARGED_CHANCE = Math.max(0, Math.min(100, Properties.getInt(Properties.GENERAL, "charged_creeper_chance")));
    /// The tag used for charged creepers.
    public static final NBTTagCompound CHARGED_TAG = new NBTTagCompound();
    static {
        SpawnerItemCreeper.CHARGED_TAG.setBoolean("powered", true);
    }

    public SpawnerItemCreeper(NBTTagCompound tag, int wt, boolean rel, boolean brute) {
        super(tag, wt, rel, brute);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        world.setBlock(x, y, z, Blocks.mob_spawner, 0, 2);
        SpawnBuilder spawner = new SpawnBuilder(world, random, x, y, z);
        if (spawner.isValid) {
            this.copySpawnTag(this.itemTag, spawner.spawnerTag, x, y, z);
            if (random.nextInt(100) < SpawnerItemCreeper.CHARGED_CHANCE) {
                if (spawner.spawnerTag.hasKey("SpawnData")) {
                    NBTTagCompound data = spawner.spawnerTag.getCompoundTag("SpawnData");
                    for (String name : (Collection<String>) SpawnerItemCreeper.CHARGED_TAG.func_150296_c()) {
                        data.setTag(name, SpawnerItemCreeper.CHARGED_TAG.getTag(name).copy());
                    }
                }
                else {
                    spawner.setSpawnData((NBTTagCompound) SpawnerItemCreeper.CHARGED_TAG.copy());
                }
            }
            spawner.write();
        }
        world.markBlockForUpdate(x, y, z);
    }
}