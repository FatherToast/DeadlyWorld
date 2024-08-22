package fathertoast.deadlyworld.client;

import fathertoast.deadlyworld.client.renderer.entity.*;
import fathertoast.deadlyworld.client.renderer.entity.MiniZombieRenderer;
import fathertoast.deadlyworld.client.renderer.tile.DeadlySpawnerTileEntityRenderer;
import fathertoast.deadlyworld.client.renderer.tile.FloorTrapTileEntityRenderer;
import fathertoast.deadlyworld.client.renderer.tile.MiniSpawnerTileEntityRenderer;
import fathertoast.deadlyworld.client.renderer.tile.StormDrainTileEntityRenderer;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWTileEntities;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber( value = Dist.CLIENT, modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class ClientRegister {
    
    @SubscribeEvent
    public static void onClientSetup( FMLClientSetupEvent event ) {
        MinecraftForge.EVENT_BUS.register( new ClientEvents() );

        setBlockRenderTypes();
        registerTileRenderers();
        registerEntityRenderers( event.getMinecraftSupplier() );
    }
    
    /** Sets the right render type for the given blocks. */
    private static void setBlockRenderTypes() {
        for(Block spawnerBlock : DWBlocks.spawnerBlocks()) {
            RenderTypeLookup.setRenderLayer( spawnerBlock, RenderType.cutout() );
        }

        for (Block floorTrapBlock : DWBlocks.floorTrapBlocks()) {
            RenderTypeLookup.setRenderLayer(floorTrapBlock, RenderType.cutout());
        }
    }
    
    private static void registerTileRenderers() {
        ClientRegistry.bindTileEntityRenderer( DWTileEntities.DEADLY_SPAWNER.get(), DeadlySpawnerTileEntityRenderer::new );
        ClientRegistry.bindTileEntityRenderer( DWTileEntities.MINI_SPAWNER.get(), MiniSpawnerTileEntityRenderer::new );
        ClientRegistry.bindTileEntityRenderer( DWTileEntities.STORM_DRAIN.get(), StormDrainTileEntityRenderer::new );
        ClientRegistry.bindTileEntityRenderer( DWTileEntities.FLOOR_TRAP.get(), FloorTrapTileEntityRenderer::new );
    }

    private static void registerEntityRenderers( Supplier<Minecraft> game ) {
        // New mobs
        RenderingRegistry.registerEntityRenderingHandler( DWEntities.MIMIC.get(), MimicRenderer::new );

        // Mini mobs
        RenderingRegistry.registerEntityRenderingHandler( DWEntities.MINI_CREEPER.get(), MiniCreeperRenderer::new );
        RenderingRegistry.registerEntityRenderingHandler( DWEntities.MINI_ZOMBIE.get(), MiniZombieRenderer::new );
        RenderingRegistry.registerEntityRenderingHandler( DWEntities.MINI_SKELETON.get(), MiniSkeletonRenderer::new );
        RenderingRegistry.registerEntityRenderingHandler( DWEntities.MINI_SPIDER.get(), MiniSpiderRenderer::new );
        RenderingRegistry.registerEntityRenderingHandler( DWEntities.MICRO_GHAST.get(), MicroGhastRenderer::new );

        // Projectiles
        RenderingRegistry.registerEntityRenderingHandler( DWEntities.MINI_ARROW.get(), MiniArrowRenderer::new );
        registerSpriteRenderer( DWEntities.MICRO_FIREBALL, game, 0.15F, true );
    }

    private static <T extends Entity & IRendersAsItem>
    void registerSpriteRenderer( RegistryObject<EntityType<T>> entityType, Supplier<Minecraft> minecraftSupplier, float scale, boolean fullBright ) {
        ItemRenderer itemRenderer = minecraftSupplier.get().getItemRenderer();
        RenderingRegistry.registerEntityRenderingHandler( entityType.get(), ( renderManager ) ->
                new SpriteRenderer<>( renderManager, itemRenderer, scale, fullBright ) );
    }
}