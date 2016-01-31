package toast.deadlyWorld;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;

public class SpawnBuilder {
    /// The RNG being used to generate the world.
    public Random random;
    /// The mob spawner entity being edited.
    public TileEntityMobSpawner mobSpawner;
    /// The NBT tag for the mob spawner.
    public NBTTagCompound spawnerTag;
    /// Returns true if this spawn builder should be used.
    public boolean isValid;

    public SpawnBuilder(World world, Random rand, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityMobSpawner) {
            this.random = rand;
            this.mobSpawner = (TileEntityMobSpawner) tileEntity;
            this.spawnerTag = new NBTTagCompound();
            this.mobSpawner.writeToNBT(this.spawnerTag);
            this.isValid = true;
        }
    }

    public SpawnBuilder(TileEntityMobSpawner tileEntity, Random rand) {
        this.mobSpawner = tileEntity;
        if (this.mobSpawner != null) {
            this.random = rand;
            this.spawnerTag = new NBTTagCompound();
            this.mobSpawner.writeToNBT(this.spawnerTag);
            this.isValid = true;
        }
    }

    /// Saves the NBT tag data to the mob spawner.
    public void write() {
        this.mobSpawner.readFromNBT(this.spawnerTag);
        this.mobSpawner.getWorldObj().markBlockForUpdate(this.mobSpawner.xCoord, this.mobSpawner.yCoord, this.mobSpawner.zCoord);
    }

    /// Sets the mob type of the first spawn (or all spawns if potentials are not set).
    /// Default: Pig.
    public void setType(String type) {
        this.spawnerTag.setString("EntityId", type);
    }

    /// Sets the delay before the first spawn. Set to -1 to skip first spawn.
    /// Default: 20.
    public void setDelay(int delay) {
        this.spawnerTag.setShort("Delay", (short) delay);
    }

    /// Sets min and max spawn delays.
    /// Default: 200.
    public void setMinDelay(int min) {
        this.spawnerTag.setShort("MinSpawnDelay", (short) min);
    }

    /// Default: 800.
    public void setMaxDelay(int max) {
        this.spawnerTag.setShort("MaxSpawnDelay", (short) max);
    }

    public void setMinAndMaxDelay(int min, int max) {
        this.setMinDelay(min);
        this.setMaxDelay(max);
    }

    /// Sets the number of spawn attempts.
    /// Default: 4.
    public void setSpawnCount(int count) {
        this.spawnerTag.setShort("SpawnCount", (short) count);
    }

    /// Sets the max nearby entities.
    /// Default: 6.
    public void setMaxNearbyEntities(int max) {
        this.spawnerTag.setShort("MaxNearbyEntities", (short) max);
    }

    /// Sets the required player radius (in blocks) to activate.
    /// Default: 16.
    public void setPlayerRange(int range) {
        this.spawnerTag.setShort("RequiredPlayerRange", (short) range);
    }

    /// Sets the spawn radius (in blocks).
    /// Default: 4.
    public void setSpawnRange(int range) {
        this.spawnerTag.setShort("SpawnRange", (short) range);
    }

    /// Sets the additional NBT data for the first mob spawned (or all, if potentials are not set).
    /// Default: null.
    public void setSpawnData(NBTTagCompound data) {
        if (data == null) {
            this.spawnerTag.removeTag("SpawnData");
            return;
        }
        this.spawnerTag.setTag("SpawnData", data.copy());
    }

    /// Sets the list of entities the mob spawner will choose from.
    /// Default: null.
    public void setPotentials(String[] types, int[] weights, NBTTagCompound[] properties) {
        if (types == null) {
            this.spawnerTag.removeTag("SpawnPotentials");
            return;
        }
        int length = types.length;
        if (length == 0 || length != weights.length || length != properties.length) {
            this.spawnerTag.removeTag("SpawnPotentials");
            return;
        }
        NBTTagList tagList = new NBTTagList();
        NBTTagCompound entry;
        for (int i = 0; i < length; i++) {
            entry = new NBTTagCompound();
            entry.setString("Type", types[i]);
            entry.setInteger("Weight", weights[i]);
            entry.setTag("Properties", properties[i].copy());
            tagList.appendTag(entry);
        }
        this.spawnerTag.setTag("SpawnPotentials", tagList);
    }
}