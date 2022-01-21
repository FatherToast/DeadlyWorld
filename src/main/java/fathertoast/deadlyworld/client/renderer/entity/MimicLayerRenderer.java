package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fathertoast.deadlyworld.common.entity.MimicEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;

public class MimicLayerRenderer<T extends MimicEntity> extends LayerRenderer<T, MimicModel<T>> {

    private static final RenderType CHEST = RenderType.entityCutout(new ResourceLocation("textures/entity/chest/normal.png"));

    public MimicLayerRenderer(IEntityRenderer<T, MimicModel<T>> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T mimic, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        IVertexBuilder vertexBuilder = buffer.getBuffer(CHEST);
        this.getParentModel().renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
