package fathertoast.deadlyworld.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class MiniZombieRenderer extends ZombieRenderer {
    
    public MiniZombieRenderer( EntityRendererProvider.Context renderContext ) {
        super( renderContext );
        shadowRadius = 0.2F;
    }
    
    @Override
    public void scale( Zombie zombie, PoseStack stack, float partialTick ) {
        stack.scale( 0.4F, 0.4F, 0.4F );
    }
}