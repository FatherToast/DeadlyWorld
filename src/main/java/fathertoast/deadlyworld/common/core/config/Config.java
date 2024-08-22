package fathertoast.deadlyworld.common.core.config;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.field.AbstractConfigField;
import fathertoast.deadlyworld.common.core.config.file.ToastConfigSpec;
import fathertoast.deadlyworld.common.core.config.file.TomlHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Used as the sole hub for all config access from outside the config package.
 * <p>
 * Contains references to all config files used in this mod, which in turn provide direct 'getter' access to each
 * configurable value.
 */
public class Config {
    /** The root folder for config files in this mod. */
    public static final File CONFIG_DIR = new File( FMLPaths.CONFIGDIR.get().toFile(), "FatherToast/" + DeadlyWorld.MOD_ID + "/" );
    
    public static final BlocksConfig BLOCKS = new BlocksConfig( CONFIG_DIR, "blocks" );
    public static final GeneralConfig GENERAL = new GeneralConfig( CONFIG_DIR, "general" );
    
    /** Mapping of each dimension type to its config. */
    private static HashMap<RegistryKey<World>, DimensionConfigGroup> DIMENSIONS;
    private static DimensionConfigGroup OVERWORLD_CONFIGS;
    
    /**
     * @return The group of configs associated with the given world's dimension.
     * <p>
     * Returns the overworld's config if the requested dimension config was not properly loaded;
     * throws an exception if dimension configs have not yet been loaded.
     */
    public static DimensionConfigGroup getDimensionConfigs( World world ) {
        return getDimensionConfigs( world.dimension() );
    }
    
    /**
     * @return The group of configs associated with the given dimension type key.
     * <p>
     * Returns the overworld's config if the requested dimension config was not properly loaded;
     * throws an exception if dimension configs have not yet been loaded.
     */
    public static DimensionConfigGroup getDimensionConfigs( RegistryKey<World> dimension ) {
        if( OVERWORLD_CONFIGS == null ) {
            throw new IllegalStateException( "Attempted to access dimension configs before any have been loaded." );
        }
        final DimensionConfigGroup configs = DIMENSIONS.get( dimension );
        return configs == null ? OVERWORLD_CONFIGS : configs;
    }
    
    /** Performs initial loading of configs in this mod that need to be loaded immediately. */
    public static void preInitialize() {
        AbstractConfigField.loadingCategory = null;
        
        BLOCKS.SPEC.initialize();
    }
    
    /** Performs initial loading of configs in this mod. */
    public static void initialize() {
        AbstractConfigField.loadingCategory = null;
        
        GENERAL.SPEC.initialize();
        
        // Loading overworld config before the
        // others to prevent our world gen features
        // from exploding the universe with anger
        OVERWORLD_CONFIGS = new DimensionConfigGroup( CONFIG_DIR, World.OVERWORLD );
        OVERWORLD_CONFIGS.initialize();
        DIMENSIONS = new HashMap<>();
        DIMENSIONS.put( World.OVERWORLD, OVERWORLD_CONFIGS );
    }
    
    /** Performs loading of configs in this mod that depend on dynamic registries. */
    public static void initializeDynamic( MinecraftServer server ) {
        AbstractConfigField.loadingCategory = null;
        
        //TODO Actually register dimensions dynamically - DynamicRegistries.builtin().dimensionTypes()?
        //  - Note; should make sure overworld is loaded no matter what, and maybe store a reference to it to use as a default in case of issues

        final List<RegistryKey<World>> temp = new ArrayList<>( server.levelKeys() );
        
        // Keep track of opened files so we can close any we don't need
        final HashMap<RegistryKey<World>, DimensionConfigGroup> previousDims = DIMENSIONS;
        
        // Load dimension configs
        DIMENSIONS = new HashMap<>();
        
        for( RegistryKey<World> dimension : temp ) {
            // Use previously opened config if available and remove to prevent it from being closed
            final DimensionConfigGroup dimConfigs = previousDims != null && previousDims.containsKey( dimension ) ?
                    previousDims.remove( dimension ) :
                    new DimensionConfigGroup( CONFIG_DIR, dimension );
            
            // Load it and store the reference
            dimConfigs.initialize();
            DIMENSIONS.put( dimension, dimConfigs );
        }
        if( OVERWORLD_CONFIGS == null ) { OVERWORLD_CONFIGS = DIMENSIONS.get( World.OVERWORLD ); }
        
        // Close any configs no longer being used
        if( previousDims != null && !previousDims.isEmpty() ) {
            for( DimensionConfigGroup dimConfigs : previousDims.values() ) { dimConfigs.destroy(); }
        }
    }
    
    /**
     * Represents one config file that contains a reference for each configurable value within and a specification
     * that defines the file's format.
     */
    public static abstract class AbstractConfig {
        /** The spec used by this config that defines the file's format. */
        public final ToastConfigSpec SPEC;
        
        AbstractConfig( File dir, String fileName, String... fileDescription ) {
            AbstractConfigField.loadingCategory = "";
            SPEC = new ToastConfigSpec( dir, fileName );
            SPEC.header( TomlHelper.newComment( fileDescription ) );
        }
    }
    
    /**
     * Represents one config file that contains a reference for each configurable value within and a specification
     * that defines the file's format.
     */
    public static abstract class AbstractCategory {
        /** The spec used by this config that defines the file's format. */
        protected final ToastConfigSpec SPEC;
        
        AbstractCategory( ToastConfigSpec parent, String name, String... categoryDescription ) {
            AbstractConfigField.loadingCategory = name + ".";
            SPEC = parent;
            SPEC.category( name, TomlHelper.newComment( categoryDescription ) );
        }
    }
    
    /** @return The string with all spaces replaced with underscores. */
    static String noSpaces( String str ) { return str.replace( ' ', '_' ); }
}