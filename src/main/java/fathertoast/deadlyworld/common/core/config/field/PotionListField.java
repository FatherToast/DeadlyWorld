package fathertoast.deadlyworld.common.core.config.field;

import fathertoast.deadlyworld.common.core.config.util.PotionEntry;
import fathertoast.deadlyworld.common.core.config.util.PotionList;
import net.minecraft.potion.Effect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PotionListField extends AbstractPotionListField<PotionList> {
    /** The string to use in place of a registry key for a default entry. */
    public static final String REG_KEY_DEFAULT = "default";

    /** Provides a detailed description of how to use entity lists. Recommended to put at the top of any file using potion lists. */
    public static List<String> verboseDescription() {
        List<String> comment = new ArrayList<>();
        comment.add( "Potion List fields: General format = [ \"namespace:effect weight duration amplifier ...\", ... ]" );
        comment.add( "  Potion lists are arrays of potion effect types. Some potion lists specify a number of values linked to each potion effect." );
        comment.add( "  Potion effects are defined by their key in the effect registry, usually following the pattern 'namespace:effect_name'." );
        comment.add( "  '" + REG_KEY_DEFAULT + "' can be used instead of a potion effect registry key to provide default values for all entities." );
        comment.add( "  An asterisk '*' can be used to match multiple potion effects. For example, 'minecraft:*' will match all vanilla effects." );
        comment.add( "  List entries by default match any potion effect derived from (i.e. based on) their potion effect. A tilde '~' prefix" );
        comment.add( "    disables that extra matching. For example, '~minecraft:regeneration'." );
        comment.add( "  There is no real rule for deriving, even in vanilla, but the hope is that mod-added mobs will derive from their base mob." );
        return comment;
    }

    /** Creates a new field. */
    public PotionListField( String key, PotionList defaultValue, String... description ) {
        super( key, defaultValue, description );
    }

    /** @return A newly constructed entity list from the given entries. */
    @Override
    protected PotionList createNewList( List<PotionEntry> entryList ) { return new PotionList( entryList ); }

    /**
     * Represents two potion list fields, a blacklist and a whitelist, combined into one.
     * The blacklist cannot contain values, but the whitelist can have any settings.
     */
    public static class Combined {
        /** The whitelist. To match, the entry must be present here. */
        private final PotionListField WHITELIST;
        /** The blacklist. Entries present here are ignored entirely. */
        private final PotionListField BLACKLIST;

        /** Links two lists together as blacklist and whitelist. */
        public Combined( PotionListField whitelist, PotionListField blacklist ) {
            WHITELIST = whitelist;
            BLACKLIST = blacklist;
            if( blacklist.valueDefault.getRequiredValues() != 0 ) {
                throw new IllegalArgumentException( "Blacklists cannot have values! See: " + blacklist.getKey() );
            }
        }

        /** @return True if the effect is contained in this list. */
        public boolean contains( Effect effect ) {
            return effect != null && !BLACKLIST.get().contains( effect ) && WHITELIST.get().contains( effect );
        }

        /**
         * @param effect The effect to retrieve values for.
         * @return The array of values of the best-match entry. Returns null if the effect is not contained in this entity list.
         */
        public double[] getValues( Effect effect ) {
            return effect != null && !BLACKLIST.get().contains( effect ) ? WHITELIST.get().getValues( effect ) : null;
        }

        /**
         * @param effect The effect to retrieve a value for.
         * @return The first value in the best-match entry's value array. Returns 0 if the effect is not contained in this
         * potion list or has no values specified. This should only be used for 'single value' lists.
         * @see PotionList#setSingleValue()
         * @see PotionList#setSinglePercent()
         */
        public double getValue( Effect effect ) {
            return effect != null && !BLACKLIST.get().contains( effect ) ? WHITELIST.get().getValue( effect ) : 0.0;
        }

        /**
         * @param effect The effect to roll a value for.
         * @return Randomly rolls the first percentage value in the best-match entry's value array. Returns false if the effect
         * is not contained in this potion list or has no values specified. This should only be used for 'single percent' lists.
         * @see PotionList#setSinglePercent()
         */
        public boolean rollChance( Effect effect, Random random ) {
            return effect != null && !BLACKLIST.get().contains( effect ) && WHITELIST.get().rollChance( effect, random );
        }
    }
}
