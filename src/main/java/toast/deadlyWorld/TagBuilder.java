package toast.deadlyWorld;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import toast.deadlyWorld.feature.SpawnerItemCreeper;
import toast.deadlyWorld.feature.SpawnerItemRandom;

public class TagBuilder {
    /// Default variables for configurable spawner stats.
    public static final int MAX_DELAY = 800;
    public static final int MIN_DELAY = 200;
    public static final int MAX_NEARBY = 6;
    public static final int PLAYER_RANGE = 16;
    public static final int SPAWN_COUNT = 4;
    public static final int SPAWN_RANGE = 4;

    /// The tile entity mob spawner tag being built.
    public NBTTagCompound spawnerTag;

    public TagBuilder(NBTTagCompound tag) {
        this.spawnerTag = tag;
    }

    /// Sets the mob type of the first spawn (or all spawns if potentials are not set).
    /// Default: Pig.
    public void setType(String type) {
        this.spawnerTag.setString("EntityId", type);
    }

    /// Sets the delay before the first spawn. Set to -1 to skip first spawn.
    public void setDelay(int delay) {
        if (delay == 20) {
            this.spawnerTag.removeTag("Delay");
        }
        else {
            this.spawnerTag.setShort("Delay", (short) delay);
        }
    }

    /// Sets min and max spawn delays.
    public void setMinDelay(int min) {
        if (min == TagBuilder.MIN_DELAY) {
            this.spawnerTag.removeTag("MinSpawnDelay");
        }
        else {
            this.spawnerTag.setShort("MinSpawnDelay", (short) min);
        }
    }

    public void setMaxDelay(int max) {
        if (max == TagBuilder.MAX_DELAY) {
            this.spawnerTag.removeTag("MaxSpawnDelay");
        }
        else {
            this.spawnerTag.setShort("MaxSpawnDelay", (short) max);
        }
    }

    public void setMinAndMaxDelay(int min, int max) {
        this.setMinDelay(min);
        this.setMaxDelay(max);
    }

    /// Sets the number of spawn attempts.
    public void setSpawnCount(int count) {
        if (count == TagBuilder.SPAWN_COUNT) {
            this.spawnerTag.removeTag("SpawnCount");
        }
        else {
            this.spawnerTag.setShort("SpawnCount", (short) count);
        }
    }

    /// Sets the max nearby entities.
    public void setMaxNearbyEntities(int max) {
        if (max == TagBuilder.MAX_NEARBY) {
            this.spawnerTag.removeTag("MaxNearbyEntities");
        }
        else {
            this.spawnerTag.setShort("MaxNearbyEntities", (short) max);
        }
    }

    /// Sets the required player radius (in blocks) to activate.
    public void setPlayerRange(int range) {
        if (range == TagBuilder.PLAYER_RANGE) {
            this.spawnerTag.removeTag("RequiredPlayerRange");
        }
        else {
            this.spawnerTag.setShort("RequiredPlayerRange", (short) range);
        }
    }

    /// Sets the spawn radius (in blocks).
    public void setSpawnRange(int range) {
        if (range == TagBuilder.SPAWN_RANGE) {
            this.spawnerTag.removeTag("SpawnRange");
        }
        else {
            this.spawnerTag.setShort("SpawnRange", (short) range);
        }
    }

    /// Sets the additional NBT data for the first mob spawned (or all, if potentials are not set).
    public void setSpawnData(NBTTagCompound data) {
        if (data == null) {
            this.spawnerTag.removeTag("SpawnData");
        }
        else {
            this.spawnerTag.setTag("SpawnData", data.copy());
        }
    }

    /// Sets the list of entities the mob spawner will choose from.
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

