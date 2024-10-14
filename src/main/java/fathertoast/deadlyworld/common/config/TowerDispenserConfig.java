package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.tile.tower.TowerType;

public class TowerDispenserConfig extends FeatureConfig {
    
    public final TowerDispenserConfig.TowerDispenserTypeCategory SIMPLE;
    public final TowerDispenserConfig.PotionTowerDispenserTypeCategory POTION;
    
    /** Builds the config spec that should be used for this config. */
    TowerDispenserConfig( ConfigManager manager, DimensionConfigGroup dimConfigs ) {
        super( manager, dimConfigs, "tower_dispenser" );
        
        SPEC.newLine();
        SPEC.describeEntityList();
        
        SPEC.newLine();
        //SPEC.describePotionList();
        
        SIMPLE = new TowerDispenserTypeCategory( this, TowerType.SIMPLE, 0.16, 12, 52, 0.3,
                9.0, true, 20, 40, 2.0, 1.0, 0.08 );
        
        POTION = new PotionTowerDispenserTypeCategory( this, TowerType.POTION, 0.16, 12, 52, 0.3,
                9.0, true, 20, 40, 1.0, 1.0, 0.2 );
    }
    
    public static class TowerDispenserTypeCategory extends FeatureTypeCategory {
        
        public final DoubleField chestChance;
        
        public final DoubleField activationRange;
        public final BooleanField checkSight;
        
        public final IntField minAttackDelay;
        public final IntField maxAttackDelay;
        public final DoubleField attackDamage;
        
        public final DoubleField projectileSpeed;
        public final DoubleField projectileVariance;
        
        
        TowerDispenserTypeCategory( FeatureConfig parent, TowerType type,
                                    double placements, int minHeight, int maxHeight, double chestCh,
                                    double activationRange, boolean checkSight, int minAttackDelay,
                                    int maxAttackDelay, double attackDamage, double projectileSpeed, double projectileVariance ) {
            super( parent, type.toString(), placements, minHeight, maxHeight );
            
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
            
            this.activationRange = SPEC.define( new DoubleField( "activation_range", activationRange, DoubleField.Range.NON_NEGATIVE,
                    "The " + FEATURE_TYPE_NAME + " is active as long as a player is within this distance (spherical distance)." ) );
            
            this.checkSight = SPEC.define( new BooleanField( "activation_sight_check", checkSight,
                    "When the sight check is enabled, " + FEATURE_TYPE_NAME + " will only activate when they have direct",
                    "line-of-sight to a player within activation range. The " + FEATURE_TYPE_NAME + "'s delay will continue to tick down,",
                    "but it will wait to actually activate until it has line-of-sight." ) );
            
            SPEC.newLine();
            
            this.minAttackDelay = SPEC.define( new IntField( "min_attack_delay", minAttackDelay, IntField.Range.POSITIVE,
                    "The minimum amount of ticks that must pass before this " + FEATURE_TYPE_NAME + " can",
                    "trigger again." ) );
            
            this.maxAttackDelay = SPEC.define( new IntField( "max_attack_delay", maxAttackDelay, IntField.Range.POSITIVE,
                    "The maximum amount of ticks that must pass before this " + FEATURE_TYPE_NAME + " can",
                    "trigger again." ) );
            
            this.attackDamage = SPEC.define( new DoubleField( "attack_damage", attackDamage, DoubleField.Range.NON_NEGATIVE,
                    "The base damage of the projectile fired." ) );
            
            SPEC.newLine();
            
            this.projectileSpeed = SPEC.define( new DoubleField( "projectile_speed", projectileSpeed, DoubleField.Range.NON_NEGATIVE,
                    "The speed of the projectile fired." ) );
            
            this.projectileVariance = SPEC.define( new DoubleField( "projectile_variance", projectileVariance, DoubleField.Range.NON_NEGATIVE,
                    "The inaccuracy of the projectiles this " + FEATURE_TYPE_NAME + " fires." ) );
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