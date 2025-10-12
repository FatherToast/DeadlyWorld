package fathertoast.deadlyworld.client.renderer.entity.layer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import fathertoast.deadlyworld.client.renderer.entity.model.ChestMimicModel;
import fathertoast.deadlyworld.common.entity.ChestMimic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ChestMimicChestLayer extends RenderLayer<ChestMimic, ChestMimicModel> {
    
    private static final Map<Block, ResourceLocation> TEXTURES_BY_BLOCK = new HashMap<>();
    private static final ResourceLocation VANILLA_TEXTURE = ResourceLocation.withDefaultNamespace( "textures/entity/chest/normal.png" );
    
    private final ChestMimicModel layerModel;
    
    
    public ChestMimicChestLayer( RenderLayerParent<ChestMimic, ChestMimicModel> parent, ChestMimicModel layerModel ) {
        super( parent );
        this.layerModel = layerModel;
    }
    
    @Override
    public void render( PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ChestMimic chestMimic,
                        float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch ) {
        
        final ResourceLocation textureLoc = TEXTURES_BY_BLOCK.getOrDefault( chestMimic.getDisguiseState().getBlock(), VANILLA_TEXTURE );
        
        coloredCutoutModelCopyLayerRender( getParentModel(), layerModel, textureLoc, poseStack, bufferSource,
                packedLight, chestMimic, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTick,
                1.0F, 1.0F, 1.0F ); // RGB
    }
    
    /**
     * Called from {@link fathertoast.deadlyworld.client.ClientRegister#onClientSetup(FMLClientSetupEvent)}.<br><br>
     * Here we boldly try and guess the correct texture path for each chest block and store them for later.
     */
    public static void validateChestTextures() {
        for( Block block : ForgeRegistries.BLOCKS.getValues() ) {
            if( block instanceof AbstractChestBlock<?> ) {
                final ResourceLocation id = ForgeRegistries.BLOCKS.getKey( block );
                
                for( ResourceLocation texturePath : createCommonLocations( id ) ) {
                    // Try loading the texture. If nothing goes wrong, assume we are good to go
                    try {
                        Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow( texturePath );
                        NativeImage.read( resource.open() );
                        
                        TEXTURES_BY_BLOCK.put( block, texturePath );
                    }
                    catch( Exception ignored ) { }
                }
            }
        }
    }
    
    /**
     * Creates an array of commonly seen locations
     * for chest textures for the given block ID.
     */
    private static ResourceLocation[] createCommonLocations( ResourceLocation blockId ) {
        ResourceLocation[] consideredPaths = new ResourceLocation[3];
        
        consideredPaths[0] = ResourceLocation.fromNamespaceAndPath( blockId.getNamespace(), "textures/model/" + blockId.getPath() + ".png" );
        consideredPaths[1] = ResourceLocation.fromNamespaceAndPath( blockId.getNamespace(), "textures/entity/chest/" + blockId.getPath() + ".png" );
        consideredPaths[2] = ResourceLocation.fromNamespaceAndPath( blockId.getNamespace(), "textures/entity/chest/" + blockId.getPath().replaceFirst( "_chest", "" ) + ".png" );
        
        return consideredPaths;
    }
}