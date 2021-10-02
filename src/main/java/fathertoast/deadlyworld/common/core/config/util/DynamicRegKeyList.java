package fathertoast.deadlyworld.common.core.config.util;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.field.IStringArray;
import fathertoast.deadlyworld.common.core.config.file.TomlHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//TODO This class is temporarily just a set of strings until I feel like fixing it; This class may just get deleted completely

/**
 * A list of entity-value entries used to link one or more numbers to specific entity types.
 */
@SuppressWarnings( { "unused", "SameParameterValue" } )
public class DynamicRegKeyList implements IStringArray {
    //** The registry that contains this list. */
    //private final Registry<T> REGISTRY;
    /** The registry keys in this list. */
    //private final List<RegistryKey<T>> KEYS;
    private final List<String> KEYS;
    
    /**
     * Create a new entity list from an array of entries. Used for creating default configs.
     * <p>
     * By default, entity lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    //@SafeVarargs // The only thing we are doing is calling Arrays#asList, which is also safe
    //public DynamicRegKeyList( Registry<T> registry, RegistryKey<T>... keys ) { this( registry, Arrays.asList( keys ) ); }
    public DynamicRegKeyList( String... keys ) { this( Arrays.asList( keys ) ); }
    
    /**
     * Create a new entity list from a list of entries.
     * <p>
     * By default, entity lists will allow any non-zero number of values, and the value(s) can be any numerical double.
     * These parameters can be changed with helper methods that alter the number of values or values' bounds and return 'this'.
     */
    //public DynamicRegKeyList( Registry<T> registry, List<RegistryKey<T>> keys ) {
    public DynamicRegKeyList( List<String> keys ) {
        //REGISTRY = registry;
        KEYS = Collections.unmodifiableList( keys );
    }
    
    /** @return A string representation of this object. */
    @Override
    public String toString() { return TomlHelper.toLiteral( toStringList().toArray() ); }
    
    /** @return Returns true if this object has the same value as another object. */
    @Override
    public boolean equals( Object other ) {
        if( !(other instanceof DynamicRegKeyList) ) return false;
        // Compare by the string list view of the object
        return toStringList().equals( ((DynamicRegKeyList/*<?>*/) other).toStringList() );
    }
    
    /** @return A list of strings that will represent this object when written to a toml file. */
    @Override
    public List<String> toStringList() {
        // Create a list of the keys in string format
        /*
        final List<String> list = new ArrayList<>( KEYS.size() );
        for( DynamicRegKey key : KEYS ) {
            list.add( key.toString() );
        }
        */
        return KEYS;
    }
    
    /** @return True if the registry key is contained in this list. */
    //public boolean contains( RegistryKey<T> registryKey ) {
    public boolean contains( String registryKey ) {
        return KEYS.contains( registryKey );
    }
}