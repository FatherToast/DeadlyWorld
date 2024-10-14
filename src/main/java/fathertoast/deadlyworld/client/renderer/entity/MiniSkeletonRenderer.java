package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class MiniSkeletonRenderer extends SkeletonRenderer {
    
    public MiniSkeletonRenderer( EntityRendererProvider.Context renderContext ) {
        super( renderContext );
        shadowRadius = 0.2F;
    }
    
    @Override
    public void scale( AbstractSkeleton skeleton, PoseStack stack, float partialTick ) {
        stack.scale( 0.4F, 0.4F, 0.4F );
    }
}