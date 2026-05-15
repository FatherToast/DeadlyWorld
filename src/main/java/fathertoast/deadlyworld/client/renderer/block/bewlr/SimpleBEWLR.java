package fathertoast.deadlyworld.client.renderer.block.bewlr;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Supplier;

/**
 * A simple BEWLR implementation that takes a block entity supplier.
 * The supplier's result is cached after the first call and used
 * to render the block entity renderer's model.
 */
public class SimpleBEWLR extends BlockEntityWithoutLevelRenderer {
    
    private final Supplier<BlockEntity> blockEntitySupplier;
    
    public SimpleBEWLR( BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet, Supplier<BlockEntity> blockEntitySupplier ) {
        super( renderDispatcher, modelSet );
        this.blockEntitySupplier = Suppliers.memoize( blockEntitySupplier::get );
    }
    
    @Override
    public void renderByItem( ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlayTexture ) {
        blockEntityRenderDispatcher.renderItem( blockEntitySupplier.get(), poseStack, bufferSource, packedLight, overlayTexture );
    }
}
