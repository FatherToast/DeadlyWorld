package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fathertoast.deadlyworld.common.entity.MimicEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

// Made with Blockbench 4.1.3
@OnlyIn( Dist.CLIENT )
@ParametersAreNonnullByDefault
public class MimicModel<T extends Entity> extends EntityModel<T> {
    
    private final ModelRenderer bottom;
    private final ModelRenderer teethBottom;
    private final ModelRenderer base;
    
    private final ModelRenderer top;
    private final ModelRenderer teethTop;
    private final ModelRenderer nose;
    
    private final ModelRenderer legs;
    
    private final ModelRenderer legLFace;
    private final ModelRenderer legLFront;
    private final ModelRenderer legLMid;
    private final ModelRenderer legLBack;
    private final ModelRenderer legLButt;
    
    private final ModelRenderer legRFace;
    private final ModelRenderer legRFront;
    private final ModelRenderer legRMid;
    private final ModelRenderer legRBack;
    private final ModelRenderer legRButt;
    
    public MimicModel() {
        texWidth = 64;
        texHeight = 64;
        
        // Bottom parts
        bottom = new ModelRenderer( this );
        bottom.setPos( 0.0F, 20.0F, 0.0F );
        
        teethBottom = new ModelRenderer( this );
        teethBottom.setPos( 0.0F, 1.0F, 0.0F );
        bottom.addChild( teethBottom );
        setRotationAngle( teethBottom, -1.5708F, 0.0F, 0.0F );
        teethBottom.texOffs( 0, 43 ).addBox( -6.0F, -6.0F, -11.0F, 12.0F, 12.0F, 1.0F, 0.0F, false );
        
        base = new ModelRenderer( this );
        base.setPos( 0.0F, 1.0F, 0.0F );
        bottom.addChild( base );
        setRotationAngle( base, 3.1416F, 0.0F, 0.0F );
        base.texOffs( 0, 19 ).addBox( -7.0F, 0.0F, -7.0F, 14.0F, 10.0F, 14.0F, 0.0F, false );
        
        // Top parts
        top = new ModelRenderer( this );
        top.setPos( 0.0F, 12.0F, 7.0F );
        
        teethTop = new ModelRenderer( this );
        teethTop.setPos( 0.0F, 0.0F, 0.0F );
        top.addChild( teethTop );
        setRotationAngle( teethTop, -1.5708F, 0.0F, 0.0F );
        teethTop.texOffs( 38, 43 ).addBox( -6.0F, 1.0F, 0.0F, 12.0F, 12.0F, 1.0F, 0.0F, false );
        
        nose = new ModelRenderer( this );
        nose.setPos( 0.0F, 0.0F, 0.0F );
        top.addChild( nose );
        setRotationAngle( nose, 3.1416F, 0.0F, 0.0F );
        nose.texOffs( 0, 0 ).addBox( -1.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F, 0.0F, false );
        nose.texOffs( 0, 0 ).addBox( -7.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F, false );
        
        // Leg parts
        legs = new ModelRenderer( this );
        legs.setPos( 0.0F, 24.0F, 0.0F );
        
        legLButt = new ModelRenderer( this );
        legLButt.setPos( 3.0F, -3.0F, 6.0F );
        legs.addChild( legLButt );
        legLButt.texOffs( 0, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        legRButt = new ModelRenderer( this );
        legRButt.setPos( -3.0F, -3.0F, 6.0F );
        legs.addChild( legRButt );
        legRButt.texOffs( 2, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        legLFace = new ModelRenderer( this );
        legLFace.setPos( 3.0F, -3.0F, -6.0F );
        legs.addChild( legLFace );
        legLFace.texOffs( 2, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        legRFront = new ModelRenderer( this );
        legRFront.setPos( -6.0F, -3.0F, -4.0F );
        legs.addChild( legRFront );
        legRFront.texOffs( 2, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        legLBack = new ModelRenderer( this );
        legLBack.setPos( 6.0F, -3.0F, 4.0F );
        legs.addChild( legLBack );
        legLBack.texOffs( 2, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        legLMid = new ModelRenderer( this );
        legLMid.setPos( 6.0F, -3.0F, 0.0F );
        legs.addChild( legLMid );
        legLMid.texOffs( 0, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        legLFront = new ModelRenderer( this );
        legLFront.setPos( 6.0F, -3.0F, -4.0F );
        legs.addChild( legLFront );
        legLFront.texOffs( 1, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        legRBack = new ModelRenderer( this );
        legRBack.setPos( -6.0F, -3.0F, 4.0F );
        legs.addChild( legRBack );
        legRBack.texOffs( 1, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        legRFace = new ModelRenderer( this );
        legRFace.setPos( -3.0F, -3.0F, -6.0F );
        legs.addChild( legRFace );
        legRFace.texOffs( 0, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        legRMid = new ModelRenderer( this );
        legRMid.setPos( -6.0F, -3.0F, 0.0F );
        legs.addChild( legRMid );
        legRMid.texOffs( 0, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, 0.0F, false );
        
        resetAllAngles();
    }
    
    @Override
    public void setupAnim( T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
        resetAllAngles();
        
        // Lid bob
        top.xRot -= MathHelper.sin( ageInTicks * 0.067F ) * 0.11F + 0.11F;
        
        // Lid animation
        
        // Walk animation
        final float swingPhaseL = limbSwing * 0.6662F;
        final float swingPhaseR = swingPhaseL + 3.1415927F;
        final float swingX = limbSwingAmount * 0.6F;
        final float swingY = limbSwingAmount * 0.3F;
        
        rotateAxialLegForWalking( legLFace, 4, swingPhaseL, swingX, swingY );
        rotateSideLegForWalking( legLFront, 3, swingPhaseL, swingX, swingY );
        rotateSideLegForWalking( legLMid, 2, swingPhaseL, swingX, swingY );
        rotateSideLegForWalking( legLBack, 1, swingPhaseL, swingX, swingY );
        rotateAxialLegForWalking( legLButt, 0, swingPhaseL, swingX, swingY );
        
        rotateAxialLegForWalking( legRFace, 4, swingPhaseR, swingX, swingY );
        rotateSideLegForWalking( legRFront, 3, swingPhaseR, swingX, swingY );
        rotateSideLegForWalking( legRMid, 2, swingPhaseR, swingX, swingY );
        rotateSideLegForWalking( legRBack, 1, swingPhaseR, swingX, swingY );
        rotateAxialLegForWalking( legRButt, 0, swingPhaseR, swingX, swingY );
    }
    
    private void rotateAxialLegForWalking( ModelRenderer leg, int numFromButt, float phase, float x, float y ) {
        //noinspection SuspiciousNameCombination
        rotateSideLegForWalking( leg, numFromButt, phase, y, x ); // Just swap x and y magnitudes
    }
    
    private void rotateSideLegForWalking( ModelRenderer leg, int numFromButt, float phase, float x, float y ) {
        final float legPhase = phase + numFromButt * 0.4F;
        leg.xRot += MathHelper.sin( legPhase ) * x;
        leg.yRot += MathHelper.cos( legPhase ) * y;
    }
    
    @Override
    public void renderToBuffer( MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha ) {
        bottom.render( matrixStack, buffer, packedLight, packedOverlay );
        top.render( matrixStack, buffer, packedLight, packedOverlay );
        legs.render( matrixStack, buffer, packedLight, packedOverlay );
    }
    
    private void setRotationAngle( ModelRenderer modelRenderer, float x, float y, float z ) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
    
    private void resetAllAngles() {
        setRotationAngle( bottom, 0.0F, 0.0F, 0.0F );
        setRotationAngle( top, 0.0F, 0.0F, 0.0F );
        
        setRotationAngle( legLFace, -0.7854F, 0.0F, -0.3491F );
        setRotationAngle( legLFront, -0.3491F, 0.0F, -0.7854F );
        setRotationAngle( legLMid, 0.0F, 0.0F, -0.7854F );
        setRotationAngle( legLBack, 0.3491F, 0.0F, -0.7854F );
        setRotationAngle( legLButt, 0.7854F, 0.0F, -0.3491F );
        
        setRotationAngle( legRFace, -0.7854F, 0.0F, 0.3491F );
        setRotationAngle( legRFront, -0.3491F, 0.0F, 0.7854F );
        setRotationAngle( legRMid, 0.0F, 0.0F, 0.7854F );
        setRotationAngle( legRBack, 0.3491F, 0.0F, 0.7854F );
        setRotationAngle( legRButt, 0.7854F, 0.0F, 0.3491F );
    }
}