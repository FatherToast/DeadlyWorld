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
    
    public final ChestConfig CHESTS;
    public final SpawnerConfig SPAWNERS;
    public final TowerConfig TOWERS;
    public final FloorTrapConfig FLOOR_TRAPS;
    public final WaterTrapConfig WATER_TRAPS;
    public final SpikeTrapConfig SPIKE_TRAPS;
    public final EnvHazardConfig ENV_HAZARDS;
    public final DungeonConfig DUNGEONS;
    
    public DimensionConfigGroup( ConfigManager manager, ResourceKey<Level> dimension ) {
        DIMENSION = dimension;
        
        // Organized in folder: configs/DeadlyWorld/worldgen/<modid>/<dimension>/
        final String dir = "worldgen/" + dimension.location().getNamespace() + "/" + dimension.location().getPath() + "/";
        
        CHESTS = group( new ChestConfig( manager, dir, this ) );
        SPAWNERS = group( new SpawnerConfig( manager, dir, this ) );
        TOWERS = group( new TowerConfig( manager, dir, this ) );
        FLOOR_TRAPS = group( new FloorTrapConfig( manager, dir, this ) );
        WATER_TRAPS = group( new WaterTrapConfig( manager, dir, this ) );
        SPIKE_TRAPS = group( new SpikeTrapConfig( manager, dir, this ) );
        ENV_HAZARDS = group( new EnvHazardConfig( manager, dir, this ) );
        DUNGEONS = group( new DungeonConfig( manager, dir, this ) );
    }
    
    /** @return The short name for this dimension (e.g. "'the_nether' dimension"). */
    public String dimensionName() { return "'" + DIMENSION.location().getPath() + "' dimension"; }
    
    /** @return The long name for this dimension (e.g. "'the_nether' dimension from 'minecraft'"). */
    public String longDimensionName() {
        return dimensionName() + " from '" + DIMENSION.location().getNamespace() + "'";
    }
}