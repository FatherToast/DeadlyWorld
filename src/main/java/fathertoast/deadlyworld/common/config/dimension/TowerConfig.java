package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.crust.api.config.common.value.RegistryValueEntry;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.config.field.WeightedPotionList;
import fathertoast.deadlyworld.common.config.field.WeightedPotionListField;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.registries.ForgeRegistries;

import static fathertoast.deadlyworld.common.util.References.DEPTH_0;
import static fathertoast.deadlyworld.common.util.References.DEPTH_LAVA;

public class TowerConfig extends FeatureConfig {
    
    public final TowerTypeCategory SIMPLE;
    public final TowerTypeCategory FIRE;
    public final PotionTowerTypeCategory POTION;
    public final TowerTypeCategory GATLING;
    public final TowerTypeCategory FIREBALL;
    
    
    /** Builds the config spec that should be used for this config. */
    TowerConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "tower" );
        
        SPEC.newLine();
        // SPEC.describePotionList();
        
        SIMPLE = new TowerTypeCategory( this, TowerType.SIMPLE, 0.6, DEPTH_LAVA, DEPTH_0, 0.3,
                9.0, true, 20, 40, 3.0, 1.0, 0.08 );
        
        FIRE = new TowerTypeCategory( this, TowerType.FIRE, 0.2, DEPTH_LAVA, DEPTH_0, 0.3,
                9.0, true, 20, 40, 1.0, 1.2, 0.08 );
        
        POTION = new PotionTowerTypeCategory( this, TowerType.POTION, 0.3, DEPTH_LAVA, DEPTH_0, 0.3,
                9.0, true, 20, 40, 0.5, 1.0, 0.2, 0.2 );
        
        GATLING = new TowerTypeCategory( this, TowerType.GATLING, 0.6, DEPTH_LAVA, DEPTH_0, 0.3,
                11.0, true, 6, 7, 1.0, 1.0, 0.08 );
        
        FIREBALL = new TowerTypeCategory( this, TowerType.FIREBALL, 0.6, DEPTH_LAVA, DEPTH_0, 0.3,
                15.0, true, 20, 40, 2.0, 1.3, 0.4 );
    }
    
    public static class TowerTypeCategory extends FeatureTypeCategory {
        
        //public final DoubleField chestChance;
        
        public final DoubleField activationRange;
        public final DoubleField checkSightChance;
        
        public final IntField.RandomRange attackDelay;
        
        public final DoubleField attackDamage;
        
        public final DoubleField projectileSpeed;
        public final DoubleField projectileVariance;
        
        
        TowerTypeCategory( FeatureConfig parent, TowerType type,
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
            
            attackDelay = new IntField.RandomRange( SPEC, "attack_delay", minAttackDelay, maxAttackDelay, 0, Short.MAX_VALUE,
                    "The minimum and maximum (inclusive) delay between attacks, in ticks. (20 ticks = 1 second)" );
            
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
    
    public static class PotionTowerTypeCategory extends TowerTypeCategory {
        
        public final WeightedPotionListField potionList;
        public final DoubleField dynamicChance;
        
        PotionTowerTypeCategory( FeatureConfig parent, TowerType type, double
                placements, int minHeight, int maxHeight, double chestCh, double activationRange, boolean checkSight, int minAttackDelay,
                                 int maxAttackDelay, double damage, double projectileSpeed, double projectileVariance, double dynamicCh ) {
            super( parent, type, placements, minHeight, maxHeight, chestCh, activationRange, checkSight, minAttackDelay, maxAttackDelay,
                    damage, projectileSpeed, projectileVariance );
            
            SPEC.newLine();
            
            potionList = SPEC.define( new WeightedPotionListField( "potion_list", makeDefaultPotionList(),
                    "Weighted list of potion effects that can be used by " + FEATURE_TYPE_NAME + "s when shooting tipped arrows. One of these is chosen",
                    "at random when the tower dispenser is generated. If the tower dispenser is generated as 'dynamic_chance' it will pick again",
                    "between each potion effect.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            
            dynamicChance = SPEC.define( new DoubleField( "dynamic_chance", dynamicCh, DoubleField.Range.PERCENT,
                    "The chance for " + FEATURE_TYPE_NAME + " to generate as 'dynamicChance'.",
                    "Dynamic potion towers pick a new potion every time they shoot a tipped arrow.",
                    DimensionConfigHelper.MESSAGE_CONFIGURED_FEATURE_OVERRIDE ) );
        }
        
        /** @return The default potion list to use for the potion tower dispenser config. */
        @SuppressWarnings( "ConstantConditions" )
        protected WeightedPotionList makeDefaultPotionList() {
            if( isNetherDimension() ) {
                return new WeightedPotionList(
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.WITHER ), 5, 100, 0 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.MOVEMENT_SLOWDOWN ), 30, 200, 2 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.POISON ), 20, 100, 1 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.BLINDNESS ), 10, 200, 0 )
                );
            }
            if( isEndDimension() ) {
                return new WeightedPotionList(
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.LEVITATION ), 40, 240, 0 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.CONFUSION ), 40, 200, 0 ),
                        new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.WEAKNESS ), 20, 280, 2 )
                );
            }
            // For the overworld, as well as any dimensions added by mods
            return new WeightedPotionList(
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.POISON ), 30, 280, 0 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.MOVEMENT_SLOWDOWN ), 20, 300, 1 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.WEAKNESS ), 20, 250, 1 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.HARM ), 20, 1, 1 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.HUNGER ), 20, 500, 1 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.BLINDNESS ), 20, 250 ),
                    new RegistryValueEntry<>( ForgeRegistries.MOB_EFFECTS.getKey( MobEffects.UNLUCK ), 5, 9000 )
            );
        }
    }
}