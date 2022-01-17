package fathertoast.deadlyworld.datagen;

import fathertoast.deadlyworld.datagen.loot.DWLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class DataGatherer {

    public static void onGatherData(GatherDataEvent event) {
        final DataGenerator generator = event.getGenerator();
        
        if( event.includeServer() ) {
            generator.addProvider( new DWLootTableProvider( generator ) );
        }
    }
}