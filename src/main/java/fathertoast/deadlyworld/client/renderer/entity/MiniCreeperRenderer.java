package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class MiniCreeperRenderer extends CreeperRenderer {
    
    public MiniCreeperRenderer( EntityRendererProvider.Context renderContext ) {
        super( renderContext );
        shadowRadius = 0.2F;
    }
    
    @Override
    protected void scale( Creeper creeper, PoseStack stack, float partialTick ) {
        float swelling = creeper.getSwelling( partialTick );
        float coolness = 1.0F + Mth.sin( swelling * 100.0F ) * swelling * 0.01F;
        swelling = Mth.clamp( swelling, 0.0F, 1.0F );
        
        float widthScale = (1.0F + swelling * 0.4F) * coolness;
        float heightScale = (1.0F + swelling * 0.1F) / coolness;
        stack.scale( widthScale * 0.4F, heightScale * 0.4F, widthScale * 0.4F );
    }
}