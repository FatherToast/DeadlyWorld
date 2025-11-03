package fathertoast.deadlyworld.client.renderer.entity;

import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.client.renderer.entity.model.MiniChestMimicModel;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.MiniChestMimic;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MiniChestMimicRenderer extends MobRenderer<MiniChestMimic, MiniChestMimicModel> {
    
    private static final ResourceLocation TEXTURE = DeadlyWorld.rl( "textures/entity/mini_chest_mimic.png" );
    
    
    public MiniChestMimicRenderer( EntityRendererProvider.Context context ) {
        super( context, new MiniChestMimicModel( context.bakeLayer( DWModelLayers.MINI_CHEST_MIMIC ) ), 0.25F );
    }
    
    @Override
    public ResourceLocation getTextureLocation( MiniChestMimic mimic ) {
        return TEXTURE;
    }
}
