package fathertoast.deadlyworld.common.core.config;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.field.AbstractConfigField;
import fathertoast.deadlyworld.common.core.config.file.ToastConfigSpec;
import fathertoast.deadlyworld.common.core.config.file.TomlHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    public static final GeneralConfig GENERAL = new GeneralConfig( CONFIG_DIR, "general" );
    
    /** Mapping of each dimension type to its config. */
    private static HashMap<RegistryKey<DimensionType>, DimensionConfigGroup> DIMENSIONS;
    
    /** Performs initial loading of configs in this mod. */
    public static void initialize() {
        AbstractConfigField.loadingCategory = null;
        
        GENERAL.SPEC.initialize();
    }
    
    /** Performs loading of configs in this mod that depend on dynamic registries. */
    public static void initializeDynamic() {
        AbstractConfigField.loadingCategory = null;
        
        //TODO Actually register dimensions dynamically - DynamicRegistries.builtin().dimensionTypes()?
        //  - Note; should make sure overworld is loaded no matter what, and maybe store a reference to it to use as a default in case of issues
        final List<RegistryKey<DimensionType>> temp = Arrays.asList( DimensionType.OVERWORLD_LOCATION, DimensionType.NETHER_LOCATION );
        
        // Keep track of opened files so we can close any we don't need
        final HashMap<RegistryKey<DimensionType>, DimensionConfigGroup> previousDims = DIMENSIONS;
        
        // Load dimension configs
        DIMENSIONS = new HashMap<>();
        for( RegistryKey<DimensionType> dimension : temp ) {
            // Use previously opened config if available and remove to prevent it from being closed
            final DimensionConfigGroup dimConfigs = previousDims != null && previousDims.containsKey( dimension ) ?
                    previousDims.remove( dimension ) :
                    new DimensionConfigGroup( CONFIG_DIR, dimension );
            
            // Load it and store the reference
            dimConfigs.initialize();
            DIMENSIONS.put( dimension, dimConfigs );
        }
        
        // Close any configs no longer being used
        if( previousDims != null && !previousDims.isEmpty() ) {
            for( DimensionConfigGroup dimConfigs : previousDims.values() ) { dimConfigs.destroy(); }
        }
    }
    
    public static DimensionConfigGroup getDimensionConfigs( RegistryKey<DimensionType> dimension ) { return DIMENSIONS.get( dimension ); }
    
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
        
        /** @return The string with all spaces replaced with underscores. */
        protected final String noSpaces( String str ) { return str.replace( ' ', '_' ); }
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
        
        /** @return The string with all spaces replaced with underscores. */
        protected final String noSpaces( String str ) { return str.replace( ' ', '_' ); }
    }
}