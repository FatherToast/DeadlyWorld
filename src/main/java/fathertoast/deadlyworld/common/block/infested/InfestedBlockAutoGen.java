package fathertoast.deadlyworld.common.block.infested;

import fathertoast.crust.api.ICrustApi;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Contains helper methods and the bulk of the logic for auto-generating infested blocks at runtime.
 */
public class InfestedBlockAutoGen {
    /** The logger used for infested block auto-generation. */
    public static final Logger LOG = LogManager.getLogger( DeadlyWorld.MOD_ID + "/infested" );
    
    /** Called during the mod construction phase to initialize infested block auto-generation. */
    public static void initialize() {
        Config.INFESTED_BLOCKS.SPEC.initialize();
        DWBlocks.registerInfestedBlocks();
    }
    
    /** @return The host block from the registry. Throws an exception if the block does not exist. */
    public static Block getHostBlockOrThrow( ResourceLocation hostBlockLoc ) {
        Block hostBlock = ForgeRegistries.BLOCKS.getValue( hostBlockLoc );
        if( hostBlock == null ) {
            throw new IllegalStateException( "Attempted to load infested block for host '" + hostBlockLoc.getPath() +
                    "' before mod '" + hostBlockLoc.getNamespace() + "' is loaded!" );
        }
        return hostBlock;
    }
    
    /**
     * Builds all infested blocks, registers them, populates the given infested blocks list with the results,
     * and then adjusts load order such that we load after all host blocks, if needed.
     */
    public static void buildInfestedBlocks( final List<RegistryObject<DeadlyInfestedBlock>> infestedBlocks,
                                            Function<ResourceLocation, RegistryObject<DeadlyInfestedBlock>> register ) {
        final Set<String> dependencies = new HashSet<>( Config.INFESTED_BLOCKS.AUTO_GEN.dependencies.get() );
        final boolean autoDependencies = dependencies.isEmpty();
        
        // Register an infested block for each host block we believe might exist
        final Set<ResourceLocation> registered = new HashSet<>( Config.INFESTED_BLOCKS.AUTO_GEN.hostBlocks.get().size() );
        for( String hostName : Config.INFESTED_BLOCKS.AUTO_GEN.hostBlocks.get() ) {
            final ResourceLocation hostBlockLoc;
            try {
                hostBlockLoc = ResourceLocation.parse( hostName );
            }
            catch( ResourceLocationException ex ) {
                LOG.warn( "Skipping host block '{}' with invalid name: {}", hostName, ex.getMessage() );
                continue;
            }
            if( registered.contains( hostBlockLoc ) ) {
                LOG.warn( "Skipping duplicate host block '{}'", hostBlockLoc );
            }
            else if( autoDependencies && !ModList.get().isLoaded( hostBlockLoc.getNamespace() ) ) {
                LOG.warn( "Skipping host block '{}' for undetected mod '{}'", hostName, hostBlockLoc.getNamespace() );
            }
            else {
                infestedBlocks.add( register.apply( hostBlockLoc ) );
                if( autoDependencies ) dependencies.add( hostBlockLoc.getNamespace() );
                registered.add( hostBlockLoc );
            }
        }
        
        // Remove some dependencies we know don't matter and then apply
        List.of( ResourceLocation.DEFAULT_NAMESPACE, "forge", DeadlyWorld.MOD_ID, ICrustApi.MOD_ID ).forEach( dependencies::remove );
        InfestedBlockAutoGen.adjustLoadOrder( dependencies );
    }
    
