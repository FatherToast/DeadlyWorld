package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
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

        SPIKES = new PitfallTrapConfig.PitfallTrapTypeCategory( this, PitfallTrapType.SPIKES, 0.3, DEPTH_LAVA, DEPTH_1, 4, 4 );
        LAVA = new PitfallTrapConfig.PitfallTrapTypeCategory( this, PitfallTrapType.LAVA, 0.3, DEPTH_LAVA, DEPTH_5, 4, 4 );
        COBWEB = new PitfallTrapConfig.PitfallTrapTypeCategory( this, PitfallTrapType.COBWEB, 0.3, DEPTH_LAVA, DEPTH_1, 4, 4 );
    }

    public static class PitfallTrapTypeCategory extends FeatureTypeCategory {

        public final IntField radius;
        public final IntField depth;


        PitfallTrapTypeCategory( FeatureConfig parent, PitfallTrapType type,
                               double placements, int minHeight, int maxHeight, int radius, int depth ) {
            this( parent, type.toString(), placements, minHeight, maxHeight, radius, depth );
        }

        PitfallTrapTypeCategory( FeatureConfig parent, String name,
                               double placements, int minHeight, int maxHeight, int rad, int dpth ) {
            super( parent, name, placements, minHeight, maxHeight );

            SPEC.newLine();

            radius = SPEC.define( new IntField( "radius", rad, 1, 6,
                    "The radius of the pit generated for this trap",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );

            depth = SPEC.define( new IntField( "depth", dpth, 2, 10,
                    "The depth of the pit generated for this trap",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
}
