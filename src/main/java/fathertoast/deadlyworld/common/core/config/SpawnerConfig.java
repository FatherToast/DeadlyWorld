package fathertoast.deadlyworld.common.core.config;

import fathertoast.deadlyworld.common.block.properties.SpawnerType;
import fathertoast.deadlyworld.common.core.config.field.BooleanField;
import fathertoast.deadlyworld.common.core.config.field.DoubleField;
import fathertoast.deadlyworld.common.core.config.field.IntField;
import fathertoast.deadlyworld.common.core.config.field.WeightedEntityListField;
import fathertoast.deadlyworld.common.core.config.file.ToastConfigSpec;
import fathertoast.deadlyworld.common.core.config.file.TomlHelper;
import fathertoast.deadlyworld.common.core.config.util.EntityEntry;
import fathertoast.deadlyworld.common.core.config.util.WeightedEntityList;
import fathertoast.deadlyworld.common.util.References;
import net.minecraft.entity.EntityType;

import java.io.File;

public class SpawnerConfig extends FeatureConfig {
    
    public final SpawnerTypeCategory LONE;
    public final SpawnerTypeCategory STREAM;
    public final SpawnerTypeCategory SWARM;
    public final SpawnerTypeCategory NEST;
    public final BrutalSpawnerCategory BRUTAL;
    
    /** Builds the config spec that should be used for this config. */
    SpawnerConfig( File dir, DimensionConfigGroup dimConfigs ) {
        super( dir, dimConfigs, "spawner" );
        
        SPEC.newLine();
        SPEC.describeEntityList();
        
        SPEC.newLine();
        SPEC.comment(
                "Progressive Spawn Delay:",
                "  By default, spawners added by this mod use a mechanic called 'progressive spawn delay'. Unlike vanilla",
                "    spawners that have a completely random delay chosen anywhere from 10 to 40 seconds (what awful variance!),",
                "    Deadly World spawners will start from a 10 second delay and slowly increase up to 40 seconds delay as you",
                "    continue to stand close to them (with the same vanilla delay limits of 200-800 ticks).",
                "  A spawner's 'delay buildup' starts at its minimum delay and increases by its delay progression (" +
                        References.PLUS_OR_MINUS + "10%)",
                "    with each successful spawn, up to its maximum delay.",
                "  When no players are within the spawner's activation range, its 'delay buildup' is continuously decreased by",
                "    its delay recovery, back down to its minimum delay.",
                "  The delay, delay progression, and delay recovery for each spawner type are determined by these configs when",
                "    generated and can then be overwritten for individual spawners by nbt editing."
        );
        
        LONE = new SpawnerTypeCategory( SPEC, this, SpawnerType.LONE, 0.16, 12, 52, 0.3,
                16.0, false, 200, 800, 40, 4, 4.0, 0.1 );
        
        STREAM = new SpawnerTypeCategory( SPEC, this, SpawnerType.STREAM, 0.04, 12, 42, 1.0,
                16.0, true, 0, 400, 10, 1, 2.0, 0.95 );
        
        SWARM = new SpawnerTypeCategory( SPEC, this, SpawnerType.SWARM, 0.04, 12, 32, 1.0,
                20.0, true, 400, 2400, 100, 12, 8.0, 0.05 );
        
        BRUTAL = new BrutalSpawnerCategory( SPEC, this, SpawnerType.BRUTAL, 0.04, 12, 32, 1.0,
                16.0, true, 200, 800, 100, 2, 3.0, 0.05 );
        
        NEST = new NestSpawnerCategory( SPEC, this, SpawnerType.NEST, 0.16, 12, 62, 0.3,
                16.0, false, 100, 400, 20, 6, 6.0, 0.0 );
    }
    
    public static class SpawnerTypeCategory extends FeatureTypeCategory {
        
        public final DoubleField chestChance;
        
        public final DoubleField activationRange;
        public final BooleanField checkSight;
        
        public final IntField.RandomRange delay;
        public final IntField delayProgression;
        public final DoubleField delayRecovery;
        
        public final IntField spawnCount;
        public final DoubleField spawnRange;
        
        public final DoubleField dynamicChance;
        public final WeightedEntityListField spawnList;
        
        public final DoubleField addedFollowRange;
        public final DoubleField addedMaxHealth;
        public final DoubleField increasedMaxHealth;
        public final DoubleField addedKnockbackResist;
        public final DoubleField addedArmor;
        public final DoubleField addedArmorToughness;
        public final DoubleField addedDamage;
        public final DoubleField increasedDamage;
        public final DoubleField addedKnockback;
        public final DoubleField increasedSpeed;
        
