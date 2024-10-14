package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.client.renderer.entity.*;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {//TODO
    
    @SubscribeEvent
    static void onClientSetup( FMLClientSetupEvent event ) {
        MinecraftForge.EVENT_BUS.register( new ClientEvents() );
        
        setBlockRenderTypes();
        registerTileRenderers();
    }
    
    /** Sets the right render type for the given blocks. */
    static void setBlockRenderTypes() {
        //        for( Block spawnerBlock : DWBlocks.spawnerBlocks() ) {
        //            RenderTypeLookup.setRenderLayer( spawnerBlock, RenderType.cutout() );
        //        }
        //
        //        for( Block floorTrapBlock : DWBlocks.floorTrapBlocks() ) {
        //            RenderTypeLookup.setRenderLayer( floorTrapBlock, RenderType.cutout() );
        //        }
    }
    
    static void registerTileRenderers() {
        //        ClientRegistry.bindTileEntityRenderer( DWTileEntities.DEADLY_SPAWNER.get(), DeadlySpawnerTileEntityRenderer::new );
        //        ClientRegistry.bindTileEntityRenderer( DWTileEntities.MINI_SPAWNER.get(), MiniSpawnerTileEntityRenderer::new );
        //        ClientRegistry.bindTileEntityRenderer( DWTileEntities.STORM_DRAIN.get(), StormDrainTileEntityRenderer::new );
        //        ClientRegistry.bindTileEntityRenderer( DWTileEntities.FLOOR_TRAP.get(), FloorTrapTileEntityRenderer::new );
    }
    
    @SubscribeEvent
    static void registerLayerDefs( EntityRenderersEvent.RegisterLayerDefinitions event ) {
    }
    
    @SubscribeEvent
    static void registerEntityRenderers( EntityRenderersEvent.RegisterRenderers event ) {
        // New mobs
        //event.registerEntityRenderer( DWEntities.MIMIC.get(), MimicRenderer::new );
        
        // Mini mobs
        event.registerEntityRenderer( DWEntities.MINI_CREEPER.get(), MiniCreeperRenderer::new );
        event.registerEntityRenderer( DWEntities.MINI_ZOMBIE.get(), MiniZombieRenderer::new );
        event.registerEntityRenderer( DWEntities.MINI_SKELETON.get(), MiniSkeletonRenderer::new );
        event.registerEntityRenderer( DWEntities.MINI_SPIDER.get(), MiniSpiderRenderer::new );
        
        // Projectiles
        event.registerEntityRenderer( DWEntities.MINI_ARROW.get(), MiniArrowRenderer::new );
    }
}