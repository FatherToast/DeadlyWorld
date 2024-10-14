package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.ConfigManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Groups together every config file used for a single dimension.
 */
public class DimensionConfigGroup extends ConfigGroup {
    
    public final ResourceKey<Level> DIMENSION;
    
    public final MainDimensionConfig MAIN;
    public final SpawnerConfig SPAWNERS;
    public final FloorTrapConfig FLOOR_TRAPS;
    public final TowerDispenserConfig TOWER_DISPENSERS;
    
    DimensionConfigGroup( ConfigManager manager, ResourceKey<Level> dimension ) {
        DIMENSION = dimension;
        //final File dimensionDir = new File( dir,
        //        "dimensions/" + dimension.location().getNamespace() + "/" + dimension.location().getPath() );
        // TODO Have below organized in folder: configs/DeadlyWorld/dimensions/<modid>/<dimension>/
        
        MAIN = group( new MainDimensionConfig( manager, this ) );
        SPAWNERS = group( new SpawnerConfig( manager, this ) );
        FLOOR_TRAPS = group( new FloorTrapConfig( manager, this ) );
        TOWER_DISPENSERS = group( new TowerDispenserConfig( manager, this ) );
    }
    
    /** @return The short name for this dimension (e.g. "'the_nether' dimension"). */
    public String dimensionName() { return "'" + DIMENSION.location().getPath() + "' dimension"; }
    
    /** @return The long name for this dimension (e.g. "'the_nether' dimension from 'minecraft'"). */
    public String longDimensionName() {
        return "'" + DIMENSION.location().getPath() + "' dimension from '" + DIMENSION.location().getNamespace() + "'";
    }
}