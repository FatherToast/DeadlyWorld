package fathertoast.deadlyworld.common.core.config.util;

import fathertoast.deadlyworld.common.core.config.field.EntityListField;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class PotionEntry {
    /** The entity type this entry is defined for. If this is null, then this entry will match any entity. */
    public final Supplier<? extends Effect> EFFECT;
    /** The values given to this entry. Null for comparison objects. */
    public final double[] VALUES;


    PotionEntry( Effect effect ) {
        this( () -> effect );
    }

    PotionEntry( Supplier<Effect> effectSupplier ) {
        EFFECT = effectSupplier;
        VALUES = null;
    }

    public PotionEntry( Supplier<Effect> effectSupplier, double... values ) {
        EFFECT = effectSupplier;
        VALUES = values;
    }

    public PotionEntry( Effect effect, double... values ) {
        EFFECT = () -> effect;
        VALUES = values;
    }

    public PotionEntry( double... values ) {
        this( () -> null, values );
    }

    /**
     * @return Returns true if the given entity description is contained within this one (is more specific).
     * <p>
     * This operates under the assumption that there will not be multiple default entries or multiple non-extendable
     * entries for the same class in a list.
     */
    public boolean contains( PotionEntry entry ) {
        // Handle default entries
        if(EFFECT == null ) return true;
        return entry.EFFECT != null;
    }

    /**
     * @return The string representation of this potion list entry, as it would appear in a config file.
     * <p>
     * Format is "~registry_key value0 value1 ...", the ~ prefix is optional.
     */
    @Override
    public String toString() { return toString( false ); }

    /**
     * @return The string representation of this potion list entry, as it would appear in a config file.
     * <p>
     * Format is "~registry_key value0 value1 ...", the ~ prefix is optional.
     */
    public String toString( boolean castToInt ) {
        // Start with the entity type registry key
        ResourceLocation resource = EFFECT == null ? null : ForgeRegistries.POTIONS.getKey( EFFECT.get() );
        StringBuilder str = new StringBuilder( resource == null ? EntityListField.REG_KEY_DEFAULT : resource.toString() );

        // Append values array
        if( VALUES != null && VALUES.length > 0 ) {
            for( double value : VALUES ) {
                str.append( ' ' );
                if( castToInt ) { str.append( (int) value ); }
                else { str.append( value ); }
            }
        }
        return str.toString();
    }
}
