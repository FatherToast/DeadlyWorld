package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.api.DWRegistries;
import fathertoast.deadlyworld.api.DecoyType;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWDecoyTypes;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DWDecoyTagsProvider extends TagsProvider<DecoyType> {
    
    public DWDecoyTagsProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper ) {
        super( output, DWRegistries.DECOY_TYPE_REGISTRY.get().getRegistryKey(), lookupProvider, DeadlyWorld.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider holderLookup ) {
        addAll( DWTags.DecoyTypes.OVERWORLD, DWDecoyTypes.ENTRIES_FOR_TAGS.get( DWTags.DecoyTypes.OVERWORLD ) );
        addAll( DWTags.DecoyTypes.THE_NETHER, DWDecoyTypes.ENTRIES_FOR_TAGS.get( DWTags.DecoyTypes.THE_NETHER ) );
        addAll( DWTags.DecoyTypes.ANY_DIMENSION, DWDecoyTypes.ENTRIES_FOR_TAGS.get( DWTags.DecoyTypes.ANY_DIMENSION ) );
    }
    
    /** Add all features in a list to a tag. */
    protected void addAll( TagKey<DecoyType> tagKey, List<RegistryObject<DecoyType>> decoyTypes ) {
        final TagAppender<DecoyType> tag = tag( tagKey );
        decoyTypes.forEach( ( regObj ) -> tag.add( ResourceKey.create( DWRegistries.DECOY_TYPE_REGISTRY.get().getRegistryKey(), regObj.getId() ) ) );
    }
    
    /** Add all tags to another. */
    @SafeVarargs
    protected final void addTags( TagKey<DecoyType> tagKey, TagKey<DecoyType>... tagsToAdd ) {
        final TagAppender<DecoyType> tag = tag( tagKey );
        for( TagKey<DecoyType> tagToAdd : tagsToAdd ) tag.addTag( tagToAdd );
    }
}
