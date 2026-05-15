package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DWFluidTagsProvider extends FluidTagsProvider {
    
    public DWFluidTagsProvider( PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper fileHelper ) {
        super( packOutput, lookupProvider, DeadlyWorld.MOD_ID, fileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider provider ) {
        tag( FluidTags.LAVA ).add(
                DWFluids.RUNNY_LAVA_SOURCE.get(),
                DWFluids.RUNNY_LAVA_FLOWING.get()
        );
    }
}
