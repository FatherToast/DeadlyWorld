package fathertoast.deadlyworld.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.deadlyworld.common.entity.MiniChestMimic;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class MiniChestMimicModel extends EntityModel<MiniChestMimic> {
    
    private final ModelPart base;
    private final ModelPart lid;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    
    public MiniChestMimicModel( ModelPart root ) {
        base = root.getChild( "base" );
        lid = root.getChild( "lid" );
        leftLeg = root.getChild( "leftLeg" );
        rightLeg = root.getChild( "rightLeg" );
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        
        partDefinition.addOrReplaceChild( "base", CubeListBuilder.create()
                .texOffs( 0, 0 )
                .addBox( -3.0F, -7.0F, -3.0F, 6.0F, 4.0F, 6.0F,
                        new CubeDeformation( 0.0F ) )
                .texOffs( 0, 21 )
                .addBox( -2.0F, -8.0F, -2.0F, 4.0F, 1.0F, 4.0F,
                        new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 24.0F, 0.0F ) );
        
        partDefinition.addOrReplaceChild( "lid", CubeListBuilder.create()
                .texOffs( 0, 10 )
                .addBox( -3.0F, -2.0F, -6.0F, 6.0F, 2.0F, 6.0F,
                        new CubeDeformation( 0.0F ) )
                .texOffs( 0, 18 )
                .addBox( -1.0F, -1.0F, -7.0F, 2.0F, 2.0F, 1.0F,
                        new CubeDeformation( 0.0F ) ), PartPose.offset( 0.0F, 17.0F, 3.0F ) );
        
        partDefinition.addOrReplaceChild( "leftLeg", CubeListBuilder.create()
                        .texOffs( 0, 0 )
                        .addBox( -1.0F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( -1.0F, 20.0F, 0.0F ) );
        
        partDefinition.addOrReplaceChild( "rightLeg", CubeListBuilder.create()
                        .texOffs( 0, 0 )
                        .addBox( 0.0F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( 1.0F, 20.0F, 0.0F ) );
        
        return LayerDefinition.create( meshDefinition, 32, 32 );
    }
    
    @Override
    public void setupAnim( MiniChestMimic entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
        lid.xRot = (Mth.cos( limbSwing * 0.5F ) * (limbSwingAmount * 0.3F)) - 0.25F;
        
        rightLeg.xRot = Mth.cos( limbSwing * 0.6662F ) * 1.4F * limbSwingAmount;
        leftLeg.xRot = Mth.cos( limbSwing * 0.6662F + (float) Math.PI ) * 1.4F * limbSwingAmount;
    }
    
    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha ) {
        base.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        lid.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        leftLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        rightLeg.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
    }
}
