package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.block.pitfall.PitfallTrapType;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;

import static fathertoast.deadlyworld.common.util.References.*;

public class PitfallTrapConfig extends FeatureConfig {

    public final PitfallTrapConfig.PitfallTrapTypeCategory SPIKES;
    public final PitfallTrapConfig.PitfallTrapTypeCategory LAVA;
    public final PitfallTrapConfig.PitfallTrapTypeCategory COBWEB;


    /** Builds the config spec that should be used for this config. */
    PitfallTrapConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "pitfall trap" );

        SPEC.newLine();

        SPIKES = new PitfallTrapConfig.PitfallTrapTypeCategory( this, PitfallTrapType.SPIKES,
                0.3, DEPTH_LAVA, DEPTH_1, 1, 4, 2, 4 );
        LAVA = new PitfallTrapConfig.PitfallTrapTypeCategory( this, PitfallTrapType.LAVA,
                0.3, DEPTH_LAVA, DEPTH_4, 1, 5, 2, 4 );
        COBWEB = new PitfallTrapConfig.PitfallTrapTypeCategory( this, PitfallTrapType.COBWEB,
                0.3, DEPTH_LAVA, DEPTH_1, 1, 4, 2, 4 );
    }

    public static class PitfallTrapTypeCategory extends FeatureTypeCategory {

        public final IntField.RandomRange radius;
        public final IntField.RandomRange depth;


        PitfallTrapTypeCategory( FeatureConfig parent, PitfallTrapType type,
                               double placements, int minHeight, int maxHeight, int minRad, int maxRad, int minDpth, int maxDpth ) {
            this( parent, type.toString(), placements, minHeight, maxHeight, minRad, maxRad, minDpth, maxDpth );
        }

        PitfallTrapTypeCategory( FeatureConfig parent, String name,
                               double placements, int minHeight, int maxHeight, int minRad, int maxRad, int minDpth, int maxDpth ) {
            super( parent, name, placements, minHeight, maxHeight );

            SPEC.newLine();

            radius = new IntField.RandomRange( SPEC, "radius", minRad, maxRad, 1, 10,
                    "The minimum and maximum (inclusive) radius of the pit generated for this trap.",
                    "Note that a bigger radius will make it harder to pass generation criteria and make the trap less likely to generate.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );

            depth = new IntField.RandomRange( SPEC, "depth", minDpth, maxDpth, 2, 6,
                    "The minimum and maximum (inclusive) depth of the pit generated for this trap",
                    "Note that a greater depth will make it harder to pass generation criteria and make the trap less likely to generate.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );
        }
    }
}
