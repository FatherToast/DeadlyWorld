package fathertoast.deadlyworld.config;

import fathertoast.deadlyworld.config.field.*;
import fathertoast.deadlyworld.config.file.ToastConfigSpec;
import fathertoast.deadlyworld.config.util.BlockList;
import fathertoast.deadlyworld.config.util.EntityEntry;
import fathertoast.deadlyworld.config.util.EntityList;
import net.minecraft.entity.EntityType;

import java.io.File;

public class GeneralConfig extends Config.AbstractConfig {
    
    public final General GENERAL;
    
    /** Builds the config spec that should be used for this config. */
    GeneralConfig( File dir, String fileName ) {
        super( dir, fileName,
                "This config contains options for several miscellaneous features in the mod, such as:",
                "NOTHING, YET!"
        );
    
        SPEC.newLine();
        SPEC.describeEntityList();
        SPEC.newLine();
        SPEC.describeBlockList();
        
        GENERAL = new General( SPEC );
    }
    
    public static class General extends Config.AbstractCategory {
        
        public final EntityListField.Combined entityList;
        
        public final BooleanField requiresTarget;
        public final BooleanField requiresTools;
        
        public final BooleanField leaveDrops;
        
        public final DoubleField breakSpeed;
        public final BooleanField madCreepers;
        
        public final BooleanField targetDoors;
        public final BlockListField.Combined targetList;
        
        General( ToastConfigSpec parent ) {
            super( parent, "general",
                    "Options to customize NOTHING AT ALL." );
            
            entityList = new EntityListField.Combined(
                    SPEC.define( new EntityListField( "entities.whitelist", new EntityList(
                            new EntityEntry( EntityType.ZOMBIE, 1.0 ), new EntityEntry( EntityType.CREEPER, 1.0 )
                    ).setSinglePercent(),
                            "List of mobs that can gain door breaking AI (note that the entity must have task-based AI enabled).",
                            "Additional value after the entity type is the chance (0.0 to 1.0) for entities of that type to spawn with the AI." ) ),
                    SPEC.define( new EntityListField( "entities.blacklist", new EntityList().setNoValues() ) )
            );
            
            SPEC.newLine();
            
            requiresTarget = SPEC.define( new BooleanField( "require_target", true,
                    "If true, mobs will only break doors while they are chasing an attack target.",
                    "Disabling this typically leads to mobs smashing into your house to get to blocks they are targeting",
                    "as part of an idle griefing or fiddling behavior, such as torches or chests." ) );
            requiresTools = SPEC.define( new BooleanField( "require_tools", true,
                    "If true, mobs will only break doors they have the tools to harvest.",
                    "For example, they will only break iron doors if they have a pickaxe." ) );
            
            SPEC.newLine();
            
            leaveDrops = SPEC.define( new BooleanField( "leave_drops", true,
                    "If true, doors broken by mobs will leave item drops." ) );
            
            SPEC.newLine();
            
            breakSpeed = SPEC.define( new DoubleField( "break_speed", 0.33, DoubleField.Range.POSITIVE,
                    "The block breaking speed multiplier for mobs breaking doors, relative to the player's block breaking speed." ) );
            madCreepers = SPEC.define( new BooleanField( "mad_creepers", true,
                    "If true, creepers will resort to what they know best when they meet a door blocking their path." ) );
            
            SPEC.newLine();
            
            targetDoors = SPEC.define( new BooleanField( "targets.auto_target_doors", true,
                    "If true, door breaking AI will automatically target all blocks that derive from the",
                    "vanilla doors, fence gates, and trapdoors." ) );
            targetList = new BlockListField.Combined(
                    SPEC.define( new BlockListField( "targets.whitelist", new BlockList(),
                            "List of blocks that that can be broken by the door breaking AI." ) ),
                    SPEC.define( new BlockListField( "targets.blacklist", new BlockList() ) )
            );
        }
    }
}