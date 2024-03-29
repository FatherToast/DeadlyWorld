package fathertoast.deadlyworld.common.core.config;

import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.io.File;

/**
 * Groups together every config file used for a single dimension.
 */
public class DimensionConfigGroup extends ConfigGroup {
    
    public final RegistryKey<World> DIMENSION;
    
    public final MainDimensionConfig MAIN;
    public final SpawnerConfig SPAWNERS;
    public final FloorTrapConfig FLOOR_TRAPS;
    public final TowerDispenserConfig TOWER_DISPENSERS;
    
    DimensionConfigGroup( File dir, RegistryKey<World> dimension ) {
        DIMENSION = dimension;
        final File dimensionDir = new File( dir,
                "dimensions/" + dimension.location().getNamespace() + "/" + dimension.location().getPath() );
        
        MAIN = group( new MainDimensionConfig( dimensionDir, this ) );
        SPAWNERS = group( new SpawnerConfig( dimensionDir, this ) );
        FLOOR_TRAPS = group( new FloorTrapConfig( dimensionDir, this ) );
        TOWER_DISPENSERS = group( new TowerDispenserConfig( dimensionDir, this ));
    }
    
    /** @return The short name for this dimension (e.g. "'the_nether' dimension"). */
    public String dimensionName() { return "'" + DIMENSION.location().getPath() + "' dimension"; }
    
    /** @return The long name for this dimension (e.g. "'the_nether' dimension from 'minecraft'"). */
    public String longDimensionName() {
        return "'" + DIMENSION.location().getPath() + "' dimension from '" + DIMENSION.location().getNamespace() + "'";
    }
}