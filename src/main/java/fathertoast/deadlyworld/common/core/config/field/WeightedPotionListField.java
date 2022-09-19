package fathertoast.deadlyworld.common.core.config.field;

import fathertoast.deadlyworld.common.core.config.file.TomlHelper;
import fathertoast.deadlyworld.common.core.config.util.PotionEntry;
import fathertoast.deadlyworld.common.core.config.util.WeightedPotionList;

import javax.annotation.Nullable;
import java.util.List;

public class WeightedPotionListField extends AbstractPotionListField<WeightedPotionList> {
    /** Creates a new field. */
    public WeightedPotionListField( String key, WeightedPotionList defaultValue, String... description ) {
        super( key, defaultValue, description );
    }

    /**
     * Loads this field's value from the given raw toml value. If anything goes wrong, correct it at the lowest level possible.
     * <p>
     * For example, a missing value should be set to the default, while an out-of-range value should be adjusted to the
     * nearest in-range value
     */
    @Override
    public void load( @Nullable Object raw ) {
        super.load( raw );
        super.get().calculateTotalWeight();
    }

    /** @return The string representation for the field range to use. */
    @Override
    protected String getFieldRange() { return TomlHelper.fieldRange( (int) valueDefault.getMinValue(), (int) valueDefault.getMaxValue() ); }

    /** @return A newly constructed entity list from the given entries. */
    @Override
    protected WeightedPotionList createNewList( List<PotionEntry> entryList ) { return new WeightedPotionList( entryList ); }
}
