package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Used as the sole hub for all config access from outside the config package.
 * <p>
 * Contains references to all config files used in this mod, which in turn provide direct 'getter' access to each
 * configurable value.
 */
public class Config {
    private static final ConfigManager MANAGER = ConfigManager.create( "DeadlyWorld" );
    
    public static final MainConfig MAIN = new MainConfig( MANAGER, "_main" );
    
    public static final BlocksConfig BLOCKS = new BlocksConfig( MANAGER, "blocks" );
    public static final EntitiesConfig ENTITIES = new EntitiesConfig( MANAGER, "entities" );
    
    /** Mapping of each dimension type to its config. */
    private static HashMap<ResourceKey<Level>, DimensionConfigGroup> DIMENSIONS;
    private static DimensionConfigGroup DEFAULT_CONFIGS;
    
    /**
     * @return The group of configs associated with the given world's dimension,
     * or the default configs if the requested dimension configs do not exist or are not loaded.
     * @throws IllegalStateException if dimension configs have not yet been loaded.
     */
    public static DimensionConfigGroup getDimensionConfigs( @Nullable Level level ) {
        return getDimensionConfigs( level == null ? null : level.dimension() );
    }
    
    /**
     * @return The group of configs associated with the given dimension type key,
     * or the default configs if the requested dimension configs do not exist or are not loaded.
     * @throws IllegalStateException if dimension configs have not yet been loaded.
     */
    public static DimensionConfigGroup getDimensionConfigs( @Nullable ResourceKey<Level> dimension ) {
        assertLoaded();
        if( dimension == null ) return DEFAULT_CONFIGS;
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
        
        MAIN.SPEC.initialize();
        BLOCKS.SPEC.initialize();
        ENTITIES.SPEC.initialize();
        
        DEFAULT_CONFIGS = new DimensionConfigGroup( MANAGER, Level.OVERWORLD );
        DEFAULT_CONFIGS.initialize();
        DIMENSIONS = new HashMap<>();
        DIMENSIONS.put( Level.OVERWORLD, DEFAULT_CONFIGS );
        for( String dimension : MAIN.GENERAL.extraDimensions.get() ) {
            ResourceKey<Level> key = ResourceKey.create( Registries.DIMENSION, new ResourceLocation( dimension ) );
            if( DIMENSIONS.containsKey( key ) ) continue;
            DimensionConfigGroup dimConfigs = new DimensionConfigGroup( MANAGER, key );
            dimConfigs.initialize();
            DIMENSIONS.put( key, dimConfigs );
        }
        
        MANAGER.freezeFileWatcher = false;
    }
}