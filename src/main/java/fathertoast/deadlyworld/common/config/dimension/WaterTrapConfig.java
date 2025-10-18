package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.EntityEntry;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.config.field.WeightedEntityList;
import fathertoast.deadlyworld.common.config.field.WeightedEntityListField;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import net.minecraft.world.entity.EntityType;

import static fathertoast.deadlyworld.common.util.References.*;

public class WaterTrapConfig extends FeatureConfig {
    
    public final SeaMineCategory NORMAL_SEA_MINE;
    public final SeaMineCategory PUFFER_SEA_MINE;
    public final SeaMineCategory GUARDIAN_SEA_MINE;
    public final SeaMineMobTrapTypeCategory SEA_MINE_MOB;
    
    
    WaterTrapConfig(ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "water trap" );
        
        flagAsWaterFeature();

        NORMAL_SEA_MINE = new SeaMineCategory( this, SeaMineType.NORMAL, 0.2, DEPTH_3, DEPTH_SEA_LEVEL, 2, 8 );

        PUFFER_SEA_MINE = new SeaMineCategory( this, SeaMineType.PUFFER, 0.06, DEPTH_3, DEPTH_SEA_LEVEL, 2, 8 );

        GUARDIAN_SEA_MINE = new SeaMineCategory( this, SeaMineType.GUARDIAN, 0.06, DEPTH_3, DEPTH_SEA_LEVEL, 2, 8 );

        SEA_MINE_MOB = new SeaMineMobTrapTypeCategory( this, "sea_mine_mob", 0.2, DEPTH_2, DEPTH_SEA_LEVEL, 0.05,
                6.0, true, 20, 60, 1, 1 );
    }
    
    public static class SeaMineCategory extends FeatureTypeCategory {
        
        public final IntField.RandomRange distanceFromBottom;
        
        SeaMineCategory( FeatureConfig parent, SeaMineType type, double placements, int minHeight, int maxHeight,
                         int minDistFromBottom, int maxDistFromBottom ) {
            super( parent, type.toString(), placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            distanceFromBottom = new IntField.RandomRange( SPEC, "dist_from_bottom", minDistFromBottom, maxDistFromBottom, IntField.Range.NON_NEGATIVE,
                    "How far up from the ocean floor in blocks the mine will be placed, with a trail of chains underneath it.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );
        }
    }

    public static class SeaMineMobTrapTypeCategory extends TrapConfig.TrapTypeCategory {

        public final DoubleField decoyChance;

        public final DoubleField activationRange;
        public final DoubleField checkSightChance;

        public final IntField triggersRemaining;
        public final IntField.RandomRange resetTime;

        public final WeightedEntityListField spawnList;
        public final IntField mobCount;

        public final DoubleField speedMultiplier;
        public final DoubleField healthMultiplier;

        SeaMineMobTrapTypeCategory( FeatureConfig parent, String name, double placements, int minHeight, int maxHeight, double decoyCh,
                                    double activationRng, boolean checkSight, int minResetTime, int maxResetTime, int triggers,
                                    int mobCnt ) {
            super( parent, name, placements, minHeight, maxHeight, decoyCh, activationRng, checkSight, triggers, minHeight, maxHeight );

            SPEC.newLine();

            decoyChance = SPEC.define( new DoubleField( "decoy_chance", decoyCh, DoubleField.Range.PERCENT,
                    "The chance for " + FEATURE_TYPE_NAME + " to generate with a decoy above it.",
                    "Decoys range from fake cakes, illusionary mobs and other visual distractions that are not real.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );

            SPEC.newLine();

            activationRange = SPEC.define( standardActivationRangeField( activationRng ) );
            checkSightChance = SPEC.define( standardCheckSightField( checkSight ) );

            SPEC.newLine();

            triggersRemaining = SPEC.define( new IntField( "triggers", triggers, -1, Short.MAX_VALUE,
                    "How many times the trap can trigger before it gets \"used up\".",
                    "Setting this to -1 equals infinite triggers.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            resetTime = new IntField.RandomRange( SPEC, "reset_time", minResetTime, maxResetTime, 0, Short.MAX_VALUE,
                    "The minimum and maximum (inclusive) amount of time that must pass before a previously " +
                            "triggered trap resets, in ticks. (20 ticks = 1 second)",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );

            SPEC.newLine();

            spawnList = SPEC.define( new WeightedEntityListField( "spawn_list", makeDefaultSpawnList(),
                    "Weighted list of mobs that can be spawned by " + FEATURE_TYPE_NAME + ". One of these is chosen " +
                            "at random when the trap is generated.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );

            mobCount = SPEC.define( new IntField( "mob_count", mobCnt, IntField.Range.POSITIVE,
                    "The amount of mobs to spawn when this trap triggers." ) );

            SPEC.newLine();

            speedMultiplier = SPEC.define( new DoubleField( "speed_multiplier", 1.5, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier used when modifying the movement speed of the mobs spawned by this trap.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            healthMultiplier = SPEC.define( new DoubleField( "health_multiplier", 0.5, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier used when modifying the health of the mobs spawned by this trap.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }

        /** @return The default spawn list to use for this trap type and dimension. */
        protected WeightedEntityList makeDefaultSpawnList() {
            if( isNetherDimension() ) {
                return new WeightedEntityList();
            }
            if( isEndDimension() ) {
                return new WeightedEntityList();
            }
            // For the overworld, as well as any dimensions added by mods
            return new WeightedEntityList(
                    new EntityEntry( EntityType.DROWNED, 100 ),
                    new EntityEntry( EntityType.SQUID, 50 )
            );
        }
    }
}