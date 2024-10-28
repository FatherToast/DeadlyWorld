package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.resources.ResourceKey;
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
    private static DimensionConfigGroup DEFAULT_CONFIGS;
    
    /**
     * @return The group of configs to use when configs do not exist or are not loaded.
     * @throws IllegalStateException if dimension configs have not yet been loaded.
     */
    public static DimensionConfigGroup getDefaultConfigs() {
        assertLoaded();
        return DEFAULT_CONFIGS;
    }
    
    /**
     * @return The group of configs associated with the given world's dimension,
     * or the default configs if the requested dimension configs do not exist or are not loaded.
     * @throws IllegalStateException if dimension configs have not yet been loaded.
     */
    public static DimensionConfigGroup getDimensionConfigs( Level level ) {
        return getDimensionConfigs( level.dimension() );
    }
    
    /**
     * @return The group of configs associated with the given dimension type key,
     * or the default configs if the requested dimension configs do not exist or are not loaded.
     * @throws IllegalStateException if dimension configs have not yet been loaded.
     */
    public static DimensionConfigGroup getDimensionConfigs( ResourceKey<Level> dimension ) {
        assertLoaded();
        final DimensionConfigGroup configs = DIMENSIONS.get( dimension );
        return configs == null ? DEFAULT_CONFIGS : configs;
    }
    
    /** @throws IllegalStateException if dimension configs have not yet been loaded. */
    private static void assertLoaded() {
        if( DEFAULT_CONFIGS == null )
            throw new IllegalStateException( "Attempted to access dimension configs before any have been loaded." );
    }
    
    /** Performs loading of configs in this mod. Called by the mod's constructor. */
    public static void initialize() {
        MANAGER.freezeFileWatcher = true;
        
        GLOBAL.SPEC.initialize();
        BLOCKS.SPEC.initialize();
        ENTITIES.SPEC.initialize();
        
        DEFAULT_CONFIGS = new DimensionConfigGroup( MANAGER, Level.OVERWORLD ); // For now, default = overworld = the only configs
        DEFAULT_CONFIGS.initialize();
        DIMENSIONS = new HashMap<>(); // TODO load a config group for each dimension in a list field in GLOBAL
        DIMENSIONS.put( Level.OVERWORLD, DEFAULT_CONFIGS );
        
        MANAGER.freezeFileWatcher = false;
    }
}