package fathertoast.deadlyworld.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.deadlyworld.common.entity.MiniSpawnerMimic;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MiniSpawnerMimicModel extends EntityModel<MiniSpawnerMimic> {
    
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart head;
    
    public MiniSpawnerMimicModel( ModelPart root ) {
        rightLeg = root.getChild( "rightLeg" );
        leftLeg = root.getChild( "leftLeg" );
        head = root.getChild( "head" );
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        
        partDefinition.addOrReplaceChild( "rightLeg", CubeListBuilder.create().texOffs( 0, 0 )
                        .addBox( -0.5F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( -2.5F, 18.0F, 0.5F ) );
        
        partDefinition.addOrReplaceChild( "leftLeg", CubeListBuilder.create().texOffs( 0, 0 )
                        .addBox( -0.5F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( 2.5F, 18.0F, 0.5F ) );
        
        partDefinition.addOrReplaceChild( "head", CubeListBuilder.create().texOffs( 0, 0 )
                        .addBox( -4.0F, -13.0F, -4.0F, 8.0F, 8.0F, 8.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        
        return LayerDefinition.create( meshDefinition, 64, 64 );
    }
    
    @Override
    public void setupAnim( MiniSpawnerMimic miniSpawnerMimic, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
        rightLeg.xRot = Mth.cos( limbSwing * 0.6662F ) * 1.4F * limbSwingAmount;
        leftLeg.xRot = Mth.cos( limbSwing * 0.6662F + (float) Math.PI ) * 1.4F * limbSwingAmount;
    }
    
    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
                                float red, float green, float blue, float alpha ) {
        
        rightLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        leftLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        head.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
    }
}
