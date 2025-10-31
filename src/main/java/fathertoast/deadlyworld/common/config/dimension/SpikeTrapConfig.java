package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
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

        STATIC = new SpikeTrapConfig.SpikeTrapTypeCategory( this, SpikeTrapType.STATIC, 0.3, DEPTH_LAVA, DEPTH_0, 3.0F,
                8, 20, 5, 2 );
        MECHANICAL = new SpikeTrapConfig.SpikeTrapTypeCategory( this, SpikeTrapType.MECHANICAL, 0.3, DEPTH_LAVA, DEPTH_0, 4.0F,
                8, 20, 5, 2 );
    }

    public static class SpikeTrapTypeCategory extends FeatureTypeCategory {

        public final IntField.RandomRange patchSize;
        public final IntField xzSpread;
        public final IntField ySpread;

        public final DoubleField damage;

        SpikeTrapTypeCategory( FeatureConfig parent, SpikeTrapType type,
                          double placements, int minHeight, int maxHeight, double damage,
                               int minPatchSize, int maxPatchSize, int xzSpread, int ySpread ) {
            this( parent, type.toString(), placements, minHeight, maxHeight, damage, minPatchSize, maxPatchSize, xzSpread, ySpread );
        }

        SpikeTrapTypeCategory( FeatureConfig parent, String name,
                          double placements, int minHeight, int maxHeight, double dmg,
                               int minPatchSize, int maxPatchSize, int xzSprd, int ySprd ) {
            super( parent, name, placements, minHeight, maxHeight );

            SPEC.newLine();

            patchSize = new IntField.RandomRange( SPEC, "patch_size", minPatchSize, maxPatchSize, IntField.Range.POSITIVE,
                    "The minimum and maximum (inclusive) amount of spike trap blocks that can potentially generate in a patch." );

            xzSpread = SPEC.define( new IntField( "spread.vertical", xzSprd, 1, 15,
                    "How far on the X and Z axis placements can be offset in a spike trap patch." ) );

            ySpread = SPEC.define( new IntField( "spread.horizontal", ySprd, 0, 10,
                    "How far on the Y axis placements can be offset in a spike trap patch." ) );

            SPEC.newLine();

            damage = SPEC.define( new DoubleField( "damage", dmg, DoubleField.Range.NON_NEGATIVE,
                    "The amount of damage this spike trap deals to players standing on it",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
}
