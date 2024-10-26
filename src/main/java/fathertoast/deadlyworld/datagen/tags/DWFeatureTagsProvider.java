package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import fathertoast.deadlyworld.datagen.worldgen.DWFeatureProvider;
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

public class DWFeatureTagsProvider extends TagsProvider<PlacedFeature> {
    public DWFeatureTagsProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper ) {
        super( output, Registries.PLACED_FEATURE, lookupProvider, DeadlyWorld.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider holderLookup ) {
        addAll( DWTags.Features.OVERWORLD, DWFeatureProvider.OVERWORLD_FEATURES );
        addAll( DWTags.Features.THE_NETHER, DWFeatureProvider.NETHER_FEATURES );
        
        addTags( DWTags.Features.ALL, DWTags.Features.OVERWORLD, DWTags.Features.THE_NETHER );
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