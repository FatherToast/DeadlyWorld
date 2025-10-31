package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.client.renderer.entity.model.SpawnerMimicModel;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class SpawnerMimicRenderer extends MobRenderer<SpawnerMimic, SpawnerMimicModel<SpawnerMimic>> {
    
    private static final ResourceLocation TEXTURE = DeadlyWorld.rl( "textures/entity/spawner_mimic.png" );
    
    private final EntityRenderDispatcher entityRenderer;
    
    public SpawnerMimicRenderer( EntityRendererProvider.Context context ) {
        super( context, new SpawnerMimicModel<>( context.bakeLayer( DWModelLayers.SPAWNER_MIMIC ) ), 0.65F );
        entityRenderer = context.getEntityRenderDispatcher();
    }
    
    @Override
    public ResourceLocation getTextureLocation( SpawnerMimic mimic ) { return TEXTURE; }
    
    @Override
    public void render( SpawnerMimic spawnerMimic, float rotation, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight ) {
        super.render( spawnerMimic, rotation, partialTick, stack, buffer, packedLight );
        
        ProgressiveDelaySpawner spawner = spawnerMimic.getSpawner();
        if( spawner == null ) return;
        
        stack.pushPose();
        
        Entity entity = spawner.getOrCreateDisplayEntity( spawnerMimic.level(), spawnerMimic.getRandom(), spawnerMimic.blockPosition() );
        if( entity != null ) {
            float scale = 0.53125F;
            float girth = Math.max( entity.getBbWidth(), entity.getBbHeight() );
            if( girth > 1.0F ) scale /= girth;
            
            stack.translate( 0.0F, 0.825F, 0.0F );
            stack.mulPose( Axis.YP.rotationDegrees( (float) Mth.lerp( partialTick, spawner.getoSpin(), spawner.getSpin() ) * 10.0F ) );
            stack.mulPose( Axis.XP.rotationDegrees( -30.0F ) );
            stack.scale( scale, scale, scale );
            entityRenderer.render( entity, 0.0, 0.0, 0.0, 0.0F,
                    partialTick, stack, buffer, packedLight );
        }
        
        stack.popPose();
    }
    
    @Override
    protected boolean isShaking( SpawnerMimic spawnerMimic ) {
        ProgressiveDelaySpawner spawner = spawnerMimic.getSpawner();
        
        if( spawner != null && spawner.isDisabled() )
            return true;
        
        return super.isShaking( spawnerMimic );
    }
}