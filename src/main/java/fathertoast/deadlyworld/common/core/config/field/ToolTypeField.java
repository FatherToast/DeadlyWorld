package fathertoast.deadlyworld.common.core.config.field;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.file.TomlHelper;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a config field representing a tool type or null.
 * We cannot reliably validate tool types, so we simply provide the vanilla tool types as suggestions.
 */
public class ToolTypeField extends GenericField<ToolType> {
    /** The raw toml value representing a null tool type. */
    private static final String NULL_TYPE = "none";
    /** The tool type name validator; copied from ToolType itself. */
    private static final Pattern VALID_NAME = Pattern.compile( "[^a-z_]" );
    
    /** Creates a new field that accepts any valid tool type. */
    public ToolTypeField( String key, @Nullable ToolType defaultValue, String... description ) {
        super( key, defaultValue, description );
    }
    
    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    public void appendFieldInfo( List<String> comment ) {
        comment.add( TomlHelper.fieldInfoValidValues( "Tool Type", valueDefault.getName(),
                NULL_TYPE, ToolType.AXE.getName(), ToolType.HOE.getName(), ToolType.PICKAXE.getName(), ToolType.SHOVEL.getName(),
                "<mod_added_tool_type>" ) );
    }
    
    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value
     */
    @Override
    public void load( @Nullable Object raw ) {
        ToolType newValue = null;
        if( raw instanceof String ) {
            // Parse the value
            if( NULL_TYPE.equals( raw ) ) {
                value = null;
                return;
            }
            newValue = parseValue( (String) raw );
        }
        if( newValue == null ) {
            // Value cannot be parsed to this field
            if( raw != null ) {
                DeadlyWorld.LOG.warn( "Invalid value for {} \"{}\"! " +
                                "Tool types must only contain lowercase letters (a-z) and underscores (_). " +
                                "Falling back to default. Invalid value: {}",
                        getClass(), getKey(), raw );
            }
            newValue = valueDefault;
        }
        value = newValue;
    }
    
    /** @return Attempts to parse the string literal as one of the valid values for this field and returns it, or null if invalid. */
    private ToolType parseValue( String name ) {
        return VALID_NAME.matcher( name ).find() ? null : ToolType.get( name );
    }
    
    /** @return The raw toml value that should be assigned to this field in the config file. */
    @Override
    public Object getRaw() { return value == null ? NULL_TYPE : value.getName(); }
}