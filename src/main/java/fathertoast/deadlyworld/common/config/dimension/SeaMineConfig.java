package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import net.minecraft.world.level.Level;

import static fathertoast.deadlyworld.common.util.References.*;

public class SeaMineConfig extends FeatureConfig {

    public final SeaMineCategory NORMAL;
    public final SeaMineCategory PUFFER;
    public final SeaMineCategory GUARDIAN;


    SeaMineConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "sea mine" );

        if( Level.NETHER.equals( dimConfigs.DIMENSION ) ) {
            SPEC.decreaseIndent();
            SPEC.newLine();
            SPEC.comment( "Because the nether is full of lava, sea mine features doesn't really generate anywhere.",
                    "This config only really exists because of the way per-dimension-configs are handled internally in DeadlyWorld." );
            SPEC.increaseIndent();
        }

        NORMAL = new SeaMineCategory( this, SeaMineType.NORMAL, 0.2D, DEPTH_4, DEPTH_SKY, 3, 6, 4.0 );
        PUFFER = new SeaMineCategory( this, SeaMineType.PUFFER, 0.06D, DEPTH_4, DEPTH_SKY, 3, 6, 2.5 );
        GUARDIAN = new SeaMineCategory( this, SeaMineType.GUARDIAN, 0.06D, DEPTH_4, DEPTH_SKY, 3, 6, 2.5 );
    }

    public static class SeaMineCategory extends FeatureTypeCategory {

        public final IntField.RandomRange distanceFromBottom;


        SeaMineCategory( FeatureConfig parent, SeaMineType type, double placements, int minHeight, int maxHeight,
                        int minDistFromBottom, int maxDistFromBottom, double explPower ) {
            super( parent, type.toString(), placements, minHeight, maxHeight );

            SPEC.newLine();

            distanceFromBottom = new IntField.RandomRange( SPEC, "dist_from_bottom", minDistFromBottom, maxDistFromBottom, IntField.Range.POSITIVE,
                    "How far up from the ocean floor in blocks the mine will be placed, with a trail of chains underneath it." );
        }
    }
}
