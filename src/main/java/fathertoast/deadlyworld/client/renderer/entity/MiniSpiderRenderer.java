package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.deadlyworld.common.entity.MiniSpider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class MiniSpiderRenderer extends SpiderRenderer<MiniSpider> {
    
    public MiniSpiderRenderer( EntityRendererProvider.Context renderContext ) {
        super( renderContext );
        shadowRadius = 0.3F;
    }
    
    @Override
    public void scale( MiniSpider spider, PoseStack stack, float partialTick ) {
        stack.scale( 0.4F, 0.4F, 0.4F );
    }
}