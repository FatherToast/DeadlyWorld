package fathertoast.deadlyworld.common.config.field;

import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.value.EntityEntry;
import fathertoast.crust.api.config.common.value.EntityList;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class WeightedEntityList extends EntityList {
    
    /** The entity-value entries in this list. */
    private final EntityEntry[] ENTRIES;
    
    private final double TOTAL_WEIGHT;
    
    /**
     * Create a new weighted entity list from a list of entries. Does not support "default" entries.
     * Extendability is generally ignored.
     * <p>
     * Weighted entity lists will require exactly one value, and the value can be any non-negative double.
     */
    public WeightedEntityList( List<EntityEntry> entries ) { this( entries.toArray( new EntityEntry[0] ) ); }
    
    /**
     * Create a new weighted entity list from an array of entries. Used for creating default configs.
     * Does not support "default" entries. Extendability is generally ignored.
     * <p>
     * Weighted entity lists will require exactly one value, and the value can be any non-negative double.
     */
    public WeightedEntityList( EntityEntry... entries ) {
        super( entries );
        ENTRIES = entries;
        
        super.setMultiValue( 1 );
        super.setRange( 0.0, Double.POSITIVE_INFINITY );
        
        // Calculate the total weight
        double weight = 0;
        for( EntityEntry entry : entries ) {
            if( entry.ENTITY_KEY == null ) {
                //ConfigUtil.LOG.warn( "Invalid entry for {} \"{}\"! Invalid entry: {}",
                //        FIELD.getClass(), FIELD.getKey(), ENTITY_KEY.toString() );
                DeadlyWorld.LOG.warn( "Default entries are not supported for weighted lists." );
            }
            else weight += entry.VALUES[0];
        }
        TOTAL_WEIGHT = weight;
    }
    
    public ListTag toNBT( ListTag tag ) {
        for( int i = 0; i < ENTRIES.length; i++ ) {
            tag.addTag( i, StringTag.valueOf( ENTRIES[i].toString() ) );
        }
        return tag;
    }
    
    @Override
    public EntityList setMultiValue( int numberOfValues ) {
        throw new UnsupportedOperationException( "Weighted entity lists must support exactly one value." );
    }
    
    @Override
    public EntityList setRange( double min, double max ) {
        throw new UnsupportedOperationException( "Weighted entity lists must support all non-negative values." );
    }
    
    /** @return Returns true if this list was implicitly disabled by setting all weights to 0. */
    public boolean isDisabled() { return TOTAL_WEIGHT <= 0; }
    
    /** @return Selects an entity type from the list at random. */
    @Nullable
    public EntityType<?> next( Random random ) { return next( random.nextDouble() ); }
    
    /** @return Selects an entity type from the list at random. */
    @Nullable
    public EntityType<?> next( RandomSource random ) { return next( random.nextDouble() ); }
    
    /** @return Selects an entity type from the list at random. */
    @Nullable
    private EntityType<?> next( double roll ) {
        if( isDisabled() ) return null;
        
        double choice = roll * TOTAL_WEIGHT;
        for( EntityEntry entry : ENTRIES ) {
            if( entry.ENTITY_KEY != null ) {
                choice -= entry.VALUES[0];
                if( choice < 0 ) return ForgeRegistries.ENTITY_TYPES.getValue( entry.ENTITY_KEY );
            }
        }
        
        ConfigUtil.LOG.error( "Weighting error occurred while rolling random item! Not good. :(" );
        return null;
    }
}