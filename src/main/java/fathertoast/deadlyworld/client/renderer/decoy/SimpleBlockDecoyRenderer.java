package fathertoast.deadlyworld.client.renderer.decoy;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.deadlyworld.api.IDecoyProvider;
import fathertoast.deadlyworld.api.client.IDecoyRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.data.ModelData;

public class SimpleBlockDecoyRenderer implements IDecoyRenderer {
    
    private final Block block;
    
    public SimpleBlockDecoyRenderer( Block block ) {
        this.block = block;
    }
    
    
    @Override
    public void render( IDecoyProvider decoyProvider, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packetLight ) {
        final BlockPos pos = decoyProvider.getProviderPos();
        final Level level = decoyProvider.getProviderLevel();
        
        if( level == null ) return;
        
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        
        poseStack.pushPose();
        poseStack.translate( 0.0, 1.0D, 0.0D );
        
        blockRenderer.renderBatched( block.defaultBlockState(), pos.above(), level, poseStack, bufferSource.getBuffer( RenderType.cutout() ), false, level.random, ModelData.EMPTY, RenderType.cutout() );
        
        poseStack.popPose();
    }
}
