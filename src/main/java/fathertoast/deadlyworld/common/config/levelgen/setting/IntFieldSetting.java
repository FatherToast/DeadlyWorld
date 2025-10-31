package fathertoast.deadlyworld.common.config.levelgen.setting;

import com.mojang.serialization.Codec;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;

import javax.annotation.Nullable;

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
        AbstractConfigField foundField = Config.getField( MOD_ID, FILE, KEY );
        if( foundField instanceof IntField intField ) {
            setField = intField;
            return true;
        }
        return false;
    }
}