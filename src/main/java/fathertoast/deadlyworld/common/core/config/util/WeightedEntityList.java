package fathertoast.deadlyworld.common.core.config.util;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    // TODO - Sorry for messing around with your config classes Toast,
    //        feel free to remove or change anything I do in these waters
    //        at any given time.
    /**
     * Tries to create a new entity list from the given Compound tag.
     *
     * @param compoundNBT The Compound tag to try and load entity entries from.
     *
     * @return A new WeightedEntityList if any entries were successfully loaded
     *         from the Compound tag. Returns null if not.
     */
    @Nullable
    public static WeightedEntityList loadFromNBT(@Nonnull CompoundNBT compoundNBT) {
        List<EntityEntry> entries = new ArrayList<>();

        for (String key : compoundNBT.getAllKeys()) {
            if (compoundNBT.contains(key, Constants.NBT.TAG_STRING)) {
                // Assuming the entity ID and the weight is split by a space.
                // If not... :biglist:
                String[] entry = compoundNBT.getString(key).split(" ");

                if (entry.length == 0 || entry.length > 2) {
                    DeadlyWorld.LOG.error("Failed to read weighted entity list entry; should only contain an entity ID and a numeric weight. Malformed entry: \"{}\"", (Object) entry);
                    return null;
                }
                else {
                    try {
                        // Assuming the first index contains the entity ID
                        // and the second index contains the weight.
                        ResourceLocation entityId = ResourceLocation.tryParse(entry[0]);
                        double weight = Double.parseDouble(entry[1]);

                        if (entityId == null) {
                            DeadlyWorld.LOG.error("Failed to read entity ID for weighted entity list entry (entity ID is malformed or invalid): \"{}\"", (Object) entry);
                        }
                        else {
                            // VVV THIS IS WHERE THE ENTRIES GET ADDED VVV
                            if (ForgeRegistries.ENTITIES.containsKey(entityId)) {
                                EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(entityId);
                                entries.add(new EntityEntry(entityType, weight));
                            }
                            else {
                                DeadlyWorld.LOG.error("Failed to read entity ID for weighted entity list entry (entity ID doesn't exist in the registry): \"{}\"", (Object) entry);
                            }
                        }
                    }
                    catch (NumberFormatException e) {
                        DeadlyWorld.LOG.error("Failed to read weight value for weighted entity list entry (value could not be parsed as a double): \"{}\"", (Object) entry);
                        e.printStackTrace();
                    }
                }
            }
        }
        if (entries.isEmpty()) {
            return null;
        }
        else {
            // SUCCESS                                                                                                                 :biglist:
            WeightedEntityList entityList = new WeightedEntityList(entries);
            entityList.calculateTotalWeight();
            return entityList;
        }
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