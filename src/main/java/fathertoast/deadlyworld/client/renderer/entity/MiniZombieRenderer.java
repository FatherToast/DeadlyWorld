package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.entity.monster.ZombieEntity;

public class MiniZombieRenderer extends ZombieRenderer {

    public MiniZombieRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        this.shadowRadius = 0.2F;
    }

    @Override
    public void scale(ZombieEntity zombie, MatrixStack matrixStack, float partialTick) {
        matrixStack.scale(0.4F, 0.4F, 0.4F);
    }
}
