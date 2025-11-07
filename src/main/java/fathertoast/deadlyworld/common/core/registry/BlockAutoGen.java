package fathertoast.deadlyworld.common.core.registry;

import fathertoast.crust.api.ICrustApi;
import fathertoast.deadlyworld.common.block.misc.DeadlyInfestedBlock;
import fathertoast.deadlyworld.common.block.unstable.UnstableBlock;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Contains helper methods and the bulk of the logic for auto-generating blocks at runtime by
 * copying many properties (name, model, etc.) from "origin" blocks specified by configs.
 */
public final class BlockAutoGen {
    /** The logger used for block auto-generation. */
    public static final Logger LOG = LogManager.getLogger( DeadlyWorld.MOD_ID + "/block_autogen" );
    
    /** Called during the mod construction phase to initialize block auto-generation. */
    public static void initialize() {
        Config.BLOCK_AUTO_GEN.SPEC.initialize();
        Config.INFESTED_BLOCKS.SPEC.initialize();
        Config.UNSTABLE_BLOCKS.SPEC.initialize();
        registerAutoGenBlocks();
    }
    
    /**
     * Sets up factories for all auto-generated blocks, registers them, populates the auto-generated blocks list,
     * and then adjusts load order, if needed, such that we load after all origin blocks.
     */
    public static void registerAutoGenBlocks() {
        final ArrayList<RegistryObject<? extends IAutoGenBlock>> autoGenBlocks = new ArrayList<>();
        final Set<String> dependencies = new HashSet<>( Config.BLOCK_AUTO_GEN.GENERAL.blockAutoGenDependencies.get() );
        final boolean autoDependencies = dependencies.isEmpty();
        
        // Register infested blocks
        registerAutoGenBlockCategory( autoGenBlocks, dependencies, autoDependencies, DeadlyInfestedBlock.BLOCK_KEY,
                Config.INFESTED_BLOCKS.AUTO_GEN.hostBlocks.get(), ( originBlockLoc ) -> DWBlocks.registerAutoGenBlock(
                        DeadlyInfestedBlock.BLOCK_KEY, originBlockLoc, DeadlyInfestedBlock::new ) );
        // Register unstable blocks
        registerAutoGenBlockCategory( autoGenBlocks, dependencies, autoDependencies, UnstableBlock.BLOCK_KEY,
                Config.UNSTABLE_BLOCKS.AUTO_GEN.hostBlocks.get(), ( originBlockLoc ) -> DWBlocks.registerAutoGenBlock(
                        UnstableBlock.BLOCK_KEY, originBlockLoc, UnstableBlock::new ) );
        
        autoGenBlocks.trimToSize();
        DWBlocks.AUTO_GEN_BLOCKS = Collections.unmodifiableList( autoGenBlocks );
        
        // Remove some dependencies we know don't matter and then apply
        List.of( ResourceLocation.DEFAULT_NAMESPACE, "forge", DeadlyWorld.MOD_ID, ICrustApi.MOD_ID ).forEach( dependencies::remove );
        BlockAutoGen.adjustLoadOrder( dependencies );
    }
    
    /** Checks for any auto-generated infested blocks among the missing mappings and handles them accordingly. */
    public static void remapMissingBlocks( List<MissingMappingsEvent.Mapping<Block>> mappings ) {
        final Map<String, StringBuilder> messages = new HashMap<>();
        final Map<String, Block> fallbackBlock = new HashMap<>();
        fallbackBlock.put( DeadlyInfestedBlock.BLOCK_KEY, Config.INFESTED_BLOCKS.AUTO_GEN.getFallbackBlock() );
        fallbackBlock.put( UnstableBlock.BLOCK_KEY, Config.UNSTABLE_BLOCKS.AUTO_GEN.getFallbackBlock() );
        
        // Remap any mappings we can safely assume previously belonged to an auto-generated block to the config default
        for( MissingMappingsEvent.Mapping<Block> mapping : mappings ) {
            if( !DeadlyWorld.MOD_ID.equals( mapping.getKey().getNamespace() ) ) continue;
            
            String[] autoGenBlockPath = BlockAutoGen.tryParse( mapping.getKey() );
            if( autoGenBlockPath.length == 3 ) { // Length of 3 means it is an auto-generated block
                String blockKey = autoGenBlockPath[0];
                ResourceLocation originBlockLoc = ResourceLocation.fromNamespaceAndPath( autoGenBlockPath[1], autoGenBlockPath[2] );
                
                if( !messages.containsKey( blockKey ) ) {
                    messages.put( blockKey, new StringBuilder( "Missing blocks are:" ) );
                }
                messages.get( blockKey ).append( " \"" ).append( originBlockLoc ).append( "\"," );
                
                Block fallback = fallbackBlock.get( blockKey );
                if( fallback != null ) {
                    mapping.remap( fallback );
                }
                else {
                    LOG.warn( "Missing fallback block to remap missing {} auto-gen block '{}'",
                            blockKey, originBlockLoc );
                }
            }
        }
        
        // Give the user some helpful feedback and a string they can paste into their config
        if( !messages.isEmpty() ) {
            for( Map.Entry<String, StringBuilder> messageEntry : messages.entrySet() ) {
                BlockAutoGen.LOG.warn( "Missing {} block config entries: {}", messageEntry.getKey(),
                        messageEntry.getValue().substring( 0, messageEntry.getValue().length() - 1 ) );
            }
        }
    }
    
    
    /** @return The path to assign for an auto-gen block based on the given origin block's resource location. */
    public static String pathFor( String blockKey, ResourceLocation originBlockLoc ) {
        return blockKey + "/" + originBlockLoc.getNamespace() + "/" + originBlockLoc.getPath();
    }
    
