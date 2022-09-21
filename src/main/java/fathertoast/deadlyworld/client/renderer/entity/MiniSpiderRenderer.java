package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.deadlyworld.common.entity.MiniSpiderEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpiderRenderer;

public class MiniSpiderRenderer extends SpiderRenderer<MiniSpiderEntity> {

    public MiniSpiderRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        this.shadowRadius = 0.3F;
    }

    @Override
    public void scale(MiniSpiderEntity spider, MatrixStack matrixStack, float partialTick) {
        matrixStack.scale(0.4F, 0.4F, 0.4F);
    }
}
