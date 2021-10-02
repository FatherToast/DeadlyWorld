package fathertoast.deadlyworld.common.core.config;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection used to group multiple related configs into one simple group.
 */
public abstract class ConfigGroup {
    /** A list of all the configs contained within this group. */
    private final List<Config.AbstractConfig> GROUPED_CONFIGS = new ArrayList<>();
    
    /**
     * Convenience method. Used to add a config to this group and assign a field in a single line.
     *
     * @return The config that was just added.
     */
    protected <T extends Config.AbstractConfig> T group( T config ) {
        GROUPED_CONFIGS.add( config );
        return config;
    }
    
    /** Loads the config group from disk. */
    public void initialize() {
        for( Config.AbstractConfig config : GROUPED_CONFIGS ) { config.SPEC.initialize(); }
    }
    
    /** Called when the config group is no longer needed. */
    public void destroy() {
        for( Config.AbstractConfig config : GROUPED_CONFIGS ) { config.SPEC.destroy(); }
    }
}