package fathertoast.deadlyworld.common.block.pitfall;

import fathertoast.deadlyworld.common.block.IFeatureConfigProvider;
import fathertoast.deadlyworld.common.block.spike_trap.BaseSpikeTrapBlock;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.PitfallTrapConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.world.levelgen.trap.PitfallTrapFeature;
import net.minecraft.world.level.Level;

import java.util.function.Function;
import java.util.function.Supplier;

public enum PitfallTrapType implements IFeatureConfigProvider<PitfallTrapConfig.PitfallTrapTypeCategory> {

    SPIKES( "spikes", ( dimConfigs ) -> dimConfigs.PITFALL_TRAPS.SPIKES ),
    LAVA("lava", (dimConfigs ) -> dimConfigs.PITFALL_TRAPS.LAVA ),
    COBWEB( "cobweb", ( dimConfigs ) -> dimConfigs.PITFALL_TRAPS.COBWEB );

    public static final String BLOCK_CATEGORY = "pitfall_trap";

    /** The unique id for this pitfall trap type. This is used to save and load from disk. */
    private final String id;
    private final String displayName;

    /** A function that returns the feature config associated with this pitfall trap type for a given dimension config. */
    private final Function<DimensionConfigGroup, PitfallTrapConfig.PitfallTrapTypeCategory> configGetter;


    PitfallTrapType( String name, Function<DimensionConfigGroup, PitfallTrapConfig.PitfallTrapTypeCategory> configGetter ) {
        this( name, name.replace( "_", " " ) + " pitfall traps", configGetter );
    }

    PitfallTrapType( String name, String prettyName, Function<DimensionConfigGroup, PitfallTrapConfig.PitfallTrapTypeCategory> configGetter ) {
        id = name;
        displayName = prettyName;
        this.configGetter = configGetter;
    }

    public String getDisplayName() { return displayName; }

    /** @return A Supplier of the pitfall trap block to register for this spike trap type */
    public Supplier<PitfallTrapBlock> getBlock() { return () -> new PitfallTrapBlock( this ); }

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
     * If there exists no type with the given ID, default to {@link PitfallTrapType#SPIKES}
     *
     * @param ID The ID of the pitfall trap type.
     * @return A pitfall trap type matching the given ID.
     */
    public static PitfallTrapType getFromID( String ID ) {
        for( PitfallTrapType pitfallTrapType : values() ) {
            if( pitfallTrapType.toString().equals( ID ) ) {
                return pitfallTrapType;
            }
        }
        return SPIKES;
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
