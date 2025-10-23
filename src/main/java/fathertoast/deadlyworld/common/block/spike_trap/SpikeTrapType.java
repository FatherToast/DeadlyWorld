package fathertoast.deadlyworld.common.block.spike_trap;

import fathertoast.deadlyworld.common.block.IFeatureConfigProvider;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.SpikeTrapConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Function;
import java.util.function.Supplier;

public enum SpikeTrapType implements IFeatureConfigProvider<SpikeTrapConfig.SpikeTrapTypeCategory> {

    NORMAL( "normal", ( dimConfigs ) -> dimConfigs.SPIKE_TRAPS.NORMAL );

    public static final String BLOCK_CATEGORY = "spike_trap";

    /** The unique id for this sea mine type. This is used to save and load from disk. */
    private final String id;
    private final String displayName;

    /** A function that returns the feature config associated with this spike trap type for a given dimension config. */
    private final Function<DimensionConfigGroup, SpikeTrapConfig.SpikeTrapTypeCategory> configGetter;


    SpikeTrapType( String name, Function<DimensionConfigGroup, SpikeTrapConfig.SpikeTrapTypeCategory> configFunction ) {
        this( name, name.replace( "_", " " ) + " spike traps", configFunction );
    }

    SpikeTrapType( String name, String prettyName, Function<DimensionConfigGroup, SpikeTrapConfig.SpikeTrapTypeCategory> configFunction ) {
        id = name;
        displayName = prettyName;
        configGetter = configFunction;
    }

    public String getDisplayName() { return displayName; }

    /** @return A Supplier of the Spike FloorTrap Block to register for this Sea Mine Type */
    public Supplier<SpikeTrapBlock> getBlock() { return () -> new SpikeTrapBlock( this ); }

    @Override
    public SpikeTrapConfig.SpikeTrapTypeCategory getConfig( Level level ) {
        return configGetter.apply( Config.getDimensionConfigs( level ) );
    }

    @Override
    public SpikeTrapConfig.SpikeTrapTypeCategory getConfig( DimensionConfigGroup dimConfig ) {
        return configGetter.apply( dimConfig );
    }

    /**
     * Returns a SpikeTrapType from ID.
     * If there exists no SpikeTrapType with the given ID, default to {@link SpikeTrapType#NORMAL}
     *
     * @param ID The ID of the SpikeTrapType.
     * @return A SpikeTrapType matching the given ID.
     */
    public static SpikeTrapType getFromID( String ID ) {
        for( SpikeTrapType spikeTrapType : values() ) {
            if( spikeTrapType.toString().equals( ID ) ) {
                return spikeTrapType;
            }
        }
        return NORMAL;
    }

    @Override
    public String toString() { return id; }

    public static SpikeTrapType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to fetch invalid spike trap type from index '{}'", index );
            return NORMAL;
        }
        return values()[index];
    }
}
