package fathertoast.deadlyworld.common.util;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ItemHelper {
    
    /**
     * Loads all ItemStacks in the given CompoundTag and
     * adds them to a new NonNullList merged with the given list.<br>
     * Assumes the default value of the given NonNullList is {@link ItemStack#EMPTY}<br>
     * Does not care about slot IDs.
     * <br><br>
     *
     * @param tag  The CompoundTag to read item stacks from.
     * @param list The NonNullList to merge with.
     * @return A NonNullList containing the item stacks of the given list and the
     * item stacks loaded from the given compound tag.
     */
    public static NonNullList<ItemStack> loadAllItems( CompoundTag tag, NonNullList<ItemStack> list ) {
        ListTag listTag = tag.getList( "Items", Tag.TAG_COMPOUND );
        List<ItemStack> tempList = new ArrayList<>();
        
        // Copy over non-empty item stacks
        for( ItemStack itemStack : list ) {
            if( itemStack == ItemStack.EMPTY ) {
                continue;
            }
            tempList.add( itemStack );
        }
        
        // Load items from tag and add to list
        for( int i = 0; i < listTag.size(); ++i ) {
            CompoundTag itemTag = listTag.getCompound( i );
            tempList.add( ItemStack.of( itemTag ) );
        }
        // Return new NonNullList; the loaded item stacks and provided list merged.
        return NonNullList.of( ItemStack.EMPTY, tempList.toArray( new ItemStack[0] ) );
    }
}
