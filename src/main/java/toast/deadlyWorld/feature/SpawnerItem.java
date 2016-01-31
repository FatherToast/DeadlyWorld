package toast.deadlyWorld.feature;

import java.util.Collection;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld.SpawnBuilder;
import toast.deadlyWorld.TagBuilder;
import toast.deadlyWorld._DeadlyWorld;

public class SpawnerItem implements WorldFeatureItem {
    /// Creates and returns an appropriate array of spawner items.
    public static SpawnerItem[] buildItems(String name) {
        int length = _DeadlyWorld.DUNGEON_MOBS.length;
        if (name == "") {
            // Do nothing
        }
        else if (name == "dungeon") {
            length = _DeadlyWorld.DUNGEON_MOBS.length;
        }
        else if (name == "spawner_brutal") {
            length = _DeadlyWorld.SPAWNERS.length;
        }
        else if (name == "spawner_rogue") {
            length = _DeadlyWorld.SPAWNERS.length;
        }
        else if (name == "spawner_swarm") {
            length = _DeadlyWorld.MOBS.length;
        }
        else if (name == "spawner_vein") {
            length = _DeadlyWorld.SPAWNERS.length;
        }
        else if (name == "spawner_trap") {
            length = _DeadlyWorld.SPAWNERS.length;
        }
        else if (name == "potion") {
            length = _DeadlyWorld.POTIONS.length;
        }

        SpawnerItem[] items = new SpawnerItem[length];
        for (int i = 0; i < length; i++) {
            items[i] = SpawnerItem.buildSpawner(name, i);
        }
        return items;
    }

    /// Creates and returns an appropriate spawner item.
    public static SpawnerItem buildSpawner(String name, int index) {
        if (name == "") {
            // Do nothing
        }
        else if (name == "dungeon")
            return SpawnerItem.buildMobSpawner(name, index, Properties.DUNGEON_SPAWNERS, _DeadlyWorld.DUNGEON_MOBS);
        else if (name == "spawner_brutal")
            return SpawnerItem.buildMobSpawner(name, index, Properties.BRUTAL_SPAWNERS, _DeadlyWorld.SPAWNERS);
        else if (name == "spawner_rogue")
            return SpawnerItem.buildMobSpawner(name, index, Properties.SPAWNERS, _DeadlyWorld.SPAWNERS);
        else if (name == "spawner_swarm")
            return SpawnerItem.buildMobSpawner(name, index, Properties.SPAWNER_SWARMS, _DeadlyWorld.MOBS);
        else if (name == "spawner_vein")
            return SpawnerItem.buildMobSpawner(name, index, Properties.SPAWNER_VEINS, _DeadlyWorld.SPAWNERS);

        else if (name == "spawner_trap") {
            NBTTagCompound tag = TagBuilder.createMobSpawnerTrap(_DeadlyWorld.SPAWNERS[index]);
            int weight = Properties.getInt(Properties.SPAWNER_TRAPS, _DeadlyWorld.SPAWNERS[index].toLowerCase());
            if (_DeadlyWorld.SPAWNERS[index].equalsIgnoreCase("RANDOM"))
                return new SpawnerItemRandom(tag, weight, true, false);
            else if (_DeadlyWorld.SPAWNERS[index].equalsIgnoreCase("CREEPER"))
                return new SpawnerItemCreeper(tag, weight, true, false);
            else
                return new SpawnerItem(tag, weight, true, false);
        }
        else if (name == "potion")
            return new SpawnerItem(TagBuilder.createPotionSpawner(_DeadlyWorld.POTIONS[index]), Properties.getInt(Properties.POTION_TRAPS, _DeadlyWorld.POTIONS[index].toLowerCase()), true, false);
        else if (name == "tnt")
            return new SpawnerItem(TagBuilder.createTNTSpawner(), 0, true, false);
        else if (name == "fire")
            return new SpawnerItem(TagBuilder.createFireSpawner(), 0, true, false);

        else if (name == "arrow")
            return new SpawnerItem(TagBuilder.createArrowSpawner(index == 1), 0, true, false);
        else if (name == "silver_nest") {
            if (index == 2) {
                TagBuilder tag = new TagBuilder(TagBuilder.createTNTSpawner());
                tag.setPlayerRange(3);
                return new SpawnerItem(tag.spawnerTag, 0, true, false);
            }
            TagBuilder tag = new TagBuilder(new NBTTagCompound());
            tag.setType("Silverfish");
            tag.setMinAndMaxDelay(Properties.getInt(Properties.NESTS, "_min_delay"), Properties.getInt(Properties.NESTS, "_max_delay"));
            tag.setSpawnCount(Properties.getInt(Properties.NESTS, "_spawn_count"));
            tag.setMaxNearbyEntities(Properties.getInt(Properties.NESTS, "_nearby_entity_cap"));
            tag.setSpawnRange(Properties.getInt(Properties.NESTS, "_spawn_range"));
            tag.setPlayerRange(Properties.getInt(Properties.NESTS, "_player_range"));
            if (index == 1) {
                NBTTagCompound data = new NBTTagCompound();
                TagBuilder.addPotionEffect(data, Potion.poison, 0);
                TagBuilder.addPotionEffect(data, Potion.regeneration, 1);
                TagBuilder.addPotionEffect(data, Potion.damageBoost, 0);
                TagBuilder.addPotionEffect(data, Potion.resistance, 2);
                tag.setSpawnData(data);
            }
            return new SpawnerItem(tag.spawnerTag, 0, false, false);
        }
        return null;
    }

