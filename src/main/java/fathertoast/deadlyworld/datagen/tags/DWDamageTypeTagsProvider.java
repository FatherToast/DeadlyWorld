package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.util.DWDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DWDamageTypeTagsProvider extends DamageTypeTagsProvider {
    
    public DWDamageTypeTagsProvider( PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper fileHelper ) {
        super( packOutput, lookupProvider, DeadlyWorld.MOD_ID, fileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider provider ) {
        tag( DamageTypeTags.BYPASSES_SHIELD ).addOptional( DWDamageTypes.SPIKE_TRAP.location() );
        tag( DamageTypeTags.ALWAYS_TRIGGERS_SILVERFISH ).addOptional( DWDamageTypes.TRIGGER_SILVERFISH.location() );
    }
}