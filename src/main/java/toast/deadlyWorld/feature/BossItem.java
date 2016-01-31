package toast.deadlyWorld.feature;

import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import toast.deadlyWorld.AttributeHelper;
import toast.deadlyWorld.NameHelper;
import toast.deadlyWorld.Properties;
import toast.deadlyWorld.TagBuilder;

public class BossItem implements WorldFeatureItem {
    /// Array of all generally beneficial potions for mobs.
    public static final Potion[] potions = { Potion.moveSpeed, Potion.damageBoost, Potion.resistance, Potion.fireResistance, Potion.waterBreathing, Potion.field_76434_w /** health boost */
    , Potion.field_76444_x /** absorption */
    };
    /// Array of all useful enchantments for each equipment type.
    public static final Enchantment[][] enchantments = {
		{ /** bow */ Enchantment.looting, Enchantment.unbreaking, Enchantment.power, Enchantment.punch, Enchantment.flame, Enchantment.infinity },
		{ /** tool */ Enchantment.sharpness, Enchantment.smite, Enchantment.baneOfArthropods, Enchantment.knockback, Enchantment.fireAspect, Enchantment.looting, Enchantment.efficiency, Enchantment.silkTouch, Enchantment.unbreaking, Enchantment.fortune },
		{ /** armor */ Enchantment.protection, Enchantment.fireProtection, Enchantment.featherFalling, Enchantment.blastProtection, Enchantment.projectileProtection, Enchantment.respiration, Enchantment.aquaAffinity, Enchantment.thorns, Enchantment.unbreaking }
	};
    /// Mob stats.
    public static final int REGEN = Properties.getInt(Properties.BOSSES, "regeneration");
    public static final int RESISTANCE = Properties.getInt(Properties.BOSSES, "resistance");
    public static final boolean FIRE_RESISTANCE = Properties.getBoolean(Properties.BOSSES, "fire_resistance");
    public static final boolean WATER_BREATHING = Properties.getBoolean(Properties.BOSSES, "water_breathing");
    public static final double HEALTH_MULT = Properties.getDouble(Properties.BOSSES, "health_multiplier");
    public static final double KNOCK_RESIST = Properties.getDouble(Properties.BOSSES, "knockback_resistance");
    public static final double SPEED_MULT = Properties.getDouble(Properties.BOSSES, "speed_multiplier");
    public static final double ATTACK_BONUS = Properties.getDouble(Properties.BOSSES, "damage_bonus");
    /// Mob equipment stats.
    public static final double LEVEL_UP = Properties.getDouble(Properties.BOSSES, "level_up_chance");
    public static final double EQUIP_CHANCE = Properties.getDouble(Properties.BOSSES, "equip_chance");
    public static final double ENCHANT_CHANCE = Properties.getDouble(Properties.BOSSES, "enchantment_chance");
    public static final double EFFECT_CHANCE = Properties.getDouble(Properties.BOSSES, "effect_chance");

    /// The entity class for this item.
    public final Class entityClass;
    /// The weight of this item.
    private final int weight;
    /// The entity's dimensions for placement. -1 is 1x2m (standard), 0 is 1x1m, 1 is 2x1m.
    public final byte type;

    public BossItem(String name, int wt) {
        this.entityClass = (Class) EntityList.stringToClassMapping.get(name);
        this.weight = wt;
        this.type = (byte) (this.entityClass.equals(EntitySpider.class) ? 1 : this.entityClass.equals(EntityCaveSpider.class) ? 0 : -1);
    }

