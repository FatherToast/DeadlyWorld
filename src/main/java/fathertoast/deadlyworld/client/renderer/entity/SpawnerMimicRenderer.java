package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.client.renderer.entity.model.SpawnerMimicModel;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SpawnerRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SpawnerMimicRenderer extends MobRenderer<SpawnerMimic, SpawnerMimicModel<SpawnerMimic>> {

    private static final ResourceLocation TEXTURE = DeadlyWorld.resourceLoc("textures/entity/spawner_mimic.png");


    public SpawnerMimicRenderer( EntityRendererProvider.Context context ) {
        super( context, new SpawnerMimicModel<>( context.bakeLayer( DWModelLayers.SPAWNER_MIMIC ) ), 0.65F );
    }

    @Override
    public ResourceLocation getTextureLocation( SpawnerMimic mimic ) {
        return TEXTURE;
    }

    @Override
    public void render( SpawnerMimic spawnerMimic, float f, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight ) {
        super.render( spawnerMimic, f, partialTick, poseStack, bufferSource, packedLight );
        spawnerMimic.getSpawner().getOrCreateDisplayEntity( spawnerMimic.level(), spawnerMimic.getRandom(), spawnerMimic.blockPosition() );
    }
}
