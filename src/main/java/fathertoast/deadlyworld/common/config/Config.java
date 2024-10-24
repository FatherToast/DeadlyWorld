package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import java.util.HashMap;

/**
 * Used as the sole hub for all config access from outside the config package.
 * <p>
 * Contains references to all config files used in this mod, which in turn provide direct 'getter' access to each
 * configurable value.
 */
public class Config {
    
    private static final ConfigManager MANAGER = ConfigManager.create( "DeadlyWorld" );
    
    public static final GlobalConfig GLOBAL = new GlobalConfig( MANAGER, "_global" );
    
    public static final BlocksConfig BLOCKS = new BlocksConfig( MANAGER, "blocks" );
    public static final EntitiesConfig ENTITIES = new EntitiesConfig( MANAGER, "entities" );
    
    /** Mapping of each dimension type to its config. */
    private static HashMap<ResourceKey<Level>, DimensionConfigGroup> DIMENSIONS;
    private static DimensionConfigGroup OVERWORLD_CONFIGS;
    
    /**
     * @return The group of configs associated with the given world's dimension.
     * <p>
     * Returns the overworld's config if the requested dimension config was not properly loaded;
     * throws an exception if dimension configs have not yet been loaded.
     */
    public static DimensionConfigGroup getDimensionConfigs( Level level ) {
        return getDimensionConfigs( level.dimension() );
    }
    
    /**
     * @return The group of configs associated with the given dimension type key.
     * <p>
     * Returns the overworld's config if the requested dimension config was not properly loaded;
     * throws an exception if dimension configs have not yet been loaded.
     */
    public static DimensionConfigGroup getDimensionConfigs( ResourceKey<Level> dimension ) {
        if( OVERWORLD_CONFIGS == null ) {
            throw new IllegalStateException( "Attempted to access dimension configs before any have been loaded." );
        }
        final DimensionConfigGroup configs = DIMENSIONS.get( dimension );
        return configs == null ? OVERWORLD_CONFIGS : configs;
    }
    
    /** Performs initial loading of certain configs in this mod. Called by the mod's constructor. */
    public static void preInitialize() {
        MANAGER.freezeFileWatcher = true;
        
        GLOBAL.SPEC.initialize();
        BLOCKS.SPEC.initialize();
        ENTITIES.SPEC.initialize();
        
        MANAGER.freezeFileWatcher = false;
    }
    
    /** Performs initial loading of certain configs in this mod. Called during FMLCommonSetupEvent. */
    public static void initialize() {
        MANAGER.freezeFileWatcher = true;
        
        // Loading overworld config before the
        // others to prevent our world gen features
        // from exploding the universe with anger
        OVERWORLD_CONFIGS = new DimensionConfigGroup( MANAGER, Level.OVERWORLD );
        OVERWORLD_CONFIGS.initialize();
        DIMENSIONS = new HashMap<>();
        DIMENSIONS.put( Level.OVERWORLD, OVERWORLD_CONFIGS );
        
        MANAGER.freezeFileWatcher = false;
    }
    
    /** Performs loading of configs in this mod that depend on dynamic registries. Called during ServerStartingEvent. */
    public static void initializeDynamic( MinecraftServer server ) {
        //TODO Actually register dimensions dynamically
        
        //        final List<RegistryKey<World>> temp = new ArrayList<>( server.levelKeys() );
        //
        //        // Keep track of opened files so we can close any we don't need
        //        final HashMap<RegistryKey<World>, DimensionConfigGroup> previousDims = DIMENSIONS;
        //
        //        // Load dimension configs
        //        DIMENSIONS = new HashMap<>();
        //
        //        for( RegistryKey<World> dimension : temp ) {
        //            // Use previously opened config if available and remove to prevent it from being closed
        //            final DimensionConfigGroup dimConfigs = previousDims != null && previousDims.containsKey( dimension ) ?
        //                    previousDims.remove( dimension ) :
        //                    new DimensionConfigGroup( CONFIG_DIR, dimension );
        //
        //            // Load it and store the reference
        //            dimConfigs.initialize();
        //            DIMENSIONS.put( dimension, dimConfigs );
        //        }
        //        if( OVERWORLD_CONFIGS == null ) { OVERWORLD_CONFIGS = DIMENSIONS.get( World.OVERWORLD ); }
        //
        //        // Close any configs no longer being used
        //        if( previousDims != null && !previousDims.isEmpty() ) {
        //            for( DimensionConfigGroup dimConfigs : previousDims.values() ) { dimConfigs.destroy(); }
        //        }
    }
}