package fathertoast.deadlyworld.common.item;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class RunnyLavaBucketItem extends BucketItem {
    
    public RunnyLavaBucketItem( Supplier<? extends Fluid> supplier, Properties builder ) {
        super( supplier, builder );
    }
    
    @Override
    public int getBurnTime( ItemStack itemStack, @Nullable RecipeType<?> recipeType ) {
        return 20000;
    }
}
