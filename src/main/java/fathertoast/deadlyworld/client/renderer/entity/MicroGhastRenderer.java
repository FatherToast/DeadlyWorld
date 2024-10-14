package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ghast;

public class MicroGhastRenderer extends MobRenderer<Ghast, GhastModel<Ghast>> {

    private static final ResourceLocation GHAST_LOCATION = new ResourceLocation( "textures/entity/ghast/ghast.png" );
    private static final ResourceLocation GHAST_SHOOTING_LOCATION = new ResourceLocation( "textures/entity/ghast/ghast_shooting.png" );

    public MicroGhastRenderer( EntityRendererProvider.Context context ) {
        super( context, new GhastModel<>( context.bakeLayer( ModelLayers.GHAST ) ), 0.1F );
    }

    @Override
    public ResourceLocation getTextureLocation( Ghast microGhast ) {
        return microGhast.isCharging() ? GHAST_SHOOTING_LOCATION : GHAST_LOCATION;
    }

    @Override
    protected void scale( Ghast microGhast, PoseStack poseStack, float partialTick ) {
        poseStack.scale(0.1F, 0.1F, 0.1F);
    }
}
