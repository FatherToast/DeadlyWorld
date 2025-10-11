package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.AttributeListField;
import fathertoast.crust.api.config.common.value.AttributeEntry;
import fathertoast.crust.api.config.common.value.AttributeList;
import fathertoast.deadlyworld.common.config.field.RLValueListField;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

import java.util.List;

public class EntitiesConfig extends AbstractConfigFile {
    
    public final Minis MINIS;
    public final Mimics MIMICS;
    
    /** Builds the config spec that should be used for this config. */
    EntitiesConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName,
                "This config contains options for miscellaneous features in the mod."
        );
        
        SPEC.newLine();
        SPEC.describeAttributeList();
        
        MINIS = new Minis( this );
        MIMICS = new Mimics( this );
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
    
    public static class Mimics extends AbstractConfigCategory<EntitiesConfig> {
        
        public final AttributeListField chestAttributes;
        public final RLValueListField chestTargetLootTables;
        
        public final AttributeListField jukeboxAttributes;
        
        public final AttributeListField spawnerAttributes;
        
        public final AttributeListField miniSpawnerAttributes;
        
        Mimics( EntitiesConfig parent ) {
            super( parent, "mimics",
                    "Options to customize misc global settings." );
            
            chestAttributes = mimicAttributes( "chest" );
            
            chestTargetLootTables = SPEC.define( new RLValueListField( "chest_target_loot_tables",
                    1, defaultChestMimicLootTables(),
                    "List of IDs for loot tables that can have a Mimic Core item added to them by Deadly World.",
                    "Each ID is paired with a chance value (the chance for a Mimic Core to be added to the loot table).",
                    "Chance is in percents, so it should range from 0.0 to 1.0.",
                    "Mimic Cores are what makes chests come alive and become Chest Mimics, so if one exists in a chest's inventory,",
                    "be it either because of a loot modifier or because someone put it there, the chest will come alive when opened." ) );
            
            SPEC.newLine();
            
            jukeboxAttributes = mimicAttributes( "jukebox" );
            
            SPEC.newLine();
            
            spawnerAttributes = mimicAttributes( "spawner" );
            
            SPEC.newLine();
            
            miniSpawnerAttributes = miniMimicAttributes( "mini", "mini_mimics" );
            ;
        }
        
        private List<String> defaultChestMimicLootTables() {
            return List.of(
                    chestMimicLootTableEntry( BuiltInLootTables.ABANDONED_MINESHAFT, 0.2 ),
                    chestMimicLootTableEntry( BuiltInLootTables.ANCIENT_CITY, 0.2 ),
                    chestMimicLootTableEntry( BuiltInLootTables.BASTION_BRIDGE, 0.2 ),
                    chestMimicLootTableEntry( BuiltInLootTables.BASTION_TREASURE, 0.2 ),
                    chestMimicLootTableEntry( BuiltInLootTables.DESERT_PYRAMID, 0.2 ),
                    chestMimicLootTableEntry( BuiltInLootTables.END_CITY_TREASURE, 0.2 ),
                    chestMimicLootTableEntry( BuiltInLootTables.IGLOO_CHEST, 0.1 ),
                    chestMimicLootTableEntry( BuiltInLootTables.JUNGLE_TEMPLE, 0.2 ),
                    chestMimicLootTableEntry( BuiltInLootTables.NETHER_BRIDGE, 0.2 ),
                    chestMimicLootTableEntry( BuiltInLootTables.RUINED_PORTAL, 0.1 ),
                    chestMimicLootTableEntry( BuiltInLootTables.SIMPLE_DUNGEON, 0.3 ),
                    chestMimicLootTableEntry( BuiltInLootTables.SPAWN_BONUS_CHEST, 0.5 ),
                    chestMimicLootTableEntry( BuiltInLootTables.STRONGHOLD_CORRIDOR, 0.3 ),
                    chestMimicLootTableEntry( BuiltInLootTables.WOODLAND_MANSION, 0.1 )
            );
        }
        
        private static String chestMimicLootTableEntry( ResourceLocation lootTableId, double chance ) {
            return lootTableId + " " + chance;
        }
        
        private AttributeListField mimicAttributes( String key ) {
            return mimicAttributes( key, key + " mimics" );
        }
        
        private AttributeListField mimicAttributes( String key, String name ) {
            AttributeList defaults = new AttributeList(
                    AttributeEntry.add( Attributes.MAX_HEALTH, 15.0D ),
                    AttributeEntry.mult( Attributes.MOVEMENT_SPEED, 1.0 ),
                    AttributeEntry.add( Attributes.ATTACK_DAMAGE, 2.0 )
            );
            return SPEC.define( new AttributeListField( key + "_attributes", defaults,
                    "Attribute modifiers for " + name + "." ) );
        }
        
        private AttributeListField miniMimicAttributes( String key, String name ) {
            AttributeList defaults = new AttributeList(
                    AttributeEntry.add( Attributes.MAX_HEALTH, 10.0D ),
                    AttributeEntry.mult( Attributes.MOVEMENT_SPEED, 1.25 ),
                    AttributeEntry.add( Attributes.ATTACK_DAMAGE, 1.5 )
            );
            return SPEC.define( new AttributeListField( key + "_attributes", defaults,
                    "Attribute modifiers for " + name + "." ) );
        }
    }
}