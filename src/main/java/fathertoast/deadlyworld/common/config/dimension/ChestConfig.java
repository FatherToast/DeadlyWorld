package fathertoast.deadlyworld.common.config.dimension;

import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.InjectionWrapperField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.block.chest.ChestType;
import fathertoast.deadlyworld.common.item.IEventType;
import fathertoast.deadlyworld.common.item.InfestedEventType;
import fathertoast.deadlyworld.common.item.SurpriseEventType;
import fathertoast.deadlyworld.common.util.DimensionConfigHelper;
import net.minecraft.util.RandomSource;

import static fathertoast.deadlyworld.common.util.References.*;

public class ChestConfig extends FeatureConfig {
    
    public final ChestTypeCategory SIMPLE;
    public final ChestTypeCategory VALUABLE;
    public final ChestTypeCategory TNT_TRAP;
    public final InfestedChestTypeCategory INFESTED;
    public final SurpriseChestTypeCategory SURPRISE;
    
    /** Builds the config spec that should be used for this config. */
    ChestConfig( ConfigManager manager, String dir, DimensionConfigGroup dimConfigs ) {
        super( manager, dir, dimConfigs, "chest" );
        
        SIMPLE = new ChestTypeCategory( this, ChestType.SIMPLE, 0.3, DEPTH_LAVA, DEPTH_0 );
        VALUABLE = new ChestTypeCategory( this, ChestType.VALUABLE, 0.05, DEPTH_LAVA, DEPTH_3 );
        TNT_TRAP = new ChestTypeCategory( this, ChestType.TNT_TRAP, 0.05, DEPTH_LAVA, DEPTH_2 );
        INFESTED = new InfestedChestTypeCategory( this, ChestType.INFESTED, 0.1, DEPTH_LAVA, DEPTH_0,
                InfestedEventType.values() );
        SURPRISE = new SurpriseChestTypeCategory( this, ChestType.SURPRISE, 0.1, DEPTH_LAVA, DEPTH_1,
                SurpriseEventType.values() );
    }
    
    public static class ChestTypeCategory extends FeatureTypeCategory {
        ChestTypeCategory( FeatureConfig parent, ChestType type, double placements, int minHeight, int maxHeight ) {
            super( parent, type.toString(), placements, minHeight, maxHeight );
            
            if( isSubfeature() ) {
                // Chest types should not be used for chests that are strictly subfeatures; just use a regular chest
                throw new IllegalStateException( "Chest type features must have placements!" );
            }
        }
    }
    
    public static class EventChestTypeCategory extends ChestTypeCategory {
        /** The total weight of included events. A negative value means the weight needs to be calculated. */
        private int cachedTotalWeight = -1;
        
        private final IntField[] eventWeights;
        
        EventChestTypeCategory( ChestConfig parent, ChestType type, double placements, int minHeight, int maxHeight,
                                IEventType... eventTypes ) {
            super( parent, type, placements, minHeight, maxHeight );
            
            SPEC.newLine();
            
            eventWeights = new IntField[eventTypes.length];
            for( int i = 0; i < eventTypes.length; i++ ) {
                IEventType event = eventTypes[i];
                SPEC.define( new InjectionWrapperField<>(
                        eventWeights[i] = new IntField( "weight." + event.getId(), event.getDefaultWeight(), IntField.Range.NON_NEGATIVE,
                                "The chance for " + FEATURE_TYPE_NAME + " to " + event.getDescription() + " when triggered.",
                                DimensionConfigHelper.MESSAGE_LOOT_TABLE_OVERRIDE ), this::invalidate
                ) );
            }
        }
        
        public int nextEventIndex( RandomSource random ) {
            validate();
            // Do the random roll
            if( cachedTotalWeight > 0 ) {
                int choice = random.nextInt( cachedTotalWeight );
                for( int i = 0; i < eventWeights.length; i++ ) {
                    choice -= eventWeights[i].get();
                    if( choice < 0 ) return i;
                }
            }
            // All weights are set to 0 in the config, this causes one to be picked with unweighted random
            return -1;
        }
        
        private void invalidate( IntField field ) { cachedTotalWeight = -1; } // Clear cached value
        
        private void validate() {
            if( cachedTotalWeight < 0 ) {
                // No cached value; calculate total weight
                cachedTotalWeight = 0;
                for( IntField eventWeight : eventWeights ) cachedTotalWeight += eventWeight.get();
            }
        }
    }
    
    public static class InfestedChestTypeCategory extends EventChestTypeCategory {
        
        public final IntField spiderCount;
        public final DoubleField spiderSpeed;
        
        public final IntField silverfishCount;
        public final DoubleField silverfishSpeed;
        
