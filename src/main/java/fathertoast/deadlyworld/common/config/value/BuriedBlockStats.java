package fathertoast.deadlyworld.common.config.value;

import fathertoast.crust.api.config.common.value.collection.value.DoubleValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.IntValueCodec;
import fathertoast.crust.api.config.common.value.collection.value.MultiValueCodec;

/**
 * A simple multi-value codec.
 * When loaded as a value, holds the minimum and maximum y-values and number of placements for a buried block.
 */
public class BuriedBlockStats extends MultiValueCodec<BuriedBlockStats> {
    /** The standard mob effect stats codec that defaults to 0 duration and 0 amplitude. */
    public static final BuriedBlockStats CODEC = new BuriedBlockStats();
    
    /** @return New mob effect stats with the provided default values. */
    public static BuriedBlockStats of( int minY, int maxY, double placements ) {
        return new BuriedBlockStats( minY, maxY, placements );
    }
    
    
    /** The minimum y-value to generate at. */
    public final SubValue<Integer> minY = subValue( IntValueCodec.ANY,
            IntValueCodec.ANY.getFormat( "MinY" ) );
    
    /** The maximum y-value to generate at. */
    public final SubValue<Integer> maxY = subValue( IntValueCodec.ANY,
            IntValueCodec.ANY.getFormat( "MaxY" ) );
    
    /** The number of placement attempts to make per chunk. */
    public final SubValue<Double> placements = subValue( DoubleValueCodec.NON_NEGATIVE,
            DoubleValueCodec.NON_NEGATIVE.getFormat( "Placements" ) );
    
    /** The constructor used to define default values. */
    public BuriedBlockStats( int min, int max, double places ) {
        minY.set( min );
        maxY.set( max );
        placements.set( places );
    }
    
    /** The no-args constructor used to create the codec "singleton" and for value loading. */
    public BuriedBlockStats() {}
}