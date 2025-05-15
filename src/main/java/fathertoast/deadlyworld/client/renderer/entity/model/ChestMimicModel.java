package fathertoast.deadlyworld.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.deadlyworld.common.entity.ChestMimic;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class ChestMimicModel extends EntityModel<ChestMimic> {

    private final ModelPart bottom;
    private final ModelPart top;
    private final ModelPart bb_main;

    public ChestMimicModel( ModelPart root ) {
        bottom = root.getChild( "bottom" );
        top = root.getChild( "top" );
        bb_main = root.getChild( "bb_main" );
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bottom = partdefinition.addOrReplaceChild("bottom", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition teethBottom_r1 = bottom.addOrReplaceChild("teethBottom_r1", CubeListBuilder.create().texOffs(0, 43).addBox(-6.0F, -6.0F, -11.0F, 12.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition base_r1 = bottom.addOrReplaceChild("base_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-7.0F, 0.0F, -7.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition top = partdefinition.addOrReplaceChild("top", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 7.0F));

        PartDefinition teethTop_r1 = top.addOrReplaceChild("teethTop_r1", CubeListBuilder.create().texOffs(38, 43).addBox(-6.0F, 1.0F, 0.0F, 12.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition nose_r1 = top.addOrReplaceChild("nose_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-7.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition legLButt_r1 = bb_main.addOrReplaceChild("legLButt_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -3.0F, 6.0F, 0.7854F, 0.0F, -0.3491F));

        PartDefinition legRButt_r1 = bb_main.addOrReplaceChild("legRButt_r1", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -3.0F, 6.0F, 0.7854F, 0.0F, 0.3491F));

        PartDefinition legLFace_r1 = bb_main.addOrReplaceChild("legLFace_r1", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -3.0F, -6.0F, -0.7854F, 0.0F, -0.3491F));

        PartDefinition legRFront_r1 = bb_main.addOrReplaceChild("legRFront_r1", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -3.0F, -4.0F, -0.3491F, 0.0F, 0.7854F));

        PartDefinition legLBack_r1 = bb_main.addOrReplaceChild("legLBack_r1", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -3.0F, 4.0F, 0.3491F, 0.0F, -0.7854F));

        PartDefinition legLMid_r1 = bb_main.addOrReplaceChild("legLMid_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition legLFront_r1 = bb_main.addOrReplaceChild("legLFront_r1", CubeListBuilder.create().texOffs(1, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.0F, -3.0F, -4.0F, -0.3491F, 0.0F, -0.7854F));

        PartDefinition legRBack_r1 = bb_main.addOrReplaceChild("legRBack_r1", CubeListBuilder.create().texOffs(1, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -3.0F, 4.0F, 0.3491F, 0.0F, 0.7854F));

        PartDefinition legRFace_r1 = bb_main.addOrReplaceChild("legRFace_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -3.0F, -6.0F, -0.7854F, 0.0F, 0.3491F));

        PartDefinition legRMid_r1 = bb_main.addOrReplaceChild("legRMid_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim( ChestMimic entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {

    }

    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha ) {
        bottom.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        top.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        bb_main.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
    }
}
