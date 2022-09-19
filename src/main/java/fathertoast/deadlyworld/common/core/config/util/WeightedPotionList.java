package fathertoast.deadlyworld.common.core.config.util;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedPotionList extends PotionList {
    /** The total weight of all entity entries in the list. */
    private int totalWeight;

    /**
     * Create a new entity list from a list of entries.
     * <p>
     * By default, entity lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    public WeightedPotionList( List<PotionEntry> entries ) { super( entries ); }

    /**
     * Create a new entity list from an array of entries. Used for creating default configs.
     * <p>
     * By default, entity lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    public WeightedPotionList( PotionEntry... entries ) { super( entries ); }

    // Enforce the rules for the weighted list regardless of constructor used
    {
        // Use the direct super setters to avoid throwing UnsupportedOperationException
        super.setMultiValue( 3 );
        super.setRange( 0.0, Double.POSITIVE_INFINITY );
    }

    /**
     * Tries to create a new entity list from the given Compound tag.
     *
     * @param compoundNBT The Compound tag to try and load entity entries from.
     *
     * @return A new WeightedEntityList if any entries were successfully loaded
     *         from the Compound tag. Returns null if not.
     */
    @Nullable
    public static WeightedPotionList loadFromNBT(@Nonnull CompoundNBT compoundNBT) {
        List<PotionEntry> entries = new ArrayList<>();

        for (String key : compoundNBT.getAllKeys()) {
            if (compoundNBT.contains(key, Constants.NBT.TAG_STRING)) {
                // Assuming the entity ID and the weight is split by a space.
                // If not... :biglist:
                String[] entry = compoundNBT.getString(key).split(" ");

                if (entry.length == 0 || entry.length > 4) {
                    DeadlyWorld.LOG.error("Failed to read weighted potion list entry; should only contain an effect ID, numeric weight, duration and amplifier. Malformed entry: \"{}\"", (Object) entry);
                    return null;
                }
                else {
                    try {
                        // Assuming the first index contains the entity ID
                        ResourceLocation entityId = ResourceLocation.tryParse(entry[0]);
                        double weight = Double.parseDouble(entry[1]);
                        int duration = Integer.parseInt(entry[2]);
                        int amplifier = Integer.parseInt(entry[3]);

                        if (entityId == null) {
                            DeadlyWorld.LOG.error("Failed to read effect ID for weighted potion list entry (effect ID is malformed or invalid): \"{}\"", (Object) entry);
                        }
                        else {
                            // VVV THIS IS WHERE THE ENTRIES GET ADDED VVV
                            if (ForgeRegistries.POTIONS.containsKey(entityId)) {
                                Effect entityType = ForgeRegistries.POTIONS.getValue(entityId);
                                entries.add(new PotionEntry(entityType, weight, duration, amplifier));
                            }
                            else {
                                DeadlyWorld.LOG.error("Failed to read effect ID for weighted potion list entry (effect ID doesn't exist in the registry): \"{}\"", (Object) entry);
                            }
                        }
                    }
                    catch (NumberFormatException e) {
                        DeadlyWorld.LOG.error("Failed to read weight value for weighted potion list entry (value could not be parsed as a double): \"{}\"", (Object) entry);
                    }
                }
            }
        }
        if (entries.isEmpty()) {
            return null;
        }
        else {
            // SUCCESS                                                                                                                 :biglist:
            WeightedPotionList potionList = new WeightedPotionList(entries);
            potionList.calculateTotalWeight();
            return potionList;
        }
    }

    /** @return A list of strings that will represent this object when written to a toml file. */
    @Override
    public List<String> toStringList() {
        // Create a list of the entries in string format
        final List<String> list = new ArrayList<>( ENTRIES.length );
        for( PotionEntry entry : ENTRIES ) {
            list.add( entry.toString( true ) );
        }
        return list;
    }

    /** Updates the total weight for this weighted entity list. */
    public void calculateTotalWeight() {
        int newWeight = 0;
        for( PotionEntry entry : ENTRIES ) {
            if( entry.VALUES.length > 0 && entry.VALUES[0] > 0 ) {
                if( (double) newWeight + entry.VALUES[0] > Integer.MAX_VALUE ) {
                    DeadlyWorld.LOG.error( "Weighted potion list has a total weight greater than the maximum ({})! List:{}",
                            Integer.MAX_VALUE, this );
                    newWeight = Integer.MAX_VALUE;
                    break;
                }
                newWeight += (int) entry.VALUES[0];
            }
        }
        totalWeight = newWeight;
    }

    /** @return Randomly picks a potion effect type from this weighted potion list. Null if no entries can be picked from the list. */
    @Nullable
    public EffectInstance next(Random random ) {
        if( totalWeight > 0 ) {
            int choice = random.nextInt( totalWeight );
            for( PotionEntry entry : ENTRIES ) {
                if( entry.VALUES.length == 3 && entry.VALUES[0] > 0 && entry.VALUES[1] > 0 && entry.VALUES[2] >= 0) {
                    choice -= (int) entry.VALUES[0];
                    if( choice < 0 ) {
                        return new EffectInstance(entry.EFFECT.get(), (int) entry.VALUES[1], (int) entry.VALUES[2]);
                    }
                }
            }
            DeadlyWorld.LOG.error( "Something has gone dramatically wrong with a weighted potion list! Weight={}; List:{}",
                    totalWeight, this );
        }
        return null;
    }

    /** Marks this potion list as a simple weighted listing; exactly one non-negative integer weight per entry. */
    public PotionList setWeightedRandom() {
        return setSingleValue().setRangePos();
    }

    /** Marks this entity list as multi-value; each entry will have the specified number of values. */
    @Override
    public PotionList setMultiValue( int numberOfValues ) {
        throw new UnsupportedOperationException( "Cannot change; weighted potion lists only support a single value!" );
    }

    /** Bounds entry values in this list to the specified limits, inclusive. Note that 0 must be within the range. */
    @Override
    public PotionList setRange( double min, double max ) {
        throw new UnsupportedOperationException( "Cannot change; weighted potion lists only support the non-negative range (>= 0)!" );
    }
}