        InfestedChestTypeCategory( ChestConfig parent, ChestType type, double placements, int minHeight, int maxHeight,
                                   IEventType... eventTypes ) {
            super( parent, type, placements, minHeight, maxHeight, eventTypes );
            
            SPEC.newLine();
            
            spiderCount = SPEC.define( new IntField( "spiders.count", 8, IntField.Range.NON_NEGATIVE,
                    "The number of mini spiders spawned when a spider-infested container is opened.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            spiderSpeed = SPEC.define( new DoubleField( "spiders.launch_speed", 0.3, DoubleField.Range.NON_NEGATIVE,
                    "The maximum horizontal speed spawned mini spiders are launched at.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            
            SPEC.newLine();
            
            silverfishCount = SPEC.define( new IntField( "silverfish.count", 4, IntField.Range.NON_NEGATIVE,
                    "The number of silverfish spawned when a silverfish-infested container is opened.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            silverfishSpeed = SPEC.define( new DoubleField( "silverfish.launch_speed", 0.2, DoubleField.Range.NON_NEGATIVE,
                    "The maximum horizontal speed spawned silverfish are launched at.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
    
    public static class SurpriseChestTypeCategory extends EventChestTypeCategory {
        
        public final IntField.RandomRange tntFuseTime;
        public final IntField tntCount;
        public final DoubleField tntSpeed;
        
        public final IntField poisonGasDelay;
        public final IntField poisonGasDuration;
        public final DoubleField poisonGasMaxRadius;
        public final IntField poisonGasEffectPotency;
        public final IntField poisonGasEffectDuration;
        
        public final IntField witherGasDelay;
        public final IntField witherGasDuration;
        public final DoubleField witherGasMaxRadius;
        public final IntField witherGasEffectPotency;
        public final IntField witherGasEffectDuration;
        
        SurpriseChestTypeCategory( ChestConfig parent, ChestType type, double placements, int minHeight, int maxHeight,
                                   IEventType... eventTypes ) {
            super( parent, type, placements, minHeight, maxHeight, eventTypes );
            
            SPEC.newLine();
            
            tntFuseTime = new IntField.RandomRange( SPEC, "tnt.fuse_time", 40, 60, IntField.Range.NON_NEGATIVE,
                    "The delay before spawned TNT explodes, in ticks. (20 ticks = 1 second).",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE );
            tntCount = SPEC.define( new IntField( "tnt.count", 1, IntField.Range.NON_NEGATIVE,
                    "The number of TNT spawned when a TNT-rigged container is opened.",
                    "Note that setting this higher than 1 will pretty much guarantee everything in the container gets exploded.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            tntSpeed = SPEC.define( new DoubleField( "tnt.launch_speed", 0.05, DoubleField.Range.NON_NEGATIVE,
                    "The maximum horizontal speed spawned TNT are launched at.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            
            SPEC.newLine();
            
            poisonGasDelay = SPEC.define( new IntField( "poison_gas.delay", 20, IntField.Range.NON_NEGATIVE,
                    "The delay before the poison gas cloud starts spreading, in ticks. (20 ticks = 1 second)",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            poisonGasDuration = SPEC.define( new IntField( "poison_gas.duration", 40, IntField.Range.NON_NEGATIVE,
                    "The duration (after its initial delay) until the poison gas cloud reaches max size and disappears, in ticks.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            poisonGasMaxRadius = SPEC.define( new DoubleField( "poison_gas.max_radius", 12.0, 0.5, Double.POSITIVE_INFINITY,
                    "The maximum distance, in blocks, the poison gas cloud spreads from its origin.",
                    "Note the cloud starts at 0.5 radius and linearly increases to max radius at exactly its max duration.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            poisonGasEffectPotency = SPEC.define( new IntField( "poison_gas.effect.potency", 0, IntField.Range.NON_NEGATIVE,
                    "Potency of the poison effect applied by poison gas clouds. (0 = Poison I, 1 = Poison II, etc.)",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            poisonGasEffectDuration = SPEC.define( new IntField( "poison_gas.effect.duration", 200, IntField.Range.NON_NEGATIVE,
                    "Duration of the poison effect applied by poison gas clouds, in ticks.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            
            SPEC.newLine();
            
            witherGasDelay = SPEC.define( new IntField( "wither_gas.delay", 20, IntField.Range.NON_NEGATIVE,
                    "The delay before the withering gas cloud starts spreading, in ticks. (20 ticks = 1 second)",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            witherGasDuration = SPEC.define( new IntField( "wither_gas.duration", 40, IntField.Range.NON_NEGATIVE,
                    "The duration (after its initial delay) until the withering gas cloud reaches max size and disappears, in ticks.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            witherGasMaxRadius = SPEC.define( new DoubleField( "wither_gas.max_radius", 12.0, 0.5, Double.POSITIVE_INFINITY,
                    "The maximum distance, in blocks, the withering gas cloud spreads from its origin.",
                    "Note the cloud starts at 0.5 radius and linearly increases to max radius at exactly its max duration.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            witherGasEffectPotency = SPEC.define( new IntField( "wither_gas.effect.potency", 0, IntField.Range.NON_NEGATIVE,
                    "Potency of the wither effect applied by withering gas clouds. (0 = Wither I, 1 = Wither II, etc.)",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
            witherGasEffectDuration = SPEC.define( new IntField( "wither_gas.effect.duration", 200, IntField.Range.NON_NEGATIVE,
                    "Duration of the wither effect applied by withering gas clouds, in ticks.",
                    DimensionConfigHelper.MESSAGE_NO_OVERRIDE ) );
        }
    }
}