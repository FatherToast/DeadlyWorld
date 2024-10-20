package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.BooleanField;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.IntField;
import fathertoast.deadlyworld.common.block.floortrap.FloorTrapType;
import fathertoast.deadlyworld.common.block.spawner.SpawnerType;
import fathertoast.deadlyworld.common.block.tower.TowerType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.HashMap;
import java.util.Map;

public class BlocksConfig extends AbstractConfigFile {
    
    private final Map<String, BlockCategory> LOOKUP = new HashMap<>();
    
    /** Builds the config spec that should be used for this config. */
    BlocksConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options to control the physical properties of the blocks added by this mod."
        );
        
        // Spawners
        for( SpawnerType type : SpawnerType.values() ) {
            LOOKUP.put( toKey( SpawnerType.CATEGORY, type.toString() ), new BlockCategory( this, SpawnerType.CATEGORY, type.toString(),
                    5.0, 1200.0, 2 ) );
        }
        
        // Floor Traps
        for( FloorTrapType type : FloorTrapType.values() ) {
            LOOKUP.put( toKey( FloorTrapType.CATEGORY, type.toString() ), new BlockCategory( this, FloorTrapType.CATEGORY, type.toString(),
                    5.0, 1200.0, 1 ) );
        }
        
        // Tower Dispensers
        for( TowerType type : TowerType.values() ) {
            LOOKUP.put( toKey( TowerType.CATEGORY, type.toString() ), new BlockCategory( this, TowerType.CATEGORY, type.toString(),
                    5.0, 1200.0, 1 ) );
        }
        
        //TODO add storm drain; will possibly include in a "water traps" category
    }
    
    public BlockCategory get( SpawnerType type ) { return get( SpawnerType.CATEGORY, type.toString() ); }
    
    public BlockCategory get( FloorTrapType type ) {
        return get( FloorTrapType.CATEGORY, type.toString() );
    }
    
    public BlockCategory get( TowerType type ) {
        return get( TowerType.CATEGORY, type.toString() );
    }
    
    private BlockCategory get( String category, String type ) {
        BlocksConfig.BlockCategory blockCategory = LOOKUP.get( toKey( category, type ) );
        
        if( blockCategory == null )
            throw new IllegalStateException( DeadlyWorld.logPrefix( this.getClass() ) + "No block category exists for type '" + type + '"' );
        
        return blockCategory;
    }
    
    private static String toKey( String category, String type ) { return category + "." + type; }
    
    public static class BlockCategory extends AbstractConfigCategory<BlocksConfig> {
        
        public final DoubleField destroyTime;
        public final DoubleField explosionResistance;
        
        public final BooleanField requiresTool;
        
        public final DoubleField slipperiness;
        public final DoubleField speedFactor;
        public final DoubleField jumpFactor;
        
        public final IntField lightLevel;
        
        BlockCategory( BlocksConfig parent, String blockCat, String type, double breakTime, double explosionResist, int toolLevel ) {
            super( parent, toKey( blockCat, type ),
                    "Options to customize the physical properties of the " + type + " " + blockCat + " block." );
            final String name = type + " " + blockCat + " block";
            
            destroyTime = SPEC.define( new DoubleField( "hardness", breakTime, -1.0, Double.POSITIVE_INFINITY,
                    "Influences the time it takes to break " + name + "s. Actual time in seconds is 1.5 times",
                    "this value when using a valid tool (ignoring any tool speed modifiers), and 5 times this value when",
                    "using an invalid tool. A negative hardness value makes it impossible to break by hand, like bedrock.",
                    "For reference: 1.5 = stone, 5 = vanilla spawner, 50 = obsidian, -1 = bedrock" ) );
            explosionResistance = SPEC.define( new DoubleField( "explosion_resistance", explosionResist, DoubleField.Range.NON_NEGATIVE,
                    "How resistant " + name + "s are to being destroyed by explosions. This is usually equal",
                    "to the hardness value, but frequently higher for stone/metal materials.",
                    "For reference: 6 = stone, 5 = vanilla spawner, 1200 = obsidian, 3600000 = bedrock" ) );
            
            SPEC.newLine();
            
            requiresTool = SPEC.define( new BooleanField( "requires_tool", false,
                    "If true, " + name + "s will not drop any loot unless broken by the harvest tool." ) );
            
            SPEC.newLine();
            
            slipperiness = SPEC.define( new DoubleField( "slipperiness", 0.6, DoubleField.Range.ANY,
                    "How slippery " + name + "s are to move on.  The game physics start to make no sense",
                    "if this is set outside the range of 0 to 1, but I guess you can do that if you want.",
                    "For reference: 0.6 = most things, 0.8 = slime, 0.98 = ice, 0.989 = blue ice" ) );
            speedFactor = SPEC.define( new DoubleField( "speed_factor", 1.0, DoubleField.Range.ANY,
                    "A speed multiplier applied to entities actively moving on " + name + "s.",
                    "For reference: 1.0 = most things, 0.4 = soul sand" ) );
            jumpFactor = SPEC.define( new DoubleField( "jump_factor", 1.0, DoubleField.Range.ANY,
                    "A jump power multiplier applied to entities jumping on " + name + "s.",
                    "For reference: 1.0 = most things, 0.5 = honey" ) );
            
            SPEC.newLine();
            
            lightLevel = SPEC.define( new IntField( "light_level", 0, 0, 15,
                    "The level of light emitted by " + name + "s.",
                    "For reference: 0 = most things, 7 = glow lichen, 14 = torch, 15 = glowstone" ) );
        }
        
        /** Called by this mod's blocks during construction to apply configured stats. */
        public BlockBehaviour.Properties adjustBlockProperties( BlockBehaviour.Properties props ) {
            props.requiresCorrectToolForDrops = requiresTool.get();
            return props.strength( (float) destroyTime.get(), (float) explosionResistance.get() )
                    .friction( (float) slipperiness.get() ).speedFactor( (float) speedFactor.get() ).jumpFactor( (float) jumpFactor.get() )
                    .lightLevel( ( state ) -> lightLevel.get() );
        }
    }
}