    /// Adjusts the spawn potentials' relative positions to absolute.
    public static NBTTagList adjustPotentials(NBTTagList potentials, double x, double y, double z) {
        int length = potentials.tagCount();
        for (int i = length; i-- > 0;) {
            NBTTagCompound tag = potentials.getCompoundTagAt(i);
            if (tag.hasKey("Properties")) {
                TagBuilder.adjustSpawnData(tag.getCompoundTag("Properties"), x, y, z);
            }
        }
        return potentials;
    }

    /// Adjusts the spawn data's relative position to absolute.
    public static NBTTagCompound adjustSpawnData(NBTTagCompound spawnData, double x, double y, double z) {
        if (spawnData.hasKey("Pos")) {
            NBTTagList pos = spawnData.getTagList("Pos", new NBTTagDouble(0.0).getId());
            spawnData.setTag("Pos", TagBuilder.doubleNBTTagList(pos.func_150309_d(0) + x, pos.func_150309_d(1) + y, pos.func_150309_d(2) + z));
        }
        return spawnData;
    }

    /// Sets the mob's health.
    public static void setHealth(NBTTagCompound tag, float health) {
        tag.setFloat("HealF", health);
    }

    /// Sets the despawning on or off.
    public static void setDespawning(NBTTagCompound tag, boolean despawn) {
        tag.setBoolean("PersistenceRequired", !despawn);
    }

    /// Sets the equipment.
    public static void setEquipment(NBTTagCompound tag, ItemStack... equipment) {
        NBTTagList tagList = new NBTTagList();
        NBTTagCompound data;
        for (int i = 0; i < 5; i++) {
            data = new NBTTagCompound();
            if (equipment[i] != null) {
                equipment[i].writeToNBT(data);
            }
            tagList.appendTag(data);
        }
        tag.setTag("Equipment", tagList);
    }

    /// Sets the equipment drop chances.
    public static void setDropChances(NBTTagCompound tag, float... chances) {
        NBTTagList tagList = new NBTTagList();
        for (int i = 0; i < 5; i++) {
            tagList.appendTag(new NBTTagFloat(chances[i]));
        }
        tag.setTag("DropChances", tagList);
    }

    /// Sets the potition of the entity.
    public static void setPosition(NBTTagCompound tag, double x, double y, double z) {
        tag.setTag("Pos", TagBuilder.doubleNBTTagList(x, y, z));
    }

    /// Sets the motion of the entity.
    public static void setMotion(NBTTagCompound tag, double x, double y, double z) {
        tag.setTag("Motion", TagBuilder.doubleNBTTagList(x, y, z));
    }

    /// Sets the acceleration of the fireball.
    public static void setFireballHeading(NBTTagCompound tag, double x, double y, double z) {
        tag.setTag("direction", TagBuilder.doubleNBTTagList(x, y, z));
    }

    /// Returns a new tag list containing the given double values.
    public static NBTTagList doubleNBTTagList(double... data) {
        NBTTagList tagList = new NBTTagList();
        int length = data.length;
        for (int i = 0; i < length; i++) {
            tagList.appendTag(new NBTTagDouble(data[i]));
        }
        return tagList;
    }

