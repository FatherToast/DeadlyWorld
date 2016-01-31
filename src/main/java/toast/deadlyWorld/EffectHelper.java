package toast.deadlyWorld;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class EffectHelper {
    /// Sets the item's color. No effect on most items.
    public static void dye(ItemStack itemStack, int color) {
        if (itemStack.stackTagCompound == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        if (!itemStack.stackTagCompound.hasKey("display")) {
            itemStack.stackTagCompound.setTag("display", new NBTTagCompound());
        }
        itemStack.stackTagCompound.getCompoundTag("display").setInteger("color", color);
    }

    /// Adds a custom attribute modifier to the item stack.
    public static void addModifier(ItemStack itemStack, String attribute, double value, int operation) {
        if (itemStack.stackTagCompound == null) {
            itemStack.stackTagCompound = new NBTTagCompound();
        }
        if (!itemStack.stackTagCompound.hasKey("AttributeModifiers")) {
            itemStack.stackTagCompound.setTag("AttributeModifiers", new NBTTagList());
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("AttributeName", attribute);
        tag.setString("Name", "MobProperties|" + Integer.toString(_DeadlyWorld.random.nextInt(), Character.MAX_RADIX));
        tag.setDouble("Amount", value);
        tag.setInteger("Operation", operation);
        UUID id = UUID.randomUUID();
        tag.setLong("UUIDMost", id.getMostSignificantBits());
        tag.setLong("UUIDLeast", id.getLeastSignificantBits());
        itemStack.stackTagCompound.getTagList("AttributeModifiers", tag.getId()).appendTag(tag);
    }
}