    /** Checks for any auto-generated infested blocks among the missing mappings and handles them accordingly. */
    public static void remapMissingBlocks( List<MissingMappingsEvent.Mapping<Block>> mappings ) {
        // Remap any mappings we can safely assume previously belonged to an infested block to the config default
        final Block defaultInfestedBlock = Config.INFESTED_BLOCKS.AUTO_GEN.getFallbackBlock();
        final StringBuilder message = new StringBuilder( "Missing host blocks are:" );
        boolean missing = false;
        for( MissingMappingsEvent.Mapping<Block> mapping : mappings ) {
            ResourceLocation hostBlockLoc = DeadlyInfestedBlock.hostLocFrom( mapping.getKey() );
            if( hostBlockLoc != null ) {
                mapping.remap( defaultInfestedBlock );
                message.append( " \"" ).append( hostBlockLoc ).append( "\"," );
                missing = true;
            }
        }
        // Give the user some helpful feedback and a string they can paste into their config
        if( missing ) {
            InfestedBlockAutoGen.LOG.warn( "Blocks missing from your '{}' config setting in '{}.toml'!",
                    Config.INFESTED_BLOCKS.AUTO_GEN.hostBlocks.getKey(), Config.INFESTED_BLOCKS.SPEC.NAME );
            InfestedBlockAutoGen.LOG.warn( message.substring( 0, message.length() - 1 ) );
        }
    }
    
    /**
     * Attempts to ensure our blocks load after all blocks from mods in the dependencies set.
     * We use reflection to actually change load order, so we do everything we can to avoid that, if possible.
     */
    public static void adjustLoadOrder( Set<String> dependencies ) {
        // No relevant dependencies - nice!
        if( dependencies.isEmpty() ) return;
        
        // Check load order
        final ModList modList = ModList.get();
        final ModListCrawler crawler = new ModListCrawler( dependencies );
        modList.forEachModInOrder( crawler );
        
        // We are already loading after all our dependencies
        if( crawler.deadlyWorldIndex > crawler.latestDependencyIndex ) return;
        
        // Adjust mod load order so our blocks get constructed after all dependencies
        LOG.debug( "Detected at least one dynamic dependency mod loading after Deadly World. Attempting to adjust load order..." );
        try {
            // Scary reflection stuff
            final Field field = modList.getClass().getDeclaredField( "sortedContainers" );
            field.setAccessible( true );
            @SuppressWarnings( "unchecked" )
            List<ModContainer> sortedContainers = (List<ModContainer>) field.get( modList );
            
            // Yoink DW and slap it directly behind the latest dependency
            ArrayList<ModContainer> newlySorted = new ArrayList<>( sortedContainers );
            ModContainer deadlyWorldModContainer = newlySorted.remove( crawler.deadlyWorldIndex );
            if( DeadlyWorld.MOD_ID.equals( deadlyWorldModContainer.getModId() ) ) {
                newlySorted.add( crawler.latestDependencyIndex, deadlyWorldModContainer );
                newlySorted.trimToSize();
                field.set( modList, Collections.unmodifiableList( newlySorted ) );
                LOG.debug( "... successfully adjusted load order!" );
            }
            else {
                LOG.error( "... failed to adjust load order - mod list has shifted somehow! " +
                        "Things are probably about to explode." );
            }
        }
        catch( NoSuchFieldException | IllegalAccessException | ClassCastException ex ) {
            LOG.error( "... failed to adjust load order - exception encountered! " +
                    "Things are probably about to explode.", ex );
        }
    }
    
    /** Passed into {@link ModList#forEachModInOrder(Consumer)} to determine load order (without needing reflection). */
    private static class ModListCrawler implements Consumer<ModContainer> {
        int deadlyWorldIndex = -1;
        int latestDependencyIndex = -1;
        
        private final Set<String> dependencies;
        private int index = -1;
        
        public ModListCrawler( Set<String> dependencies ) { this.dependencies = dependencies; }
        
        @Override
        public void accept( ModContainer mod ) {
            // Start by incrementing index
            index++;
            final String modId = mod.getModId();
            
            // Find where Deadly World sits in the load order
            if( DeadlyWorld.MOD_ID.equals( modId ) ) {
                if( deadlyWorldIndex >= 0 ) {
                    LOG.error( "Deadly World appears multiple times in the mod list for some reason?! {} -> {}",
                            deadlyWorldIndex, index );
                }
                deadlyWorldIndex = index;
            }
            // Clock the highest index for any dependency
            else if( dependencies.contains( modId ) ) {
                latestDependencyIndex = index;
                
                if( deadlyWorldIndex >= 0 && index > deadlyWorldIndex ) {
                    LOG.debug( "Dynamic dependency mod '{}' comes after Deadly World", modId );
                }
            }
        }
    }
}