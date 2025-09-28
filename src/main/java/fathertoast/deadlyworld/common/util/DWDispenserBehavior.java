package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.registry.DWItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

/** Helper class for registering custom dispenser behaviors. */
public class DWDispenserBehavior {

    public static void register() {
        DispenseItemBehavior dispenserFluidFromBucket = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute( BlockSource source, ItemStack itemStack ) {
                DispensibleContainerItem dci = (DispensibleContainerItem) itemStack.getItem();
                BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
                Level level = source.getLevel();

                if ( dci.emptyContents( null, level, blockpos, null, itemStack ) ) {
                    dci.checkExtraContent( null, level, itemStack, blockpos );
                    return new ItemStack( Items.BUCKET );
                }
                else {
                    return defaultBehavior.dispense( source, itemStack );
                }
            }
        };

        DispenserBlock.registerBehavior( DWItems.RUNNY_LAVA_BUCKET.get(), dispenserFluidFromBucket );
    }
}
