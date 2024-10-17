package fathertoast.deadlyworld.common.config.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.EntityListField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.EntityEntry;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WeightedEntityListField extends EntityListField {
    
    public static WeightedEntityList fromNBT( ListTag tag, int reqValues, double minVal, double maxVal ) {
        EntityEntry[] entries = new EntityEntry[tag.size()];
        for( int i = 0; i < entries.length; i++ ) {
            entries[i] = parseEntry( tag.getString( i ), null, reqValues, minVal, maxVal,
                    WeightedEntityListField.class, "<nbt>" );
        }
        return new WeightedEntityList( entries );
    }
    
    /** Creates a new field. */
    public WeightedEntityListField( String key, WeightedEntityList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** @return Returns the config field's value. */
    public WeightedEntityList get() { return (WeightedEntityList) value; }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public WeightedEntityList getValue() { return (WeightedEntityList) value; }
    
    /** @return The default value of this field. */
    @Override
    public WeightedEntityList getDefaultValue() { return (WeightedEntityList) valueDefault; }
    
    /**
     * Loads this field's value from the given value or raw toml. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value and print a warning explaining the change.
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        
        if( raw instanceof WeightedEntityList ) {
            value = (WeightedEntityList) raw;
        }
        else {
            List<String> list = TomlHelper.parseStringList( raw );
            List<EntityEntry> entryList = new ArrayList<>();
            for( String line : list ) {
                entryList.add( parseEntry( line, this, valueDefault.getRequiredValues(),
                        valueDefault.getMinValue(), valueDefault.getMaxValue(), getClass(), getKey() ) );
            }
            value = new WeightedEntityList( entryList );
        }
    }
    
    /** Parses a single entry line and returns the result. */
    private static EntityEntry parseEntry( final String line, @Nullable final WeightedEntityListField field, final int reqValues,
                                           final double minVal, final double maxVal, final Class<?> type, final String key ) {
        String modifiedLine = line;
        
        // Check if the entry should be "specific", i.e. check for entity class equality rather than instanceof
        final boolean extendable;
        if( line.startsWith( "~" ) ) {
            modifiedLine = line.substring( 1 );
            extendable = false;
        }
        else {
            extendable = true;
        }
        
        // Parse the entity-value array
        final String[] args = modifiedLine.split( " " );
        final ResourceLocation regKey;
        if( REG_KEY_DEFAULT.equalsIgnoreCase( args[0].trim() ) ) {
            // Handle the special case of a default entry
            regKey = null;
        }
        else {
            // Normal entry
            regKey = new ResourceLocation( args[0].trim() );
        }
        final List<Double> valuesList = new ArrayList<>();
        final int actualValues = args.length - 1;
        
        // Variable-value; just needs at least one value
        if( reqValues < 0 ) {
            if( actualValues < 1 ) {
                ConfigUtil.LOG.warn( "Entry has too few values for {} \"{}\"! Expected at least one value. " +
                                "Replacing missing value with 0. Invalid entry: {}",
                        type, key, line );
                valuesList.add( 0.0 );
            }
            else {
                // Parse all values
                for( int i = 1; i < args.length; i++ ) {
                    valuesList.add( parseValue( args[i], line, minVal, maxVal, type, key ) );
                }
            }
        }
        // Specified value; must have the exact number of values
        else {
            if( reqValues > actualValues ) {
                ConfigUtil.LOG.warn( "Entry has too few values for {} \"{}\"! " +
                                "Expected {} values, but detected {}. Replacing missing values with 0. Invalid entry: {}",
                        type, key, reqValues, actualValues, line );
            }
            else if( reqValues < actualValues ) {
                ConfigUtil.LOG.warn( "Entry has too many values for {} \"{}\"! " +
                                "Expected {} values, but detected {}. Deleting additional values. Invalid entry: {}",
                        type, key, reqValues, actualValues, line );
            }
            
            // Parse all values
            for( int i = 1; i < reqValues + 1; i++ ) {
                if( i < args.length ) {
                    valuesList.add( parseValue( args[i], line, minVal, maxVal, type, key ) );
                }
                else {
                    valuesList.add( 0.0 );
                }
            }
        }
        
        // Convert to array
        final double[] values = new double[valuesList.size()];
        for( int i = 0; i < values.length; i++ ) {
            values[i] = valuesList.get( i );
        }
        return new EntityEntry( field, regKey, extendable, values );
    }
    
    /** Parses a single value argument and returns a valid result. */
    private static double parseValue( final String arg, final String line,
                                      final double minVal, final double maxVal, final Class<?> type, final String key ) {
        // Try to parse the value
        double value;
        try {
            value = Double.parseDouble( arg );
        }
        catch( NumberFormatException ex ) {
            // This is thrown if the string is not a parsable number
            ConfigUtil.LOG.warn( "Invalid value for {} \"{}\"! Falling back to 0. Invalid entry: {}",
                    type, key, line );
            value = 0.0;
        }
        // Verify value is within range
        if( value < minVal ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is below the minimum ({})! Clamping value. Invalid value: {}",
                    type, key, minVal, value );
            value = minVal;
        }
        else if( value > maxVal ) {
            ConfigUtil.LOG.warn( "Value for {} \"{}\" is above the maximum ({})! Clamping value. Invalid value: {}",
                    type, key, maxVal, value );
            value = maxVal;
        }
        return value;
    }
}