        SpawnerTypeCategory( ToastConfigSpec parent, FeatureConfig feature, SpawnerType type,
                             double placements, int minHeight, int maxHeight, double chestCh, double activationRng, boolean sightCheck,
                             int minDelay, int maxDelay, int delayPrgr, int spawnCnt, double spawnRng, double dynamicCh ) {
            super( parent, feature, type.NAME, placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            chestChance = SPEC.define( new DoubleField( "chest_chance", chestCh, DoubleField.Range.PERCENT,
                    "The chance for a chest to generate beneath " + FEATURE_TYPE_NAME + "s.",
                    //TODO
                    "For reference, the loot table for these chests is '" + "<TBD>"/*DeadlyWorld.toString( type.LOOT_TABLE )*/ + "'." ) );
            
            SPEC.newLine();
            
            activationRange = SPEC.define( new DoubleField( "activation_range", activationRng, DoubleField.Range.POSITIVE,
                    "The spawner is active as long as a player is within this distance (spherical distance)." ) );
            checkSight = SPEC.define( new BooleanField( "activation_sight_check", sightCheck,
                    "When the sight check is enabled, " + FEATURE_TYPE_NAME + "s will only spawn when they have direct",
                    "line-of-sight to a player within activation range. The spawner's delay will continue to tick down,",
                    "but it will wait to actually spawn until it has line-of-sight." ) );
            
            SPEC.newLine();
            
            delay = new IntField.RandomRange(
                    SPEC.define( new IntField( "delay.min", minDelay, IntField.Range.NON_NEGATIVE,
                            "The minimum and maximum (inclusive) delay between spawns, in ticks. (20 ticks = 1 second)" ) ),
                    SPEC.define( new IntField( "delay.max", maxDelay, IntField.Range.NON_NEGATIVE ) )
            );
            delayProgression = SPEC.define( new IntField( "delay.progression", delayPrgr, IntField.Range.NON_NEGATIVE,
                    "Each spawn increases the spawner's delay buildup by this many ticks (" + References.PLUS_OR_MINUS +
                            "10%). Set this to 0 to",
                    "revert to the vanilla spawner behavior (simple random between min and max).",
                    "See above for a more in-depth description of progressive spawn delay." ) );
            delayRecovery = SPEC.define( new DoubleField( "delay.recovery_rate", delayPrgr * 0.0025, DoubleField.Range.POSITIVE,
                    "The rate at which the spawn delay buildup on spawners recovers while no players are within range.",
                    "Inactive spawners' delay are reduced by this value each tick (20 times per second)." ) );
            
            SPEC.newLine();
            
            spawnCount = SPEC.define( new IntField( "spawn_count", spawnCnt, IntField.Range.NON_NEGATIVE,
                    "The number of mobs to attempt creating with each spawn. May spawn fewer depending on nearby obstructions." ) );
            spawnRange = SPEC.define( new DoubleField( "spawn_range", spawnRng, DoubleField.Range.POSITIVE,
                    "The maximum horizontal range to spawn mobs in." ) );
            
            SPEC.newLine();
            
            dynamicChance = SPEC.define( new DoubleField( "dynamic_chance", dynamicCh, DoubleField.Range.PERCENT,
                    "The chance for a " + FEATURE_TYPE_NAME + " to generate as 'dynamic'.",
                    "Dynamic spawners pick a new mob to spawn after each spawn." ) );
            spawnList = SPEC.define( new WeightedEntityListField( "spawn_list", makeDefaultSpawnList( feature ),
                    "Weighted list of mobs that can be spawned by " + FEATURE_TYPE_NAME + "s. One of these is chosen",
                    "at random when the spawner is generated. Spawners that are generated as 'dynamic' will pick again",
                    "between each spawn." ) );
            
            SPEC.newLine();
            
            final boolean brutal = type == SpawnerType.BRUTAL;
            SPEC.comment( "Attribute modifiers applied to entities spawned by " + FEATURE_TYPE_NAME + "s, if applicable.",
                    "Modifiers are disabled if their value is set to 0.",
                    "Added modifiers use the 'addition' operation and increased modifiers use the 'multiply base' operation.",
                    TomlHelper.multiFieldInfo( DoubleField.Range.ANY ) );
            addedFollowRange = SPEC.define( new DoubleField( "modifier.added_follow_range", 0.0, (String[]) null ) );
            addedMaxHealth = SPEC.define( new DoubleField( "modifier.added_max_health", brutal ? 5.0 : 0.0, (String[]) null ) );
            increasedMaxHealth = SPEC.define( new DoubleField( "modifier.increased_max_health", brutal ? 0.2 : 0.0, (String[]) null ) );
            addedKnockbackResist = SPEC.define( new DoubleField( "modifier.added_knockback_resistance", brutal ? 0.5 : 0.0, (String[]) null ) );
            addedArmor = SPEC.define( new DoubleField( "modifier.added_armor", brutal ? 12.0 : 0.0, (String[]) null ) );
            addedArmorToughness = SPEC.define( new DoubleField( "modifier.added_armor_toughness", brutal ? 8.0 : 0.0, (String[]) null ) );
            addedDamage = SPEC.define( new DoubleField( "modifier.added_damage", brutal ? 1.0 : 0.0, (String[]) null ) );
            increasedDamage = SPEC.define( new DoubleField( "modifier.increased_damage", brutal ? 0.2 : 0.0, (String[]) null ) );
            addedKnockback = SPEC.define( new DoubleField( "modifier.added_knockback", brutal ? 2.0 : 0.0, (String[]) null ) );
            increasedSpeed = SPEC.define( new DoubleField( "modifier.increased_speed", brutal ? 0.1 : 0.0, (String[]) null ) );
        }
        
        /** @return The default spawn list to use for this spawner type and dimension. */
        protected WeightedEntityList makeDefaultSpawnList( FeatureConfig feature ) {
            if( isNetherDimension( feature ) ) {
                return new WeightedEntityList(
                        new EntityEntry( EntityType.WITHER_SKELETON, 200 ),
                        new EntityEntry( EntityType.HUSK, 100 ),
                        new EntityEntry( EntityType.BLAZE, 100 ),
                        new EntityEntry( EntityType.CAVE_SPIDER, 10 ),
                        new EntityEntry( EntityType.CREEPER, 10 ),
                        new EntityEntry( EntityType.MAGMA_CUBE, 10 )
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
                    new EntityEntry( EntityType.CREEPER, 10 ),
                    new EntityEntry( EntityType.SILVERFISH, 10 )
            );
        }
    }
    
    public static class BrutalSpawnerCategory extends SpawnerTypeCategory {
        
        public final BooleanField ambientFx;
        public final BooleanField fireResistance;
        public final BooleanField waterBreathing;
        
        BrutalSpawnerCategory( ToastConfigSpec parent, FeatureConfig feature, SpawnerType type,
                               double placements, int minHeight, int maxHeight, double chestCh, double activationRng, boolean sightCheck,
                               int minDelay, int maxDelay, int delayPrgr, int spawnCnt, double spawnRng, double dynamicCh ) {
            super( parent, feature, type, placements, minHeight, maxHeight, chestCh, activationRng, sightCheck,
                    minDelay, maxDelay, delayPrgr, spawnCnt, spawnRng, dynamicCh );
            
            SPEC.newLine();
            
            ambientFx = SPEC.define( new BooleanField( "brutal_ambient_fx", false,
                    "If true, the potion effects below will not display potion effects particles." ) );
            fireResistance = SPEC.define( new BooleanField( "brutal_fire_resistance", true,
                    "If true, non-creeper mobs spawned by " + FEATURE_TYPE_NAME + "s will have the",
                    "'fire resistance' potion effect." ) );
            waterBreathing = SPEC.define( new BooleanField( "brutal_water_breathing", true,
                    "If true, non-creeper mobs spawned by " + FEATURE_TYPE_NAME + "s will have the",
                    "'water breathing' potion effect." ) );
        }
    }
    
    public static class NestSpawnerCategory extends SpawnerTypeCategory {
        NestSpawnerCategory( ToastConfigSpec parent, FeatureConfig feature, SpawnerType type,
                             double placements, int minHeight, int maxHeight, double chestCh, double activationRng, boolean sightCheck,
                             int minDelay, int maxDelay, int delayPrgr, int spawnCnt, double spawnRng, double dynamicCh ) {
            super( parent, feature, type, placements, minHeight, maxHeight, chestCh, activationRng, sightCheck,
                    minDelay, maxDelay, delayPrgr, spawnCnt, spawnRng, dynamicCh );
        }
        
        /** @return The default spawn list to use for this spawner type and dimension. */
        @Override
        protected WeightedEntityList makeDefaultSpawnList( FeatureConfig feature ) {
            return new WeightedEntityList( new EntityEntry( EntityType.SILVERFISH, 100 ) );
        }
    }
}