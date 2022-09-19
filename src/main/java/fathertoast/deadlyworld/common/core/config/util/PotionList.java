package fathertoast.deadlyworld.common.core.config.util;

import fathertoast.deadlyworld.common.core.config.field.IStringArray;
import fathertoast.deadlyworld.common.core.config.file.TomlHelper;
import net.minecraft.potion.Effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PotionList implements IStringArray {
    /** The effect-value entries in this list. */
    protected final PotionEntry[] ENTRIES;

    /** The number of values each entry must have. If this is negative, then entries may have any non-zero number of values. */
    private int entryValues = -1;
    /** The minimum value accepted for entry values in this list. */
    private double minValue = Double.NEGATIVE_INFINITY;
    /** The maximum value accepted for entry values in this list. */
    private double maxValue = Double.POSITIVE_INFINITY;

    /**
     * Create a new entity list from a list of entries.
     * <p>
     * By default, entity lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    public PotionList( List<PotionEntry> entries ) { this( entries.toArray( new PotionEntry[0] ) ); }

    /**
     * Create a new entity list from an array of entries. Used for creating default configs.
     * <p>
     * By default, entity lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    public PotionList( PotionEntry... entries ) { ENTRIES = entries; }

    /** @return A string representation of this object. */
    @Override
    public String toString() { return TomlHelper.toLiteral( toStringList().toArray() ); }

    /** @return Returns true if this object has the same value as another object. */
    @Override
    public boolean equals( Object other ) {
        if( !(other instanceof PotionList) ) return false;
        // Compare by the string list view of the object
        return toStringList().equals( ((PotionList) other).toStringList() );
    }

    /** @return A list of strings that will represent this object when written to a toml file. */
    @Override
    public List<String> toStringList() {
        // Create a list of the entries in string format
        final List<String> list = new ArrayList<>( ENTRIES.length );
        for( PotionEntry entry : ENTRIES ) {
            list.add( entry.toString() );
        }
        return list;
    }

    /** @return True if the potion effect is contained in this list. */
    public boolean contains( Effect effect ) {
        final PotionEntry targetEntry = new PotionEntry( effect );
        for( PotionEntry currentEntry : ENTRIES ) {
            if( currentEntry.contains( targetEntry ) )
                return true;
        }
        return false;
    }

    /** @return True if this list contains no entries. */
    public boolean isEmpty() {
        return ENTRIES.length == 0;
    }

    /** @return All potion effect entries in this entity list. */
    public PotionEntry[] getAllEntries() {
        return this.ENTRIES;
    }

    /**
     * @param effect The potion effect to retrieve values for.
     * @return The array of values of the given effect. If an entry for the effect doesn't exist, return null.
     */
    public double[] getValues( Effect effect ) {
        for( PotionEntry currentEntry : ENTRIES ) {
            if (currentEntry.EFFECT == effect)
                return currentEntry.VALUES;
        }
        return null;
    }

    /**
     * @param effect The effect to retrieve a value for.
     * @return The first value in the best-match entry's value array. Returns 0 if the entity is not contained in this
     * entity list or has no values specified. This should only be used for 'single value' lists.
     * @see #setSingleValue()
     * @see #setSinglePercent()
     */
    public double getValue( Effect effect ) {
        final double[] values = getValues( effect );
        return values == null || values.length < 1 ? 0.0 : values[0];
    }

    /**
     * @param effect The effect to roll a value for.
     * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the entity
     * is not contained in this entity list or has no values specified. This should only be used for 'single percent' lists.
     * @see #setSinglePercent()
     */
    public boolean rollChance( Effect effect, Random random ) {
        return ENTRIES.length > 0 && effect != null && random.nextDouble() < getValue( effect );
    }

    /** Marks this entity list as a simple percentage listing; exactly one percent (0 to 1) per entry. */
    public PotionList setSinglePercent() { return setSingleValue().setRange0to1(); }

    /** Marks this entity list as identification only; no values will be linked to any entries. */
    public PotionList setNoValues() { return setMultiValue( 0 ); }

    /** Marks this entity list as single-value; each entry will have exactly one value. */
    public PotionList setSingleValue() { return setMultiValue( 1 ); }

    /** Marks this entity list as multi-value; each entry will have the specified number of values. */
    public PotionList setMultiValue( int numberOfValues ) {
        entryValues = numberOfValues;
        return this;
    }

    /** Bounds entry values in this list between 0 and 1, inclusive. */
    public PotionList setRange0to1() { return setRange( 0.0, 1.0 ); }

    /** Bounds entry values in this list to any positive value (>= +0). */
    public PotionList setRangePos() { return setRange( 0.0, Double.POSITIVE_INFINITY ); }

    /** Bounds entry values in this list to the specified limits, inclusive. Note that 0 must be within the range. */
    public PotionList setRange( double min, double max ) {
        minValue = min;
        maxValue = max;
        return this;
    }

    /**
     * @return The number of values that must be included in each entry.
     * A negative value implies any non-zero number of values is allowed.
     */
    public int getRequiredValues() { return entryValues; }

    /** @return The minimum value that can be given to entry values. */
    public double getMinValue() { return minValue; }

    /** @return The maximum value that can be given to entry values. */
    public double getMaxValue() { return maxValue; }
}
