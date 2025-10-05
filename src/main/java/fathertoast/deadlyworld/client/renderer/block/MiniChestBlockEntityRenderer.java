package fathertoast.deadlyworld.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.common.block.entity.MiniChestBlockEntity;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Calendar;

public class MiniChestBlockEntityRenderer<T extends MiniChestBlockEntity> implements BlockEntityRenderer<T> {

    private static final ResourceLocation TEXTURE = DeadlyWorld.resourceLoc( "textures/entity/mini_chest.png" );

    private final ModelPart lid;
    private final ModelPart base;
    // TODO - christmas textures are very fun, might add later
    //private boolean xmasTextures;

    public MiniChestBlockEntityRenderer( BlockEntityRendererProvider.Context context ) {
        Calendar calendar = Calendar.getInstance();

        if ( calendar.get( Calendar.MONTH ) + 1 == Calendar.UNDECIMBER
                && calendar.get( Calendar.DATE ) >= 24
                && calendar.get( Calendar.DATE ) <= 26 ) {
            //this.xmasTextures = true;
        }
        ModelPart root = context.bakeLayer( DWModelLayers.MINI_CHEST );
        base = root.getChild("base");
        lid = root.getChild("lid");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("base", CubeListBuilder.create()
                        .texOffs( 0, 0 )
                        .addBox( -3.0F, -4.0F, -3.0F, 6.0F, 4.0F, 6.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( 0.0F, 24.0F, 0.0F ) );

        partDefinition.addOrReplaceChild( "lid", CubeListBuilder.create()
                        .texOffs( 0, 10 )
                        .addBox( -3.0F, -2.0F, -6.0F, 6.0F, 2.0F, 6.0F,
                                new CubeDeformation( 0.0F ) )
                        .texOffs( 0, 18 )
                        .addBox( -1.0F, -1.0F, -7.0F, 2.0F, 2.0F, 1.0F,
                                new CubeDeformation( 0.0F ) ),
                PartPose.offset( 0.0F, 20.0F, 3.0F ) );

        return LayerDefinition.create( meshDefinition, 32, 32 );
    }

    @Override
    public void render( T miniChest, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlayTexture ) {
        Level level = miniChest.getLevel();
        boolean hasLevel = level != null;
        BlockState state = hasLevel ? miniChest.getBlockState() : DWBlocks.MINI_CHEST.get().defaultBlockState().setValue( ChestBlock.FACING, Direction.SOUTH );

        poseStack.pushPose();
        float rotation = state.getValue( ChestBlock.FACING ).toYRot();
        poseStack.translate( 0.5F, 1.5F, 0.5F );
        poseStack.mulPose( Axis.YP.rotationDegrees( -rotation ) );
        poseStack.mulPose( Axis.XP.rotationDegrees( 180.0F ) );

        VertexConsumer buffer = bufferSource.getBuffer( RenderType.entityCutout( TEXTURE ) );

        float openness = miniChest.getOpenNess( partialTick );
        openness = 1.0F - openness;
        openness = 1.0F - openness * openness * openness;

        render( poseStack, buffer, lid, base, openness, packedLight, overlayTexture );

        poseStack.popPose();
    }

    private void render( PoseStack poseStack, VertexConsumer buffer, ModelPart lid, ModelPart base, float openness, int packedLight, int overlayTexture ) {
        lid.xRot = -( openness * ( (float) Math.PI / 2F ) );
        lid.render( poseStack, buffer, packedLight, overlayTexture );
        base.render( poseStack, buffer, packedLight, overlayTexture );
    }
}
