package fathertoast.deadlyworld.datagen.tags;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.core.registry.DWEntities;
import fathertoast.deadlyworld.common.core.registry.DWTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DWEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public DWEntityTypeTagsProvider( PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper ) {
        super( output, lookupProvider, DeadlyWorld.MOD_ID, existingFileHelper );
    }
    
    @Override
    protected void addTags( HolderLookup.Provider holderLookup ) {
        // Minis
        tag( DWTags.EntityTypes.MINI ).add(
                DWEntities.MINI_CREEPER.get(), DWEntities.MICRO_GHAST.get(),
                DWEntities.MINI_SKELETON.get(), DWEntities.MINI_SPIDER.get(),
                DWEntities.MINI_ZOMBIE.get()
        );
        
        tag( DWTags.EntityTypes.CREEPERS ).add( DWEntities.MINI_CREEPER.get() );
        tag( DWTags.EntityTypes.GHASTS ).add( DWEntities.MICRO_GHAST.get() );
        tag( EntityTypeTags.SKELETONS ).add( DWEntities.MINI_SKELETON.get() );
        tag( DWTags.EntityTypes.SPIDERS ).add( DWEntities.MINI_SPIDER.get() );
        tag( DWTags.EntityTypes.ZOMBIES ).add( DWEntities.MINI_ZOMBIE.get() );

        // Mimics
        tag( DWTags.EntityTypes.MIMIC ).add(
                DWEntities.CHEST_MIMIC.get(), DWEntities.JUKEBOX_MIMIC.get(),
                DWEntities.SPAWNER_MIMIC.get(), DWEntities.MINI_SPAWNER_MIMIC.get()
        );
        
        // Projectiles
        tag( EntityTypeTags.ARROWS ).add( DWEntities.MINI_ARROW.get() );
        tag( DWTags.EntityTypes.FIREBALLS ).add( DWEntities.MICRO_FIREBALL.get() );
        tag( EntityTypeTags.IMPACT_PROJECTILES ).addTag( DWTags.EntityTypes.FIREBALLS );

        // Freeze immune
        tag( EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES ).add(
                DWEntities.SPAWNER_MIMIC.get(), DWEntities.MINI_SPAWNER_MIMIC.get()
        );
    }
}