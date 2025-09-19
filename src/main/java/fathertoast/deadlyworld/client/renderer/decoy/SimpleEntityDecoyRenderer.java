package fathertoast.deadlyworld.client.renderer.decoy;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.deadlyworld.api.IDecoyProvider;
import fathertoast.deadlyworld.api.client.IDecoyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class SimpleEntityDecoyRenderer implements IDecoyRenderer {

    private final EntityType<?> entityType;
    private Entity displayEntity;


    public SimpleEntityDecoyRenderer( EntityType<?> entityType ) {
        if ( Minecraft.getInstance().getEntityRenderDispatcher().renderers.get( entityType ) == null )
            throw new IllegalArgumentException( "Attempted to create SimpleEntityDecoyRenderer instance with an entity type that has no registered renderer!" );
        this.entityType = entityType;
    }


    @Override
    public void render( IDecoyProvider decoyProvider, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packetLight ) {
        final BlockPos pos = decoyProvider.getProviderPos();
        final Level level = decoyProvider.getProviderLevel();

        if ( level == null ) return;

        if ( displayEntity == null  ) {
            displayEntity = entityType.create( decoyProvider.getProviderLevel() );

            if ( displayEntity == null )
                throw new IllegalStateException( "SimpleEntityDecoyRenderer failed to create display entity instance!" );
        }
        EntityRenderDispatcher entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();

        poseStack.pushPose();
        poseStack.translate( 0.5D, 1.0D, 0.5D );

        int packedLightAbove = getPackedLightCoords( level, pos.above() );
        entityRenderer.render( displayEntity, 0.0D, 0.0D, 0.0D, 0.0F, partialTick, poseStack, bufferSource, packedLightAbove );

        poseStack.popPose();
    }

    public final int getPackedLightCoords( Level level, BlockPos pos ) {
        int blockLight = level.getBrightness( LightLayer.BLOCK, pos );
        int skyLight = level.getBrightness( LightLayer.SKY, pos );

        return LightTexture.pack( blockLight, skyLight );
    }
}
