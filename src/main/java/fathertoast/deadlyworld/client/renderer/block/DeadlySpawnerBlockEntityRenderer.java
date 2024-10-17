package fathertoast.deadlyworld.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fathertoast.deadlyworld.common.block.spawner.DeadlySpawnerBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;

/**
 * Modified copy-paste of {@link net.minecraft.client.renderer.blockentity.SpawnerRenderer}.
 */
public class DeadlySpawnerBlockEntityRenderer implements BlockEntityRenderer<DeadlySpawnerBlockEntity> {
    
    private final EntityRenderDispatcher entityRenderer;
    
    public DeadlySpawnerBlockEntityRenderer( BlockEntityRendererProvider.Context renderContext ) {
        entityRenderer = renderContext.getEntityRenderer();
    }
    
    public void render( DeadlySpawnerBlockEntity blockEntity, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int overlayTexture ) {
        Level level = blockEntity.getLevel();
        if( level == null ) return;
        
        stack.pushPose();
        stack.translate( 0.5F, 0.0F, 0.5F );
        
        BaseSpawner spawner = blockEntity.getSpawner();
        Entity entity = spawner.getOrCreateDisplayEntity( level, level.getRandom(), blockEntity.getBlockPos() );
        if( entity != null ) {
            float scale = 0.53125F;
            float girth = Math.max( entity.getBbWidth(), entity.getBbHeight() );
            if( girth > 1.0F ) scale /= girth;
            
            stack.translate( 0.0F, 0.4F, 0.0F );
            stack.mulPose( Axis.YP.rotationDegrees( (float) Mth.lerp( partialTick, spawner.getoSpin(), spawner.getSpin() ) * 10.0F ) );
            stack.translate( 0.0F, -0.2F, 0.0F );
            stack.mulPose( Axis.XP.rotationDegrees( -30.0F ) );
            stack.scale( scale, scale, scale );
            entityRenderer.render( entity, 0.0, 0.0, 0.0, 0.0F,
                    partialTick, stack, buffer, packedLight );
        }
        
        stack.popPose();
    }
}