    private static SpawnerItem buildMobSpawner(String name, int index, String category, String[] mobArray) {
        SpawnerItem item = null;
        int weight = Properties.getInt(category, mobArray[index].toLowerCase());
        boolean brutal = name == "spawner_brutal";
        if (mobArray[index].equalsIgnoreCase("RANDOM")) {
            item = new SpawnerItemRandom(TagBuilder.createMobSpawnerRandom(name), weight, false, brutal);
        }
        else if (mobArray[index].equalsIgnoreCase("CREEPER")) {
            item = new SpawnerItemCreeper(TagBuilder.createMobSpawnerCreeper(name), weight, false, brutal);
        }
        else {
            item = new SpawnerItem(TagBuilder.createMobSpawner(name, mobArray[index]), weight, false, brutal);
        }
        TagBuilder tag = new TagBuilder(item.itemTag);
        if (name == "spawner_vein") {
            tag.setPlayerRange(6);
            tag.setSpawnRange(10);
        }
        else {
            tag.setMinDelay(Properties.getInt(category, "_min_delay"));
            tag.setMaxDelay(Properties.getInt(category, "_max_delay"));
            tag.setSpawnCount(Properties.getInt(category, "_spawn_count"));
            tag.setMaxNearbyEntities(Properties.getInt(category, "_nearby_entity_cap"));
            tag.setSpawnRange(Properties.getInt(category, "_spawn_range"));
            tag.setPlayerRange(Properties.getInt(category, "_player_range"));
        }
        return item;
    }

    /// Writes brutal mob tags to the given NBT.
    public static NBTTagCompound applyBrutalStats(NBTTagCompound tag) {
        for (String name : (Collection<String>) BrutalSpawner.BASE_TAG.func_150296_c()) {
            tag.setTag(name, BrutalSpawner.BASE_TAG.getTag(name).copy());
        }
        return tag;
    }

    /// The spawner's tags.
    public final NBTTagCompound itemTag;
    /// The weight of this item.
    private final int weight;
    /// True if this is a relative spawner.
    public final boolean relative;
    /// True if this is a brutal spawner.
    public final boolean brutal;

    public SpawnerItem(NBTTagCompound tag, int wt, boolean rel, boolean brute) {
        this.itemTag = (NBTTagCompound) tag.copy();
        this.weight = wt;
        this.relative = rel;
        this.brutal = brute;

        if (this.brutal) {
            NBTTagCompound data = this.itemTag.getCompoundTag("SpawnData");
            if (!this.itemTag.hasKey("SpawnData")) {
                this.itemTag.setTag("SpawnData", data);
                if (this.itemTag.getString("EntityId") == "Skeleton") {
                    TagBuilder.setEquipment(data, new ItemStack(Items.bow), null, null, null, null);
                }
            }
            SpawnerItem.applyBrutalStats(data);

            if (this.itemTag.hasKey("SpawnPotentials")) {
                NBTTagList potentials = this.itemTag.getTagList("SpawnPotentials", new NBTTagCompound().getId());
                int length = potentials.tagCount();
                for (int i = length; i-- > 0;) {
                    NBTTagCompound tagEntry = potentials.getCompoundTagAt(i);
                    if (tagEntry.hasKey("Properties")) {
                        SpawnerItem.applyBrutalStats(tagEntry.getCompoundTag("Properties"));
                    }
                }
            }
        }
    }

    /// Copies spawner data from one tag to another.
    public void copySpawnTag(NBTTagCompound copyFrom, NBTTagCompound copyTo, int x, int y, int z) {
        if (this.relative) {
            this.copySpawnTagRel(copyFrom, copyTo, x, y, z);
        }
        else {
            for (String name : (Collection<String>) copyFrom.func_150296_c()) {
                copyTo.setTag(name, copyFrom.getTag(name));
            }
        }
    }

    /// Copies relative spawner data to a tag and updates its position to the given coords.
    private void copySpawnTagRel(NBTTagCompound copyFrom, NBTTagCompound copyTo, int x, int y, int z) {
        for (String name : (Collection<String>) copyFrom.func_150296_c()) {
            NBTBase tag = copyFrom.getTag(name);
            if (name.equals("SpawnData")) {
                copyTo.setTag(name, TagBuilder.adjustSpawnData((NBTTagCompound) tag.copy(), (double) x, (double) y, (double) z));
            }
            else if (name.equals("SpawnPotentials")) {
                copyTo.setTag(name, TagBuilder.adjustPotentials((NBTTagList) tag.copy(), (double) x, (double) y, (double) z));
            }
            else {
                copyTo.setTag(name, tag.copy());
            }
        }
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        world.setBlock(x, y, z, Blocks.mob_spawner, 0, 2);
        SpawnBuilder spawner = new SpawnBuilder(world, random, x, y, z);
        if (spawner.isValid) {
            this.copySpawnTag(this.itemTag, spawner.spawnerTag, x, y, z);
            spawner.write();
        }
        world.markBlockForUpdate(x, y, z);
    }

    /// Returns the weight of this item.
    @Override
    public int getWeight() {
        return this.weight;
    }
}