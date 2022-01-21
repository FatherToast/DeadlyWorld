package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.entity.monster.AbstractSkeletonEntity;

public class MiniSkeletonRenderer extends SkeletonRenderer {

    public MiniSkeletonRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        this.shadowRadius = 0.2F;
    }

    @Override
    public void scale(AbstractSkeletonEntity skeleton, MatrixStack matrixStack, float partialTick) {
        matrixStack.scale(0.4F, 0.4F, 0.4F);
    }
}
