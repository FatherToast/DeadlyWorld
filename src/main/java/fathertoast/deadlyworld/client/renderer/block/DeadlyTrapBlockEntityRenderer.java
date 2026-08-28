package fathertoast.deadlyworld.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.deadlyworld.api.client.IDecoyRenderer;
import fathertoast.deadlyworld.api.lib.DWRegistries;
import fathertoast.deadlyworld.api.registry.decoy.DecoyType;
import fathertoast.deadlyworld.client.DWModelLayers;
import fathertoast.deadlyworld.client.DecoyRendererRegistry;
import fathertoast.deadlyworld.common.block.entity.FloorTrapBlockEntity;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nullable;

public class DeadlyTrapBlockEntityRenderer implements BlockEntityRenderer<FloorTrapBlockEntity> {
    
    private static final ResourceLocation TOP_OVERLAY = DeadlyWorld.rl( "textures/block/floor_trap_overlay.png" );
    
    private final ModelPart topOverlay;
    
    
    public DeadlyTrapBlockEntityRenderer( BlockEntityRendererProvider.Context renderContext ) {
        ModelPart root = renderContext.bakeLayer( DWModelLayers.DEADLY_TRAP_OVERLAY );
        topOverlay = root.getChild( "overlay" );
    }
    
    public static LayerDefinition createOverlayLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        
        partDefinition.addOrReplaceChild( "overlay",
                CubeListBuilder.create()
                        .texOffs( -16, 0 )
                        .addBox( -8.0F, -16.0F, -8.0F, 16.0F, 0.0F, 16.0F ),
                PartPose.offset( 0.0F, 32.0F, 0.0F )
        );
        
        return LayerDefinition.create( meshDefinition, 16, 16 );
    }
    
    @Override
    public void render( FloorTrapBlockEntity deadlyTrap, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int overlayTexture ) {
        BlockState camoState = deadlyTrap.getCamoState();
        
        BlockPos pos = deadlyTrap.getBlockPos();
        Level level = deadlyTrap.getLevel();
        
        poseStack.pushPose();
        
        Minecraft.getInstance().getBlockRenderer().renderBatched( camoState, pos, level, poseStack, buffer.getBuffer( RenderType.cutout() ),
                false, level.random, ModelData.EMPTY, RenderType.cutout() );
        
        renderTop( poseStack, level, pos, buffer, overlayTexture );
        
        // Render decoy if trap has a decoy type
        DecoyType decoyType = deadlyTrap.getDecoyType();
        
        if( decoyType != null && deadlyTrap.isDecoyActive() ) {
            try {
                IDecoyRenderer decoyRenderer = DecoyRendererRegistry.getRendererForType( deadlyTrap.getDecoyType() );
                
                if( decoyRenderer == null ) {
                    throw new NullPointerException( "Decoy type with ID \" " + DWRegistries.DECOY_TYPE_REGISTRY.get().getKey( decoyType )
                            + " \"is missing decoy renderer!" );
                }
                decoyRenderer.render( deadlyTrap, poseStack, buffer, partialTick, packedLight );
            }
            catch( Exception e ) {
                e.printStackTrace( System.err );
            }
        }
        poseStack.popPose();
    }
    
    private void renderTop( PoseStack poseStack, @Nullable Level level, BlockPos origin, MultiBufferSource buffer, int overlayTexture ) {
        poseStack.pushPose();
        // Move the overlay model a tiiiny bit up to avoid Z-fighting at close ranges (hardly noticeable at longer ranges)
        poseStack.translate( 0.5D, 0.001D, 0.5D );
        // Use light color of above position if possible, since the block we are at is solid
        int packedLight = level == null
                ? 15728880
                : LevelRenderer.getLightColor( level, origin.above() );
        
        topOverlay.render( poseStack, buffer.getBuffer( RenderType.entityCutout( TOP_OVERLAY ) ), packedLight, overlayTexture );
        poseStack.popPose();
    }
}