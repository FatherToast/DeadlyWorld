package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class MiniCreeperRenderer extends CreeperRenderer {
    
    public MiniCreeperRenderer( EntityRendererManager rendererManager ) {
        super( rendererManager );
        this.shadowRadius = 0.2F;
    }
    
    @Override
    protected void scale( CreeperEntity creeper, MatrixStack matrixStack, float partialTick ) {
        float swelling = creeper.getSwelling( partialTick );
        float coolness = 1.0F + MathHelper.sin( swelling * 100.0F ) * swelling * 0.01F;
        
        swelling = MathHelper.clamp( swelling, 0.0F, 1.0F );
        swelling *= swelling;
        swelling *= swelling;
        
        float widthScale = (1.0F + swelling * 0.4F) * coolness;
        float heightScale = (1.0F + swelling * 0.1F) / coolness;
        matrixStack.scale( widthScale * 0.4F, heightScale * 0.4F, widthScale * 0.4F );
    }
}