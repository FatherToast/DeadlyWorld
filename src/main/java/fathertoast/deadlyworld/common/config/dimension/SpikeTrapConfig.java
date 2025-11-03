package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.block.spike_trap.SpikeTrapType;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;

import static fathertoast.deadlyworld.common.util.References.DEPTH_0;
import static fathertoast.deadlyworld.common.util.References.DEPTH_LAVA;

public class SpikeTrapConfig extends FeatureConfig {
    
    public final SpikeTrapConfig.SpikeTrapTypeCategory MUNDANE;
    public final SpikeTrapConfig.SpikeTrapTypeCategory POISON;
    public final SpikeTrapConfig.SpikeTrapTypeCategory FIERY;
    public final SpikeTrapConfig.SpikeTrapTypeCategory WITHERING;
    public final SpikeTrapConfig.SpikeTrapTypeCategory MECHANICAL_MUNDANE;
    public final SpikeTrapConfig.SpikeTrapTypeCategory MECHANICAL_POISON;
    public final SpikeTrapConfig.SpikeTrapTypeCategory MECHANICAL_FIERY;
    public final SpikeTrapConfig.SpikeTrapTypeCategory MECHANICAL_WITHERING;
    
    
    /** Builds the config spec that should be used for this config. */
    SpikeTrapConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "spike trap" );
        
        final boolean isNether = isNetherDimension();
        
        MUNDANE = new SpikeTrapConfig.SpikeTrapTypeCategory( this, SpikeTrapType.MUNDANE, isNether ? 0.0 : 0.1, DEPTH_LAVA, DEPTH_0,
                3.0F, 25, 5, 2 );
        
        POISON = new SpikeTrapConfig.PotionSpikeTrapTypeCategory( this, SpikeTrapType.POISON, isNether ? 0.0 : 0.05, DEPTH_LAVA, DEPTH_0,
                2.0F, 20, 5, 2, 200, 0 );
        
        FIERY = new SpikeTrapConfig.FierySpikeTrapTypeCategory( this, SpikeTrapType.FIERY, isNether ? 0.15 : 0.05, DEPTH_LAVA, DEPTH_0,
                2.0F, 20, 5, 2, 6 );
        
        WITHERING = new SpikeTrapConfig.PotionSpikeTrapTypeCategory( this, SpikeTrapType.WITHERING, isNether ? 0.05 : 0.0, DEPTH_LAVA, DEPTH_0,
                2.0F, 20, 5, 2, 140, 0 );
        
        MECHANICAL_MUNDANE = new SpikeTrapConfig.SpikeTrapTypeCategory( this, SpikeTrapType.MECHANICAL_MUNDANE, isNether ? 0.0 : 0.1, DEPTH_LAVA, DEPTH_0,
                4.0F, 25, 5, 2 );
        
        MECHANICAL_POISON = new SpikeTrapConfig.PotionSpikeTrapTypeCategory( this, SpikeTrapType.MECHANICAL_POISON, isNether ? 0.0 : 0.05, DEPTH_LAVA, DEPTH_0,
                3.0F, 20, 5, 2, 100, 1 );
        
        MECHANICAL_FIERY = new SpikeTrapConfig.FierySpikeTrapTypeCategory( this, SpikeTrapType.MECHANICAL_FIERY, isNether ? 0.15 : 0.05, DEPTH_LAVA, DEPTH_0,
                3.0F, 20, 5, 2, 4 );
        
        MECHANICAL_WITHERING = new SpikeTrapConfig.PotionSpikeTrapTypeCategory( this, SpikeTrapType.MECHANICAL_WITHERING, isNether ? 0.05 : 0.0, DEPTH_LAVA, DEPTH_0,
                3.0F, 20, 5, 2, 80, 1 );
    }
    
    public static class SpikeTrapTypeCategory extends FeatureTypeCategory {
        
        public final IntField placementTries;
        public final IntField xzSpread;
        public final IntField ySpread;
        
        public final DoubleField damage;
        
        SpikeTrapTypeCategory( FeatureConfig parent, SpikeTrapType type,
                               double placements, int minHeight, int maxHeight, double damage,
                               int placemntTries, int xzSpread, int ySpread ) {
            this( parent, type.toString(), placements, minHeight, maxHeight, damage, placemntTries, xzSpread, ySpread );
        }
        
        SpikeTrapTypeCategory( FeatureConfig parent, String name,
                               double placements, int minHeight, int maxHeight, double dmg,
                               int placemntTries, int xzSprd, int ySprd ) {
            super( parent, name, placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            placementTries = SPEC.define( new IntField( "placement_tries", placemntTries, IntField.Range.POSITIVE,
                    "The amount of placement tries when placing " +
                            "a spike trap block in a spike trap patch.",
                    "Not to be confused with the 'placements' field above." ) );
            
            xzSpread = SPEC.define( new IntField( "spread.vertical", xzSprd, 1, 15,
                    "How far on the X and Z axis placements can be offset in a spike trap patch." ) );
            
            ySpread = SPEC.define( new IntField( "spread.horizontal", ySprd, 0, 10,
                    "How far on the Y axis placements can be offset in a spike trap patch." ) );
            
            SPEC.newLine();
            
            damage = SPEC.define( new DoubleField( "damage", dmg, DoubleField.Range.NON_NEGATIVE,
                    "The amount of damage this spike trap deals to players standing on it",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
    
    public static class PotionSpikeTrapTypeCategory extends SpikeTrapTypeCategory {
        
        public final IntField effectDuration;
        public final IntField effectAmpl;
        
        PotionSpikeTrapTypeCategory( FeatureConfig parent, SpikeTrapType type, double placements, int minHeight, int maxHeight,
                                     double damage, int placementTries, int xzSpread, int ySpread, int effectDur, int effectAmp ) {
            super( parent, type, placements, minHeight, maxHeight, damage, placementTries, xzSpread, ySpread );
            
            SPEC.newLine();
            
            effectDuration = SPEC.define( new IntField( "effect_duration", effectDur, IntField.Range.POSITIVE,
                    "The duration of this spike trap type's potion effect when applied to mobs or players (in ticks)." ) );
            
            effectAmpl = SPEC.define( new IntField( "effect_amplifier", effectAmp, 0, 255,
                    "The amplifier of this spike trap type's potion effect when applied to mobs or players." ) );
        }
    }
    
    public static class FierySpikeTrapTypeCategory extends SpikeTrapTypeCategory {
        
        public final IntField secondsOnFire;
        
        FierySpikeTrapTypeCategory( FeatureConfig parent, SpikeTrapType type, double placements, int minHeight, int maxHeight,
                                    double damage, int placementTries, int xzSpread, int ySpread, int fireDuration ) {
            super( parent, type, placements, minHeight, maxHeight, damage, placementTries, xzSpread, ySpread );
            
            SPEC.newLine();
            
            secondsOnFire = SPEC.define( new IntField( "seconds_on_fire", fireDuration, IntField.Range.POSITIVE,
                    "This determines how much time in seconds players and mobs should be burning after coming in contact with this spike trap." ) );
        }
    }
}