package fathertoast.deadlyworld.client.renderer.entity;

import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.client.renderer.entity.layer.ChestMimicChestLayer;
import fathertoast.deadlyworld.client.renderer.entity.model.ChestMimicModel;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.ChestMimic;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChestMimicRenderer extends MobRenderer<ChestMimic, ChestMimicModel> {
    
    private static final ResourceLocation TEXTURE = DeadlyWorld.rl( "textures/entity/chest_mimic.png" );
    
    
    public ChestMimicRenderer( EntityRendererProvider.Context context ) {
        super( context, new ChestMimicModel( context.bakeLayer( DWModelLayers.CHEST_MIMIC ) ), 0.65F );
        addLayer( new ChestMimicChestLayer( this, new ChestMimicModel( context.bakeLayer( DWModelLayers.CHEST_MIMIC ) ) ) );
    }
    
    @Override
    public ResourceLocation getTextureLocation( ChestMimic mimic ) {
        return TEXTURE;
    }
}
