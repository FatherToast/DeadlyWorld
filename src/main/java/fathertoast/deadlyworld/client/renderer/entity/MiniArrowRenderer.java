package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fathertoast.deadlyworld.common.entity.MiniArrow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Modified copy-paste of {@link net.minecraft.client.renderer.entity.ArrowRenderer} combined with {@link TippableArrowRenderer}.
 */
@OnlyIn( Dist.CLIENT )
public class MiniArrowRenderer extends EntityRenderer<MiniArrow> {
    
    public MiniArrowRenderer( EntityRendererProvider.Context renderContext ) { super( renderContext ); }
    
    @Override
    public ResourceLocation getTextureLocation( MiniArrow miniArrow ) {
        return miniArrow.getColor() > 0 ? TippableArrowRenderer.TIPPED_ARROW_LOCATION : TippableArrowRenderer.NORMAL_ARROW_LOCATION;
    }
    
    @Override
    public void render( MiniArrow arrow, float rotation, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight ) {
        stack.pushPose();
        stack.mulPose( Axis.YP.rotationDegrees( Mth.lerp( partialTick, arrow.yRotO, arrow.getYRot() ) - 90.0F ) );
        stack.mulPose( Axis.ZP.rotationDegrees( Mth.lerp( partialTick, arrow.xRotO, arrow.getXRot() ) ) );
        
        float f9 = (float) arrow.shakeTime - partialTick;
        if( f9 > 0.0F ) {
            float f10 = -Mth.sin( f9 * 3.0F ) * f9;
            stack.mulPose( Axis.ZP.rotationDegrees( f10 ) );
        }
        
        stack.mulPose( Axis.XP.rotationDegrees( 45.0F ) );
        stack.scale( 0.05625F * 0.4F, 0.05625F * 0.4F, 0.05625F * 0.4F );
        stack.translate( -4.0F, 0.0F, 0.0F );
        VertexConsumer vertexConsumer = buffer.getBuffer( RenderType.entityCutout( getTextureLocation( arrow ) ) );
        PoseStack.Pose posestack$pose = stack.last();
        Matrix4f pose = posestack$pose.pose();
        Matrix3f normal = posestack$pose.normal();
        vertex( pose, normal, vertexConsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight );
        vertex( pose, normal, vertexConsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight );
        vertex( pose, normal, vertexConsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight );
        vertex( pose, normal, vertexConsumer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight );
        vertex( pose, normal, vertexConsumer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight );
        vertex( pose, normal, vertexConsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight );
        vertex( pose, normal, vertexConsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight );
        vertex( pose, normal, vertexConsumer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight );
        
        for( int j = 0; j < 4; j++ ) {
            stack.mulPose( Axis.XP.rotationDegrees( 90.0F ) );
            vertex( pose, normal, vertexConsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight );
            vertex( pose, normal, vertexConsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight );
            vertex( pose, normal, vertexConsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight );
            vertex( pose, normal, vertexConsumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight );
        }
        
        stack.popPose();
        super.render( arrow, rotation, partialTick, stack, buffer, packedLight );
    }
    
    protected void vertex( Matrix4f pose, Matrix3f normal, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int xNormal, int zNormal, @SuppressWarnings( "SameParameterValue" ) int yNormal, int uv2 ) {
        vertexConsumer.vertex( pose, (float) x, (float) y, (float) z )
                .color( 255, 255, 255, 255 ).uv( u, v )
                .overlayCoords( OverlayTexture.NO_OVERLAY ).uv2( uv2 )
                .normal( normal, (float) xNormal, (float) yNormal, (float) zNormal ).endVertex();
    }
}