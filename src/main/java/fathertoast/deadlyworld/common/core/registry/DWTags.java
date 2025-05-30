package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Supplier;

public final class DWTags {
    
    public static final class Blocks {
        public static final BlockWithItem SPAWNERS = tag( "spawners" );
        public static final BlockWithItem TRAPS = tag( "traps" );
        public static final BlockWithItem TOWER_DISPENSERS = tag( "tower_dispensers" );

        private static BlockWithItem tag( String name ) {
            return new BlockWithItem( BlockTags.create( DeadlyWorld.resourceLoc( name ) ),
                    ItemTags.create( DeadlyWorld.resourceLoc( name ) ) );
        }
    }
    
    public static final class EntityTypes {
        public static final TagKey<EntityType<?>> MIMIC = tag( "mimic" );
        public static final TagKey<EntityType<?>> MINI = tag( "mini" );
        
        public static final TagKey<EntityType<?>> CREEPERS = sharedTag( "creepers" );
        public static final TagKey<EntityType<?>> GHASTS = sharedTag( "ghasts" );
        public static final TagKey<EntityType<?>> SPIDERS = sharedTag( "spiders" );
        public static final TagKey<EntityType<?>> ZOMBIES = sharedTag( "zombies" );
        
        public static final TagKey<EntityType<?>> FIREBALLS = sharedTag( "fireballs" );
        
        private static TagKey<EntityType<?>> tag( String name ) {
            return TagKey.create( Registries.ENTITY_TYPE, DeadlyWorld.resourceLoc( name ) );
        }
        
        private static TagKey<EntityType<?>> sharedTag( String name ) {
            return TagKey.create( Registries.ENTITY_TYPE, new ResourceLocation( "forge", name ) );
        }
    }
    
    public static final class Features {
        public static final TagKey<PlacedFeature> OVERWORLD = tag( "overworld" );
        public static final TagKey<PlacedFeature> THE_NETHER = tag( "nether" );
        public static final TagKey<PlacedFeature> ANY_DIMENSION = tag( "any_dimension" );
        
        //        public static final TagKey<PlacedFeature> SPAWNERS = tag( "spawners" );
        //        public static final TagKey<PlacedFeature> TRAPS = tag( "traps" );
        
        private static TagKey<PlacedFeature> tag( String name ) {
            return TagKey.create( Registries.PLACED_FEATURE, DeadlyWorld.resourceLoc( name ) );
        }
        
        private static void initialize() { }
    }
    
    public record BlockWithItem(TagKey<Block> blockTag, TagKey<Item> itemTag) implements Supplier<TagKey<Block>> {
        @Override
        public TagKey<Block> get() { return blockTag(); }
    }
}