package fathertoast.deadlyworld.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;

public class FloorTrapTileEntityRenderer extends TileEntityRenderer<FloorTrapTileEntity> {

    private static final ResourceLocation TOP_OVERLAY = DeadlyWorld.resourceLoc("textures/misc/floor_trap_overlay.png");

    private final ModelRenderer topOverlay;


    public FloorTrapTileEntityRenderer( TileEntityRendererDispatcher rendererDispatcher ) {
        super( rendererDispatcher );
        topOverlay = new ModelRenderer(16, 16, 0, 0);
        topOverlay.setPos(0.0F, 24.0F, 0.0F);
        topOverlay.texOffs(-16, 0)
                .addBox(-8.0F, -16.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);
    }

    @Override
    public void render(FloorTrapTileEntity floorTrap, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, int overlayTexture ) {
        matrixStack.pushPose( );

        BlockState camoState = floorTrap.getCamoState();

        if ( camoState != null ) {
            BlockPos pos = floorTrap.getBlockPos();
            World world = floorTrap.getLevel();
            Minecraft.getInstance().getBlockRenderer().renderModel( camoState, pos, world, matrixStack, buffer.getBuffer( RenderType.cutout() ), false, world.random, EmptyModelData.INSTANCE );
        }
        renderTop(matrixStack, buffer, packedLight, overlayTexture);
        matrixStack.popPose( );
    }

    private void renderTop(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, int overlayTexture) {
        matrixStack.pushPose();
        // Move the overlay model a tiiiny bit up to avoid Z-fighting at close ranges (hardly noticeable at longer ranges)
        matrixStack.translate(0.5D, 0.5002D, 0.5D);
        topOverlay.render(matrixStack, buffer.getBuffer(RenderType.entityCutout(TOP_OVERLAY)), packedLight, overlayTexture);
        matrixStack.popPose();
    }
}
