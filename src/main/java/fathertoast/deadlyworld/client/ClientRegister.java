package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.client.renderer.block.DeadlySpawnerBlockEntityRenderer;
import fathertoast.deadlyworld.client.renderer.entity.*;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlockEntities;
import fathertoast.deadlyworld.common.core.registry.DWCreativeModeTabs;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWItems;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
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
    static void onClientSetup( FMLClientSetupEvent event ) {
        MinecraftForge.EVENT_BUS.register( new ClientEvents() );
        
        registerBlockEntityRenderers();
    }
    
    static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register( DWBlockEntities.DEADLY_SPAWNER.get(), DeadlySpawnerBlockEntityRenderer::new );
        BlockEntityRenderers.register( DWBlockEntities.MINI_SPAWNER.get(), DeadlySpawnerBlockEntityRenderer::new );
        //        BlockEntityRenderers.register( DWBlockEntities.FLOOR_TRAP.get(), FloorTrapBlockEntityRenderer::new );
        //        BlockEntityRenderers.register( DWBlockEntities.STORM_DRAIN.get(), StormDrainBlockEntityRenderer::new );
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
        event.registerEntityRenderer( DWEntities.MICRO_GHAST.get(), MicroGhastRenderer::new );
        
        // Projectiles
        event.registerEntityRenderer( DWEntities.MINI_ARROW.get(), MiniArrowRenderer::new );
        registerThrownRenderer( DWEntities.MICRO_FIREBALL.get(), 0.15F, true, event );
    }
    
    private static <T extends Entity & ItemSupplier> void registerThrownRenderer( EntityType<T> entityType, float scale, boolean fullBright, EntityRenderersEvent.RegisterRenderers event ) {
        event.registerEntityRenderer( entityType, ( context ) -> new ThrownItemRenderer<>( context, scale, fullBright ) );
    }
    
    @SubscribeEvent
    static void buildCreativeContents( BuildCreativeModeTabContentsEvent event ) {
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