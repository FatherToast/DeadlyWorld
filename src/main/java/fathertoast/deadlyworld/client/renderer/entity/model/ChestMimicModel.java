package fathertoast.deadlyworld.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.deadlyworld.common.entity.ChestMimic;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class ChestMimicModel extends EntityModel<ChestMimic> {
    
    private final ModelPart bottom;
    private final ModelPart top;
    private final ModelPart[] legs;
    private final ModelPart legLButt;
    private final ModelPart legLBack;
    private final ModelPart legLMid;
    private final ModelPart legLFront;
    private final ModelPart legLFace;
    private final ModelPart legRButt;
    private final ModelPart legRBack;
    private final ModelPart legRMid;
    private final ModelPart legRFront;
    private final ModelPart legRFace;
    
    
    public ChestMimicModel( ModelPart root ) {
        bottom = root.getChild( "bottom" );
        top = root.getChild( "top" );
        legLButt = root.getChild( "legLButt" );
        legLBack = root.getChild( "legLBack" );
        legLMid = root.getChild( "legLMid" );
        legLFront = root.getChild( "legLFront" );
        legLFace = root.getChild( "legLFace" );
        legRButt = root.getChild( "legRButt" );
        legRBack = root.getChild( "legRBack" );
        legRMid = root.getChild( "legRMid" );
        legRFront = root.getChild( "legRFront" );
        legRFace = root.getChild( "legRFace" );
        
        legs = new ModelPart[] {
                legLButt, legLBack, legLMid, legLFront, legLFace,
                legRButt, legRBack, legRMid, legRFront, legRFace
        };
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        
        PartDefinition bottom = partDefinition.addOrReplaceChild( "bottom", CubeListBuilder.create(), PartPose.offset( 0.0F, 20.0F, 0.0F ) );
        bottom.addOrReplaceChild( "teethBottom_r1", CubeListBuilder.create().texOffs( 0, 43 ).addBox( -6.0F, -6.0F, -11.0F, 12.0F, 12.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 1.0F, 0.0F, -1.5708F, 0.0F, 0.0F ) );
        bottom.addOrReplaceChild( "base_r1", CubeListBuilder.create().texOffs( 0, 19 ).addBox( -7.0F, 0.0F, -7.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 1.0F, 0.0F, 3.1416F, 0.0F, 0.0F ) );
        
        PartDefinition top = partDefinition.addOrReplaceChild( "top", CubeListBuilder.create(), PartPose.offsetAndRotation( 0.0F, 12.0F, 7.0F, -0.1745F, 0.0F, 0.0F ) );
        top.addOrReplaceChild( "teethTop_r1", CubeListBuilder.create().texOffs( 38, 43 ).addBox( -6.0F, 1.0F, 0.0F, 12.0F, 12.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F ) );
        top.addOrReplaceChild( "nose_r1", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -1.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) )
                .texOffs( 0, 0 ).addBox( -7.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F ) );
        
        PartDefinition legRButt = partDefinition.addOrReplaceChild( "legRButt", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legRButt.addOrReplaceChild( "legRButt_r1", CubeListBuilder.create().texOffs( 2, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -3.0F, -3.0F, 6.0F, 0.7854F, 0.0F, 0.3491F ) );
        
        PartDefinition legRBack = partDefinition.addOrReplaceChild( "legRBack", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legRBack.addOrReplaceChild( "legRBack_r1", CubeListBuilder.create().texOffs( 1, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -6.0F, -3.0F, 4.0F, 0.3491F, 0.0F, 0.7854F ) );
        
        PartDefinition legRMid = partDefinition.addOrReplaceChild( "legRMid", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legRMid.addOrReplaceChild( "legRMid_r1", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -6.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.7854F ) );
        
        PartDefinition legRFront = partDefinition.addOrReplaceChild( "legRFront", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legRFront.addOrReplaceChild( "legRFront_r1", CubeListBuilder.create().texOffs( 2, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -6.0F, -3.0F, -4.0F, -0.3491F, 0.0F, 0.7854F ) );
        
        PartDefinition legRFace = partDefinition.addOrReplaceChild( "legRFace", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legRFace.addOrReplaceChild( "legRFace_r1", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( -3.0F, -3.0F, -6.0F, -0.7854F, 0.0F, 0.3491F ) );
        
        PartDefinition legLButt = partDefinition.addOrReplaceChild( "legLButt", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legLButt.addOrReplaceChild( "legLButt_r1", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 3.0F, -3.0F, 6.0F, 0.7854F, 0.0F, -0.3491F ) );
        
        PartDefinition legLBack = partDefinition.addOrReplaceChild( "legLBack", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legLBack.addOrReplaceChild( "legLBack_r1", CubeListBuilder.create().texOffs( 2, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 6.0F, -3.0F, 4.0F, 0.3491F, 0.0F, -0.7854F ) );
        
        PartDefinition legLMid = partDefinition.addOrReplaceChild( "legLMid", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legLMid.addOrReplaceChild( "legLMid_r1", CubeListBuilder.create().texOffs( 0, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 6.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.7854F ) );
        
        PartDefinition legLFront = partDefinition.addOrReplaceChild( "legLFront", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legLFront.addOrReplaceChild( "legLFront_r1", CubeListBuilder.create().texOffs( 1, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 6.0F, -3.0F, -4.0F, -0.3491F, 0.0F, -0.7854F ) );
        
        PartDefinition legLFace = partDefinition.addOrReplaceChild( "legLFace", CubeListBuilder.create(), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        legLFace.addOrReplaceChild( "legLFace_r1", CubeListBuilder.create().texOffs( 2, 0 ).addBox( -0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation( 0.0F ) ), PartPose.offsetAndRotation( 3.0F, -3.0F, -6.0F, -0.7854F, 0.0F, -0.3491F ) );
        
        return LayerDefinition.create( meshDefinition, 64, 64 );
    }
    
    // TODO - Low priority, but maybe shine up animation a bit in the future.
    @Override
    public void setupAnim( ChestMimic chestMimic, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
        //top.xRot = Mth.sin( (ageInTicks * 0.1F) + ( Mth.cos( limbSwing * 0.6662F ) * limbSwingAmount ) ) - 0.25F;
        
        //top.xRot = ( 0.1F * idleOscillate + Mth.cos( limbSwing * 0.6662F ) * limbSwingAmount ) - 0.25F;
        top.xRot = (Mth.cos( limbSwing * 0.5F ) * (limbSwingAmount * 0.3F)) - 0.25F;
        
        final float buttYRot = -(Mth.cos( limbSwing * 0.6662F * 2.0F + 0.0F ) * 0.4F) * limbSwingAmount;
        final float backYRot = -(Mth.cos( limbSwing * 0.6662F * 2.0F + (float) Math.PI ) * 0.4F) * limbSwingAmount;
        final float midYRot = -(Mth.cos( limbSwing * 0.6662F * 2.0F + ((float) Math.PI / 2F) ) * 0.4F) * limbSwingAmount;
        final float frontYRot = -(Mth.cos( limbSwing * 0.6662F * 2.0F + ((float) Math.PI * 1.5F) ) * 0.4F) * limbSwingAmount;
        final float buttZRot = Math.abs( Mth.sin( limbSwing * 0.6662F + 0.0F ) * 0.4F ) * limbSwingAmount;
        final float backZRot = Math.abs( Mth.sin( limbSwing * 0.6662F + (float) Math.PI ) * 0.4F ) * limbSwingAmount;
        final float midZRot = Math.abs( Mth.sin( limbSwing * 0.6662F + ((float) Math.PI / 2F) ) * 0.4F ) * limbSwingAmount;
        final float frontZRot = Math.abs( Mth.sin( limbSwing * 0.6662F + ((float) Math.PI * 1.5F) ) * 0.4F ) * limbSwingAmount;
        
        legLButt.yRot = buttYRot;
        legRButt.yRot = buttYRot;
        legLBack.yRot = backYRot;
        legRBack.yRot = backYRot;
        legLMid.yRot = midYRot;
        legRMid.yRot = midYRot;
        legLFront.yRot = frontYRot;
        legRFront.yRot = frontYRot;
        legLFace.yRot = buttYRot;
        legRFace.yRot = buttYRot;
        legLButt.zRot = buttZRot;
        legRButt.zRot = buttZRot;
        legLBack.zRot = backZRot;
        legRBack.zRot = backZRot;
        legLMid.zRot = midZRot;
        legRMid.zRot = midZRot;
        legLFront.zRot = frontZRot;
        legRFront.zRot = frontZRot;
        legLFace.zRot = buttZRot;
        legRFace.zRot = buttZRot;
    }
    
    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha ) {
        bottom.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        top.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        
        for( ModelPart leg : legs )
            leg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
    }
}
