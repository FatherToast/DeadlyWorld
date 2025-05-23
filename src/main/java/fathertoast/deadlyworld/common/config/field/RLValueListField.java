package fathertoast.deadlyworld.common.config.field;

import com.mojang.datafixers.util.Pair;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.StringListField;
import fathertoast.crust.api.config.common.file.TomlHelper;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Lazy implementation of a List Field that loads
 * a list of Strings as a list of ResourceLocations
 * paired with one or multiple numeric values.<br><br>
 *
 * Actual pairs of ResourceLocations and values are stored in the field itself,
 * so calling getters from super only returns the raw list of Strings.
 */
public class RLValueListField extends StringListField {

    private final List<ResourceLocation> ENTRIES = new ArrayList<>();
    private final List<Pair<ResourceLocation, Double[]>> VALUE_PAIRS = new ArrayList<>();

    private final int valueCount;


    public RLValueListField( String key, int valueCount, List<String> defaultValue, @Nullable String... description ) {
        super( key, "Resource Location value List", defaultValue, description );
        this.valueCount = Math.max( 0, valueCount );

        parseAsRLAndValues( defaultValue );
    }

    private void parseAsRLAndValues( List<String> list ) {
        // Get all values from the list
        for( String entry : list ) {
            if (entry != null) {
                try {
                    String s = entry.trim();
                    String[] parts = s.split(" ");

                    // Make sure the total amount of components is as expected after trimming spaces
                    if (parts.length != (valueCount + 1)) {
                        ConfigUtil.LOG.error("RLValueListField '{}' contains a line with an invalid number of values. Expected {}, got {}! ", getKey(), valueCount, parts.length - 1);
                        continue;
                    }
                    final ResourceLocation rl = ResourceLocation.tryParse(parts[0]);

                    // First string is not a valid resource location, skip line
                    if (rl == null) {
                        ConfigUtil.LOG.error("RLValueListField '{}' contains a line with invalid ResourceLocation. Problematic String: '{}'", getKey(), parts[0]);
                        continue;
                    }
                    Double[] values = new Double[valueCount];

                    for (int i = 0; i < valueCount; i++) {
                        try {
                            values[i] = Double.valueOf(parts[i + 1]);
                        }
                        // Value cannot be parsed as a double
                        catch (NumberFormatException e) {
                            ConfigUtil.LOG.error("RLValueListField {} contains invalid non-numeric value! Problematic String: '{}'", getKey(), parts[i + 1]);
                            break;
                        }
                    }
                    // One or more values were invalid, skip line
                    if (values.length != valueCount)
                        continue;

                    ENTRIES.add(rl);
                    VALUE_PAIRS.add(Pair.of(rl, values));
                } catch (Exception e) {
                    ConfigUtil.LOG.error("Failed to load RLValueListField '{}'!", getKey());
                    e.printStackTrace();
                }
            }
        }
    }

    /** Adds info about the field type, format, and bounds to the end of a field's description. */
    @Override
    public void appendFieldInfo( List<String> comment ) {
        String fieldFormat;

        if ( valueCount <= 0 ) {
            // Variable number of values
            fieldFormat = "[ \"namespace:path\", ... ]";
        }
        else {
            StringBuilder format = new StringBuilder( "[ \"namespace:path " );

            for( int i = 1; i <= valueCount; i++ ) {
                format.append( "value" );
                if( valueCount > 1 ) {
                    format.append( i );
                }
                format.append( " " );
            }
            format.deleteCharAt( format.length() - 1 ).append( "\", ... ]" );
            fieldFormat = format.toString();
        }
        comment.add( TomlHelper.fieldInfoFormat( "Resource Location value List", valueDefault, fieldFormat ) );
    }

    @Override
    public void load( @Nullable Object raw ) {
        if( raw == null ) {
            value = valueDefault;
            return;
        }
        final List<String> list = TomlHelper.parseStringList( raw );
        parseAsRLAndValues( list );
        value = list;
    }

    public Iterable<ResourceLocation> getResourceLocations() {
        return ENTRIES;
    }

    @Nullable
    public Double[] getValuesFor( @Nonnull ResourceLocation resourceLocation ) {
        Objects.requireNonNull( resourceLocation );

        for ( Pair<ResourceLocation, Double[]> pair : VALUE_PAIRS ) {
            if ( resourceLocation.equals( pair.getFirst() ) ) {
                return pair.getSecond();
            }
        }
        return null;
    }
}
