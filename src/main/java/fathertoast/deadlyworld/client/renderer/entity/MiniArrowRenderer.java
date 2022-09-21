package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fathertoast.deadlyworld.common.entity.MiniArrowEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

/** Modified copy-paste of {@link ArrowRenderer} */
public class MiniArrowRenderer<T extends MiniArrowEntity> extends EntityRenderer<T> {

    public static final ResourceLocation NORMAL_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/arrow.png");
    public static final ResourceLocation TIPPED_ARROW_LOCATION = new ResourceLocation("textures/entity/projectiles/tipped_arrow.png");

    public MiniArrowRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
    }

    @Override
    public ResourceLocation getTextureLocation(T miniArrow) {
        return miniArrow.getColor() > 0 ? TIPPED_ARROW_LOCATION : NORMAL_ARROW_LOCATION;
    }

    @Override
    public void render(T miniArrow, float p_225623_2_, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(partialTick, miniArrow.yRotO, miniArrow.yRot) - 90.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(partialTick, miniArrow.xRotO, miniArrow.xRot)));

        float shakeTime = (float) miniArrow.shakeTime - partialTick;

        if (shakeTime > 0.0F) {
            float rot = -MathHelper.sin(shakeTime * 3.0F) * shakeTime;
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rot));
        }
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
        matrixStack.scale(0.02F, 0.02F, 0.02F);
        matrixStack.translate(-4.0D, 0.0D, 0.0D);

        IVertexBuilder vertexBuilder = buffer.getBuffer(RenderType.entityCutout( getTextureLocation( miniArrow ) ));
        MatrixStack.Entry matrixEntry = matrixStack.last();
        Matrix4f matrix4f = matrixEntry.pose();
        Matrix3f matrix3f = matrixEntry.normal();

        vertex(matrix4f, matrix3f, vertexBuilder, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexBuilder, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexBuilder, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexBuilder, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexBuilder, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexBuilder, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexBuilder, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight);
        vertex(matrix4f, matrix3f, vertexBuilder, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight);

        for(int i = 0; i < 4; ++i) {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            vertex(matrix4f, matrix3f, vertexBuilder, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight);
            vertex(matrix4f, matrix3f, vertexBuilder, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight);
            vertex(matrix4f, matrix3f, vertexBuilder, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight);
            vertex(matrix4f, matrix3f, vertexBuilder, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight);
        }
        matrixStack.popPose();
        super.render(miniArrow, p_225623_2_, partialTick, matrixStack, buffer, packedLight);
    }

    public void vertex(Matrix4f matrix4f, Matrix3f matrix3f, IVertexBuilder vertexBuilder, int x, int y, int z, float u, float v, int xNormal, int zNormal, int yNormal, int uv2) {
        vertexBuilder.vertex(matrix4f, (float)x, (float)y, (float)z)
                .color(255, 255, 255, 255)
                .uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(uv2)
                .normal(matrix3f, (float)xNormal, (float)yNormal, (float)zNormal)
                .endVertex();
    }
}
