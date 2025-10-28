package fathertoast.deadlyworld.datagen;

import fathertoast.deadlyworld.common.config.Config;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.util.DWDamageTypes;
import fathertoast.deadlyworld.datagen.lang.DWLangProvider;
import fathertoast.deadlyworld.datagen.loot.DWLootModProvider;
import fathertoast.deadlyworld.datagen.loot.DWLootTableProvider;
import fathertoast.deadlyworld.datagen.model.DWModelProvider;
import fathertoast.deadlyworld.datagen.tags.*;
import fathertoast.deadlyworld.datagen.worldgen.DWConfiguredFeatureProvider;
import fathertoast.deadlyworld.datagen.worldgen.DWPlacedFeatureProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber( modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DataGatherer {
    
    /** Data provider that generates registry entries from supported registry types. */
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add( Registries.DAMAGE_TYPE, DWDamageTypes::bootstrap )
            .add( Registries.CONFIGURED_FEATURE, DWConfiguredFeatureProvider::bootstrap )
            .add( Registries.PLACED_FEATURE, DWPlacedFeatureProvider::bootstrap );
    
    @SubscribeEvent
    public static void onGatherData( GatherDataEvent event ) {
        // Ensure config is loaded before doing anything.
        // Many common mod lifecycle events are not fired when running data gen.
        Config.initialize();
        
        final DataGenerator generator = event.getGenerator();
        final PackOutput packOutput = generator.getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        final ExistingFileHelper fileHelper = event.getExistingFileHelper();
        
        if( event.includeClient() ) {
            generator.addProvider( true, new DWModelProvider( packOutput, fileHelper ) );
            generator.addProvider( true, new DWLangProvider( packOutput ) );
        }
        if( event.includeServer() ) {
            DatapackBuiltinEntriesProvider builtInProvider =
                    generator.addProvider( true, new DatapackBuiltinEntriesProvider( packOutput, lookupProvider, BUILDER, Set.of( DeadlyWorld.MOD_ID ) ) );
            
            generator.addProvider( true, new DWLootTableProvider( packOutput ) );
            generator.addProvider( true, new DWLootModProvider( packOutput ) );
            
            // Tags
            DWBlockTagsProvider blockTags =
                    generator.addProvider( true, new DWBlockTagsProvider( packOutput, lookupProvider, fileHelper ) );
            generator.addProvider( true, new DWItemTagsProvider( packOutput, lookupProvider, blockTags.contentsGetter(), fileHelper ) );
            generator.addProvider( true, new DWEntityTypeTagsProvider( packOutput, lookupProvider, fileHelper ) );
            generator.addProvider( true, new DWConfiguredFeatureTagsProvider( packOutput, builtInProvider.getRegistryProvider(), fileHelper ) );
            generator.addProvider( true, new DWPlacedFeatureTagsProvider( packOutput, builtInProvider.getRegistryProvider(), fileHelper ) );
            generator.addProvider( true, new DWDecoyTagsProvider( packOutput, builtInProvider.getRegistryProvider(), fileHelper ) );
            generator.addProvider( true, new DWFluidTagsProvider( packOutput, lookupProvider, fileHelper ) );
            generator.addProvider( true, new DWDamageTypeTagsProvider( packOutput, lookupProvider, fileHelper ) );
        }
    }
}