package fathertoast.deadlyworld.datagen;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.datagen.loot.DWLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DataGatherer {
    
    @SubscribeEvent
    public static void onGatherData( GatherDataEvent event ) {
        final DataGenerator generator = event.getGenerator();
        final PackOutput packOutput = generator.getPackOutput();
        
        if( event.includeClient() ) {
            //            generator.addProvider( true, new SMBlockStateAndModelProvider( packOutput, fileHelper ) );
            //            generator.addProvider( true, new SMItemModelProvider( packOutput, fileHelper ) );
            //            for( Map.Entry<String, SMLanguageProvider.TranslationKey> entry : SMLanguageProvider.LANG_CODE_MAP.entrySet() ) {
            //                generator.addProvider( true, new SMLanguageProvider( packOutput, entry.getKey(), entry.getValue() ) );
            //            }
        }
        if( event.includeServer() ) {
            generator.addProvider( true, new DWLootTableProvider( packOutput ) );
        }
    }
}