package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.client.renderer.block.DeadlySpawnerBlockEntityRenderer;
import fathertoast.deadlyworld.client.renderer.block.DeadlyTrapBlockEntityRenderer;
import fathertoast.deadlyworld.client.renderer.entity.*;
import fathertoast.deadlyworld.client.renderer.entity.layer.ChestMimicChestLayer;
import fathertoast.deadlyworld.client.renderer.entity.model.ChestMimicModel;
import fathertoast.deadlyworld.client.renderer.entity.model.JukeboxMimicModel;
import fathertoast.deadlyworld.client.renderer.entity.model.MiniSpawnerMimicModel;
import fathertoast.deadlyworld.client.renderer.entity.model.SpawnerMimicModel;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.core.registry.DWCreativeModeTabs;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {
    
    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        MinecraftForge.EVENT_BUS.register( new ClientEvents() );
        
        registerBlockEntityRenderers();
        ChestMimicChestLayer.validateChestTextures();
    }

    private static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register( DWBlockEntities.DEADLY_SPAWNER.get(), DeadlySpawnerBlockEntityRenderer::new );
        BlockEntityRenderers.register( DWBlockEntities.MINI_SPAWNER.get(), DeadlySpawnerBlockEntityRenderer::new );
        BlockEntityRenderers.register( DWBlockEntities.DEADLY_TRAP.get(), DeadlyTrapBlockEntityRenderer::new );
        BlockEntityRenderers.register( DWBlockEntities.POTION_TRAP.get(), DeadlyTrapBlockEntityRenderer::new );
        //        BlockEntityRenderers.register( DWBlockEntities.STORM_DRAIN.get(), StormDrainBlockEntityRenderer::new );
    }
    
    @SubscribeEvent
    public static void registerLayerDefs( EntityRenderersEvent.RegisterLayerDefinitions event ) {
        event.registerLayerDefinition( DWModelLayers.JUKEBOX_MIMIC, JukeboxMimicModel::createBodyLayer );
        event.registerLayerDefinition( DWModelLayers.CHEST_MIMIC, ChestMimicModel::createBodyLayer );
        event.registerLayerDefinition( DWModelLayers.SPAWNER_MIMIC, SpawnerMimicModel::createBodyLayer );
        event.registerLayerDefinition( DWModelLayers.MINI_SPAWNER_MIMIC, MiniSpawnerMimicModel::createBodyLayer );

        event.registerLayerDefinition( DWModelLayers.DEADLY_TRAP_OVERLAY, DeadlyTrapBlockEntityRenderer::createOverlayLayer );
    }
    
    @SubscribeEvent
    public static void registerEntityRenderers( EntityRenderersEvent.RegisterRenderers event ) {
        // New mobs
        event.registerEntityRenderer( DWEntities.CHEST_MIMIC.get(), ChestMimicRenderer::new );
        event.registerEntityRenderer( DWEntities.JUKEBOX_MIMIC.get(), JukeboxMimicRenderer::new );
        event.registerEntityRenderer( DWEntities.SPAWNER_MIMIC.get(), SpawnerMimicRenderer::new );
        event.registerEntityRenderer( DWEntities.MINI_SPAWNER_MIMIC.get(), MiniSpawnerMimicRenderer::new );
        
        // Mini mobs
        event.registerEntityRenderer( DWEntities.MINI_CREEPER.get(), MiniCreeperRenderer::new );
        event.registerEntityRenderer( DWEntities.MINI_ZOMBIE.get(), MiniZombieRenderer::new );
        event.registerEntityRenderer( DWEntities.MINI_SKELETON.get(), MiniSkeletonRenderer::new );
        event.registerEntityRenderer( DWEntities.MINI_SPIDER.get(), MiniSpiderRenderer::new );
        event.registerEntityRenderer( DWEntities.MICRO_GHAST.get(), MicroGhastRenderer::new );
        
        // Projectiles
        event.registerEntityRenderer( DWEntities.MINI_ARROW.get(), MiniArrowRenderer::new );
        registerThrownRenderer( DWEntities.MICRO_FIREBALL.get(), 0.15F, true, event );

        // Misc
        event.registerEntityRenderer( DWEntities.YEET_TNT.get(), TntRenderer::new );
    }
    
    private static <T extends Entity & ItemSupplier> void registerThrownRenderer( EntityType<T> entityType, float scale, boolean fullBright, EntityRenderersEvent.RegisterRenderers event ) {
        event.registerEntityRenderer( entityType, ( context ) -> new ThrownItemRenderer<>( context, scale, fullBright ) );
    }
    
    @SubscribeEvent
    public static void buildCreativeContents( BuildCreativeModeTabContentsEvent event ) {
        if( event.getTabKey() == CreativeModeTabs.SEARCH ) {
            for( RegistryObject<Item> item : DWItems.REGISTRY.getEntries() ) {
                event.accept( item.get() );
            }
        }
        else if( event.getTabKey() == DWCreativeModeTabs.MOD_TAB.key() ) {
            for( RegistryObject<Item> item : DWItems.REGISTRY.getEntries() ) {
                event.accept( item.get() );
            }
        }
    }
}