    /// Returns a tag containing a potion for a thrown potion entity. With custom effects, damage only changes the color.
    public static NBTTagCompound potion(int damage) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("Potion", new ItemStack(Items.potionitem, 1, damage).writeToNBT(new NBTTagCompound()));
        return tag;
    }

    public static NBTTagCompound potion(ItemStack potion) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("Potion", potion.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    /// Adds a custom potion effect to the item stack and returns it for ease of use.
    public static ItemStack addPotionEffect(ItemStack itemStack, Potion potion, int duration, int amplifier) {
        if (itemStack.stackTagCompound == null) {
            itemStack.stackTagCompound = new NBTTagCompound();
        }
        if (!itemStack.stackTagCompound.hasKey("CustomPotionEffects")) {
            itemStack.stackTagCompound.setTag("CustomPotionEffects", new NBTTagList());
        }
        NBTTagCompound tag = new NBTTagCompound();
        if (potion.id != 0) {
            tag.setByte("Id", (byte) potion.id);
        }
        if (duration != 0) {
            tag.setInteger("Duration", duration);
        }
        if (amplifier != 0) {
            tag.setByte("Amplifier", (byte) amplifier);
        }
        itemStack.stackTagCompound.getTagList("CustomPotionEffects", tag.getId()).appendTag(tag);
        return itemStack;
    }

    /// Adds a potion effect to the mob's tag.
    public static void addPotionEffect(NBTTagCompound tag, Potion potion, int amplifier) {
        TagBuilder.addPotionEffect(tag, potion, Integer.MAX_VALUE, amplifier, false);
    }

    public static void addPotionEffect(NBTTagCompound tag, Potion potion, int amplifier, boolean ambient) {
        TagBuilder.addPotionEffect(tag, potion, Integer.MAX_VALUE, amplifier, ambient);
    }

    public static void addPotionEffect(NBTTagCompound tag, Potion potion, int duration, int amplifier) {
        TagBuilder.addPotionEffect(tag, potion, duration, amplifier, false);
    }

    public static void addPotionEffect(NBTTagCompound tag, Potion potion, int duration, int amplifier, boolean ambient) {
        if (!tag.hasKey("ActiveEffects")) {
            tag.setTag("ActiveEffects", new NBTTagList());
        }
        NBTTagCompound potionTag = new NBTTagCompound();
        if (potion.id != 0) {
            potionTag.setByte("Id", (byte) potion.id);
        }
        if (duration != 0) {
            potionTag.setInteger("Duration", duration);
        }
        if (amplifier != 0) {
            potionTag.setByte("Amplifier", (byte) amplifier);
        }
        if (ambient) {
            potionTag.setBoolean("Ambient", ambient);
        }
        tag.getTagList("ActiveEffects", potionTag.getId()).appendTag(potionTag);
    }

    /// Returns a tag containing the fuse time for TNT.
    public static NBTTagCompound fuse(int fuse) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Fuse", (byte) fuse);
        return tag;
    }

    /// Returns a tag containing a falling block.
    public static NBTTagCompound fallingBlock(Block block, int damage, int time) {
        return TagBuilder.fallingBlock(block, damage, time, false, 2, 40, false, (NBTTagCompound) null);
    }

    public static NBTTagCompound fallingBlock(Block block, int damage, int time, float fallDamage) {
        return TagBuilder.fallingBlock(block, damage, time, true, fallDamage, 40, false, (NBTTagCompound) null);
    }

    public static NBTTagCompound fallingBlock(Block block, int damage, int time, boolean hurtEntities, float fallDamage, int maxFallDamage, boolean dropItem, NBTTagCompound tileData) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte("Tile", (byte) Block.getIdFromBlock(block));
        tag.setInteger("TileID", Block.getIdFromBlock(block));
        tag.setByte("Data", (byte) damage);
        tag.setByte("Time", (byte) time);
        tag.setBoolean("DropItem", dropItem);
        tag.setBoolean("HurtEntities", hurtEntities);
        tag.setFloat("FallHurtAmount", fallDamage);
        tag.setInteger("FallHurtMax", maxFallDamage);
        if (tileData != null) {
            tag.setTag("TileEntityData", tileData);
        }
        return tag;
    }

    /// Makes your standard mob spawner.
    public static NBTTagCompound createMobSpawner(String type) {
        return TagBuilder.createMobSpawner("", type);
    }

    public static NBTTagCompound createMobSpawner(String name, String type) {
        TagBuilder tag = new TagBuilder(new NBTTagCompound());
        tag.setType(type);
        return tag.spawnerTag;
    }

    /// Makes your standard creeper spawner.
    public static NBTTagCompound createMobSpawnerCreeper(String name) {
        TagBuilder tag = new TagBuilder(new NBTTagCompound());
        tag.setType("Creeper");
        if (SpawnerItemCreeper.CHARGED_CHANCE > 0 && SpawnerItemCreeper.CHARGED_CHANCE < 100) {
            String[] types = { "Creeper", "Creeper" };
            int[] weights = { 100 - SpawnerItemCreeper.CHARGED_CHANCE, SpawnerItemCreeper.CHARGED_CHANCE };
            NBTTagCompound[] properties = { new NBTTagCompound(), (NBTTagCompound) SpawnerItemCreeper.CHARGED_TAG.copy() };
            tag.setPotentials(types, weights, properties);
        }
        return tag.spawnerTag;
    }

    /// Makes a randomized mob spawner.
    public static NBTTagCompound createMobSpawnerRandom(String name) {
        TagBuilder tag = new TagBuilder(new NBTTagCompound());
        boolean weightCheck = false;
        int length = SpawnerItemRandom.weights.length;
        NBTTagCompound[] properties = new NBTTagCompound[length];
        for (int i = length; i-- > 0;) {
            properties[i] = new NBTTagCompound();
            if (_DeadlyWorld.MOBS[i] == "Skeleton") {
                TagBuilder.setEquipment(properties[i], new ItemStack(Items.bow), null, null, null, null);
            }
            if (!weightCheck && SpawnerItemRandom.weights[i] > 0) {
                weightCheck = true;
            }
        }
        if (weightCheck) {
            tag.setPotentials(_DeadlyWorld.MOBS, SpawnerItemRandom.weights, properties);
        }
        else {
            tag.setType("Zombie");
        }
        return tag.spawnerTag;
    }

    /// Makes your standard mob spawner for traps.
    public static NBTTagCompound createMobSpawnerTrap(String type) {
        TagBuilder tag = new TagBuilder(new NBTTagCompound());
        if (type.equalsIgnoreCase("RANDOM")) {
            tag.spawnerTag = TagBuilder.createMobSpawnerRandom("spawner_trap");
        }
        else if (type.equalsIgnoreCase("CREEPER")) {
            tag.spawnerTag = TagBuilder.createMobSpawnerCreeper("spawner_trap");
        }
        else {
            tag.spawnerTag = TagBuilder.createMobSpawner("spawner_trap", type);
        }

        NBTTagCompound data = tag.spawnerTag.getCompoundTag("SpawnData");
        if (!tag.spawnerTag.hasKey("SpawnData")) {
            tag.spawnerTag.setTag("SpawnData", data);
            if (tag.spawnerTag.getString("EntityId") == "Skeleton") {
                TagBuilder.setEquipment(data, new ItemStack(Items.bow), null, null, null, null);
            }
        }
        data = TagBuilder.applyTNTHat(tag.spawnerTag.getString("EntityId"), data);

        if (tag.spawnerTag.hasKey("SpawnPotentials")) {
            NBTTagList potentials = tag.spawnerTag.getTagList("SpawnPotentials", new NBTTagCompound().getId());
            int length = potentials.tagCount();
            for (int i = length; i-- > 0;) {
                NBTTagCompound tagEntry = potentials.getCompoundTagAt(i);
                if (tagEntry.hasKey("Properties")) {
                    NBTTagCompound potentialData = tagEntry.getCompoundTag("Properties");
                    potentialData = TagBuilder.applyTNTHat(tagEntry.getString("Type"), potentialData);
                    TagBuilder.setPosition(potentialData, 0.5, 1.0, 0.5);
                    tagEntry.setTag("Properties", potentialData);
                }
                tagEntry.setString("Type", "PrimedTnt");
            }
        }

        TagBuilder.setPosition(data, 0.5, 1.0, 0.5);
        tag.setType("PrimedTnt");
        tag.setDelay(0);
        tag.setSpawnRange(0);
        tag.setPlayerRange(4);
        tag.setSpawnCount(1);
        tag.setSpawnData(data);
        return tag.spawnerTag;
    }

    /// Converts a standard entity tag into one of tnt riding the entity and returns the new tag.
    private static NBTTagCompound applyTNTHat(String id, NBTTagCompound tag) {
        NBTTagCompound tntTag = TagBuilder.fuse(80);
        tag.setString("id", id);
        TagBuilder.setMotion(tag, 0.0, 0.3, 0.0);
        TagBuilder.addPotionEffect(tag, Potion.moveSpeed, 1);
        TagBuilder.addPotionEffect(tag, Potion.resistance, -6);
        tntTag.setTag("Riding", tag);
        return tntTag;
    }

    /// Makes your standard arrow shooter.
    public static NBTTagCompound createArrowSpawner(boolean onFire) {
        TagBuilder tag = new TagBuilder(new NBTTagCompound());
        String[] types = new String[48];
        int[] weights = new int[48];
        NBTTagCompound[] properties = new NBTTagCompound[48];
        NBTTagCompound baseTag = new NBTTagCompound();
        baseTag.setByte("pickup", (byte) 2);
        baseTag.setDouble("damage", Math.max(2.0, Properties.getDouble(Properties.TOWERS, "_arrow_damage")));
        if (onFire) {
            baseTag.setShort("Fire", (short) 2000);
        }
        double step = 2.0 * Math.PI / 16;
        double rotation = step / 2.0;
        double elevation = -0.15;
        double xMotion, zMotion, xOffset, zOffset;
        for (int i = 48; i-- > 0;) {
            types[i] = "Arrow";
            weights[i] = 1;
            properties[i] = (NBTTagCompound) baseTag.copy();
            rotation += step;
            xMotion = MathHelper.sin((float) rotation);
            zMotion = MathHelper.cos((float) rotation);
            if (Math.abs(xMotion) < Math.abs(zMotion)) {
                xOffset = xMotion * 0.6 + 0.5;
                zOffset = zMotion < 0.0 ? -0.1 : 1.1;
            }
            else if (Math.abs(xMotion) > Math.abs(zMotion)) {
                xOffset = xMotion < 0.0 ? -0.1 : 1.1;
                zOffset = zMotion * 0.6 + 0.5;
            }
            else {
                xOffset = xMotion < 0.0 ? -0.1 : 1.1;
                zOffset = zMotion < 0.0 ? -0.1 : 1.1;
            }
            TagBuilder.setPosition(properties[i], xOffset, elevation + 0.35, zOffset);
            TagBuilder.setMotion(properties[i], xMotion, elevation, zMotion);
            if (i % 16 == 0) {
                rotation += step / 2.0;
                elevation += 0.15;
            }
        }
        tag.setType("Arrow");
        tag.setDelay(-1);
        tag.setMinAndMaxDelay(4, 8);
        tag.setSpawnCount(1);
        tag.setSpawnRange(-1);
        tag.setPlayerRange(8);
        tag.setSpawnData(baseTag);
        tag.setPotentials(types, weights, properties);
        return tag.spawnerTag;
    }

    /// Makes your standard fireball shooter.
    /* NYI - Fireballs cannot be spawned yet.
     * public static NBTTagCompound createFireballSpawner() {
     * TagBuilder tag = new TagBuilder(new NBTTagCompound());
     * String[] types = new String[48];
     * int[] weights = new int[48];
     * NBTTagCompound[] properties = new NBTTagCompound[48];
     * NBTTagCompound baseTag = new NBTTagCompound();
     * double step = 2.0 * Math.PI / (double)16;
     * double rotation = step / 2.0;
     * double elevation = -0.02;
     * double xMotion, zMotion, xOffset, zOffset;
     * for (int i = 48; i-- > 0;) {
     * types[i] = "SmallFireball";
     * weights[i] = 1;
     * properties[i] = (NBTTagCompound)baseTag.copy();
     * rotation += step;
     * xMotion = (double)MathHelper.sin((float)rotation) * 0.02;
     * zMotion = (double)MathHelper.cos((float)rotation) * 0.02;
     * if (Math.abs(xMotion) < Math.abs(zMotion)) {
     * xOffset = xMotion * 0.6 + 0.5;
     * zOffset = zMotion < 0.0 ? -0.1 : 1.1;
     * }
     * else if (Math.abs(xMotion) > Math.abs(zMotion)) {
     * xOffset = xMotion < 0.0 ? -0.1 : 1.1;
     * zOffset = zMotion * 0.6 + 0.5;
     * }
     * else {
     * xOffset = xMotion < 0.0 ? -0.1 : 1.1;
     * zOffset = zMotion < 0.0 ? -0.1 : 1.1;
     * }
     * setPosition(properties[i], xOffset, 0.5, zOffset);
     * setFireballHeading(properties[i], xMotion, elevation, zMotion);
     * if (i % 16 == 0) {
     * rotation += step / 2.0;
     * elevation += 0.02;
     * }
     * }
     * tag.setType("SmallFireball");
     * tag.setDelay(-1);
     * tag.setMinAndMaxDelay(6, 10);
     * tag.setSpawnCount(1);
     * tag.setSpawnRange(-1);
     * tag.setPlayerRange(10);
     * tag.setSpawnData(baseTag);
     * tag.setPotentials(types, weights, properties);
     * return tag.spawnerTag;
     * }
     */

    /// Makes your standard fire trap.
    public static NBTTagCompound createFireSpawner() {
        TagBuilder tag = new TagBuilder(new NBTTagCompound());
        String[] types = new String[48];
        int[] weights = new int[48];
        NBTTagCompound[] properties = new NBTTagCompound[48];
        NBTTagCompound baseTag = TagBuilder.fallingBlock(Blocks.fire, 0, 1, 1.0F);
        baseTag.setShort("Fire", (short) 2000);
        double step = 2.0 * Math.PI / 16;
        double rotation = step / 2.0;
        double elevation = 0.1;
        double xMotion, zMotion;
        for (int i = 48; i-- > 0;) {
            types[i] = "FallingSand";
            weights[i] = 1;
            properties[i] = (NBTTagCompound) baseTag.copy();
            rotation += step;
            xMotion = MathHelper.sin((float) rotation) * 0.2;
            zMotion = MathHelper.cos((float) rotation) * 0.2;
            TagBuilder.setPosition(properties[i], 0.5, 1.5, 0.5);
            TagBuilder.setMotion(properties[i], xMotion, elevation, zMotion);
            if (i % 16 == 0) {
                rotation += step / 2.0;
                elevation += 0.25;
            }
        }
        tag.setType("FallingSand");
        tag.setMinAndMaxDelay(2, 4);
        tag.setSpawnCount(1);
        tag.setSpawnRange(-1);
        tag.setPlayerRange(5);
        tag.setSpawnData(baseTag);
        tag.setPotentials(types, weights, properties);
        return tag.spawnerTag;
    }

    /// Makes your standard potion trap.
    public static NBTTagCompound createPotionSpawner(String type) {
        TagBuilder tag = new TagBuilder(new NBTTagCompound());
        NBTTagCompound data;
        if (type == "harm") {
            int potency = Math.max(0, Properties.getInt(Properties.POTION_TRAPS, "_harm_potency"));
            ItemStack itemStack = new ItemStack(Items.potionitem, 1, 16396);
            TagBuilder.addPotionEffect(itemStack, Potion.harm, 0, potency);
            data = TagBuilder.potion(itemStack);
        }
        else if (type == "poison") {
            int duration = Math.max(1, Properties.getInt(Properties.POTION_TRAPS, "_poison_duration"));
            int potency = Math.max(0, Properties.getInt(Properties.POTION_TRAPS, "_poison_potency"));
            ItemStack itemStack = new ItemStack(Items.potionitem, 1, 16388);
            TagBuilder.addPotionEffect(itemStack, Potion.poison, duration, potency);
            data = TagBuilder.potion(itemStack);
        }
        else if (type == "daze") {
            int duration = Math.max(1, Properties.getInt(Properties.POTION_TRAPS, "_daze_duration"));
            int potency = Math.max(0, Properties.getInt(Properties.POTION_TRAPS, "_daze_potency"));
            ItemStack itemStack = new ItemStack(Items.potionitem, 1, 16394);
            TagBuilder.addPotionEffect(TagBuilder.addPotionEffect(TagBuilder.addPotionEffect(TagBuilder.addPotionEffect(TagBuilder.addPotionEffect(itemStack, Potion.weakness, duration, potency), Potion.digSlowdown, duration, potency), Potion.moveSlowdown, duration >> 1, potency), Potion.blindness, duration >> 2, 0), Potion.nightVision, duration >> 4, 0);
            data = TagBuilder.potion(itemStack);
        }
        else
            return null;
        TagBuilder.setPosition(data, 0.5, 1.1, 0.5);
        TagBuilder.setMotion(data, 0.0, 0.35, 0.0);
        tag.setType("ThrownPotion");
        tag.setMinAndMaxDelay(30, 50);
        tag.setSpawnRange(-1);
        tag.setPlayerRange(3);
        tag.setSpawnCount(1);
        tag.setSpawnData(data);
        return tag.spawnerTag;
    }

    /// Makes your standard proximity bomb.
    public static NBTTagCompound createTNTSpawner() {
        TagBuilder tag = new TagBuilder(new NBTTagCompound());
        String[] types = { "PrimedTnt" };
        int[] weights = { 1 };
        NBTTagCompound[] properties = { TagBuilder.fuse(50) };
        TagBuilder.setMotion(properties[0], 0.0, 0.4, 0.0);
        NBTTagCompound data = TagBuilder.fuse(50);
        TagBuilder.setPosition(data, 0.5, 0.5, 0.5);
        tag.setType("PrimedTnt");
        tag.setDelay(0);
        tag.setMinAndMaxDelay(5, 10);
        tag.setSpawnRange(2);
        tag.setPlayerRange(4);
        tag.setSpawnCount(1);
        tag.setMaxNearbyEntities(3);
        tag.setSpawnData(data);
        tag.setPotentials(types, weights, properties);
        return tag.spawnerTag;
    }

    /// Makes your standard proximity bomb.
    /// NYI, this won't work as-is.
    public static NBTTagCompound createCaveInSpawner(String type) {
        TagBuilder tag = new TagBuilder(new NBTTagCompound());
        String[] types = { "FallingSand", "Fireball" };
        int[] weights = { 24, 1 };
        NBTTagCompound[] properties = new NBTTagCompound[2];
        if (type == "normal") {
            properties[0] = TagBuilder.fallingBlock(Blocks.cobblestone, 0, 1, 1.5F);
        }
        else if (type == "silverfish") {
            properties[0] = TagBuilder.fallingBlock(Blocks.monster_egg, 1, 1, 1.5F);
        }
        else if (type == "gravel") {
            properties[0] = TagBuilder.fallingBlock(Blocks.gravel, 0, 1, 1.0F);
        }
        else
            return null;
        properties[1] = new NBTTagCompound();
        properties[1].setInteger("ExplosionPower", 2);
        properties[1].setTag("direction", TagBuilder.doubleNBTTagList(0.0, -1.0, 0.0));
        TagBuilder.setPosition(properties[1], 0.5, 0.5, 0.5);
        tag.setType("FallingSand");
        tag.setDelay(50);
        tag.setMinAndMaxDelay(3, 6);
        tag.setSpawnRange(9);
        tag.setPlayerRange(7);
        tag.setSpawnCount(16);
        tag.setMaxNearbyEntities(Short.MAX_VALUE);
        tag.setSpawnData(properties[0]);
        tag.setPotentials(types, weights, properties);
        return tag.spawnerTag;
    }
}