package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.EntityEntry;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.common.block.trap.TrapType;
import fathertoast.deadlyworld.common.config.field.WeightedEntityList;
import fathertoast.deadlyworld.common.config.field.WeightedEntityListField;
import fathertoast.deadlyworld.common.config.field.WeightedPotionList;
import fathertoast.deadlyworld.common.config.field.WeightedPotionListField;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import static fathertoast.deadlyworld.common.util.References.*;

public class TrapConfig extends FeatureConfig {
    
    public final TrapConfig.TntTrapTypeCategory TNT;
    public final TrapConfig.TntMobTrapTypeCategory TNT_MOB;
    public final TrapConfig.PotionTrapTypeCategory POTION;
    public final TrapTypeCategory LAVA;
    
    /** Builds the config spec that should be used for this config. */
    TrapConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "trap" );
        
        SPEC.newLine();
        SPEC.describeEntityList();
        
        //SPEC.newLine();
        //SPEC.describePotionList();
        
        TNT = new TntTrapTypeCategory( this, TrapType.TNT, 0.4, DEPTH_LAVA, DEPTH_0, 0.3,
                6.0, true, 20, 60, 80, 180, 3, 2.0 );
        
        TNT_MOB = new TntMobTrapTypeCategory( this, TrapType.TNT_MOB, 0.08, DEPTH_LAVA, DEPTH_2, 0.3,
                5.0, true, 20, 60, 80, 180, 3, 0.6 );
        
        POTION = new PotionTrapTypeCategory( this, TrapType.POTION, 0.4, DEPTH_LAVA, DEPTH_0, 0.3,
                5.0, true, 20, 60 );
        
        LAVA = new TrapTypeCategory( this, TrapType.LAVA, 0.16, DEPTH_LAVA, DEPTH_3, 0.3,
                4.0, true, 20, 60 );
    }
    
    public static class TrapTypeCategory extends FeatureTypeCategory {
        
        //public final DoubleField chestChance;
        
        public final DoubleField activationRange;
        public final DoubleField checkSightChance;
        
        public final IntField.RandomRange resetTime;
        public final IntField resetTimeMin, resetTimeMax; // TODO delete after Crust update
        
        TrapTypeCategory( FeatureConfig parent, TrapType type,
                          double placements, int minHeight, int maxHeight, double ignoredChestCh,
                          double activationRng, boolean checkSight, int minResetTime, int maxResetTime ) {
            super( parent, type.toString(), placements, minHeight, maxHeight );
            
            //if( isSubfeature() ) { TODO decide whether to re-add this
            //    chestChance = null;
            //}
            //else {
            //    SPEC.newLine();
            //
            //    chestChance = SPEC.define( new DoubleField( "chest_chance", chestCh, DoubleField.Range.PERCENT,
            //            "The chance for a chest to generate beneath " + FEATURE_TYPE_NAME + ".",
            //            "For reference, the loot table for these chests is '" + DeadlyWorld.toString( type.getChestLootTable() ) + "'." ) );
            //
            //    SPEC.newLine();
            //}
            
            activationRange = SPEC.define( standardActivationRangeField( activationRng ) );
            checkSightChance = SPEC.define( standardCheckSightField( checkSight ) );
            
            SPEC.newLine();
            
            resetTime = new IntField.RandomRange(
                    resetTimeMin = SPEC.define( new IntField( "reset_time.min", minResetTime, 0, Short.MAX_VALUE,
                            "The minimum and maximum (inclusive) amount of time that must pass before a previously " +
                                    "triggered trap resets, in ticks. (20 ticks = 1 second)",
                            DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) ),
                    resetTimeMax = SPEC.define( new IntField( "reset_time.max", maxResetTime, 0, Short.MAX_VALUE ) )
            );
        }
    }
    
    public static class TntTrapTypeCategory extends TrapTypeCategory {
        
        public final IntField.RandomRange fuseTime;
        public final IntField fuseTimeMin, fuseTimeMax; // TODO delete after Crust update
        
        public final IntField tntCount;
        public final DoubleField launchSpeed;
        
        TntTrapTypeCategory( FeatureConfig parent, TrapType type, double placements, int minHeight, int maxHeight, double chestCh,
                             double activationRng, boolean checkSight, int minResetTime, int maxResetTime, int minFuseTime, int maxFuseTime, int tntCnt, double launchSpd ) {
            super( parent, type, placements, minHeight, maxHeight, chestCh, activationRng, checkSight, minResetTime, maxResetTime );
            
            SPEC.newLine();
            
            fuseTime = new IntField.RandomRange(
                    fuseTimeMin = SPEC.define( new IntField( "fuse_time.min", minFuseTime, 0, Short.MAX_VALUE,
                            "The minimum and maximum (inclusive) fuse time set on TNT spawned by this trap, " +
                                    "in ticks. (20 ticks = 1 second)",
                            DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) ),
                    fuseTimeMax = SPEC.define( new IntField( "fuse_time.max", maxFuseTime, 0, Short.MAX_VALUE ) )
            );
            
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
        
        public final WeightedEntityListField spawnList;
        public final DoubleField speedMultiplier;
        public final DoubleField healthMultiplier;
        
        TntMobTrapTypeCategory( FeatureConfig parent, TrapType type, double placements, int minHeight, int maxHeight, double chestCh,
                                double activationRng, boolean checkSight, int minResetTime, int maxResetTime, int minFuseTime, int maxFuseTime, int tntCnt, double launchSpd ) {
            super( parent, type, placements, minHeight, maxHeight, chestCh, activationRng, checkSight,
                    minResetTime, maxResetTime, minFuseTime, maxFuseTime, tntCnt, launchSpd );
            
            spawnList = SPEC.define( new WeightedEntityListField( "spawn_list", makeDefaultSpawnList(),
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
        
        /** @return The default spawn list to use for this spawner type and dimension. */
        protected WeightedEntityList makeDefaultSpawnList() {
            if( isNetherDimension() ) {
                return new WeightedEntityList(
                        new EntityEntry( EntityType.WITHER_SKELETON, 200 ),
                        new EntityEntry( EntityType.HUSK, 100 ),
                        new EntityEntry( EntityType.CAVE_SPIDER, 10 ),
                        new EntityEntry( EntityType.CREEPER, 10 )
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
                    new EntityEntry( EntityType.CREEPER, 10 )
            );
        }
    }
    
    public static class PotionTrapTypeCategory extends TrapTypeCategory {
        
        public final WeightedPotionListField potionList;
        
        PotionTrapTypeCategory( FeatureConfig parent, TrapType type, double placements, int minHeight, int maxHeight, double chestCh,
                                double activationRng, boolean checkSight, int minResetTime, int maxResetTime ) {
            super( parent, type, placements, minHeight, maxHeight, chestCh, activationRng, checkSight, minResetTime, maxResetTime );
            
            potionList = SPEC.define( new WeightedPotionListField( "potion_list", makeDefaultPotionList(),
                    "Weighted list of potion effects that can be used by " + FEATURE_TYPE_NAME + "s when hurling splash potions. One of these is chosen",
                    "at random when the trap is generated. If the trap is generated as 'dynamicChance' it will pick again",
                    "between each potion effect." ) );
        }
        
        /** @return The default spawn list to use for this spawner type and dimension. */
        @SuppressWarnings( "ConstantConditions" )
        protected WeightedPotionList makeDefaultPotionList() {
            if( isNetherDimension( ) ) {
                return new WeightedPotionList(
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.WITHER ), 5, 100, 0 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.MOVEMENT_SLOWDOWN), 30, 200, 2 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.POISON), 20, 100, 1 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.BLINDNESS), 10, 200, 0 )
                );
            }
            if( isEndDimension( ) ) {
                return new WeightedPotionList(
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.LEVITATION ), 40, 240, 0 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.CONFUSION ), 40, 200, 0 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.WEAKNESS ), 20, 280, 2 )
                );
            }
            // For the overworld, as well as any dimensions added by mods
            return new WeightedPotionList(
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.POISON ), 20, 200, 0 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.MOVEMENT_SLOWDOWN ), 20, 200, 1 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.WEAKNESS ), 20, 150, 1 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.HARM ), 20, 1, 1 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.HUNGER ), 20, 500, 1 )
            );
        }
    }
}