package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.client.renderer.entity.model.MiniSpawnerMimicModel;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.MiniSpawnerMimic;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MiniSpawnerMimicRenderer extends MobRenderer<MiniSpawnerMimic, MiniSpawnerMimicModel> {

    private static final ResourceLocation TEXTURE = DeadlyWorld.rl("textures/entity/mini_spawner_mimic.png");


    public MiniSpawnerMimicRenderer( EntityRendererProvider.Context context ) {
        super( context, new MiniSpawnerMimicModel( context.bakeLayer( DWModelLayers.MINI_SPAWNER_MIMIC ) ), 0.35F );
    }

    @Override
    public ResourceLocation getTextureLocation( MiniSpawnerMimic mimic ) {
        return TEXTURE;
    }

    @Override
    public void render( MiniSpawnerMimic spawnerMimic, float f, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight ) {
        super.render( spawnerMimic, f, partialTick, poseStack, bufferSource, packedLight );
        // TODO - Maybe render the display entity inside the mimic
        spawnerMimic.getSpawner().getOrCreateDisplayEntity( spawnerMimic.level(), spawnerMimic.getRandom(), spawnerMimic.blockPosition() );
    }

    @Override
    protected boolean isShaking( MiniSpawnerMimic spawnerMimic ) {
        ProgressiveDelaySpawner spawner = spawnerMimic.getSpawner();

        if ( spawner != null && spawner.isDisabled() )
            return true;

        return super.isShaking( spawnerMimic );
    }
}
