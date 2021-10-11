package fathertoast.deadlyworld.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import fathertoast.deadlyworld.common.tile.spawner.DeadlySpawnerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3f;

public class DeadlySpawnerTileEntityRenderer extends TileEntityRenderer<DeadlySpawnerTileEntity> {

    public DeadlySpawnerTileEntityRenderer( TileEntityRendererDispatcher rendererDispatcher ) {
        super( rendererDispatcher );
    }

    @Override
    public void render( DeadlySpawnerTileEntity spawner, float partialTick, MatrixStack matrixStack, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_ ) {
        matrixStack.pushPose( );
        matrixStack.translate( 0.5D, 0.0D, 0.5D );

        Entity entity = spawner.getRenderEntity( );

        if ( entity != null ) {
            float scale = 0.53125F;
            float entityGirth  = Math.max( entity.getBbWidth( ), entity.getBbHeight( ) );

            if ( (double) entityGirth > 1.0D ) {
                scale /= entityGirth;
            }

            matrixStack.translate( 0.0D, 0.4D, 0.0D );
            matrixStack.mulPose( Vector3f.YP.rotationDegrees( spawner.getRenderEntityRotation( partialTick ) * 10.0F ) );
            matrixStack.translate( 0.0D, -0.2D, 0.0D );
            matrixStack.mulPose( Vector3f.XP.rotationDegrees( -30.0F ) );
            matrixStack.scale( scale, scale, scale );
            Minecraft.getInstance( ).getEntityRenderDispatcher( ).render( entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTick, matrixStack, buffer, p_225616_5_ );
        }
        matrixStack.popPose( );
    }
}
