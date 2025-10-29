package fathertoast.deadlyworld.common.core.registry;

import fathertoast.deadlyworld.api.DecoyType;
import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.function.Supplier;

public final class DWTags {
    
    public static final class Blocks {
        public static final BlockWithItem SPAWNERS = tag( "spawners" );
        public static final BlockWithItem FLOOR_TRAPS = tag( "floor_traps" );
        public static final BlockWithItem TOWER_DISPENSERS = tag( "tower_dispensers" );
        public static final BlockWithItem SEA_MINES = tag( "sea_mines" );
        public static final BlockWithItem SPIKE_TRAPS = tag( "spike_traps" );
        public static final BlockWithItem PITFALL_TRAPS = tag( "pitfall_traps" );


        private static BlockWithItem tag( String name ) {
            return new BlockWithItem( BlockTags.create( DeadlyWorld.rl( name ) ),
                    ItemTags.create( DeadlyWorld.rl( name ) ) );
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
            return TagKey.create( Registries.ENTITY_TYPE, DeadlyWorld.rl( name ) );
        }
        
        private static TagKey<EntityType<?>> sharedTag( String name ) {
            return TagKey.create( Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath( "forge", name ) );
        }
    }
    
    public static final class DecoyTypes {
        public static final TagKey<DecoyType> OVERWORLD = tag( "overworld" );
        public static final TagKey<DecoyType> THE_NETHER = tag( "nether" );
        public static final TagKey<DecoyType> ANY_DIMENSION = tag( "any_dimension" );
        
        private static TagKey<DecoyType> tag( String name ) {
            return DWDecoyTypes.REGISTRY.createTagKey( DeadlyWorld.rl( name ) );
        }
    }
    
    // These tags do not affect world gen; only here for the feature placer item and to help data pack makers
    public static final class ConfiguredFeatures {
        public static final TagKey<ConfiguredFeature<?, ?>> NOT_PLACEABLE = tag( "not_placeable" );
        
        // Ore features
        public static final TagKey<ConfiguredFeature<?, ?>> ANY_DIMENSION_ORE = tag( "any_dimension_ore" );
        public static final TagKey<ConfiguredFeature<?, ?>> OVERWORLD_ORE = tag( "overworld_ore" );
        public static final TagKey<ConfiguredFeature<?, ?>> THE_NETHER_ORE = tag( "nether_ore" );
        
        public static final TagKey<ConfiguredFeature<?, ?>> VEINS = tag( "veins" );
        
        // Decoration features
        public static final TagKey<ConfiguredFeature<?, ?>> OVERWORLD = tag( "overworld" );
        public static final TagKey<ConfiguredFeature<?, ?>> THE_NETHER = tag( "nether" );
        
        public static final TagKey<ConfiguredFeature<?, ?>> LONE_CHESTS = tag( "lone_chests" );
        public static final TagKey<ConfiguredFeature<?, ?>> SPAWNERS = tag( "spawners" );
        public static final TagKey<ConfiguredFeature<?, ?>> FLOOR_TRAPS = tag( "floor_traps" );
        public static final TagKey<ConfiguredFeature<?, ?>> SPIKE_TRAPS = tag( "spike_traps" );
        public static final TagKey<ConfiguredFeature<?, ?>> PITFALL_TRAPS = tag( "pitfall_traps" );
        public static final TagKey<ConfiguredFeature<?, ?>> TOWERS = tag( "towers" );
        public static final TagKey<ConfiguredFeature<?, ?>> SEA_MINES = tag( "sea_mines" );
        public static final TagKey<ConfiguredFeature<?, ?>> DUNGEONS = tag( "dungeons" );

        // Post decoration features
        public static final TagKey<ConfiguredFeature<?, ?>> ANY_DIMENSION_POST_DECORATION = tag( "any_dimension_post_decoration" );
        
        private static TagKey<ConfiguredFeature<?, ?>> tag( String name ) {
            return TagKey.create( Registries.CONFIGURED_FEATURE, DeadlyWorld.rl( name ) );
        }
    }
    
    /**
     * Generation steps are listed in {@link net.minecraft.world.level.levelgen.GenerationStep.Decoration},
     * in the order they generate.
     */
    public static final class PlacedFeatures {
        // World gen step "underground_ores"
        public static final TagKey<PlacedFeature> ANY_DIMENSION_ORE = tag( "any_dimension_ore" );
        public static final TagKey<PlacedFeature> OVERWORLD_ORE = tag( "overworld_ore" );
        public static final TagKey<PlacedFeature> THE_NETHER_ORE = tag( "nether_ore" );
        
        public static final TagKey<PlacedFeature> VEINS = tag( "veins" );
        
        // World gen step "underground_decoration"
        public static final TagKey<PlacedFeature> OVERWORLD = tag( "overworld" );
        public static final TagKey<PlacedFeature> THE_NETHER = tag( "nether" );
        
        public static final TagKey<PlacedFeature> LONE_CHESTS = tag( "lone_chests" );
        public static final TagKey<PlacedFeature> SPAWNERS = tag( "spawners" );
        public static final TagKey<PlacedFeature> FLOOR_TRAPS = tag( "floor_traps" );
        public static final TagKey<PlacedFeature> SPIKE_TRAPS = tag( "spike_traps" );
        public static final TagKey<PlacedFeature> PITFALL_TRAPS = tag( "pitfall_traps" );
        public static final TagKey<PlacedFeature> TOWERS = tag( "towers" );
        public static final TagKey<PlacedFeature> SEA_MINES = tag( "sea_mines" );
        public static final TagKey<PlacedFeature> DUNGEONS = tag( "dungeons" );

        // World gen step "fluid_springs"
        public static final TagKey<PlacedFeature> ANY_DIMENSION_POST_DECORATION = tag( "any_dimension_post_decoration" );
        
        private static TagKey<PlacedFeature> tag( String name ) {
            return TagKey.create( Registries.PLACED_FEATURE, DeadlyWorld.rl( name ) );
        }
    }
    
    public record BlockWithItem(TagKey<Block> blockTag, TagKey<Item> itemTag) implements Supplier<TagKey<Block>> {
        @Override
        public TagKey<Block> get() { return blockTag(); }
    }
}