package fathertoast.deadlyworld.common.config.value;

import fathertoast.crust.api.config.common.field.collection.FuzzyValueListField;
import fathertoast.crust.api.config.common.value.collection.value.MobEffectStats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * Represents a config field with a mob effect list value.
 * Use {@link #potionEntries()} to iterate through the defined list of mob effect instances.
 */
@SuppressWarnings( "unused" )
public class MobEffectListField extends FuzzyValueListField<MobEffect, MobEffectStats, MobEffectList> {
    
    /** Creates a new field. */
    public MobEffectListField( String key, MobEffectList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /**
     * @return An iterator over the key-value pairs represented by the keys in this list that can be used in
     * an enhanced for loop. The iterator skips over null objects, but it may still return null in some cases.
     */
    public Iterator<MobEffectInstance> potionEntries() { return get().potionEntries(); }
}