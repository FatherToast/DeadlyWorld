package fathertoast.deadlyworld.common.item;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface ICustomTabContents {
    /** Called by the client to generate this item's creative tab contents. */
    List<ItemStack> buildTabContents();
}