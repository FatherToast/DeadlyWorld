package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.client.renderer.entity.model.MiniSpawnerMimicModel;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.MiniSpawnerMimic;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class MiniSpawnerMimicRenderer extends MobRenderer<MiniSpawnerMimic, MiniSpawnerMimicModel> {
    
    private static final ResourceLocation TEXTURE = DeadlyWorld.rl( "textures/entity/mini_spawner_mimic.png" );
    
    private final EntityRenderDispatcher entityRenderer;
    
    public MiniSpawnerMimicRenderer( EntityRendererProvider.Context context ) {
        super( context, new MiniSpawnerMimicModel( context.bakeLayer( DWModelLayers.MINI_SPAWNER_MIMIC ) ), 0.35F );
        entityRenderer = context.getEntityRenderDispatcher();
    }
    
    @Override
    public ResourceLocation getTextureLocation( MiniSpawnerMimic mimic ) { return TEXTURE; }
    
    @Override
    public void render( MiniSpawnerMimic spawnerMimic, float rotation, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight ) {
        super.render( spawnerMimic, rotation, partialTick, stack, buffer, packedLight );
        
        ProgressiveDelaySpawner spawner = spawnerMimic.getSpawner();
        if( spawner == null ) return;
        
        stack.pushPose();
        
        Entity entity = spawner.getOrCreateDisplayEntity( spawnerMimic.level(), spawnerMimic.getRandom(), spawnerMimic.blockPosition() );
        if( entity != null ) {
            float scale = 0.265625F;
            float girth = Math.max( entity.getBbWidth(), entity.getBbHeight() );
            if( girth > 1.0F ) scale /= girth;
            
            stack.translate( 0.0F, 0.525F, 0.0F );
            stack.mulPose( Axis.YP.rotationDegrees( (float) Mth.lerp( partialTick, spawner.getoSpin(), spawner.getSpin() ) * 10.0F ) );
            stack.mulPose( Axis.XP.rotationDegrees( -30.0F ) );
            stack.scale( scale, scale, scale );
            entityRenderer.render( entity, 0.0, 0.0, 0.0, 0.0F,
                    partialTick, stack, buffer, packedLight );
        }
        
        stack.popPose();
    }
    
    @Override
    protected boolean isShaking( MiniSpawnerMimic spawnerMimic ) {
        ProgressiveDelaySpawner spawner = spawnerMimic.getSpawner();
        
        if( spawner != null && spawner.isDisabled() )
            return true;
        
        return super.isShaking( spawnerMimic );
    }
}