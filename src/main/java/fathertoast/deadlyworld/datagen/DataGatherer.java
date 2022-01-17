package fathertoast.deadlyworld.datagen;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.datagen.loot.DWLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@SuppressWarnings( "unused" )
@Mod.EventBusSubscriber( modid = DeadlyWorld.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class DataGatherer {
    
    @SubscribeEvent
    public void onGatherData( GatherDataEvent event ) {
        final DataGenerator generator = event.getGenerator();
        
        if( event.includeServer() ) {
            generator.addProvider( new DWLootTableProvider( generator ) );
        }
    }
}