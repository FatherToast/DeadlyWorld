package fathertoast.deadlyworld.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.deadlyworld.common.entity.ChestMimic;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class ChestMimicModel extends EntityModel<ChestMimic> {

    private final ModelPart bottom;
    private final ModelPart top;
    private final ModelPart body;
    private final ModelPart[] legs = new ModelPart[10];


    public ChestMimicModel( ModelPart root ) {
        bottom = root.getChild( "bottom" );
        top = root.getChild( "top" );
        body = root.getChild( "body" );

        legs[0] = body.getChild( "legLButt_r1" );
        legs[1] = body.getChild( "legRButt_r1" );

        legs[2] = body.getChild( "legLFace_r1" );
        legs[3] = body.getChild( "legRFace_r1" );

        legs[4] = body.getChild( "legLFront_r1" );
        legs[5] = body.getChild( "legRFront_r1" );

        legs[6] = body.getChild( "legLMid_r1" );
        legs[7] = body.getChild( "legRMid_r1" );

        legs[8] = body.getChild( "legLBack_r1" );
        legs[9] = body.getChild( "legRBack_r1" );
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition bottom = partDefinition.addOrReplaceChild("bottom", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, 0.0F));

        bottom.addOrReplaceChild("teethBottom_r1", CubeListBuilder.create()
                        .texOffs(0, 43)
                        .addBox(-6.0F, -6.0F, -11.0F, 12.0F, 12.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        bottom.addOrReplaceChild("base_r1", CubeListBuilder.create()
                        .texOffs(0, 19)
                        .addBox(-7.0F, 0.0F, -7.0F, 14.0F, 10.0F, 14.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition top = partDefinition.addOrReplaceChild("top", CubeListBuilder.create(),
                PartPose.offset(0.0F, 12.0F, 7.0F));

        top.addOrReplaceChild("teethTop_r1", CubeListBuilder.create()
                        .texOffs(38, 43)
                        .addBox(-6.0F, 1.0F, 0.0F, 12.0F, 12.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        top.addOrReplaceChild("nose_r1", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F))
                        .texOffs(0, 0)
                        .addBox(-7.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        body.addOrReplaceChild("legLButt_r1", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.0F, -3.0F, 6.0F, 0.7854F, 0.0F, -0.3491F));

        body.addOrReplaceChild("legRButt_r1", CubeListBuilder.create()
                        .texOffs(2, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, -3.0F, 6.0F, 0.7854F, 0.0F, 0.3491F));

        body.addOrReplaceChild("legLFace_r1", CubeListBuilder.create()
                        .texOffs(2, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.0F, -3.0F, -6.0F, -0.7854F, 0.0F, -0.3491F));

        body.addOrReplaceChild("legRFront_r1", CubeListBuilder.create()
                        .texOffs(2, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, -3.0F, -4.0F, -0.3491F, 0.0F, 0.7854F));

        body.addOrReplaceChild("legLBack_r1", CubeListBuilder.create()
                        .texOffs(2, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, -3.0F, 4.0F, 0.3491F, 0.0F, -0.7854F));

        body.addOrReplaceChild("legLMid_r1", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, -3.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        body.addOrReplaceChild("legLFront_r1", CubeListBuilder.create()
                        .texOffs(1, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, -3.0F, -4.0F, -0.3491F, 0.0F, -0.7854F));

        body.addOrReplaceChild("legRBack_r1", CubeListBuilder.create()
                        .texOffs(1, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, -3.0F, 4.0F, 0.3491F, 0.0F, 0.7854F));

        body.addOrReplaceChild("legRFace_r1", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, -3.0F, -6.0F, -0.7854F, 0.0F, 0.3491F));

        body.addOrReplaceChild("legRMid_r1", CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, 0.0F, -0.5F, 1.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-6.0F, -3.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        return LayerDefinition.create( meshDefinition, 64, 64 );
    }

    // TODO - Get some animation going eventually
    @Override
    public void setupAnim( ChestMimic chestMimic, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {

    }

    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha ) {
        bottom.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        top.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
        body.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
    }
}
