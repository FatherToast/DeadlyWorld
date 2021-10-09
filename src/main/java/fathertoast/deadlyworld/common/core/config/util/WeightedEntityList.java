package fathertoast.deadlyworld.common.core.config.util;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedEntityList extends EntityList {
    /** The total weight of all entity entries in the list. */
    private int totalWeight;
    
    /**
     * Create a new entity list from a list of entries.
     * <p>
     * By default, entity lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    public WeightedEntityList( List<EntityEntry> entries ) { super( entries ); }
    
    /**
     * Create a new entity list from an array of entries. Used for creating default configs.
     * <p>
     * By default, entity lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    public WeightedEntityList( EntityEntry... entries ) { super( entries ); }
    
    // Enforce the rules for the weighted list regardless of constructor used
    {
        // Use the direct super setters to avoid throwing UnsupportedOperationException
        super.setMultiValue( 1 );
        super.setRange( 0.0, Double.POSITIVE_INFINITY );
    }
    
    /** @return A list of strings that will represent this object when written to a toml file. */
    @Override
    public List<String> toStringList() {
        // Create a list of the entries in string format
        final List<String> list = new ArrayList<>( ENTRIES.length );
        for( EntityEntry entry : ENTRIES ) {
            list.add( entry.toString( true ) );
        }
        return list;
    }
    
    /** Updates the total weight for this weighted entity list. */
    public void calculateTotalWeight() {
        int newWeight = 0;
        for( EntityEntry entry : ENTRIES ) {
            if( entry.VALUES.length > 0 && entry.VALUES[0] > 0 ) {
                if( (double) newWeight + entry.VALUES[0] > Integer.MAX_VALUE ) {
                    DeadlyWorld.LOG.error( "Weighted entity list has a total weight greater than the maximum ({})! List:{}",
                            Integer.MAX_VALUE, this );
                    newWeight = Integer.MAX_VALUE;
                    break;
                }
                newWeight += (int) entry.VALUES[0];
            }
        }
        totalWeight = newWeight;
    }
    
    /** @return Randomly picks an entity type from this weighted entity list. Null if no entries can be picked from the list. */
    public EntityType<? extends Entity> next( Random random ) {
        if( totalWeight > 0 ) {
            int choice = random.nextInt( totalWeight );
            for( EntityEntry entry : ENTRIES ) {
                if( entry.VALUES.length > 0 && entry.VALUES[0] > 0 ) {
                    choice -= (int) entry.VALUES[0];
                    if( choice < 0 ) return entry.TYPE;
                }
            }
            DeadlyWorld.LOG.error( "Something has gone dramatically wrong with a weighted entity list! Weight={}; List:{}",
                    totalWeight, this );
        }
        return null;
    }
    
    /** Marks this entity list as a simple weighted listing; exactly one non-negative integer weight per entry. */
    public EntityList setWeightedRandom() {
        return setSingleValue().setRangePos();
    }
    
    /** Marks this entity list as multi-value; each entry will have the specified number of values. */
    @Override
    public EntityList setMultiValue( int numberOfValues ) {
        throw new UnsupportedOperationException( "Cannot change; weighted entity lists only support a single value!" );
    }
    
    /** Bounds entry values in this list to the specified limits, inclusive. Note that 0 must be within the range. */
    @Override
    public EntityList setRange( double min, double max ) {
        throw new UnsupportedOperationException( "Cannot change; weighted entity lists only support the non-negative range (>= 0)!" );
    }
}