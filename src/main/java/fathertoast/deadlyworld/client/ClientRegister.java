package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.client.renderer.tile.DeadlySpawnerTileEntityRenderer;
import fathertoast.deadlyworld.client.renderer.tile.StormDrainTileEntityRenderer;
import fathertoast.deadlyworld.common.block.DeadlySpawnerBlock;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.registry.DWBlocks;
import fathertoast.deadlyworld.common.registry.DWTileEntities;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {
    
    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        setBlockRenderTypes();
        registerTileRenderers();
    }
    
    /** Sets the right render type for the given blocks. */
    private static void setBlockRenderTypes() {
        for( Block spawnerBlock : DWBlocks.spawnerBlocks() ) {
            RenderTypeLookup.setRenderLayer( spawnerBlock, RenderType.cutout() );
        }
    }
    
    private static void registerTileRenderers() {
        ClientRegistry.bindTileEntityRenderer(DWTileEntities.DEADLY_SPAWNER.get(), DeadlySpawnerTileEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(DWTileEntities.STORM_DRAIN.get(), StormDrainTileEntityRenderer::new);
    }
}