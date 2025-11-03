package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.client.renderer.block.DeadlySpawnerBlockEntityRenderer;
import fathertoast.deadlyworld.client.renderer.entity.model.SpawnerMimicModel;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

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
        renderDisplayEntity( spawnerMimic, partialTick, stack, buffer, packedLight, entityRenderer,
                0.53125F, 0.825F );
    }
    
    public static void renderDisplayEntity( SpawnerMimic spawnerMimic,
                                            float partialTick, PoseStack stack,
                                            MultiBufferSource buffer, int packedLight, EntityRenderDispatcher entityRenderer,
                                            float scale, float offset ) {
        DeadlySpawnerBlockEntityRenderer.renderDisplayEntity( spawnerMimic.getSpawner(),
                spawnerMimic.level(), spawnerMimic.getRandom(), spawnerMimic.blockPosition(),
                partialTick, stack, buffer, packedLight, entityRenderer,
                scale, new Vec3( 0.0F, offset, 0.0F ) );
    }
    
    @Override
    protected boolean isShaking( SpawnerMimic spawnerMimic ) {
        ProgressiveDelaySpawner spawner = spawnerMimic.getSpawner();
        
        if( spawner != null && spawner.isDisabled() )
            return true;
        
        return super.isShaking( spawnerMimic );
    }
}