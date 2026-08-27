package fathertoast.deadlyworld.common.config;

import fathertoast.crust.api.config.common.AbstractConfigCategory;
import fathertoast.crust.api.config.common.AbstractConfigFile;
import fathertoast.crust.api.config.common.ConfigManager;
import fathertoast.crust.api.config.common.field.DoubleField;
import fathertoast.crust.api.config.common.field.collection.AttributeOpListField;
import fathertoast.crust.api.config.common.field.collection.FuzzyMapField;
import fathertoast.crust.api.config.common.value.collection.AttributeOpList;
import fathertoast.crust.api.config.common.value.collection.FuzzyMap;
import fathertoast.crust.api.config.common.value.collection.key.ResourceLocKey;
import fathertoast.crust.api.config.common.value.collection.value.DoubleValueCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class EntitiesConfig extends AbstractConfigFile {
    
    public final Minis MINIS;
    public final Mimics MIMICS;
    public final Misc MISC;
    
    /** Builds the config spec that should be used for this config. */
    EntitiesConfig( ConfigManager manager, String fileName ) {
        super( manager, fileName, false,
                "This config contains options for the entities added by this mod."
        );
        
        SPEC.newLine();
        AttributeOpListField.describe( SPEC );
        
        MINIS = new Minis( this );
        MIMICS = new Mimics( this );
        MISC = new Misc( this );
    }
    
    public static class Minis extends AbstractConfigCategory<EntitiesConfig> {
        
        public final AttributeOpListField creeperAttributes;
        public final AttributeOpListField zombieAttributes;
        public final AttributeOpListField skeletonAttributes;
        public final AttributeOpListField spiderAttributes;
        public final AttributeOpListField ghastAttributes;
        
        public final DoubleField spookySpiderChance;
        
        Minis( EntitiesConfig parent ) {
            super( parent, "mini_mobs",
                    "Options to customize the \"mini\" entities; small versions of existing vanilla mobs." );
            
            creeperAttributes = miniAttributes( "creeper" );
            zombieAttributes = miniAttributes( "zombie" );
            skeletonAttributes = miniAttributes( "skeleton" );
            spiderAttributes = miniAttributes( "spider" );
            ghastAttributes = miniAttributes( "ghast", "micro ghasts" );
            
            SPEC.newLine();
            
            spookySpiderChance = SPEC.define( new DoubleField( "spooky_spider_chance", 0.005, DoubleField.Range.PERCENT,
                    "Chance for a very spooky spider." ) );
        }
        
        private AttributeOpListField miniAttributes( String key ) {
            return miniAttributes( key, "mini " + key + "s" );
        }
        
        private AttributeOpListField miniAttributes( String key, String name ) {
            return SPEC.define( new AttributeOpListField( key + "_attributes", new AttributeOpList.Builder<>()
                    .putMultiply( Attributes.MAX_HEALTH, 0.333 )
                    .putMultiply( Attributes.MOVEMENT_SPEED, 1.3 )
                    .putMultiply( Attributes.ATTACK_DAMAGE, 0.5 )
                    .build(),
                    "Attribute modifiers for " + name + ". If no attribute changes are defined here, " +
                            name + " will have the exact same attributes as the full-size version vanilla mob." ) );
        }
    }
    
    public static class Mimics extends AbstractConfigCategory<EntitiesConfig> {
        
        public final AttributeOpListField chestAttributes;
        public final AttributeOpListField miniChestAttributes;
        public final FuzzyMapField<ResourceLocation, Double, FuzzyMap<ResourceLocation, Double>>
                chestTargetLootTables;
        
        public final AttributeOpListField jukeboxAttributes;
        
        public final AttributeOpListField spawnerAttributes;
        public final AttributeOpListField miniSpawnerAttributes;
        
        Mimics( EntitiesConfig parent ) {
            super( parent, "mimics",
                    "Options to customize misc global settings." );
            
            chestAttributes = mimicAttributes( "chest" );
            miniChestAttributes = miniMimicAttributes( "chest" );
            chestTargetLootTables = SPEC.define( new FuzzyMapField<>( "chest_target_loot_tables",
                    defaultChestMimicLootTables(),
                    "List of IDs for loot tables that can have a Mimic Core item added to them by Deadly World.",
                    "Each ID is paired with a chance value (the chance for a Mimic Core to be added to the loot table). " +
                            "Chance is in percents, so it should range from 0.0 to 1.0.",
                    "Mimic Cores are what make chests come alive and become Chest Mimics, so if one exists in a chest's " +
                            "inventory, be it either because of a loot modifier or because someone put it there, the chest will " +
                            "come alive when opened." ) );
            
            SPEC.newLine();
            
            jukeboxAttributes = mimicAttributes( "jukebox" );
            
            SPEC.newLine();
            
            spawnerAttributes = mimicAttributes( "spawner" );
            miniSpawnerAttributes = miniMimicAttributes( "spawner" );
        }
        
        private FuzzyMap<ResourceLocation, Double> defaultChestMimicLootTables() {
            return new FuzzyMap.Builder<>( ResourceLocKey.PARSER, DoubleValueCodec.PERCENT )
                    .put( rlKey( BuiltInLootTables.ABANDONED_MINESHAFT ), 0.05 )
                    .put( rlKey( BuiltInLootTables.ANCIENT_CITY ), 0.1 )
                    .put( rlKey( BuiltInLootTables.BASTION_BRIDGE ), 0.05 )
                    .put( rlKey( BuiltInLootTables.BASTION_TREASURE ), 0.1 )
                    .put( rlKey( BuiltInLootTables.DESERT_PYRAMID ), 0.05 )
                    .put( rlKey( BuiltInLootTables.END_CITY_TREASURE ), 0.05 )
                    .put( rlKey( BuiltInLootTables.IGLOO_CHEST ), 0.05 )
                    .put( rlKey( BuiltInLootTables.JUNGLE_TEMPLE ), 0.05 )
                    .put( rlKey( BuiltInLootTables.NETHER_BRIDGE ), 0.05 )
                    .put( rlKey( BuiltInLootTables.RUINED_PORTAL ), 0.1 )
                    .put( rlKey( BuiltInLootTables.SIMPLE_DUNGEON ), 0.05 )
                    .put( rlKey( BuiltInLootTables.STRONGHOLD_CORRIDOR ), 0.05 )
                    .put( rlKey( BuiltInLootTables.STRONGHOLD_CROSSING ), 0.05 )
                    .put( rlKey( BuiltInLootTables.STRONGHOLD_LIBRARY ), 0.1 )
                    .put( rlKey( BuiltInLootTables.WOODLAND_MANSION ), 0.1 )
                    .build();
        }
        
        private static ResourceLocKey rlKey( ResourceLocation lootTableId ) {
            return ResourceLocKey.of( lootTableId );
        }
        
        private AttributeOpListField mimicAttributes( String key ) {
            return mimicAttributes( key, key + " mimics" );
        }
        
        private AttributeOpListField mimicAttributes( String key, String name ) {
            return SPEC.define( new AttributeOpListField( key + "_attributes", new AttributeOpList(),
                    "Attribute modifiers for " + name + "." ) );
        }
        
        private AttributeOpListField miniMimicAttributes( String key ) {
            return miniMimicAttributes( key, "mini " + key + " mimics" );
        }
        
        private AttributeOpListField miniMimicAttributes( String key, String name ) {
            return SPEC.define( new AttributeOpListField( "mini_" + key + "_attributes", new AttributeOpList.Builder<>()
                    .putMultiply( Attributes.MAX_HEALTH, 0.333 )
                    .putMultiply( Attributes.MOVEMENT_SPEED, 1.3 )
                    .putMultiply( Attributes.ATTACK_DAMAGE, 0.5 )
                    .build(),
                    "Attribute modifiers for " + name + "." ) );
        }
    }
    
    public static class Misc extends AbstractConfigCategory<EntitiesConfig> {
        
        public final DoubleField yeetntKnockbackMult;
        
        public final DoubleField yeetntPower;
        
        Misc( EntitiesConfig parent ) {
            super( parent, "misc",
                    "Options to customize various entities that doesn't fit in any other categories." );
            
            yeetntKnockbackMult = SPEC.define( new DoubleField( "yeetnt.knockback_mult", 10.0, 1.0, 100.0,
                    "The multiplier used by YeetNT when calculating the knockback to apply to entities affected by the blast." ) );
            
            yeetntPower = SPEC.define( new DoubleField( "yeetnt.power", 4.0, 1.0, 100.0,
                    "The multiplier used by YeetNT when calculating its blast area." ) );
        }
    }
}