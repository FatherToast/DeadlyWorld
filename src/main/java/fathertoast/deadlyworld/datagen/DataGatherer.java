package fathertoast.deadlyworld.datagen;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.datagen.loot.DWLootTableProvider;
import fathertoast.deadlyworld.datagen.tags.DWBlockTagsProvider;
import fathertoast.deadlyworld.datagen.worldgen.DWFeatureProvider;
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
            .add(Registries.CONFIGURED_FEATURE, DWFeatureProvider::bootstrapConfigured)
            .add(Registries.PLACED_FEATURE, DWFeatureProvider::bootstrapPlaced);



    @SubscribeEvent
    public static void onGatherData( GatherDataEvent event ) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput packOutput = generator.getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        final ExistingFileHelper fileHelper = event.getExistingFileHelper();


        if( event.includeClient() ) {
            //            generator.addProvider( true, new SMBlockStateAndModelProvider( packOutput, fileHelper ) );
            //            generator.addProvider( true, new SMItemModelProvider( packOutput, fileHelper ) );
            //            for( Map.Entry<String, SMLanguageProvider.TranslationKey> entry : SMLanguageProvider.LANG_CODE_MAP.entrySet() ) {
            //                generator.addProvider( true, new SMLanguageProvider( packOutput, entry.getKey(), entry.getValue() ) );
            //            }
        }
        if( event.includeServer() ) {
            generator.addProvider( true, new DatapackBuiltinEntriesProvider( packOutput, lookupProvider, BUILDER, Set.of( DeadlyWorld.MOD_ID ) ) );
            generator.addProvider( true, new DWLootTableProvider( packOutput ) );
            generator.addProvider( true, new DWBlockTagsProvider( packOutput, lookupProvider, fileHelper ) );
        }
    }
}