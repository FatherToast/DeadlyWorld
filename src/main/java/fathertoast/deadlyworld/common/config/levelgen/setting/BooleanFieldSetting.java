package fathertoast.deadlyworld.common.config.levelgen.setting;

import com.mojang.serialization.Codec;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AbstractConfigField;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.deadlyworld.common.core.DeadlyWorld;

import javax.annotation.Nullable;
import java.util.List;

public class BooleanFieldSetting {
    public static final Codec<BooleanFieldSetting> CODEC = Codec.STRING.xmap( BooleanFieldSetting::new, BooleanFieldSetting::toString );
    
    private final String MOD_ID;
    private final String FILE;
    private final String KEY;
    
    private BooleanField setField;
    
    public BooleanFieldSetting( BooleanField field ) {
        MOD_ID = field.getSpec().MANAGER.MOD_ID;
        FILE = field.getSpec().NAME;
        KEY = field.getKey();
        setField = field;
    }
    
    public BooleanFieldSetting( String address ) {
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
    
    /** @return The config field value; prints an error and returns false if it fails. */
    public boolean get() { return get( false ); }
    
    /** @return The config field value; prints an error and returns the error value if it fails. */
    public boolean get( boolean errorValue ) {
        if( !check() ) {
            DeadlyWorld.LOG.error( "Invalid boolean field: {}", this );
            return errorValue;
        }
        return setField.get();
    }
    
    /** @return The config field value; returns null if it fails. */
    @Nullable
    public Boolean getRaw() { return check() ? setField.get() : null; }
    
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
        if( !(foundField instanceof BooleanField) ) return false;
        
        setField = (BooleanField) foundField;
        return true;
    }
}