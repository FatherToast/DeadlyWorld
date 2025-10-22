package fathertoast.deadlyworld.client.renderer.entity;

import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.client.renderer.entity.model.JukeboxMimicModel;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.JukeboxMimic;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class JukeboxMimicRenderer extends MobRenderer<JukeboxMimic, JukeboxMimicModel<JukeboxMimic>> {

    private static final ResourceLocation TEXTURE = DeadlyWorld.rl("textures/entity/jukebox_mimic.png");


    public JukeboxMimicRenderer( EntityRendererProvider.Context context ) {
        super( context, new JukeboxMimicModel<>( context.bakeLayer( DWModelLayers.JUKEBOX_MIMIC ) ), 0.65F );
    }

    @Override
    public ResourceLocation getTextureLocation( JukeboxMimic mimic ) {
        return TEXTURE;
    }
}
