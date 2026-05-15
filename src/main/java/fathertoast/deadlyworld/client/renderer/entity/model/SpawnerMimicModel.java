package fathertoast.deadlyworld.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class SpawnerMimicModel<T extends SpawnerMimic> extends EntityModel<T> {
    
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    
    public SpawnerMimicModel( ModelPart root ) {
        head = root.getChild( "head" );
        rightHindLeg = root.getChild( "rightHindLeg" );
        leftHindLeg = root.getChild( "leftHindLeg" );
        rightFrontLeg = root.getChild( "rightFrontLeg" );
        leftFrontLeg = root.getChild( "leftFrontLeg" );
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        
        partDefinition.addOrReplaceChild( "head", CubeListBuilder.create().texOffs( 0, 0 )
                        .addBox( -8.0F, -16.0F, -8.0F, 16.0F, 16.0F, 16.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( 0.0F, 15.0F, 0.0F ) );
        
        partDefinition.addOrReplaceChild( "rightHindLeg", CubeListBuilder.create().texOffs( 0, 0 )
                        .addBox( -1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( -6.0F, 14.0F, 6.0F ) );
        
        partDefinition.addOrReplaceChild( "leftHindLeg", CubeListBuilder.create().texOffs( 0, 0 )
                        .addBox( -1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( 6.0F, 14.0F, 6.0F ) );
        
        partDefinition.addOrReplaceChild( "rightFrontLeg", CubeListBuilder.create().texOffs( 0, 0 )
                        .addBox( -1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( -6.0F, 14.0F, -6.0F ) );
        
        partDefinition.addOrReplaceChild( "leftFrontLeg", CubeListBuilder.create().texOffs( 0, 0 )
                        .addBox( -1.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( 6.0F, 14.0F, -6.0F ) );
        
        return LayerDefinition.create( meshDefinition, 64, 128 );
    }
    
    @Override
    public void setupAnim( SpawnerMimic spawnerMimic, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
        rightHindLeg.xRot = Mth.cos( limbSwing * 0.6662F ) * 1.4F * limbSwingAmount;
        leftHindLeg.xRot = Mth.cos( limbSwing * 0.6662F + (float) Math.PI ) * 1.4F * limbSwingAmount;
        rightFrontLeg.xRot = Mth.cos( limbSwing * 0.6662F + (float) Math.PI ) * 1.4F * limbSwingAmount;
        leftFrontLeg.xRot = Mth.cos( limbSwing * 0.6662F ) * 1.4F * limbSwingAmount;
    }
    
    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                                float red, float green, float blue, float alpha ) {
        
        head.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        rightHindLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        leftHindLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        rightFrontLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        leftFrontLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
    }
}
