package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;

import static fathertoast.deadlyworld.common.util.References.*;

import javax.annotation.Nullable;

public class TowerDispenserConfig extends FeatureConfig {
    
    public final TowerDispenserConfig.TowerDispenserTypeCategory SIMPLE;
    public final TowerDispenserConfig.PotionTowerDispenserTypeCategory POTION;
    
    /** Builds the config spec that should be used for this config. */
    TowerDispenserConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "tower dispenser" );
        
        SPEC.newLine();
        SPEC.describeEntityList();
        
        //SPEC.newLine();
        //SPEC.describePotionList();
        
        SIMPLE = new TowerDispenserTypeCategory( this, TowerType.SIMPLE, 0.8, DEPTH_LAVA, DEPTH_0, 0.3,
                9.0, true, 20, 40, 2.0, 1.0, 0.08 );
        
        POTION = new PotionTowerDispenserTypeCategory( this, TowerType.POTION, 0.4, DEPTH_LAVA, DEPTH_0, 0.3,
                9.0, true, 20, 40, 0.0, 1.0, 0.2 );
    }
    
    public static class TowerDispenserTypeCategory extends FeatureTypeCategory {
        
        //public final DoubleField chestChance;
        
        public final DoubleField activationRange;
        public final DoubleField checkSightChance;
        
        public final IntField.RandomRange attackDelay;
        public final IntField attackDelayMin, attackDelayMax; // TODO delete after Crust update
        @Nullable
        public final DoubleField attackDamage;
        
        public final DoubleField projectileSpeed;
        public final DoubleField projectileVariance;
        
        
        TowerDispenserTypeCategory( FeatureConfig parent, TowerType type,
                                    double placements, int minHeight, int maxHeight, double ignoredChestCh,
                                    double activationRng, boolean checkSight, int minAttackDelay,
                                    int maxAttackDelay, double damage, double projSpeed, double projVariance ) {
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
            
            attackDelay = new IntField.RandomRange(
                    attackDelayMin = SPEC.define( new IntField( "attack_delay.min", minAttackDelay, 0, Short.MAX_VALUE,
                            "The minimum and maximum (inclusive) delay between attacks, in ticks. (20 ticks = 1 second)",
                            DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) ),
                    attackDelayMax = SPEC.define( new IntField( "attack_delay.max", maxAttackDelay, 0, Short.MAX_VALUE ) )
            );
            attackDamage = damage > 0.0 ? SPEC.define( new DoubleField( "attack_damage", damage, DoubleField.Range.NON_NEGATIVE,
                    "The base damage of attacks from " + FEATURE_TYPE_NAME + ".",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) ) : null;
            
            SPEC.newLine();
            
            projectileSpeed = SPEC.define( new DoubleField( "projectile_speed", projSpeed, DoubleField.Range.NON_NEGATIVE,
                    "The base speed of projectiles fired by " + FEATURE_TYPE_NAME + ".",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
            
            projectileVariance = SPEC.define( new DoubleField( "projectile_variance", projVariance, DoubleField.Range.NON_NEGATIVE,
                    "The inaccuracy of projectiles fired by " + FEATURE_TYPE_NAME + ".",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
        }
    }
    
    public static class PotionTowerDispenserTypeCategory extends TowerDispenserTypeCategory {
        
        //        public final WeightedPotionListField potionList;
        
        PotionTowerDispenserTypeCategory( FeatureConfig parent, TowerType type, double
                placements, int minHeight, int maxHeight, double chestCh, double activationRange, boolean checkSight, int minAttackDelay,
                                          int maxAttackDelay, double attackDamage, double projectileSpeed, double projectileVariance ) {
            super( parent, type, placements, minHeight, maxHeight, chestCh, activationRange, checkSight, minAttackDelay, maxAttackDelay, attackDamage, projectileSpeed, projectileVariance );
            
            SPEC.newLine();
            
            //TODO
            //            potionList = SPEC.define( new WeightedPotionListField( "potion_list", makeDefaultPotionList( feature ),
            //                    "A weighted list" ) );
        }
        
        //        /** @return The default spawn list to use for this spawner type and dimension. */
        //        protected WeightedPotionList makeDefaultPotionList( FeatureConfig feature ) {
        //            if( isNetherDimension( feature ) ) {
        //                return new WeightedPotionList(
        //                        new PotionEntry( MobEffects.WITHER, 5, 100, 0 ),
        //                        new PotionEntry( MobEffects.MOVEMENT_SLOWDOWN, 30, 200, 2 ),
        //                        new PotionEntry( MobEffects.POISON, 20, 100, 1 ),
        //                        new PotionEntry( MobEffects.BLINDNESS, 10, 200, 0 )
        //                );
        //            }
        //            if( isEndDimension( feature ) ) {
        //                return new WeightedPotionList(
        //                        new PotionEntry( MobEffects.LEVITATION, 40, 240, 0 ),
        //                        new PotionEntry( MobEffects.CONFUSION, 40, 200, 0 ),
        //                        new PotionEntry( MobEffects.WEAKNESS, 20, 280, 2 )
        //                );
        //            }
        //            // For the overworld, as well as any dimensions added by mods
        //            return new WeightedPotionList(
        //                    new PotionEntry( MobEffects.POISON, 30, 280, 0 ),
        //                    new PotionEntry( MobEffects.MOVEMENT_SLOWDOWN, 20, 300, 1 ),
        //                    new PotionEntry( MobEffects.WEAKNESS, 20, 250, 1 ),
        //                    new PotionEntry( MobEffects.HARM, 20, 1, 1 ),
        //                    new PotionEntry( MobEffects.HUNGER, 20, 500, 1 ),
        //                    new PotionEntry( MobEffects.BLINDNESS, 20, 250 ),
        //                    new PotionEntry( MobEffects.UNLUCK, 5, 9000 )
        //            );
        //        }
    }
}