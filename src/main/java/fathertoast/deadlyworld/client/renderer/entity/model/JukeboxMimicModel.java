package fathertoast.deadlyworld.client.renderer.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fathertoast.deadlyworld.common.entity.JukeboxMimic;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class JukeboxMimicModel<T extends JukeboxMimic> extends HierarchicalModel<T> {


    private final ModelPart body;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart head;

    public JukeboxMimicModel( ModelPart root ) {
        this.body = root.getChild( "body" );
        this.rightLeg = this.body.getChild( "rightLeg" );
        this.leftLeg = this.body.getChild( "leftLeg" );
        this.head = this.body.getChild( "head" );
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        PartDefinition body = partDefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs( 0, 32 )
                .addBox( -2.0F, 0.0F, -1.0F, 3.0F, 11.0F, 3.0F,
                        new CubeDeformation( 0.0F ) ),
                PartPose.offset( -3.0F, -11.0F, 0.0F ) );

        body.addOrReplaceChild( "leftLeg", CubeListBuilder.create().texOffs( 0, 32 )
                .addBox( -1.0F, 0.0F, -1.0F, 3.0F, 11.0F, 3.0F,
                        new CubeDeformation( 0.0F ) ),
                PartPose.offset( 3.0F, -11.0F, 0.0F ) );

        body.addOrReplaceChild("head", CubeListBuilder.create().texOffs( 0, 0 )
                .addBox( -8.0F, -26.0F, -8.0F, 16.0F, 16.0F, 16.0F,
                        new CubeDeformation( 0.0F ) ),
                PartPose.offset( 0.0F, 0.0F, 0.0F ) );

        return LayerDefinition.create( meshDefinition, 64, 64 );
    }

    @Override
    public void setupAnim( JukeboxMimic jukeboxMimic, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch ) {
        root().getAllParts().forEach(ModelPart::resetPose);

        body.xRot = headPitch * ( (float) Math.PI / 180F );
        body.yRot = netHeadYaw * ( (float) Math.PI / 180F );

        rightLeg.xRot = Mth.cos( limbSwing * 0.6662F + (float) Math.PI ) * 1.4F * limbSwingAmount;
        leftLeg.xRot = Mth.cos( limbSwing * 0.6662F ) * 1.4F * limbSwingAmount;
    }

    @Override
    public void renderToBuffer( PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha ) {
        body.render( poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha );
    }

    @Override
    public ModelPart root() {
        return body;
    }
}