    /**
     * @return The meaningful portions of the auto-generated block's resource location.
     * If the length of the returned array is equal to 3, it can be assumed that the block is auto-generated.
     * In that case, the indexes are:
     * 0 = auto-gen category's "block key"
     * 1 = the origin block's namespace
     * 2 = the origin block's path
     */
    public static String[] tryParse( ResourceLocation autoGenBlockLoc ) { return autoGenBlockLoc.getPath().split( "/", 3 ); }
    
    
    /**
     * This holds the origin block for the current auto-generated block construction.
     * We do this hacky workaround so that we have access to this parameter during block state definition,
     * which is permanently assigned by the superclass constructor before any instance fields are available.
     */
    @Nullable
    private static Block blockForStateDef;
    
    public static <T extends Block & IAutoGenBlock> T generate( ResourceLocation originBlockLoc, BiFunction<Block, ResourceLocation, T> factory ) {
        Block originBlock = BlockAutoGen.getOriginBlockOrThrow( originBlockLoc );
        blockForStateDef = originBlock;
        T autoGenBlock = factory.apply( originBlock, originBlockLoc );
        blockForStateDef = null;
        return autoGenBlock;
    }
    
    
    /**
     * Call this from the auto-generated block's {@link Block#createBlockStateDefinition(StateDefinition.Builder)}
     * override to copy all state properties from its origin block.
     */
    @SuppressWarnings( "JavadocReference" )
    public static void copyBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
        // Copy the block state definition from the host
        if( blockForStateDef != null ) {
            for( Property<?> property : blockForStateDef.getStateDefinition().getProperties() ) {
                builder.add( property );
            }
        }
    }
    
    
    /** @return The origin block from the registry. Throws an exception if the block does not exist. */
    public static Block getOriginBlockOrThrow( ResourceLocation originBlockLoc ) {
        Block originBlock = ForgeRegistries.BLOCKS.getValue( originBlockLoc );
        if( originBlock == null ) {
            throw new IllegalStateException( "Attempted to load auto-generated block for origin block '" + originBlockLoc.getPath() +
                    "' before mod '" + originBlockLoc.getNamespace() + "' is loaded!" );
        }
        return originBlock;
    }
    
    /** Registers all blocks in a particular auto-generated block category. */
    private static <T extends Block & IAutoGenBlock> void registerAutoGenBlockCategory(
            final ArrayList<RegistryObject<? extends IAutoGenBlock>> autoGenBlocks, final Set<String> dependencies, final boolean autoDependencies,
            String blockKey, List<String> originNames, Function<ResourceLocation, RegistryObject<T>> register ) {
        
        final Set<ResourceLocation> registered = new HashSet<>( originNames.size() );
        for( String originName : originNames ) {
            // First, try to parse the origin name from the config as a resource location
            final ResourceLocation originBlockLoc;
            try {
                originBlockLoc = ResourceLocation.parse( originName );
            }
            catch( ResourceLocationException ex ) {
                LOG.warn( "Skipping {} origin block '{}' with invalid name: {}", blockKey, originName, ex.getMessage() );
                continue;
            }
            // Skip any duplicates within this category, angrily
            if( registered.contains( originBlockLoc ) ) {
                LOG.warn( "Skipping duplicate {} origin block '{}'", blockKey, originBlockLoc );
            }
            // If we're using auto-dependencies, skip any blocks that we think won't exist
            else if( autoDependencies && !ModList.get().isLoaded( originBlockLoc.getNamespace() ) ) {
                LOG.warn( "Skipping {} origin block '{}' for undetected mod '{}'", blockKey, originName, originBlockLoc.getNamespace() );
            }
            // Finally, do the thing
            else {
                autoGenBlocks.add( register.apply( originBlockLoc ) );
                if( autoDependencies ) dependencies.add( originBlockLoc.getNamespace() );
                registered.add( originBlockLoc );
            }
        }
    }
    
    /**
     * Attempts to ensure our auto-gen blocks load after all blocks from mods in the dependencies set.
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
    
    public enum NameStyle {
        VANILLA( "vanilla" ),
        SUSPICIOUS( "sus" ),
        IDENTITY( "identity" );
        
        private static final String LANG_KEY = "block." + DeadlyWorld.MOD_ID + ".";
        
        private final String code;
        
        NameStyle( String code ) { this.code = code; }
        
        /**
         * @return The key for the .lang file entry for this name style. It is expected to point to
         * a translation with one argument, where the origin block's translation will be inserted.
         */
        public String getLangKey( String blockKey ) { return LANG_KEY + blockKey + "." + code; }
    }
}