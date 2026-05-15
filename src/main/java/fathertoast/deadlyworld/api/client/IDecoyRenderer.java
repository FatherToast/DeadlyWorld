package fathertoast.deadlyworld.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.deadlyworld.api.IDecoyProvider;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/** Represents a decoy renderer. */
public interface IDecoyRenderer {
    
    /** Renders the decoy. */
    void render( IDecoyProvider decoyProvider, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packetLight );
    
    /** Helper method for getting the packed light at the given block position. */
    default int getPackedLightCoords( Level level, BlockPos pos ) {
        int blockLight = level.getBrightness( LightLayer.BLOCK, pos );
        int skyLight = level.getBrightness( LightLayer.SKY, pos );
        
        return LightTexture.pack( blockLight, skyLight );
    }
}
