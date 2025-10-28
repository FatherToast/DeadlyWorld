package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import fathertoast.deadlyworld.datagen.worldgen.DWPlacedFeatureProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DWPlacedFeatureTagsProvider extends TagsProvider<PlacedFeature> {
    public DWPlacedFeatureTagsProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper ) {
        super( output, Registries.PLACED_FEATURE, lookupProvider, DeadlyWorld.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider holderLookup ) {
        // Ore features
        addAll( DWTags.PlacedFeatures.ANY_DIMENSION_ORE, DWPlacedFeatureProvider.ANY_DIMENSION_ORE_FEATURES );
        addAll( DWTags.PlacedFeatures.OVERWORLD_ORE, DWPlacedFeatureProvider.OVERWORLD_ORE_FEATURES );
        addAll( DWTags.PlacedFeatures.THE_NETHER_ORE, DWPlacedFeatureProvider.NETHER_ORE_FEATURES );
        
        addAll( DWTags.PlacedFeatures.VEINS, DWPlacedFeatureProvider.VEIN_FEATURES );
        
        // Decoration features
        addAll( DWTags.PlacedFeatures.OVERWORLD, DWPlacedFeatureProvider.OVERWORLD_FEATURES );
        addAll( DWTags.PlacedFeatures.THE_NETHER, DWPlacedFeatureProvider.NETHER_FEATURES );
        
        addAll( DWTags.PlacedFeatures.LONE_CHESTS, DWPlacedFeatureProvider.LONE_CHEST_FEATURES );
        addAll( DWTags.PlacedFeatures.SPAWNERS, DWPlacedFeatureProvider.SPAWNER_FEATURES );
        addAll( DWTags.PlacedFeatures.FLOOR_TRAPS, DWPlacedFeatureProvider.FLOOR_TRAP_FEATURES );
        addAll( DWTags.PlacedFeatures.SPIKE_TRAPS, DWPlacedFeatureProvider.SPIKE_TRAP_FEATURES );
        addAll( DWTags.PlacedFeatures.PITFALL_TRAPS, DWPlacedFeatureProvider.PITFALL_TRAP_FEATURES );
        addAll( DWTags.PlacedFeatures.TOWERS, DWPlacedFeatureProvider.TOWER_FEATURES );
        addAll( DWTags.PlacedFeatures.SEA_MINES, DWPlacedFeatureProvider.SEA_MINE_FEATURES );
        addAll( DWTags.PlacedFeatures.DUNGEONS, DWPlacedFeatureProvider.DUNGEON_FEATURES );

        // Post decoration features
        addAll( DWTags.PlacedFeatures.ANY_DIMENSION_POST_DECORATION, DWPlacedFeatureProvider.ANY_DIMENSION_POST_DECORATION );
    }
    
    /** Add all features in a list to a tag. */
    protected void addAll( TagKey<PlacedFeature> tagKey, List<ResourceKey<PlacedFeature>> features ) {
        final TagAppender<PlacedFeature> tag = tag( tagKey );
        features.forEach( tag::add );
    }
    
    /** Add all tags to another. */
    @SafeVarargs
    protected final void addTags( TagKey<PlacedFeature> tagKey, TagKey<PlacedFeature>... tagsToAdd ) {
        final TagAppender<PlacedFeature> tag = tag( tagKey );
        for( TagKey<PlacedFeature> tagToAdd : tagsToAdd ) tag.addTag( tagToAdd );
    }
}