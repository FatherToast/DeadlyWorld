package fathertoast.deadlyworld.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.deadlyworld.common.tile.spawner.StormDrainTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class StormDrainTileEntityRenderer extends TileEntityRenderer<StormDrainTileEntity> {

    public StormDrainTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(StormDrainTileEntity stormDrainTile, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, int overlayTexture) {

        if (stormDrainTile.getSuctionBox() != null) {
            EntityRendererManager rendererManager = Minecraft.getInstance().getEntityRenderDispatcher();

            if (rendererManager.shouldRenderHitBoxes() && !Minecraft.getInstance().showOnlyReducedInfo()) {
                BlockPos pos = stormDrainTile.getBlockPos();
                AxisAlignedBB box = stormDrainTile.getSuctionBox().move(-pos.getX(), -pos.getY(), -pos.getZ());
                WorldRenderer.renderLineBox(matrixStack, buffer.getBuffer(RenderType.lines()), box, 0.0F, 1.0F, 0.0F, 1.0F);
            }
        }
    }
}
