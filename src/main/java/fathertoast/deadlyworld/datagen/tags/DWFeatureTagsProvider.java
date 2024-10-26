package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import fathertoast.deadlyworld.datagen.worldgen.DWFeatureProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class DWFeatureTagsProvider extends TagsProvider<PlacedFeature> {
    public DWFeatureTagsProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper ) {
        super( output, Registries.PLACED_FEATURE, lookupProvider, DeadlyWorld.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider holderLookup ) {
        final TagAppender<PlacedFeature> allTag = tag( DWTags.Features.ALL );
        DWFeatureProvider.ALL_PLACEMENTS.forEach( allTag::add );
    }
}