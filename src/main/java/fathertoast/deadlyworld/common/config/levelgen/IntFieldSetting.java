package fathertoast.deadlyworld.common.config.levelgen;

import com.mojang.serialization.Codec;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.core.DeadlyWorld;

import javax.annotation.Nullable;
import java.util.List;

public class IntFieldSetting {
    public static final Codec<IntFieldSetting> CODEC = Codec.STRING.xmap( IntFieldSetting::new, IntFieldSetting::toString );
    
    private final String MOD_ID;
    private final String FILE;
    private final String KEY;
    
    private IntField setField;
    
    public IntFieldSetting( IntField field ) {
        MOD_ID = field.getSpec().MANAGER.MOD_ID;
        FILE = field.getSpec().NAME;
        KEY = field.getKey();
        setField = field;
    }
    
    public IntFieldSetting( String address ) {
        final String[] parts = address.split( ":", 3 );
        if( parts.length != 3 ) {
            DeadlyWorld.LOG.error( "Invalid config field address: '{}'", address );
        }
        MOD_ID = parts.length > 0 ? parts[0] : "";
        FILE = parts.length > 1 ? parts[1] : "";
        KEY = parts.length > 2 ? parts[2] : "";
    }
    
    @Override
    public String toString() { return MOD_ID + ":" + FILE + ":" + KEY; }
    
    /** @return The config field value; prints an error and returns Integer.MAX_VALUE if it fails. */
    public int get() { return get( Integer.MAX_VALUE ); }
    
    /** @return The config field value; prints an error and returns the error value if it fails. */
    public int get( int errorValue ) {
        if( !check() ) {
            DeadlyWorld.LOG.error( "Invalid int field: {}", this );
            return errorValue;
        }
        return setField.get();
    }
    
    /** @return The config field value; returns null if it fails. */
    @Nullable
    public Integer getRaw() { return check() ? setField.get() : null; }
    
    /** @return True if the set field reference has been found. */
    private boolean check() {
        if( setField != null ) return true;
        
        final ConfigManager manager = ConfigManager.get( MOD_ID );
        if( manager == null ) return false;
        
        final List<AbstractConfigFile> configs = manager.getConfigs(); // TODO update Crust to provide a spec.NAME:spec map
        AbstractConfigField foundField = null;
        for( AbstractConfigFile config : configs ) {
            if( config.SPEC.NAME.equals( FILE ) ) {
                foundField = config.SPEC.getFields().get( KEY );
                break;
            }
        }
        if( !(foundField instanceof IntField) ) return false;
        
        setField = (IntField) foundField;
        return true;
    }
}