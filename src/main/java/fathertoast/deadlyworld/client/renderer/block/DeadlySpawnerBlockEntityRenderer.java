package fathertoast.deadlyworld.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fathertoast.deadlyworld.common.block.entity.DeadlySpawnerBlockEntity;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

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
        
        renderDisplayEntity( blockEntity.getSpawnerLogic(), level, level.getRandom(), blockEntity.getBlockPos(),
                partialTick, stack, buffer, packedLight, entityRenderer,
                blockEntity.getEntityRenderScale(), blockEntity.getEntityRenderOffset() );
    }
    
    public static void renderDisplayEntity( @Nullable ProgressiveDelaySpawner spawner, Level level, RandomSource random, BlockPos pos,
                                            float partialTick, PoseStack stack, MultiBufferSource buffer,
                                            int packedLight, EntityRenderDispatcher entityRenderer,
                                            float scale, Vec3 offset ) {
        if( spawner == null ) return;
        
        stack.pushPose();
        
        Entity entity = spawner.getOrCreateDisplayEntity( level, random, pos );
        if( entity != null ) {
            float girth = Math.max( entity.getBbWidth(), entity.getBbHeight() );
            if( girth > 1.0F ) scale /= girth;
            
            stack.translate( offset.x(), offset.y(), offset.z() );
            stack.mulPose( Axis.YP.rotationDegrees( (float) Mth.lerp( partialTick, spawner.getoSpin(), spawner.getSpin() ) * 10.0F ) );
            stack.mulPose( Axis.XP.rotationDegrees( -30.0F ) );
            stack.scale( scale, scale, scale );
            entityRenderer.render( entity, 0.0, 0.0, 0.0, 0.0F,
                    partialTick, stack, buffer, packedLight );
        }
        
        stack.popPose();
    }
}