package fathertoast.deadlyworld.common.util;

import fathertoast.deadlyworld.common.core.registry.DWItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

import java.util.HashMap;
import java.util.Map;

/** Helper class for registering custom dispenser behaviors. */
public final class DWDispenserBehavior {


    public static void register() {
        DispenserBlock.registerBehavior( DWItems.RUNNY_LAVA_BUCKET.get(), fluidBucketBehavior() );

        DispenserBlock.registerBehavior( Items.FISHING_ROD, fishingRodBehavior() );
    }

    private static DispenseItemBehavior fluidBucketBehavior() {
        return new DefaultDispenseItemBehavior() {
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
    }

    private static DispenseItemBehavior fishingRodBehavior() {
        return new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute( BlockSource source, ItemStack itemStack ) {
                /*
                FishingRodItem rod = (FishingRodItem) itemStack.getItem();
                DispenserBlockEntity dispenser = source.getEntity();
                DispenserPlayerWrapper wrapper = DispenserWrapperHandler.getOrCreateForPos( dispenser, itemStack );

                if ( wrapper == null ) return defaultBehavior.dispense( source, itemStack );

                rod.use( source.getLevel(), wrapper, InteractionHand.MAIN_HAND );

                return itemStack;

                 */
                return itemStack;
            }
        };
    }
}
