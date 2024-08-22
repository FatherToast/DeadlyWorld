package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.GhastRenderer;
import net.minecraft.entity.monster.GhastEntity;

public class MicroGhastRenderer extends GhastRenderer {

    public MicroGhastRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        shadowRadius = 0.0F;
    }

    protected void scale( GhastEntity ghast, MatrixStack matrixStack, float partialTick ) {
        matrixStack.scale(0.1F, 0.1F, 0.1F);
    }
}
