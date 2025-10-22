package fathertoast.deadlyworld.common.core.registry.util;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.List;

/**
 * Handler class for dealing with missing mappings from the DeadlyWorld namespace.
 */
@Mod.EventBusSubscriber( bus = Mod.EventBusSubscriber.Bus.FORGE, modid = DeadlyWorld.MOD_ID )
public final class MissingMappingsHandler {

    /**
     * Called when game data is being read when a save file is being loaded.
     */
    @SubscribeEvent
    public static void onMissingMappings( MissingMappingsEvent event ) {
        // Missing block mappings
        List<MissingMappingsEvent.Mapping<Block>> blockMappings = event.getMappings( Registries.BLOCK, DeadlyWorld.MOD_ID );
        remapMissingBlocks( blockMappings );
    }

    private static void remapMissingBlocks( List<MissingMappingsEvent.Mapping<Block>> mappings ) {
        Block defaultInfestedBlock = Config.INFESTED_BLOCKS.AUTO_GEN.getFallbackBlock();

        // Remap any mappings we can safely assume previously
        // belonged to an infested block to the config default.
        for ( MissingMappingsEvent.Mapping<Block> mapping : mappings ) {
            if ( isIdForInfested( mapping.getKey() ) ) {
                mapping.remap( defaultInfestedBlock );
            }
        }
    }

    /**
     * @return True if the given key/block ID can safely
     *         be assumed to have belonged to an infested block.
     *         Returns false otherwise.
     */
    public static boolean isIdForInfested( ResourceLocation key ) {
        if ( !key.getNamespace().equals( DeadlyWorld.MOD_ID ) )
            return false;

        final String path = key.getPath();

        return path.startsWith( "infested/" );
    }
}
