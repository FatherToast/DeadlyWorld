package fathertoast.deadlyworld.common.core.config.field;

import fathertoast.deadlyworld.common.core.config.util.EntityEntry;
import fathertoast.deadlyworld.common.core.config.util.EntityList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a config field with an entity list value.
 */
public class EntityListField extends AbstractEntityListField<EntityList> {
    /** The string to use in place of a registry key for a default entry. */
    public static final String REG_KEY_DEFAULT = "default";
    
    /** Provides a detailed description of how to use entity lists. Recommended to put at the top of any file using entity lists. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Entity List fields: General format = [ \"namespace:entity_type value1 value2 ...\", ... ]" );
        comment.add( "  Entity lists are arrays of entity types. Some entity lists specify a number of values linked to each entity type." );
        comment.add( "  Entity types are defined by their key in the entity registry, usually following the pattern 'namespace:entity_name'." );
        comment.add( "  '" + REG_KEY_DEFAULT + "' can be used instead of an entity type registry key to provide default values for all entities." );
        comment.add( "  An asterisk '*' can be used to match multiple entity types. For example, 'minecraft:*' will match all vanilla entities." );
        comment.add( "  List entries by default match any entity type derived from (i.e. based on) their entity type. A tilde '~' prefix" );
        comment.add( "    disables that extra matching. For example, '~minecraft:zombie'." );
        comment.add( "  There is no real rule for deriving, even in vanilla, but the hope is that mod-added mobs will derive from their base mob." );
        return comment;
    }
    
    /** Creates a new field. */
    public EntityListField( String key, EntityList defaultValue, String... description ) {
        super( key, defaultValue, description );
    }
    
    /** @return A newly constructed entity list from the given entries. */
    @Override
    protected EntityList createNewList( List<EntityEntry> entryList ) { return new EntityList( entryList ); }
    
    /**
     * Represents two entity list fields, a blacklist and a whitelist, combined into one.
     * The blacklist cannot contain values, but the whitelist can have any settings.
     */
    public static class Combined {
        /** The whitelist. To match, the entry must be present here. */
        private final EntityListField WHITELIST;
        /** The blacklist. Entries present here are ignored entirely. */
        private final EntityListField BLACKLIST;
        
        /** Links two lists together as blacklist and whitelist. */
        public Combined( EntityListField whitelist, EntityListField blacklist ) {
            WHITELIST = whitelist;
            BLACKLIST = blacklist;
            if( blacklist.valueDefault.getRequiredValues() != 0 ) {
                throw new IllegalArgumentException( "Blacklists cannot have values! See: " + blacklist.getKey() );
            }
        }
        
        /** @return True if the entity is contained in this list. */
        public boolean contains( Entity entity ) {
            return entity != null && !BLACKLIST.get().contains( entity ) && WHITELIST.get().contains( entity );
        }
        
        /**
         * @param entity The entity to retrieve values for.
         * @return The array of values of the best-match entry. Returns null if the entity is not contained in this entity list.
         */
        public double[] getValues( Entity entity ) {
            return entity != null && !BLACKLIST.get().contains( entity ) ? WHITELIST.get().getValues( entity ) : null;
        }
        
        /**
         * @param entity The entity to retrieve a value for.
         * @return The first value in the best-match entry's value array. Returns 0 if the entity is not contained in this
         * entity list or has no values specified. This should only be used for 'single value' lists.
         * @see EntityList#setSingleValue()
         * @see EntityList#setSinglePercent()
         */
        public double getValue( Entity entity ) {
            return entity != null && !BLACKLIST.get().contains( entity ) ? WHITELIST.get().getValue( entity ) : 0.0;
        }
        
        /**
         * @param entity The entity to roll a value for.
         * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the entity
         * is not contained in this entity list or has no values specified. This should only be used for 'single percent' lists.
         * @see EntityList#setSinglePercent()
         */
        public boolean rollChance( LivingEntity entity ) {
            return entity != null && !BLACKLIST.get().contains( entity ) && WHITELIST.get().rollChance( entity );
        }
    }
}