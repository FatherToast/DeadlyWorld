package fathertoast.deadlyworld.common.core.config;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.config.field.*;
import fathertoast.deadlyworld.common.core.config.file.ToastConfigSpec;
import fathertoast.deadlyworld.common.core.config.util.EntityEntry;
import fathertoast.deadlyworld.common.core.config.util.PotionEntry;
import fathertoast.deadlyworld.common.core.config.util.WeightedEntityList;
import fathertoast.deadlyworld.common.core.config.util.WeightedPotionList;
import fathertoast.deadlyworld.common.tile.floortrap.FloorTrapType;
import net.minecraft.entity.EntityType;
import net.minecraft.potion.Effects;

import java.io.File;

public class FloorTrapConfig extends FeatureConfig {

    public final FloorTrapConfig.TntTrapTypeCategory TNT;
    public final FloorTrapConfig.TntMobTrapTypeCategory TNT_MOB;
    public final FloorTrapConfig.PotionTrapTypeCategory POTION;

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
                6.0,true, 60, 20, 180, 80, 3, 2.0D );

        TNT_MOB = new FloorTrapConfig.TntMobTrapTypeCategory( SPEC, this, FloorTrapType.TNT_MOB, 0.16, 12, 52, 0.3,
                5.0, true, 60, 20, 180, 80, 3, 2);

        POTION = new FloorTrapConfig.PotionTrapTypeCategory( SPEC, this, FloorTrapType.POTION, 0.16, 12, 52, 0.3,
                5.0, true, 60, 20);
    }

    public static class FloorTrapTypeCategory extends FeatureTypeCategory {

        public final DoubleField chestChance;

        public final DoubleField activationRange;
        public final BooleanField checkSight;

        public final IntField maxResetTime;
        public final IntField minResetTime;


        FloorTrapTypeCategory(ToastConfigSpec parent, FeatureConfig feature, FloorTrapType type,
                              double placements, int minHeight, int maxHeight, double chestCh,
                              double activationRange, boolean checkSight, int maxResetTime, int minResetTime ) {
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

            this.maxResetTime = SPEC.define( new IntField("max_reset_time", maxResetTime, IntField.Range.POSITIVE,
                    "The maximum amount of time that must pass before a previously triggered trap resets."));

            this.minResetTime = SPEC.define( new IntField("min_reset_time", minResetTime, IntField.Range.POSITIVE,
                    "The minimum amount of time that must pass before a previously triggered trap resets."));
        }
    }

    public static class TntTrapTypeCategory extends FloorTrapTypeCategory {

        public final IntField maxFuseTime;
        public final IntField minFuseTime;

        public final IntField tntCount;
        public final DoubleField launchSpeed;


        TntTrapTypeCategory(ToastConfigSpec parent, FeatureConfig feature, FloorTrapType type, double placements, int minHeight, int maxHeight, double chestCh,
                            double activationRange, boolean checkSight, int maxResetTime, int minResetTime, int maxFuseTime, int minFuseTime, int tntCount, double launchSpeed ) {
            super(parent, feature, type, placements, minHeight, maxHeight, chestCh, activationRange, checkSight, maxResetTime, minResetTime);

            SPEC.newLine();


            this.maxFuseTime = SPEC.define( new IntField( "max_fuse_time", maxFuseTime, IntField.Range.POSITIVE,
                    "The maximum fuse time of the TNT spawned by this trap." ) );

            this.minFuseTime = SPEC.define( new IntField( "min_fuse_time", minFuseTime, IntField.Range.POSITIVE,
                    "The minimum fuse time of the TNT spawned by this trap." ) );

            SPEC.newLine();

            this.tntCount = SPEC.define( new IntField( "tnt_count", tntCount, IntField.Range.POSITIVE,
                    "The amount of TNT spawned when this trap is activated.") );

            this.launchSpeed = SPEC.define( new DoubleField( "launch_speed", launchSpeed, DoubleField.Range.POSITIVE,
                    "The velocity at which the spawned TNT gets launched when this trap activates.") );
        }
    }

    public static class TntMobTrapTypeCategory extends TntTrapTypeCategory {

        public final WeightedEntityListField spawnList;
        public final DoubleField speedMultiplier;
        public final DoubleField healthMultiplier;

        TntMobTrapTypeCategory(ToastConfigSpec parent, FeatureConfig feature, FloorTrapType type, double placements, int minHeight, int maxHeight, double chestCh,
                               double activationRange, boolean checkSight, int maxResetTime, int minResetTime, int maxFuseTime, int minFuseTime, int tntCount, double launchSpeed) {
            super(parent, feature, type, placements, minHeight, maxHeight, chestCh, activationRange, checkSight,
                    maxResetTime, minResetTime, maxFuseTime, minFuseTime, tntCount, launchSpeed);

            this.spawnList = SPEC.define( new WeightedEntityListField("spawn_list", makeDefaultSpawnList(feature),
                    "Weighted list of mobs that can be spawned by " + FEATURE_TYPE_NAME + "s. One of these is chosen",
                    "at random when the trap is generated. Traps that are generated as 'dynamic' will pick again",
                    "between each spawn."));

            SPEC.newLine();

            this.speedMultiplier = SPEC.define( new DoubleField("speed_multiplier", 1.5, DoubleField.Range.POSITIVE,
                    "The multiplier used when modifying the movement speed of the mobs spawned by this trap."));

            this.healthMultiplier = SPEC.define( new DoubleField("health_multiplier", 1.5, DoubleField.Range.POSITIVE,
                    "The multiplier used when modifying the health of the mobs spawned by this trap."));
        }

        /** @return The default spawn list to use for this spawner type and dimension. */
        protected WeightedEntityList makeDefaultSpawnList(FeatureConfig feature ) {
            if( isNetherDimension( feature ) ) {
                return new WeightedEntityList(
                        new EntityEntry( EntityType.WITHER_SKELETON, 200 ),
                        new EntityEntry( EntityType.HUSK, 100 ),
                        new EntityEntry( EntityType.CAVE_SPIDER, 10 ),
                        new EntityEntry( EntityType.CREEPER, 10 )
                );
            }
            if( isEndDimension( feature ) ) {
                return new WeightedEntityList(
                        new EntityEntry( EntityType.ENDERMAN, 200 ),
                        new EntityEntry( EntityType.CREEPER, 10 )
                );
            }
            // For the overworld, as well as any dimensions added by mods
            return new WeightedEntityList(
                    // Vanilla dungeon mobs
                    new EntityEntry( EntityType.ZOMBIE, 200 ),
                    new EntityEntry( EntityType.SKELETON, 100 ),
                    new EntityEntry( EntityType.SPIDER, 100 ),
                    // Extras
                    new EntityEntry( EntityType.CAVE_SPIDER, 10 ),
                    new EntityEntry( EntityType.CREEPER, 10 )
            );
        }
    }

    public static class PotionTrapTypeCategory extends FloorTrapTypeCategory {

        public final WeightedPotionListField potionList;

        PotionTrapTypeCategory(ToastConfigSpec parent, FeatureConfig feature, FloorTrapType type, double placements, int minHeight, int maxHeight, double chestCh,
                               double activationRange, boolean checkSight, int maxResetTime, int minResetTime) {
            super(parent, feature, type, placements, minHeight, maxHeight, chestCh, activationRange, checkSight, maxResetTime, minResetTime);

            potionList = SPEC.define( new WeightedPotionListField("potion_list", makeDefaultPotionList(feature),
                    "Weighted list of potion effects that can be used by " + FEATURE_TYPE_NAME + "s when hurling splash potions. One of these is chosen",
                    "at random when the trap is generated. If the trap is generated as 'dynamic' it will pick again",
                    "between each potion effect.") );
        }

        /** @return The default spawn list to use for this spawner type and dimension. */
        protected WeightedPotionList makeDefaultPotionList(FeatureConfig feature ) {
            if( isNetherDimension( feature ) ) {
                return new WeightedPotionList(
                        new PotionEntry( Effects.WITHER, 5, 100, 0 ),
                        new PotionEntry( Effects.MOVEMENT_SLOWDOWN, 30, 200, 2 ),
                        new PotionEntry( Effects.POISON, 20, 100, 1 ),
                        new PotionEntry( Effects.BLINDNESS, 10, 200, 0 )
                );
            }
            if( isEndDimension( feature ) ) {
                return new WeightedPotionList(
                        new PotionEntry( Effects.LEVITATION, 40, 240, 0 ),
                        new PotionEntry( Effects.CONFUSION, 40, 200, 0 ),
                        new PotionEntry( Effects.WEAKNESS, 20, 280, 2 )
                );
            }
            // For the overworld, as well as any dimensions added by mods
            return new WeightedPotionList(
                    new PotionEntry( Effects.POISON, 20, 200, 0 ),
                    new PotionEntry( Effects.MOVEMENT_SLOWDOWN, 20, 200, 1 ),
                    new PotionEntry( Effects.WEAKNESS, 20, 150, 1 ),
                    new PotionEntry( Effects.HARM, 20, 1, 1 ),
                    new PotionEntry( Effects.HUNGER, 20, 500, 1 )
            );
        }
    }
}

