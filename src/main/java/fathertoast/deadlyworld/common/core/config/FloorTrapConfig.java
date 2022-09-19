package fathertoast.deadlyworld.common.core.config;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.field.BooleanField;
import fathertoast.deadlyworld.common.core.config.field.DoubleField;
import fathertoast.deadlyworld.common.core.config.field.IntField;
import fathertoast.deadlyworld.common.core.config.file.ToastConfigSpec;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapType;

import java.io.File;

public class FloorTrapConfig extends FeatureConfig {

    public final FloorTrapConfig.TntTrapTypeCategory TNT;

    /** Builds the config spec that should be used for this config. */
    FloorTrapConfig( File dir, DimensionConfigGroup dimConfigs ) {
        super( dir, dimConfigs, "floor_trap" );

        SPEC.newLine();
        SPEC.describeEntityList();

        SPEC.newLine();
        SPEC.comment(
                "woah"
        );

        TNT = new FloorTrapConfig.TntTrapTypeCategory( SPEC, this, FloorTrapType.TNT, 0.16, 12, 52, 0.3,
                6.0,true, 16, 8, 3, 2.0D );
    }

    public static class FloorTrapTypeCategory extends FeatureTypeCategory {

        public final DoubleField chestChance;

        public final DoubleField activationRange;
        public final BooleanField checkSight;

        public final IntField maxFuseTime;
        public final IntField minFuseTime;

        FloorTrapTypeCategory(ToastConfigSpec parent, FeatureConfig feature, FloorTrapType type,
                              double placements, int minHeight, int maxHeight, double chestCh,
                              double activationRange, boolean checkSight, int maxFuseTime, int minFuseTime ) {
            super( parent, feature, type.getSerializedName(), placements, minHeight, maxHeight );

            if( isSubfeature() ) {
                chestChance = null;
            }
            else {
                SPEC.newLine();

                chestChance = SPEC.define( new DoubleField( "chest_chance", chestCh, DoubleField.Range.PERCENT,
                        "The chance for a chest to generate beneath " + FEATURE_TYPE_NAME + ".",
                        "For reference, the loot table for these chests is '" + DeadlyWorld.toString( type.getChestLootTable() ) + "'." ) );

                SPEC.newLine();
            }

            this.activationRange = SPEC.define( new DoubleField( "activation_range", activationRange, DoubleField.Range.POSITIVE,
                    "The floor trap is active as long as a player is within this distance (spherical distance)." ) );
            this.checkSight = SPEC.define( new BooleanField( "activation_sight_check", checkSight,
                    "When the sight check is enabled, " + FEATURE_TYPE_NAME + " will only activate when they have direct",
                    "line-of-sight to a player within activation range. The floor trap's delay will continue to tick down,",
                    "but it will wait to actually activate until it has line-of-sight." ) );

            SPEC.newLine();


            this.maxFuseTime = SPEC.define( new IntField( "max_fuse_time", maxFuseTime, IntField.Range.POSITIVE,
                    "The maximum amount of time that must pass before this trap activates." ) );

            this.minFuseTime = SPEC.define( new IntField( "min_fuse_time", minFuseTime, IntField.Range.POSITIVE,
                    "The minimum amount of time that must pass before this trap activates." ) );
        }
    }

    public static class TntTrapTypeCategory extends FloorTrapTypeCategory {

        public final IntField tntCount;
        public final DoubleField launchSpeed;


        TntTrapTypeCategory(ToastConfigSpec parent, FeatureConfig feature, FloorTrapType type, double placements, int minHeight, int maxHeight, double chestCh,
                            double activationRange, boolean checkSight, int maxFuseTime, int minFuseTime, int tntCount, double launchSpeed ) {
            super(parent, feature, type, placements, minHeight, maxHeight, chestCh, activationRange, checkSight, maxFuseTime, minFuseTime);

            SPEC.newLine();

            this.tntCount = SPEC.define( new IntField( "tnt_count", tntCount, IntField.Range.POSITIVE,
                    "The amount of TNT spawned when this trap is activated.") );

            this.launchSpeed = SPEC.define( new DoubleField( "launch_speed", launchSpeed, DoubleField.Range.POSITIVE,
                    "The velocity at which the spawned TNT gets launched when this trap activates.") );
        }
    }
}
