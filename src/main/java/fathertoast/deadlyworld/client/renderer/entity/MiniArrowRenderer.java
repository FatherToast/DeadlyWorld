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
    public void render(T miniArrow, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
        matrixStack.pushPose();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(MathHelper.lerp(p_225623_3_, miniArrow.yRotO, miniArrow.yRot) - 90.0F));
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(MathHelper.lerp(p_225623_3_, miniArrow.xRotO, miniArrow.xRot)));

        float lvt_17_1_ = (float)miniArrow.shakeTime - p_225623_3_;

        if (lvt_17_1_ > 0.0F) {
            float lvt_18_1_ = -MathHelper.sin(lvt_17_1_ * 3.0F) * lvt_17_1_;
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(lvt_18_1_));
        }
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(45.0F));
        matrixStack.scale(0.02F, 0.02F, 0.02F);
        matrixStack.translate(-4.0D, 0.0D, 0.0D);

        IVertexBuilder lvt_18_2_ = p_225623_5_.getBuffer(RenderType.entityCutout(this.getTextureLocation(miniArrow)));
        MatrixStack.Entry lvt_19_1_ = matrixStack.last();
        Matrix4f lvt_20_1_ = lvt_19_1_.pose();
        Matrix3f lvt_21_1_ = lvt_19_1_.normal();
        this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, p_225623_6_);
        this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, p_225623_6_);
        this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, p_225623_6_);
        this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, p_225623_6_);
        this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, p_225623_6_);
        this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, p_225623_6_);
        this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, p_225623_6_);
        this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, p_225623_6_);

        for(int lvt_22_1_ = 0; lvt_22_1_ < 4; ++lvt_22_1_) {
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
            this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, p_225623_6_);
            this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, p_225623_6_);
            this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, p_225623_6_);
            this.vertex(lvt_20_1_, lvt_21_1_, lvt_18_2_, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, p_225623_6_);
        }

        matrixStack.popPose();
        super.render(miniArrow, p_225623_2_, p_225623_3_, matrixStack, p_225623_5_, p_225623_6_);
    }

    public void vertex(Matrix4f matrix4f, Matrix3f matrix3f, IVertexBuilder vertexBuilder, int p_229039_4_, int p_229039_5_, int p_229039_6_, float p_229039_7_, float p_229039_8_, int p_229039_9_, int p_229039_10_, int p_229039_11_, int p_229039_12_) {
        vertexBuilder.vertex(matrix4f, (float)p_229039_4_, (float)p_229039_5_, (float)p_229039_6_)
                .color(255, 255, 255, 255)
                .uv(p_229039_7_, p_229039_8_).overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(p_229039_12_)
                .normal(matrix3f, (float)p_229039_9_, (float)p_229039_11_, (float)p_229039_10_)
                .endVertex();
    }
}
