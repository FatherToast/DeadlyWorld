package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AttributeListField;
import fathertoast.crust.api.config.common.value.AttributeEntry;
import fathertoast.crust.api.config.common.value.AttributeList;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EntitiesConfig extends AbstractConfigFile {
    
    public final Minis MINIS;
    
    /** Builds the config spec that should be used for this config. */
    EntitiesConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for miscellaneous features in the mod."
        );
        
        MINIS = new Minis( this );
    }
    
    public static class Minis extends AbstractConfigCategory<EntitiesConfig> {
        
        public final AttributeListField creeperAttributes;
        public final AttributeListField zombieAttributes;
        public final AttributeListField skeletonAttributes;
        public final AttributeListField spiderAttributes;
        public final AttributeListField ghastAttributes;
        
        Minis( EntitiesConfig parent ) {
            super( parent, "mini_mobs",
                    "Options to customize misc global settings." );
            
            creeperAttributes = miniAttributes( "creeper" );
            zombieAttributes = miniAttributes( "zombie" );
            skeletonAttributes = miniAttributes( "skeleton" );
            spiderAttributes = miniAttributes( "spider" );
            ghastAttributes = miniAttributes( "ghast", "micro ghasts" );
        }
        
        private AttributeListField miniAttributes( String key ) {
            return miniAttributes( key, "mini " + key + "s" );
        }
        
        private AttributeListField miniAttributes( String key, String name ) {
            AttributeList defaults = new AttributeList(
                    AttributeEntry.mult( Attributes.MAX_HEALTH, 0.333 ),
                    AttributeEntry.mult( Attributes.MOVEMENT_SPEED, 1.3 ),
                    AttributeEntry.mult( Attributes.ATTACK_DAMAGE, 0.5 )
            );
            return SPEC.define( new AttributeListField( key + "_attributes", defaults,
                    "Attribute modifiers for " + name + ". If no attribute changes are defined here, " +
                            name + " will have the exact same attributes as the full-size version vanilla mob." ) );
        }
    }
}