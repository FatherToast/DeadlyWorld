package fathertoast.deadlyworld.common.block.sea_mine;

import fathertoast.deadlyworld.common.config.dimension.DimensionConfigGroup;
import fathertoast.deadlyworld.common.config.dimension.SeaMineConfig;
import fathertoast.deadlyworld.common.core.DeadlyWorld;

import java.util.function.Function;
import java.util.function.Supplier;

public enum SeaMineType {

    NORMAL( "normal", ( dimConfigs ) -> dimConfigs.SEA_MINES.NORMAL ),
    PUFFER( "puffer", ( dimConfigs ) -> dimConfigs.SEA_MINES.PUFFER ),
    GUARDIAN( "guardian", ( dimConfigs ) -> dimConfigs.SEA_MINES.GUARDIAN );

    public static final String BLOCK_CATEGORY = "sea_mine";

    /** The unique id for this sea mine type. This is used to save and load from disk. */
    private final String id;
    private final String displayName;
    /** A function that returns the feature config associated with this sea mine type for a given dimension config. */
    private final Function<DimensionConfigGroup, SeaMineConfig.SeaMineCategory> configGetter;


    SeaMineType( String name, Function<DimensionConfigGroup, SeaMineConfig.SeaMineCategory> configFunction ) {
        this( name, name.replace( "_", " " ) + " sea mines", configFunction );
    }

    SeaMineType( String name, String prettyName, Function<DimensionConfigGroup, SeaMineConfig.SeaMineCategory> configFunction ) {
        id = name;
        displayName = prettyName;
        configGetter = configFunction;
    }

    public String getDisplayName() { return displayName; }

    /** @return A Supplier of the Sea Mine Block to register for this Sea Mine Type */
    public Supplier<SeaMineBlock> getBlock() { return () -> new SeaMineBlock( this ); }

    /**
     * Returns a SeaMineType from ID.
     * If there exists no SeaMineType with the given ID, default to {@link SeaMineType#NORMAL}
     *
     * @param ID The ID of the SeaMineType.
     * @return A SeaMineType matching the given ID.
     */
    public static SeaMineType getFromID( String ID ) {
        for( SeaMineType seaMineType : values() ) {
            if( seaMineType.toString().equals( ID ) ) {
                return seaMineType;
            }
        }
        return NORMAL;
    }

    @Override
    public String toString() { return id; }

    public SeaMineConfig.SeaMineCategory getFeatureConfig( DimensionConfigGroup dimConfigs ) { return configGetter.apply( dimConfigs ); }

    public static SeaMineType fromIndex( int index ) {
        if( index < 0 || index >= values().length ) {
            DeadlyWorld.LOG.warn( "Attempted to load invalid sea mine type from index '{}'", index );
            return NORMAL;
        }
        return values()[index];
    }
}
