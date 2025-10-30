package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DWBlockTagsProvider extends BlockTagsProvider {
    public DWBlockTagsProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper ) {
        super( output, lookupProvider, DeadlyWorld.MOD_ID, existingFileHelper );
    }
    
    @Override
    public void addTags( HolderLookup.Provider holderLookup ) {
        addAll( DWTags.Blocks.SPAWNERS, DWBlocks.SPAWNERS );
        addAll( DWTags.Blocks.FLOOR_TRAPS, DWBlocks.FLOOR_TRAPS );
        addAll( DWTags.Blocks.TOWER_DISPENSERS, DWBlocks.TOWER_DISPENSERS );
        addAll( DWTags.Blocks.SEA_MINES, DWBlocks.SEA_MINES );
        addAll( DWTags.Blocks.SPIKE_TRAPS, DWBlocks.SPIKE_TRAPS );

        addTags( BlockTags.MINEABLE_WITH_PICKAXE,
                DWTags.Blocks.SPAWNERS.get(),
                DWTags.Blocks.FLOOR_TRAPS.get(),
                DWTags.Blocks.TOWER_DISPENSERS.get(),
                DWTags.Blocks.SPIKE_TRAPS.get()
        );
        addTags( BlockTags.NEEDS_IRON_TOOL, DWTags.Blocks.SPAWNERS.get() );
    }
    
    /** Add all blocks in a list to a tag. */
    protected <T extends Block> void addAll( DWTags.BlockWithItem tagKey, List<RegistryObject<T>> blocks ) {
        addAll( tagKey.get(), blocks );
    }
    
    /** Add all blocks in a list to a tag. */
    protected <T extends Block> void addAll( TagKey<Block> tagKey, List<RegistryObject<T>> blocks ) {
        final IntrinsicTagAppender<Block> tag = tag( tagKey );
        blocks.forEach( ( regObj ) -> tag.add( regObj.get() ) );
    }
    
    /** Add all tags to another. */
    @SafeVarargs
    protected final void addTags( TagKey<Block> tagKey, TagKey<Block>... tagsToAdd ) {
        final IntrinsicTagAppender<Block> tag = tag( tagKey );
        for( TagKey<Block> tagToAdd : tagsToAdd ) tag.addTag( tagToAdd );
    }
}