package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.deadlyworld.common.block.spike_trap.SpikeTrapType;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;

import static fathertoast.deadlyworld.common.util.References.DEPTH_0;
import static fathertoast.deadlyworld.common.util.References.DEPTH_LAVA;

public class SpikeTrapConfig extends FeatureConfig {

    public final SpikeTrapConfig.SpikeTrapTypeCategory STATIC;
    public final SpikeTrapConfig.SpikeTrapTypeCategory MECHANICAL;


    /** Builds the config spec that should be used for this config. */
    SpikeTrapConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "spike trap" );

        SPEC.newLine();

        STATIC = new SpikeTrapConfig.SpikeTrapTypeCategory( this, SpikeTrapType.STATIC, 0.3, DEPTH_LAVA, DEPTH_0, 3.0F );
        MECHANICAL = new SpikeTrapConfig.SpikeTrapTypeCategory( this, SpikeTrapType.MECHANICAL, 0.3, DEPTH_LAVA, DEPTH_0, 4.0F );
    }

    public static class SpikeTrapTypeCategory extends FeatureTypeCategory {

        public final DoubleField damage;

        SpikeTrapTypeCategory( FeatureConfig parent, SpikeTrapType type,
                          double placements, int minHeight, int maxHeight, double damage ) {
            this( parent, type.toString(), placements, minHeight, maxHeight, damage );
        }

        SpikeTrapTypeCategory( FeatureConfig parent, String name,
                          double placements, int minHeight, int maxHeight, double dmg ) {
            super( parent, name, placements, minHeight, maxHeight );

            SPEC.newLine();

            damage = SPEC.define( new DoubleField( "damage", dmg, DoubleField.Range.NON_NEGATIVE,
                    "The amount of damage this spike trap deals to players standing on it",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
}
