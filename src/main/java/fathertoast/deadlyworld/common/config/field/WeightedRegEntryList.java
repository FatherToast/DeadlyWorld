package fathertoast.deadlyworld.common.config.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.value.RegistryEntryValueList;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.RandomSource;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class WeightedRegEntryList<T> extends RegistryEntryValueList<T> {

    /** The item-weight entries in this list. */
    private RegistryValueEntry<T>[] ENTRIES;

    private final double TOTAL_WEIGHT;

    /**
     * Create a new weighted registry entry list from a list of entries. Does not support "default" entries.
     * <p>
     * Weighted registry entry lists will require exactly one value, and the value can be any non-negative double.
     */
    public WeightedRegEntryList( Supplier<IForgeRegistry<T>> registry, List<RegistryValueEntry<T>> entries ) {
        this( registry, entries.toArray( new RegistryValueEntry[0] ) );
    }

    /**
     * Create a new weighted registry entry list from an array of entries. Used for creating default configs.
     * Does not support "default" entries.
     * <p>
     * Weighted registry entry lists will require exactly one value, and the value can be any non-negative double.
     */
    @SafeVarargs
    public WeightedRegEntryList( Supplier<IForgeRegistry<T>> registry, RegistryValueEntry<T>... entries ) {
        super( null, registry, entries );
        ENTRIES = entries;

        super.setMultiValue( 1 );
        super.setRange( 0.0, Double.POSITIVE_INFINITY );

        // Calculate the total weight
        double weight = 0;
        for( RegistryValueEntry<T> entry : entries ) {
            if( entry.REG_KEY == null ) {
                DeadlyWorld.LOG.warn( "Encountered value entry with null registry key. Entry will be discarded." );
            }
            else weight += entry.VALUES[0];
        }
        TOTAL_WEIGHT = weight;
    }

    public ListTag toNBT(ListTag tag ) {
        for( int i = 0; i < ENTRIES.length; i++ ) {
            tag.addTag( i, StringTag.valueOf( ENTRIES[i].toString() ) );
        }
        return tag;
    }

    @Override
    public WeightedRegEntryList<T> setMultiValue( int numberOfValues ) {
        throw new UnsupportedOperationException( "Weighted registry entry lists must support exactly 1 value." );
    }

    @Override
    public WeightedRegEntryList<T> setRange( double min, double max ) {
        throw new UnsupportedOperationException( "Weighted registry entry lists must support all non-negative values." );
    }

    /** @return Returns true if this list was implicitly disabled by setting all weights to 0. */
    public boolean isDisabled() { return TOTAL_WEIGHT <= 0; }

    /** @return Selects an item from the list at random. */
    @Nullable
    public T next( Random random ) { return next( random.nextDouble() ); }

    /** @return Selects an item from the list at random. */
    @Nullable
    public T next( RandomSource random ) { return next( random.nextDouble() ); }

    /** @return Selects an item from the list at random. */
    @Nullable
    private T next( double roll ) {
        if( isDisabled() ) return null;

        double choice = roll * TOTAL_WEIGHT;
        for( RegistryValueEntry<T> entry : ENTRIES ) {
            if( entry.REG_KEY != null ) {
                choice -= entry.VALUES[0];
                if( choice < 0 ) {
                    return getRegistry().get().getValue( entry.REG_KEY );
                }
            }
        }
        ConfigUtil.LOG.error( "Weighting error occurred while rolling random item! Not good. :(" );
        return null;
    }
}
