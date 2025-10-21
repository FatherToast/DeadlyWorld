package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import fathertoast.deadlyworld.datagen.worldgen.DWAbstractCFProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DWConfiguredFeatureTagsProvider extends TagsProvider<ConfiguredFeature<?, ?>> {
    public DWConfiguredFeatureTagsProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper ) {
        super( output, Registries.CONFIGURED_FEATURE, lookupProvider, DeadlyWorld.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider holderLookup ) {
        addAll( DWTags.ConfiguredFeatures.NOT_PLACEABLE, DWAbstractCFProvider.NOT_PLACEABLE );
        addAll( DWTags.ConfiguredFeatures.OVERWORLD, DWAbstractCFProvider.OVERWORLD_FEATURES );
        addAll( DWTags.ConfiguredFeatures.THE_NETHER, DWAbstractCFProvider.NETHER_FEATURES );
        addAll( DWTags.ConfiguredFeatures.ANY_DIMENSION, DWAbstractCFProvider.ANY_DIMENSION_FEATURES );
        
        addAll( DWTags.ConfiguredFeatures.LONE_CHESTS, DWAbstractCFProvider.LONE_CHEST_FEATURES );
        addAll( DWTags.ConfiguredFeatures.SPAWNERS, DWAbstractCFProvider.SPAWNER_FEATURES );
        addAll( DWTags.ConfiguredFeatures.TRAPS, DWAbstractCFProvider.TRAP_FEATURES );
        addAll( DWTags.ConfiguredFeatures.TOWERS, DWAbstractCFProvider.TOWER_FEATURES );
        addAll( DWTags.ConfiguredFeatures.SEA_MINES, DWAbstractCFProvider.SEA_MINE_FEATURES );
        addAll( DWTags.ConfiguredFeatures.DUNGEONS, DWAbstractCFProvider.DUNGEON_FEATURES );
    }
    
    /** Add all features in a list to a tag. */
    protected void addAll( TagKey<ConfiguredFeature<?, ?>> tagKey, List<ResourceKey<ConfiguredFeature<?, ?>>> features ) {
        final TagAppender<ConfiguredFeature<?, ?>> tag = tag( tagKey );
        features.forEach( tag::add );
    }
    
    /** Add all tags to another. */
    @SafeVarargs
    protected final void addTags( TagKey<ConfiguredFeature<?, ?>> tagKey, TagKey<ConfiguredFeature<?, ?>>... tagsToAdd ) {
        final TagAppender<ConfiguredFeature<?, ?>> tag = tag( tagKey );
        for( TagKey<ConfiguredFeature<?, ?>> tagToAdd : tagsToAdd ) tag.addTag( tagToAdd );
    }
}