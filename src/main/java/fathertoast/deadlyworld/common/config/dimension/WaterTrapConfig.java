package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.collection.RegistryWeightedListField;
import fathertoast.crust.api.config.common.value.collection.RegistryWeightedList;
import fathertoast.deadlyworld.common.block.sea_mine.SeaMineType;
import fathertoast.deadlyworld.common.config.value.MobEffectWeightedListField;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import static fathertoast.deadlyworld.common.util.References.DEPTH_LAVA;
import static fathertoast.deadlyworld.common.util.References.DEPTH_SEA_LEVEL;

public class WaterTrapConfig extends FeatureConfig {
    
    public final SeaMineCategory NORMAL_SEA_MINE;
    public final SeaMineCategory PUFFER_SEA_MINE;
    public final SeaMineCategory GUARDIAN_SEA_MINE;
    public final SeaMineMobTrapTypeCategory SEA_MINE_MOB;
    
    WaterTrapConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "water trap" );
        
        flagAsWaterFeature();
        
        NORMAL_SEA_MINE = new SeaMineCategory( this, SeaMineType.NORMAL, 0.6, 0.3,
                DEPTH_LAVA, DEPTH_SEA_LEVEL, 2, 8 );
        
        PUFFER_SEA_MINE = new SeaMineCategory( this, SeaMineType.PUFFER, 0.3, 0.1,
                DEPTH_LAVA, DEPTH_SEA_LEVEL, 2, 10 );
        
        GUARDIAN_SEA_MINE = new SeaMineCategory( this, SeaMineType.GUARDIAN, 0.2, 0.1,
                DEPTH_LAVA, DEPTH_SEA_LEVEL, 2, 6 );
        
        SEA_MINE_MOB = new SeaMineMobTrapTypeCategory( this, "sea_mine_mob", 0.3, 0.2,
                DEPTH_LAVA, DEPTH_SEA_LEVEL, 0.05, 6.0, true, 1, 20, 60 );
    }
    
    public static class SeaMineCategory extends FeatureTypeCategory {
        
        public final DoubleField countPerChunkInOcean;
        
        public final IntField.RandomRange distanceFromBottom;
        
        public final DoubleField explosionPower;
        
        public final MobEffectWeightedListField potions;
        
        
        SeaMineCategory( FeatureConfig parent, SeaMineType type, double placements, double oceanPlacements,
                         int minHeight, int maxHeight, int minDistFromBottom, int maxDistFromBottom ) {
            super( parent, type + "_sea_mine", placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            countPerChunkInOcean = SPEC.define( new DoubleField( "placements_ocean",
                    parent.DISABLED ? 0.0 : oceanPlacements, DoubleField.Range.NON_NEGATIVE,
                    "The number of placement attempts in surface water per chunk (16x16 blocks) for " + FEATURE_TYPE_NAME +
                            ". A decimal represents a chance for a placement attempt (e.g., 0.3 means 30% chance for one attempt).",
                    DimensionConfigHelper.MESSAGE_PLACED_FEATURE_OVERRIDE ) );
            
            SPEC.newLine();
            
            distanceFromBottom = new IntField.RandomRange( SPEC, "dist_from_bottom", minDistFromBottom, maxDistFromBottom, IntField.Range.NON_NEGATIVE,
                    "How far up from the ocean floor in blocks the mine will be placed, with a trail of chains underneath it.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );
            
            SPEC.newLine();
            
            explosionPower = SPEC.define( new DoubleField( "explosion_power", type.defaultExplosionPower(), DoubleField.Range.NON_NEGATIVE,
                    "The explosion power of this mine block.",
                    "For reference, vanilla creepers = 3.0 and vanilla TNT = 4.0.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            
            SPEC.newLine();
            
            potions = SPEC.define( new MobEffectWeightedListField( "potions", type.defaultPotions(),
                    "A list of potions that may be applied to creatures caught in this mine's blast.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
    
    public static class SeaMineMobTrapTypeCategory extends FloorTrapConfig.TrapTypeCategory {
        
        public final DoubleField countPerChunkInOcean;
        
        public final RegistryWeightedListField<EntityType<?>> spawnList;
        
        public final DoubleField speedMultiplier;
        public final DoubleField healthMultiplier;
        
        SeaMineMobTrapTypeCategory( FeatureConfig parent, String name, double placements, double oceanPlacements,
                                    int minHeight, int maxHeight, double decoyCh, double activationRng, boolean checkSight,
                                    int triggers, int minResetTime, int maxResetTime ) {
            super( parent, name, placements, minHeight, maxHeight, decoyCh, activationRng, checkSight, triggers, minResetTime, maxResetTime );
            
            SPEC.newLine();
            
            countPerChunkInOcean = SPEC.define( new DoubleField( "placements_ocean",
                    parent.DISABLED ? 0.0 : oceanPlacements, DoubleField.Range.NON_NEGATIVE,
                    "The number of placement attempts in surface water per chunk (16x16 blocks) for " + FEATURE_TYPE_NAME +
                            ". A decimal represents a chance for a placement attempt (e.g., 0.3 means 30% chance for one attempt).",
                    DimensionConfigHelper.MESSAGE_PLACED_FEATURE_OVERRIDE ) );
            
            SPEC.newLine();
            
            spawnList = SPEC.define( new RegistryWeightedListField<>( "spawn_choices", makeDefaultSpawnList(),
                    "Weighted list of mobs that can be spawned by " + FEATURE_TYPE_NAME + ". One of these is chosen " +
                            "at random when the trap is generated.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            
            SPEC.newLine();
            
            speedMultiplier = SPEC.define( new DoubleField( "speed_multiplier", 1.5, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier used when modifying the movement speed of the mobs spawned by this trap.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            healthMultiplier = SPEC.define( new DoubleField( "health_multiplier", 0.5, DoubleField.Range.NON_NEGATIVE,
                    "The multiplier used when modifying the health of the mobs spawned by this trap.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
        
        /** @return The default spawn list to use for this trap type and dimension. */
        protected RegistryWeightedList<EntityType<?>> makeDefaultSpawnList() {
            if( isNetherDimension() ) {
                return new RegistryWeightedList<>( ForgeRegistries.ENTITY_TYPES );
            }
            if( isEndDimension() ) {
                return new RegistryWeightedList<>( ForgeRegistries.ENTITY_TYPES );
            }
            // For the overworld, as well as any dimensions added by mods
            return new RegistryWeightedList.Builder<>( ForgeRegistries.ENTITY_TYPES )
                    .add( 100, EntityType.DROWNED )
                    .add( 50, EntityType.SQUID )
                    .build();
        }
    }
}