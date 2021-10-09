package fathertoast.deadlyworld.common.core.config;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;

import java.io.File;

/**
 * Groups together every config file used for a single dimension.
 */
public class DimensionConfigGroup extends ConfigGroup {
    
    public final RegistryKey<DimensionType> DIMENSION;
    
    public final MainDimensionConfig MAIN;
    public final SpawnerConfig SPAWNERS;
    
    DimensionConfigGroup( File dir, RegistryKey<DimensionType> dimension ) {
        DIMENSION = dimension;
        final File dimensionDir = new File( dir,
                "dimensions/" + dimension.location().getNamespace() + "/" + dimension.location().getPath() );
        
        MAIN = group( new MainDimensionConfig( dimensionDir, this ) );
        SPAWNERS = group( new SpawnerConfig( dimensionDir, this ) );
    }
    
    /** @return The short name for this dimension (e.g. "'the_nether' dimension"). */
    public String dimensionName() { return "'" + DIMENSION.location().getPath() + "' dimension"; }
    
    /** @return The long name for this dimension (e.g. "'the_nether' dimension from 'minecraft'"). */
    public String longDimensionName() {
        return "'" + DIMENSION.location().getPath() + "' dimension from '" + DIMENSION.location().getNamespace() + "'";
    }
}