package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.field.collection.EntitySetField;
import fathertoast.crust.api.config.common.field.collection.RegistryWeightedListField;
import fathertoast.crust.api.config.common.value.collection.RegistryWeightedList;
import fathertoast.deadlyworld.common.block.floor_trap.FloorTrapType;
import fathertoast.deadlyworld.common.config.value.MobEffectWeightedList;
import fathertoast.deadlyworld.common.config.value.MobEffectWeightedListField;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import static fathertoast.deadlyworld.common.util.References.*;

public class FloorTrapConfig extends FeatureConfig {
    
    public final TntTrapTypeCategory TNT;
    public final TntMobTrapTypeCategory TNT_MOB;
    public final PotionTrapTypeCategory POTION;
    public final LavaTrapTypeCategory LAVA;
    public final FireTrapTypeCategory FIRE;
    
    /** Builds the config spec that should be used for this config. */
    FloorTrapConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "floor trap" );
        
        SPEC.newLine();
        EntitySetField.describe( SPEC );
        
        final boolean isNether = isNetherDimension();
        
        TNT = new TntTrapTypeCategory( this, FloorTrapType.TNT, isNether ? 0.15 : 0.3, DEPTH_LAVA, DEPTH_0, 0.05,
                4.0, true, 1, 20, 60, 40, 50, 4, 2.0 );
        
        TNT_MOB = new TntMobTrapTypeCategory( this, FloorTrapType.TNT_MOB, 0.05, DEPTH_LAVA, DEPTH_2, 0.05,
                4.0, true, 1, 20, 60, 80, 180, 3, 0.6 );
        
        POTION = new PotionTrapTypeCategory( this, FloorTrapType.POTION, isNether ? 0.15 : 0.2, DEPTH_LAVA, DEPTH_0, 0.05,
                5.0, true, -1, 20, 60, 0.2 );
        
        LAVA = new LavaTrapTypeCategory( this, FloorTrapType.LAVA, isNether ? 0.07 : 0.1, DEPTH_LAVA, DEPTH_3, 0.05,
                5.0, true, 1, 20, 60 );
        
        FIRE = new FireTrapTypeCategory( this, FloorTrapType.FIRE, isNether ? 0.07 : 0.1, DEPTH_VOID, DEPTH_1, 0.05,
                5.0, false, -1, 4, 7, 4.0 );
    }
    
    public static class TrapTypeCategory extends FeatureTypeCategory {
        
        public final DoubleField camoChance;
        public final DoubleField decoyChance;
        
        public final DoubleField activationRange;
        public final DoubleField checkSightChance;
        
        public final IntField triggersRemaining;
        public final IntField.RandomRange resetTime;
        
        TrapTypeCategory( FeatureConfig parent, FloorTrapType type,
                          double placements, int minHeight, int maxHeight, double decoyCh,
                          double activationRng, boolean checkSight, int triggers, int minResetTime, int maxResetTime ) {
            this( parent, type.toString(), placements, minHeight, maxHeight, decoyCh,
                    activationRng, checkSight, triggers, minResetTime, maxResetTime );
            
        }
        
        TrapTypeCategory( FeatureConfig parent, String name,
                          double placements, int minHeight, int maxHeight, double decoyCh,
                          double activationRng, boolean checkSight, int triggers, int minResetTime, int maxResetTime ) {
            super( parent, name, placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            camoChance = SPEC.define( new DoubleField( "camo_chance", isOverworldDimension() ? 0.33 : 0.66, DoubleField.Range.PERCENT,
                    "The chance for " + FEATURE_TYPE_NAME + " to generate with camouflage.",
                    "Camouflage disguises the trap as a random nearby block.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
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
        }
    }
    
    public static class TntTrapTypeCategory extends TrapTypeCategory {
        
        public final IntField.RandomRange fuseTime;
        
        public final IntField tntCount;
        public final DoubleField launchSpeed;
        
        TntTrapTypeCategory( FeatureConfig parent, FloorTrapType type, double placements, int minHeight, int maxHeight, double decoyCh,
                             double activationRng, boolean checkSight, int triggers, int minResetTime, int maxResetTime, int minFuseTime, int maxFuseTime,
                             int tntCnt, double launchSpd ) {
            super( parent, type, placements, minHeight, maxHeight, decoyCh, activationRng, checkSight, triggers, minResetTime, maxResetTime );
            
            SPEC.newLine();
            
            fuseTime = new IntField.RandomRange( SPEC, "fuse_time", minFuseTime, maxFuseTime, 0, Short.MAX_VALUE,
                    "The minimum and maximum (inclusive) fuse time set on TNT spawned by this trap, " +
                            "in ticks. (20 ticks = 1 second)" );
            
            SPEC.newLine();
            
            tntCount = SPEC.define( new IntField( "tnt_count", tntCnt, IntField.Range.POSITIVE,
                    "The amount of TNT spawned when this trap is activated.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            launchSpeed = SPEC.define( new DoubleField( "launch_speed", launchSpd, DoubleField.Range.NON_NEGATIVE,
                    "The velocity at which the spawned TNT gets launched when this trap activates.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
    
    public static class TntMobTrapTypeCategory extends TntTrapTypeCategory {
        
        public final RegistryWeightedListField<EntityType<?>> spawnList;
        
        public final DoubleField speedMultiplier;
        public final DoubleField healthMultiplier;
        
        TntMobTrapTypeCategory( FeatureConfig parent, FloorTrapType type, double placements, int minHeight, int maxHeight, double decoyCh,
                                double activationRng, boolean checkSight, int triggers, int minResetTime, int maxResetTime, int minFuseTime, int maxFuseTime,
                                int tntCnt, double launchSpd ) {
            super( parent, type, placements, minHeight, maxHeight, decoyCh, activationRng, checkSight,
                    triggers, minResetTime, maxResetTime, minFuseTime, maxFuseTime, tntCnt, launchSpd );
            
            SPEC.newLine();
            
            spawnList = SPEC.define( new RegistryWeightedListField<>( "spawn_list", makeDefaultSpawnList(),
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
                return new RegistryWeightedList.Builder<>( ForgeRegistries.ENTITY_TYPES )
                        .add( 200, EntityType.WITHER_SKELETON )
                        .add( 100, EntityType.HUSK )
                        .add( 10, EntityType.CAVE_SPIDER )
                        .add( 10, EntityType.CREEPER )
                        .build();
            }
            if( isEndDimension() ) {
                return new RegistryWeightedList.Builder<>( ForgeRegistries.ENTITY_TYPES )
                        .add( 200, EntityType.ENDERMAN )
                        .add( 10, EntityType.CREEPER )
                        .build();
            }
            // For the overworld, as well as any dimensions added by mods
            return new RegistryWeightedList.Builder<>( ForgeRegistries.ENTITY_TYPES )
                    // Vanilla dungeon mobs
                    .add( 200, EntityType.ZOMBIE )
                    .add( 100, EntityType.SKELETON )
                    .add( 100, EntityType.SPIDER )
                    // Extras
                    .add( 10, EntityType.CAVE_SPIDER )
                    .add( 10, EntityType.CREEPER )
                    .build();
        }
    }
    
    public static class PotionTrapTypeCategory extends TrapTypeCategory {
        
        public final DoubleField dynamicChance;
        public final MobEffectWeightedListField potionList;
        
        PotionTrapTypeCategory( FeatureConfig parent, FloorTrapType type, double placements, int minHeight, int maxHeight, double decoyCh,
                                double activationRng, boolean checkSight, int triggers, int minResetTime, int maxResetTime, double dynamicCh ) {
            super( parent, type, placements, minHeight, maxHeight, decoyCh, activationRng, checkSight, triggers, minResetTime, maxResetTime );
            
            SPEC.newLine();
            
            dynamicChance = SPEC.define( new DoubleField( "dynamic_chance", dynamicCh, DoubleField.Range.PERCENT,
                    "The chance for " + FEATURE_TYPE_NAME + " to generate as 'dynamicChance'.",
                    "Dynamic potion traps pick a new potion every time they trigger.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            potionList = SPEC.define( new MobEffectWeightedListField( "potion_list", makeDefaultPotionList(),
                    "Weighted list of potion effects that can be used by " + FEATURE_TYPE_NAME + "s when hurling splash potions. One of these is chosen",
                    "at random when the trap is generated. If the trap is generated as 'dynamic_chance' it will pick again",
                    "between each potion effect.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
        
        /** @return The default potion list to use for this trap type and dimension. */
        protected MobEffectWeightedList makeDefaultPotionList() {
            if( isNetherDimension() ) {
                return new MobEffectWeightedList.Builder<>()
                        .put( 5, MobEffects.WITHER, 100, 0 )
                        .put( 30, MobEffects.MOVEMENT_SLOWDOWN, 200, 2 )
                        .put( 20, MobEffects.POISON, 100, 1 )
                        .put( 10, MobEffects.BLINDNESS, 200, 0 )
                        .build();
            }
            if( isEndDimension() ) {
                return new MobEffectWeightedList.Builder<>()
                        .put( 40, MobEffects.LEVITATION, 240, 0 )
                        .put( 40, MobEffects.CONFUSION, 200, 0 )
                        .put( 20, MobEffects.WEAKNESS, 280, 2 )
                        .build();
            }
            // For the overworld, as well as any dimensions added by mods
            return new MobEffectWeightedList.Builder<>()
                    .put( 20, MobEffects.POISON, 200, 0 )
                    .put( 20, MobEffects.MOVEMENT_SLOWDOWN, 200, 1 )
                    .put( 20, MobEffects.WEAKNESS, 150, 1 )
                    .put( 20, MobEffects.HARM, 1, 1 )
                    .put( 20, MobEffects.HUNGER, 500, 1 )
                    .build();
        }
    }
    
    public static class LavaTrapTypeCategory extends TrapTypeCategory {
        
        public final DoubleField runnyChance;
        
        LavaTrapTypeCategory( FeatureConfig parent, FloorTrapType type, double placements, int minHeight, int maxHeight, double decoyCh,
                              double activationRng, boolean checkSight, int triggers, int minResetTime, int maxResetTime ) {
            super( parent, type, placements, minHeight, maxHeight, decoyCh, activationRng, checkSight, triggers, minResetTime, maxResetTime );
            
            SPEC.newLine();
            
            runnyChance = SPEC.define( new DoubleField( "runny_chance", isNetherDimension() ? 0.0 : 0.05, DoubleField.Range.PERCENT,
                    "The chance for " + FEATURE_TYPE_NAME + " to place runny lava instead of vanilla lava.",
                    "Runny lava flows faster than water, even outside the Nether.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
    
    public static class FireTrapTypeCategory extends TrapTypeCategory {
        
        public final DoubleField throwPower;
        
        FireTrapTypeCategory( FeatureConfig parent, FloorTrapType type, double placements, int minHeight, int maxHeight, double decoyCh,
                              double activationRng, boolean checkSight, int triggers, int minResetTime, int maxResetTime, double thrPower ) {
            super( parent, type, placements, minHeight, maxHeight, decoyCh, activationRng, checkSight,
                    triggers, minResetTime, maxResetTime );
            
            SPEC.newLine();
            
            throwPower = SPEC.define( new DoubleField( "throw_power", thrPower, 1.0, 20.0,
                    "Determines the speed at which fire blocks are launched from this trap.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
}