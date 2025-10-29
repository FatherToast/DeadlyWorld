package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.ConfigUtil;
import fathertoast.crust.api.config.common.field.AttributeListField;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.AttributeEntry;
import fathertoast.crust.api.config.common.value.AttributeList;
import fathertoast.crust.api.config.common.value.EntityEntry;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.config.field.WeightedEntityList;
import fathertoast.deadlyworld.common.config.field.WeightedEntityListField;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;

import static fathertoast.deadlyworld.common.util.References.*;

public class SpawnerConfig extends FeatureConfig {
    
    public final SpawnerTypeCategory SIMPLE;
    public final SpawnerTypeCategory STREAM;
    public final SpawnerTypeCategory SWARM;
    public final BrutalSpawnerCategory BRUTAL;
    public final FloatySpawnerCategory FLOATY;
    public final SpawnerTypeCategory NEST;
    public final SpawnerTypeCategory MINI;
    
    public final SubfeatureSpawnerCategory BURIED;
    public final DungeonSpawnerCategory DUNGEON;
    
    /** Builds the config spec that should be used for this config. */
    SpawnerConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "spawner" );
        
        SPEC.newLine();
        SPEC.describeEntityList();
        
        SPEC.newLine();
        SPEC.comment( "Progressive Spawn Delay:",
                "  * By default, spawners added by this mod use a mechanic called 'progressive spawn delay'. Unlike vanilla " +
                        "spawners that have a completely random delay chosen anywhere from 10 to 40 seconds (what awful variance!), " +
                        "Deadly World spawners will start from a 10 second delay and slowly increase up to 40 seconds delay as you " +
                        "continue to stand close to them (with the same vanilla delay limits of 200-800 ticks).",
                "  * A spawner's 'delay buildup' starts at its minimum delay and increases by its delay progression (" +
                        ConfigUtil.PLUS_OR_MINUS + "10%) with each successful spawn, up to its maximum delay.",
                "  * While no players are within the spawner's activation range, its 'delay buildup' is continuously decreased by " +
                        "its delay recovery, back down to its minimum delay.",
                "  * The delay, delay progression, and delay recovery for each spawner type are determined by either these configs or " +
                        "the 'configured_feature' json file when generated or placed and can then be overwritten for individual " +
                        "spawners by nbt editing." );
        
        SIMPLE = new SpawnerTypeCategory( this, SpawnerType.SIMPLE, 0.5, DEPTH_LAVA, DEPTH_0,
                16, false, 200, 800, 40, 4, 4, 0.1, 0.05 );
        STREAM = new SpawnerTypeCategory( this, SpawnerType.STREAM, 0.15, DEPTH_LAVA, DEPTH_1,
                16, true, 0, 400, 10, 1, 2, 0.9, 0.05 );
        SWARM = new SpawnerTypeCategory( this, SpawnerType.SWARM, 0.1, DEPTH_LAVA, DEPTH_2,
                20, true, 400, 2400, 100, 12, 8, 0.05, 0.05 );
        BRUTAL = new BrutalSpawnerCategory( this, SpawnerType.BRUTAL, 0.05, DEPTH_LAVA, DEPTH_3,
                16, true, 200, 800, 100, 2, 3, 0.05, 0.05 );
        FLOATY = new FloatySpawnerCategory( this, SpawnerType.FLOATY, 0.05, DEPTH_LAVA, DEPTH_2,
                25, true, 200, 800, 100, 3, 4, 0.05, 0.05, 5 );
        NEST = new NestSpawnerCategory( this, SpawnerType.NEST, 0.5, DEPTH_LAVA, DEPTH_SEA_LEVEL,
                16, false, 100, 400, 20, 6, 6, 0.0, 0.05 );
        MINI = new MiniSpawnerCategory( this, SpawnerType.MINI, 0.02, DEPTH_LAVA, DEPTH_0,
                12, false, 100, 400, 20, 6, 4, 0.2, 0.2 );
        BURIED = new SubfeatureSpawnerCategory( this, SpawnerType.BURIED,
                16, false, 200, 800, 40, 4, 4, 0.1, 0.05 );
        DUNGEON = new DungeonSpawnerCategory( this, SpawnerType.DUNGEON,
                16, false, 200, 800, 40, 4, 4, 0.1, 0.05 );
    }
    
    public static class SpawnerTypeCategory extends FeatureTypeCategory {
        
        public final IntField activationRange;
        public final DoubleField checkSightChance;
        public final IntField maxNearbyEntities;
        
        public final IntField.RandomRange delay;
        public final IntField delayProgression;
        public final DoubleField delayRecovery;
        
        public final IntField maxSpawns;
        public final IntField spawnCount;
        public final IntField spawnRange;
        
        public final DoubleField dynamicChance;
        public final WeightedEntityListField spawnList;
        
        public final DoubleField mimicChance;
        
        public final AttributeListField attributeAdjustments;
        
        SpawnerTypeCategory( FeatureConfig parent, SpawnerType type,
                             double placements, int minHeight, int maxHeight,
                             int activationRng, boolean checkSight, int minDelay, int maxDelay, int delayPrgr,
                             int spawnCnt, int spawnRng, double dynamicCh, double mimicCh ) {
            super( parent, type.toString(), placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            activationRange = SPEC.define( standardActivationRangeIntField( activationRng ) );
            checkSightChance = SPEC.define( standardCheckSightField( checkSight ) );
            maxNearbyEntities = SPEC.define( new IntField( "max_nearby_entities", spawnCnt * 2, 0, Short.MAX_VALUE,
                    "Will not spawn if this many or more similar entities are in close proximity. " +
                            "Specifically, it checks a cube that extends out by the spawn range in all six directions. " +
                            "Set this to 0 to allow spawns regardless of nearby entities.",
                    "For reference, vanilla spawners have a max of 6 nearby entities.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            
            SPEC.newLine();
            
            delay = new IntField.RandomRange( SPEC, "delay", minDelay, maxDelay, 0, Short.MAX_VALUE,
                    "The minimum and maximum (inclusive) delay between spawn batches, in ticks. (20 ticks = 1 second)",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE );
            delayProgression = SPEC.define( new IntField( "delay.progression", delayPrgr, 0, Short.MAX_VALUE,
                    "Each spawn batch increases the spawner's delay buildup by this many ticks (" + ConfigUtil.PLUS_OR_MINUS +
                            "10%). Set this to 0 to revert to the vanilla spawner behavior (simple random between min and max delays).",
                    "See above for a more in-depth description of progressive spawn delay.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            delayRecovery = SPEC.define( new DoubleField( "delay.recovery_rate", delayPrgr * 0.0025, DoubleField.Range.NON_NEGATIVE,
                    "The rate at which the spawn delay buildup on spawners recovers while no players are within range.",
                    "Inactive spawners' delays are reduced by this value each tick (20 times per second).",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            
            SPEC.newLine();
            
            maxSpawns = SPEC.define( new IntField( "max_spawns", 0, 0, Short.MAX_VALUE,
                    "The total number of mobs that can be spawned by this type of spawner. Spawners permanently " +
                            "deactivate when they run out of spawns. Set this to 0 for unlimited spawns (the normal behavior).",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            spawnCount = SPEC.define( new IntField( "spawn_count", spawnCnt, 0, Short.MAX_VALUE,
                    "The number of mobs to try spawning with each spawn batch. May spawn fewer depending on nearby obstructions.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            spawnRange = SPEC.define( new IntField( "spawn_range", spawnRng, 0, Short.MAX_VALUE,
                    "The maximum horizontal range to spawn mobs in.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            
            SPEC.newLine();
            
            dynamicChance = SPEC.define( new DoubleField( "dynamic_chance", dynamicCh, DoubleField.Range.PERCENT,
                    "The chance for " + FEATURE_TYPE_NAME + " to generate as 'dynamic'.",
                    "Dynamic spawners pick a new mob to spawn after each spawn.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            spawnList = SPEC.define( new WeightedEntityListField( "spawn_list", makeDefaultSpawnList( parent ),
                    "Weighted list of mobs that can be spawned by " + FEATURE_TYPE_NAME +
                            ". One of these is chosen at random when the spawner is generated.",
                    "Spawners generated as 'dynamic' pick again after each spawn.",
                    DimensionConfigHelper.MESSAGE_WORK_IN_PROGRESS_OVERRIDE ) ); // TODO
            
            SPEC.newLine();
            
            mimicChance = SPEC.define( new DoubleField( "mimic_chance", mimicCh, DoubleField.Range.PERCENT,
                    "The chance for " + FEATURE_TYPE_NAME + " to generate as a mimic.",
                    "The mimic reveals itself when the spawner block is destroyed." ) );
            
            SPEC.newLine();
            
            attributeAdjustments = SPEC.define( new AttributeListField( "attribute_adjustments", makeDefaultAttributeList( type ),
                    "Base attribute adjustments applied to entities spawned by " + FEATURE_TYPE_NAME + ", if applicable.",
                    DimensionConfigHelper.MESSAGE_WORK_IN_PROGRESS_OVERRIDE ) ); // TODO
        }
        
        /** @return The default spawn list to use for this spawner type and dimension. */
        protected AttributeList makeDefaultAttributeList( SpawnerType type ) {
            if( type == SpawnerType.BRUTAL ) {
                return new AttributeList(
                        AttributeEntry.add( Attributes.MAX_HEALTH, 5.0 ),
                        AttributeEntry.mult( Attributes.MAX_HEALTH, 1.2 ),
                        AttributeEntry.add( Attributes.KNOCKBACK_RESISTANCE, 0.5 ),
                        AttributeEntry.add( Attributes.ARMOR, 12.0 ),
                        AttributeEntry.add( Attributes.ARMOR_TOUGHNESS, 8.0 ),
                        AttributeEntry.add( Attributes.ATTACK_DAMAGE, 1.0 ),
                        AttributeEntry.mult( Attributes.ATTACK_DAMAGE, 1.2 ),
                        AttributeEntry.add( Attributes.ATTACK_KNOCKBACK, 2.0 ),
                        AttributeEntry.mult( Attributes.MOVEMENT_SPEED, 1.1 )
                );
            }
            return new AttributeList();
        }
        
        /** @return The default spawn list to use for this spawner type and dimension. */
        protected WeightedEntityList makeDefaultSpawnList( FeatureConfig feature ) {
            if( isNetherDimension() ) {
                return new WeightedEntityList(
                        new EntityEntry( EntityType.WITHER_SKELETON, 200 ),
                        new EntityEntry( EntityType.HUSK, 100 ),
                        new EntityEntry( EntityType.BLAZE, 100 ),
                        new EntityEntry( EntityType.CAVE_SPIDER, 10 ),
                        new EntityEntry( EntityType.CREEPER, 10 ),
                        new EntityEntry( EntityType.MAGMA_CUBE, 10 )
                );
            }
            if( isEndDimension() ) {
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
                    new EntityEntry( EntityType.CREEPER, 10 ),
                    new EntityEntry( EntityType.SILVERFISH, 10 )
            );
        }
    }
    
    public static class SubfeatureSpawnerCategory extends SpawnerTypeCategory implements SubfeatureCategory {
        SubfeatureSpawnerCategory( FeatureConfig parent, SpawnerType type,
                                   int activationRng, boolean checkSight,
                                   int minDelay, int maxDelay, int delayPrgr, int spawnCnt, int spawnRng, double dynamicCh, double mimicCh ) {
            super( parent, type, 0.0, 0, 0, activationRng, checkSight,
                    minDelay, maxDelay, delayPrgr, spawnCnt, spawnRng, dynamicCh, mimicCh );
        }
    }
    
    public static class DungeonSpawnerCategory extends SubfeatureSpawnerCategory {
        
        public final BooleanField useForgeHookEntities;
        
        DungeonSpawnerCategory( FeatureConfig parent, SpawnerType type,
                                int activationRng, boolean checkSight,
                                int minDelay, int maxDelay, int delayPrgr, int spawnCnt, int spawnRng, double dynamicCh, double mimicCh ) {
            super( parent, type, activationRng, checkSight,
                    minDelay, maxDelay, delayPrgr, spawnCnt, spawnRng, dynamicCh, mimicCh );
            
            SPEC.newLine();
            
            useForgeHookEntities = SPEC.define( new BooleanField( "use_forge_hook_entities", false,
                    "If true, the type of monster to spawn is picked from Forge's internal weighted list of " +
                            "entity types that can spawn in monster rooms/simple dungeons.",
                    "If the spawner is 'dynamic' and picks a new type of monster to spawn every time it spawns, " +
                            "a new type will be picked from the weighted Forge list instead of this spawner's spawn list config.",
                    "This setting takes priority over this spawner's spawn list config.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
    
    public static class BrutalSpawnerCategory extends SpawnerTypeCategory {
        
        public final BooleanField ambientFx;
        public final BooleanField fireResistance;
        public final BooleanField waterBreathing;
        
        BrutalSpawnerCategory( FeatureConfig parent, SpawnerType type,
                               double placements, int minHeight, int maxHeight, int activationRng, boolean checkSight,
                               int minDelay, int maxDelay, int delayPrgr, int spawnCnt, int spawnRng, double dynamicCh, double mimicCh ) {
            super( parent, type, placements, minHeight, maxHeight, activationRng, checkSight,
                    minDelay, maxDelay, delayPrgr, spawnCnt, spawnRng, dynamicCh, mimicCh );
            
            SPEC.newLine();
            
            ambientFx = SPEC.define( new BooleanField( "brutal_ambient_fx", false,
                    "If true, the potion effects below will not display potion effects particles.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            fireResistance = SPEC.define( new BooleanField( "brutal_fire_resistance", true,
                    "If true, non-creeper mobs spawned by " + FEATURE_TYPE_NAME + " will have the " +
                            "'fire resistance' potion effect.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            waterBreathing = SPEC.define( new BooleanField( "brutal_water_breathing", true,
                    "If true, non-creeper mobs spawned by " + FEATURE_TYPE_NAME + " will have the " +
                            "'water breathing' potion effect.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }

    public static class FloatySpawnerCategory extends SpawnerTypeCategory {

        public final IntField distFromFloor;

        public final BooleanField ambientFx;
        public final BooleanField slowFalling;
        public final BooleanField jumpBoost;

        FloatySpawnerCategory( FeatureConfig parent, SpawnerType type,
                               double placements, int minHeight, int maxHeight, int activationRng, boolean checkSight,
                               int minDelay, int maxDelay, int delayPrgr, int spawnCnt, int spawnRng, double dynamicCh, double mimicCh, int dstFromFloor ) {
            super( parent, type, placements, minHeight, maxHeight, activationRng, checkSight,
                    minDelay, maxDelay, delayPrgr, spawnCnt, spawnRng, dynamicCh, mimicCh );

            SPEC.newLine();

            distFromFloor = SPEC.define( new IntField( "distance_from_floor", dstFromFloor, IntField.Range.POSITIVE,
                    "How many blocks up from the floor the spawner generates.",
                    "Keep in mind that the default feature requires that there is space for at least one chain block above before hitting the ceiling.",
                    "This also means the spawner doesn't generate unless there is a ceiling above where it is trying to generate",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );

            SPEC.newLine();

            ambientFx = SPEC.define( new BooleanField( "brutal_ambient_fx", false,
                    "If true, the potion effects below will not display potion effects particles.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            slowFalling = SPEC.define( new BooleanField( "floaty_slow_falling", true,
                    "If true, mobs spawned by " + FEATURE_TYPE_NAME + " will have the " +
                            "'slow falling' potion effect.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            jumpBoost = SPEC.define( new BooleanField( "floaty_jump_boost", true,
                    "If true, mobs spawned by " + FEATURE_TYPE_NAME + " will have the " +
                            "'jump boost' potion effect.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
    
    public static class NestSpawnerCategory extends SpawnerTypeCategory {
        NestSpawnerCategory( FeatureConfig parent, SpawnerType type,
                             double placements, int minHeight, int maxHeight, int activationRng, boolean checkSight,
                             int minDelay, int maxDelay, int delayPrgr, int spawnCnt, int spawnRng, double dynamicCh, double mimicCh ) {
            super( parent, type, placements, minHeight, maxHeight, activationRng, checkSight,
                    minDelay, maxDelay, delayPrgr, spawnCnt, spawnRng, dynamicCh, mimicCh );
        }
        
        /** @return The default spawn list to use for this spawner type and dimension. */
        @Override
        protected WeightedEntityList makeDefaultSpawnList( FeatureConfig feature ) {
            return new WeightedEntityList( new EntityEntry( EntityType.SILVERFISH, 100 ) );
        }
    }
    
    public static class MiniSpawnerCategory extends SpawnerTypeCategory {
        MiniSpawnerCategory( FeatureConfig parent, SpawnerType type,
                             double placements, int minHeight, int maxHeight, int activationRng, boolean checkSight,
                             int minDelay, int maxDelay, int delayPrgr, int spawnCnt, int spawnRng, double dynamicCh, double mimicCh ) {
            super( parent, type, placements, minHeight, maxHeight, activationRng, checkSight,
                    minDelay, maxDelay, delayPrgr, spawnCnt, spawnRng, dynamicCh, mimicCh );
        }
        
        /** @return The default spawn list to use for this spawner type and dimension. */
        @Override
        protected WeightedEntityList makeDefaultSpawnList( FeatureConfig feature ) {
            if( isNetherDimension() ) {
                return new WeightedEntityList(
                        new EntityEntry( null, DWEntities.MINI_ZOMBIE.getId(), true, 200 ),
                        new EntityEntry( null, DWEntities.MINI_SKELETON.getId(), true, 100 ),
                        new EntityEntry( null, DWEntities.MINI_SPIDER.getId(), true, 100 ),
                        new EntityEntry( null, DWEntities.MINI_CREEPER.getId(), true, 50 ),
                        new EntityEntry( null, DWEntities.MICRO_GHAST.getId(), true, 40 )
                );
            }
            else {
                return new WeightedEntityList(
                        new EntityEntry( null, DWEntities.MINI_ZOMBIE.getId(), true, 200 ),
                        new EntityEntry( null, DWEntities.MINI_SKELETON.getId(), true, 100 ),
                        new EntityEntry( null, DWEntities.MINI_SPIDER.getId(), true, 100 ),
                        new EntityEntry( null, DWEntities.MINI_CREEPER.getId(), true, 50 )
                );
            }
        }
    }
}