package fathertoast.deadlyworld.common.config.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.value.RegistryEntryValueList;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class WeightedPotionList extends RegistryEntryValueList<MobEffect> {

    /** The entity-value entries in this list. */
    private final RegistryValueEntry<MobEffect>[] ENTRIES;

    private final double TOTAL_WEIGHT;

    /**
     * Create a new weighted entity list from a list of entries. Does not support "default" entries.
     * Extendability is generally ignored.
     * <p>
     * Weighted entity lists will require exactly one value, and the value can be any non-negative double.
     */
    @SuppressWarnings( "unchecked" )
    public WeightedPotionList( List<RegistryValueEntry<MobEffect>> entries ) { this( entries.toArray( new RegistryValueEntry[0] ) ); }

    /**
     * Create a new weighted entity list from an array of entries. Used for creating default configs.
     * Does not support "default" entries. Extendability is generally ignored.
     * <p>
     * Weighted entity lists will require exactly one value, and the value can be any non-negative double.
     */
    @SafeVarargs
    public WeightedPotionList( RegistryValueEntry<MobEffect>... entries ) {
        super( null, () -> ForgeRegistries.MOB_EFFECTS, entries );
        ENTRIES = entries;

        super.setMultiValue( 3 );
        super.setRange( 0.0, Double.POSITIVE_INFINITY );

        // Calculate the total weight
        double weight = 0;
        for( RegistryValueEntry<MobEffect> entry : entries ) {
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
    public RegistryEntryValueList<MobEffect> setMultiValue( int numberOfValues ) {
        throw new UnsupportedOperationException( "Weighted entity lists must support exactly three values." );
    }

    @Override
    public RegistryEntryValueList<MobEffect> setRange( double min, double max ) {
        throw new UnsupportedOperationException( "Weighted entity lists must support all non-negative values." );
    }

    /** @return True if this list was implicitly disabled by setting all weights to 0. */
    public boolean isDisabled() { return TOTAL_WEIGHT <= 0; }

    /** @return True if there are no entries in this potion list. */
    public boolean isEmpty() {
        return ENTRIES == null || ENTRIES.length == 0;
    }

    /** @return Selects an entity type from the list at random. */
    @Nullable
    public MobEffectInstance next( Random random ) { return next( random.nextDouble() ); }

    /** @return Selects an entity type from the list at random. */
    @Nullable
    public MobEffectInstance next( RandomSource random ) { return next( random.nextDouble() ); }

    /** @return Selects an entity type from the list at random. */
    @Nullable
    private MobEffectInstance next( double roll ) {
        if( isEmpty() || isDisabled() ) return null;

        double choice = roll * TOTAL_WEIGHT;
        for( RegistryValueEntry<MobEffect> entry : ENTRIES ) {
            if( entry.REG_KEY != null ) {
                choice -= entry.VALUES[0];
                if( choice < 0 ) {
                    try {
                        MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue( entry.REG_KEY );
                        // noinspection ConstantConditions
                        return new MobEffectInstance( mobEffect, (int) entry.VALUES[1], (int) entry.VALUES[2] );
                    }
                    catch ( Exception e ) {
                        return null;
                    }
                }
            }
        }
        ConfigUtil.LOG.error( "Weighting error occurred while rolling random item! Not good :(" );
        return null;
    }
}
