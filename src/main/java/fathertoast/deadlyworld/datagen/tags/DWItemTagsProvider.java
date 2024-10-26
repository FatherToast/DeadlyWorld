package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DWItemTagsProvider extends ItemTagsProvider {
    public DWItemTagsProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper ) {
        super( output, lookupProvider, blockTagProvider, DeadlyWorld.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider holderLookup ) {
        copy( DWTags.Blocks.SPAWNERS );
    }
    
    /** Add all blocks in a list to a tag. */
    protected <T extends Item> void addAll( TagKey<Item> tagKey, List<RegistryObject<T>> blocks ) {
        final IntrinsicTagAppender<Item> tag = tag( tagKey );
        blocks.forEach( ( regObj ) -> tag.add( regObj.get() ) );
    }
    
    /** Add all tags to another. */
    @SafeVarargs
    protected final void addTags( TagKey<Item> tagKey, TagKey<Item>... tagsToAdd ) {
        final IntrinsicTagAppender<Item> tag = tag( tagKey );
        for( TagKey<Item> tagToAdd : tagsToAdd ) tag.addTag( tagToAdd );
    }
    
    /** Makes the item tag reflect the block tag. */
    protected void copy( DWTags.BlockWithItem blockWithItem ) { copy( blockWithItem.blockTag(), blockWithItem.itemTag() ); }
}