package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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
        final Block[] spawners = blockArray( DWBlocks.SPAWNERS );
        tag( BlockTags.MINEABLE_WITH_PICKAXE ).add( spawners );
        tag( BlockTags.NEEDS_IRON_TOOL ).add( spawners );
    }
    
    /** @return Creates a new array of all blocks extracted from a list of block registry objects. */
    private static <T extends Block> Block[] blockArray( List<RegistryObject<T>> blockRegObjects ) {
        final Block[] blocks = new Block[blockRegObjects.size()];
        
        for( int i = 0; i < blocks.length; i++ ) {
            blocks[i] = blockRegObjects.get( i ).get();
        }
        return blocks;
    }
}