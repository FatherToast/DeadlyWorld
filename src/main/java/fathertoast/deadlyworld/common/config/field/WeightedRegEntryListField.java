package fathertoast.deadlyworld.common.config.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.RegistryEntryValueListField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WeightedRegEntryListField<T> extends RegistryEntryValueListField<T> {
    
    /** Creates a new field. */
    public WeightedRegEntryListField( String key, WeightedRegEntryList<T> defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    /** @return Returns the config field's value. */
    public WeightedRegEntryList<T> get() { return (WeightedRegEntryList<T>) value; }
    
    /** @return The value that should be assigned to this field in the config file. */
    @Override
    @Nullable
    public WeightedRegEntryList<T> getValue() { return (WeightedRegEntryList<T>) value; }
    
    /** @return The default value of this field. */
    @Override
    public WeightedRegEntryList<T> getDefaultValue() { return (WeightedRegEntryList<T>) valueDefault; }
    
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
        
        if( raw instanceof WeightedRegEntryList ) {
            value = (WeightedRegEntryList) raw;
        }
        else {
            List<String> list = TomlHelper.parseStringList( raw );
            List<RegistryValueEntry<T>> entryList = new ArrayList<>();
            for( String line : list ) {
                RegistryValueEntry<T> entry = parseEntry( line, this, valueDefault.getRequiredValues(),
                        valueDefault.getMinValue(), valueDefault.getMaxValue(), getClass(), getKey() );
                
                if( entry != null ) entryList.add( entry );
            }
            value = new WeightedRegEntryList<>( valueDefault.getRegistry(), entryList );
        }
    }
    
    /** Parses a single entry line and returns the result. */
    @Nullable
    private RegistryValueEntry<T> parseEntry( final String line, @Nullable final WeightedRegEntryListField<T> field, final int reqValues,
                                              final double minVal, final double maxVal, final Class<?> type, final String key ) {
        // Parse the value array
        final String[] args = line.split( " " );
        final ResourceLocation regKey;
        if( "default".equalsIgnoreCase( args[0].trim() ) ) {
            // Default entry not allowed
            return null;
        }
        else {
            // Normal entry
            regKey = ResourceLocation.parse( args[0].trim() );
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
        return new RegistryValueEntry<>( field, regKey, values );
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