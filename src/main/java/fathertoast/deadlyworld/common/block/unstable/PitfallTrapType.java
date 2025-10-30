package fathertoast.deadlyworld.common.block.unstable;

import fathertoast.deadlyworld.common.block.IFeatureConfigProvider;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.PitfallTrapConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Function;

public enum PitfallTrapType implements IFeatureConfigProvider<PitfallTrapConfig.PitfallTrapTypeCategory> {

    SPIKES( "spikes", ( dimConfigs ) -> dimConfigs.PITFALL_TRAPS.SPIKES ),
    LAVA("lava", (dimConfigs ) -> dimConfigs.PITFALL_TRAPS.LAVA ),
    COBWEB( "cobweb", ( dimConfigs ) -> dimConfigs.PITFALL_TRAPS.COBWEB );

    public static final String BLOCK_CATEGORY = "pitfall_trap";

    /** The unique id for this pitfall trap type. This is used to save and load from disk. */
    private final String id;

    /** A function that returns the feature config associated with this pitfall trap type for a given dimension config. */
    private final Function<DimensionConfigGroup, PitfallTrapConfig.PitfallTrapTypeCategory> configGetter;


    PitfallTrapType( String name, Function<DimensionConfigGroup, PitfallTrapConfig.PitfallTrapTypeCategory> configGetter ) {
        id = name;
        this.configGetter = configGetter;
    }

    @Override
    public PitfallTrapConfig.PitfallTrapTypeCategory getConfig( Level level ) {
        return configGetter.apply( Config.getDimensionConfigs( level ) );
    }

    @Override
    public PitfallTrapConfig.PitfallTrapTypeCategory getConfig( DimensionConfigGroup dimConfig ) {
        return configGetter.apply( dimConfig );
    }

    /**
     * Returns a PitfallTrapType from ID.
     * If there exists no type with the given ID, we return null.
     *
     * @param ID The ID of the pitfall trap type.
     * @return A pitfall trap type matching the given ID.
     */
    @Nullable
    public static PitfallTrapType getFromID( String ID ) {
        for( PitfallTrapType pitfallTrapType : values() ) {
            if( pitfallTrapType.toString().equals( ID ) ) {
                return pitfallTrapType;
            }
        }
        return null;
    }

    @Override
    public String toString() { return id; }

    public static PitfallTrapType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to fetch invalid pitfall trap type from index '{}'", index );
            return SPIKES;
        }
        return values()[index];
    }
}