    /// Places this feature at the location.
    @Override
    public void place(World world, Random random, int x, int y, int z) {
        EntityLiving entity = null;
        try {
            entity = (EntityLiving) this.entityClass.getConstructor(new Class[] { World.class }).newInstance(new Object[] { world });
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        if (entity != null) {
            this.initBoss(random, entity);
            entity.setPositionAndRotation(x + (this.type == 1 ? 1.0 : 0.5), y, z + (this.type == 1 ? 1.0 : 0.5), random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntityInWorld(entity);
        }
    }

    /// Returns the weight of this item.
    @Override
    public int getWeight() {
        return this.weight;
    }

    /// Initializes a boss.
    public void initBoss(Random random, EntityLiving entity) {
        if (BossItem.REGEN >= 0) {
            NBTTagCompound tag = new NBTTagCompound();
            entity.writeToNBT(tag);
            TagBuilder.addPotionEffect(tag, Potion.regeneration, BossItem.REGEN, true);
            entity.readFromNBT(tag);
        }
        if (BossItem.RESISTANCE >= 0) {
            entity.addPotionEffect(new PotionEffect(Potion.resistance.id, Integer.MAX_VALUE, BossItem.RESISTANCE, true));
        }
        if (BossItem.FIRE_RESISTANCE) {
            entity.addPotionEffect(new PotionEffect(Potion.fireResistance.id, Integer.MAX_VALUE, 0, true));
        }
        if (BossItem.WATER_BREATHING) {
            entity.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, Integer.MAX_VALUE, 0, true));
        }

        AttributeHelper.shift(entity, SharedMonsterAttributes.attackDamage, "DW|BossDamageBonus", BossItem.ATTACK_BONUS);
        AttributeHelper.baseMult(entity, SharedMonsterAttributes.maxHealth, "DW|BossHealthMult", BossItem.HEALTH_MULT - 1.0);
        AttributeHelper.max(entity, SharedMonsterAttributes.knockbackResistance, "DW|BossKnockbackResist", BossItem.KNOCK_RESIST);
        AttributeHelper.baseMult(entity, SharedMonsterAttributes.movementSpeed, "DW|BossSpeedMult", BossItem.SPEED_MULT - 1.0);
        entity.setHealth(entity.getMaxHealth());

        String name = NameHelper.setEntityName(random, entity);
        entity.func_110163_bv(); /// sets the entity to not be despawned

        int level = 0;
        for (int i = 3; i-- > 0;)
            if (random.nextDouble() < BossItem.LEVEL_UP) {
                level++;
            }
        ItemStack[] equipment = new ItemStack[5];
        switch (level) {
            case 1:
                equipment = new ItemStack[] { new ItemStack(Items.golden_sword), new ItemStack(Items.golden_boots), new ItemStack(Items.golden_leggings), new ItemStack(Items.golden_chestplate), new ItemStack(Items.golden_helmet) };
                break;
            case 2:
                equipment = new ItemStack[] { new ItemStack(Items.iron_sword), new ItemStack(Items.iron_boots), new ItemStack(Items.iron_leggings), new ItemStack(Items.iron_chestplate), new ItemStack(Items.iron_helmet) };
                break;
            case 3:
                equipment = new ItemStack[] { new ItemStack(Items.diamond_sword), new ItemStack(Items.diamond_boots), new ItemStack(Items.diamond_leggings), new ItemStack(Items.diamond_chestplate), new ItemStack(Items.diamond_helmet) };
                break;
            default:
                equipment = new ItemStack[] { new ItemStack(Items.stone_sword), new ItemStack(Items.chainmail_boots), new ItemStack(Items.chainmail_leggings), new ItemStack(Items.chainmail_chestplate), new ItemStack(Items.chainmail_helmet) };
        }
        if (random.nextInt(2) == 0) {
            ItemStack[] tools;
            switch (level) {
                case 1:
                    tools = new ItemStack[] { new ItemStack(Items.golden_axe), new ItemStack(Items.golden_pickaxe), new ItemStack(Items.golden_shovel) };
                    break;
                case 2:
                    tools = new ItemStack[] { new ItemStack(Items.iron_axe), new ItemStack(Items.iron_pickaxe), new ItemStack(Items.iron_shovel) };
                    break;
                case 3:
                    tools = new ItemStack[] { new ItemStack(Items.diamond_axe), new ItemStack(Items.diamond_pickaxe), new ItemStack(Items.diamond_shovel) };
                    break;
                default:
                    tools = new ItemStack[] { new ItemStack(Items.stone_axe), new ItemStack(Items.stone_pickaxe), new ItemStack(Items.stone_shovel) };
            }
            equipment[0] = tools[random.nextInt(tools.length)];
        }
        if (entity instanceof EntitySkeleton) {
            equipment[0] = new ItemStack(Items.bow);
        }

        int slot = random.nextInt(2) == 0 ? random.nextInt(4) + 1 : 0;
        entity.setEquipmentDropChance(slot, 2.0F);
        int type = equipment[slot].getItem() instanceof ItemArmor ? 2 : equipment[slot].getItem() instanceof ItemBow ? 0 : 1;
        Enchantment enchantment = BossItem.enchantments[type][random.nextInt(BossItem.enchantments[type].length)];
        NameHelper.setItemName(random, equipment[slot], name, enchantment);
        EnchantmentHelper.addRandomEnchantment(random, equipment[slot], 30);
        if (!equipment[slot].stackTagCompound.hasKey("ench")) {
            equipment[slot].stackTagCompound.setTag("ench", new NBTTagList());
        }
        NBTTagList enchList = equipment[slot].getEnchantmentTagList();
        NBTTagCompound enchTag;
        override: {
            for (int i = enchList.tagCount(); i-- > 0;) {
                enchTag = enchList.getCompoundTagAt(i);
                if (enchTag.getShort("id") == enchantment.effectId) {
                    enchTag.setShort("lvl", (byte) enchantment.getMaxLevel());
                    break override;
                }
            }
            enchTag = new NBTTagCompound();
            enchTag.setShort("id", (short) enchantment.effectId);
            enchTag.setShort("lvl", (byte) enchantment.getMaxLevel());
            enchList.appendTag(enchTag);
        }
        entity.setCurrentItemOrArmor(slot, equipment[slot]);

        for (int i = 5; i-- > 0;) {
            if (i != slot && (random.nextDouble() < BossItem.EQUIP_CHANCE || equipment[i].getItem() instanceof ItemBow)) {
                if (random.nextDouble() < BossItem.ENCHANT_CHANCE) {
                    EnchantmentHelper.addRandomEnchantment(random, equipment[i], 5 + random.nextInt(11));
                }
                entity.setCurrentItemOrArmor(i, equipment[i]);
            }
        }

        if (random.nextDouble() < BossItem.EFFECT_CHANCE) {
            entity.addPotionEffect(new PotionEffect(BossItem.potions[random.nextInt(BossItem.potions.length)].id, Integer.MAX_VALUE, 1));
        }
    }
}