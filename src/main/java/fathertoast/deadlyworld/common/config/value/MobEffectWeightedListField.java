package fathertoast.deadlyworld.common.config.value;

import fathertoast.crust.api.config.common.field.collection.FuzzyWeightedValueListField;
import fathertoast.crust.api.config.common.value.collection.value.MobEffectStats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * Represents a config field with a mob effect weighted list value.
 * Use {@link #nextPotion(RandomSource)} to draw a random mob effect instance, or null if empty or nothing is drawn.
 */
public class MobEffectWeightedListField extends FuzzyWeightedValueListField<MobEffect, MobEffectStats, MobEffectWeightedList> {
    
    /** Creates a new field. */
    public MobEffectWeightedListField( String key, MobEffectWeightedList defaultValue, @Nullable String... description ) {
        super( key, defaultValue, description );
    }
    
    
    // ---- Convenience Methods ---- //
    
    /**
     * @return A randomly chosen mob effect from this list with its duration and amplifier, or
     * null if a null entry is selected or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public MobEffectInstance nextPotion( Random random ) { return get().nextPotion( random ); }
    
    /**
     * @return A randomly chosen mob effect from this list with its duration and amplifier, or
     * null if a null entry is selected or if the list is empty or disabled (all weights are 0).
     */
    @Nullable
    public MobEffectInstance nextPotion( RandomSource random ) { return get().nextPotion( random ); }
}