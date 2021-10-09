package fathertoast.deadlyworld.common.core.config.field;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.file.TomlHelper;
import fathertoast.deadlyworld.common.core.config.util.EntityEntry;
import fathertoast.deadlyworld.common.core.config.util.EntityList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an entity list value.
 */
public abstract class AbstractEntityListField<T extends EntityList> extends GenericField<T> {
    /** Creates a new field. */
    public AbstractEntityListField( String key, T defaultValue, String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    public void appendFieldInfo( List<String> comment ) {
        // Number of values to include
        final int reqValues = valueDefault.getRequiredValues();
        final String fieldFormat;
        if( reqValues < 0 ) {
            // Variable number of values
            fieldFormat = "[ \"namespace:entity_type value1 value2 ...\", ... ]";
        }
        else {
            // Specific number of values
            StringBuilder format = new StringBuilder( "[ \"namespace:entity_type " );
            for( int i = 1; i <= reqValues; i++ ) {
                format.append( "value" );
                if( reqValues > 1 ) {
                    format.append( i );
                }
                format.append( " " );
            }
            format.deleteCharAt( format.length() - 1 ).append( "\", ... ]" );
            fieldFormat = format.toString();
        }
        comment.add( TomlHelper.fieldInfoFormat( "Entity List", valueDefault, fieldFormat ) );
        
        // Range for values, if applicable
        if( reqValues != 0 ) {
            comment.add( "   Range for Values: " + getFieldRange() );
        }
        
    }
    
    /** @return The string representation for the field range to use. */
    protected String getFieldRange() { return TomlHelper.fieldRange( valueDefault.getMinValue(), valueDefault.getMaxValue() ); }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value
     */
    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        List<String> list = TomlHelper.parseStringList( raw );
        List<EntityEntry> entryList = new ArrayList<>();
        for( String line : list ) {
            EntityEntry entry = parseEntry( line );
            if( entry != null ) {
                entryList.add( entry );
            }
        }
        value = createNewList( entryList );
    }
    
    /** @return A newly constructed entity list from the given entries. */
    protected abstract T createNewList( List<EntityEntry> entryList );
    
    /** Parses a single entry line and returns a valid result if possible, or null if the entry is completely invalid. */
    @Nullable
    protected EntityEntry parseEntry( final String line ) {
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
        final EntityType<? extends Entity> entityType;
        if( EntityListField.REG_KEY_DEFAULT.equalsIgnoreCase( args[0].trim() ) ) {
            // Handle the special case of a default entry
            entityType = null;
        }
        else {
            // Normal entry
            final ResourceLocation regKey = new ResourceLocation( args[0].trim() );
            if( !ForgeRegistries.ENTITIES.containsKey( regKey ) ) {
                DeadlyWorld.LOG.warn( "Invalid entry for {} \"{}\"! Deleting entry. Invalid entry: {}",
                        getClass(), getKey(), line );
                return null;
            }
            entityType = ForgeRegistries.ENTITIES.getValue( regKey );
        }
        final List<Double> valuesList = new ArrayList<>();
        final int reqValues = valueDefault.getRequiredValues();
        final int actualValues = args.length - 1;
        
        // Variable-value; just needs at least one value
        if( reqValues < 0 ) {
            if( actualValues < 1 ) {
                DeadlyWorld.LOG.warn( "Entry has too few values for {} \"{}\"! Expected at least one value. " +
                                "Replacing missing value with 0. Invalid entry: {}",
                        getClass(), getKey(), line );
                valuesList.add( 0.0 );
            }
            else {
                // Parse all values
                for( int i = 1; i < args.length; i++ ) {
                    valuesList.add( parseValue( args[i], line ) );
                }
            }
        }
        // Specified value; must have the exact number of values
        else {
            if( reqValues > actualValues ) {
                DeadlyWorld.LOG.warn( "Entry has too few values for {} \"{}\"! " +
                                "Expected {} values, but detected {}. Replacing missing values with 0. Invalid entry: {}",
                        getClass(), getKey(), reqValues, actualValues, line );
            }
            else if( reqValues < actualValues ) {
                DeadlyWorld.LOG.warn( "Entry has too many values for {} \"{}\"! " +
                                "Expected {} values, but detected {}. Deleting additional values. Invalid entry: {}",
                        getClass(), getKey(), reqValues, actualValues, line );
            }
            
            // Parse all values
            for( int i = 1; i < reqValues + 1; i++ ) {
                if( i < args.length ) {
                    valuesList.add( parseValue( args[i], line ) );
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
        return new EntityEntry( entityType, extendable, values );
    }
    
    /** Parses a single value argument and returns a valid result. */
    private double parseValue( final String arg, final String line ) {
        // Try to parse the value
        double value;
        try {
            value = Double.parseDouble( arg );
        }
        catch( NumberFormatException ex ) {
            // This is thrown if the string is not a parsable number
            DeadlyWorld.LOG.warn( "Invalid value for {} \"{}\"! Falling back to 0. Invalid entry: {}",
                    getClass(), getKey(), line );
            value = 0.0;
        }
        // Verify value is within range
        if( value < valueDefault.getMinValue() ) {
            DeadlyWorld.LOG.warn( "Value for {} \"{}\" is below the minimum ({})! Clamping value. Invalid value: {}",
                    getClass(), getKey(), valueDefault.getMinValue(), value );
            value = valueDefault.getMinValue();
        }
        else if( value > valueDefault.getMaxValue() ) {
            DeadlyWorld.LOG.warn( "Value for {} \"{}\" is above the maximum ({})! Clamping value. Invalid value: {}",
                    getClass(), getKey(), valueDefault.getMaxValue(), value );
            value = valueDefault.getMaxValue();
        }
        return value;
    }
}