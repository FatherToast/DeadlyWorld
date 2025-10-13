package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.deadlyworld.common.config.ConfigGroup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Groups together every config file used for a single dimension.
 */
public class DimensionConfigGroup extends ConfigGroup {
    public final ResourceKey<Level> DIMENSION;
    
    public final SpawnerConfig SPAWNERS;
    public final TrapConfig TRAPS;
    public final TowerConfig TOWER_DISPENSERS;
    public final EnvHazardConfig ENV_HAZARDS;
    public final SimpleDungeonConfig SIMPLE_DUNGEONS;
    
    public DimensionConfigGroup( ConfigManager manager, ResourceKey<Level> dimension ) {
        DIMENSION = dimension;
        
        // Organized in folder: configs/DeadlyWorld/worldgen/<modid>/<dimension>/
        final String dir = "worldgen/" + dimension.location().getNamespace() + "/" + dimension.location().getPath() + "/";
        
        SPAWNERS = group( new SpawnerConfig( manager, dir, this ) );
        TRAPS = group( new TrapConfig( manager, dir, this ) );
        TOWER_DISPENSERS = group( new TowerConfig( manager, dir, this ) );
        ENV_HAZARDS = group( new EnvHazardConfig( manager, dir, this ) );
        SIMPLE_DUNGEONS = group( new SimpleDungeonConfig( manager, dir, this ) );
    }
    
    /** @return The short name for this dimension (e.g. "'the_nether' dimension"). */
    public String dimensionName() { return "'" + DIMENSION.location().getPath() + "' dimension"; }
    
    /** @return The long name for this dimension (e.g. "'the_nether' dimension from 'minecraft'"). */
    public String longDimensionName() {
        return dimensionName() + " from '" + DIMENSION.location().getNamespace() + "'";
    }
}