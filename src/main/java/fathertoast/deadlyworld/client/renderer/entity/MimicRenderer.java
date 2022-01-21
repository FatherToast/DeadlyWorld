package fathertoast.deadlyworld.client.renderer.entity;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.MimicEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn( Dist.CLIENT )
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MimicRenderer<T extends MimicEntity> extends MobRenderer<T, MimicModel<T>> {

    private static final ResourceLocation TEXTURE = DeadlyWorld.resourceLoc( "textures/entity/mimic.png" );

    public MimicRenderer( EntityRendererManager rendererManager ) {
        super( rendererManager, new MimicModel<>(), 0.5F );
        this.addLayer( new MimicLayerRenderer<>( this ));
    }
    
    @Override
    public ResourceLocation getTextureLocation( T mimic ) { return TEXTURE; }
}