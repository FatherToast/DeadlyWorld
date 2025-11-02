package fathertoast.deadlyworld.common.network.work;

import fathertoast.deadlyworld.client.ClientRegister;
import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import fathertoast.deadlyworld.common.network.message.S2CSyncPlaceableFeatures;
import fathertoast.deadlyworld.common.network.message.S2CUpdateSpawnerMimic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

public class ClientWork {
    
    public static void handleUpdateSpawnerMimic( S2CUpdateSpawnerMimic message ) {
        ClientLevel level = Minecraft.getInstance().level;
        if( level == null || message.updateTag() == null ) return;
        
        if( level.getEntity( message.entityId() ) instanceof SpawnerMimic spawnerMimic && spawnerMimic.getSpawner() != null ) {
            try {
                spawnerMimic.getSpawner().load( level, BlockPos.ZERO, message.updateTag() );
            }
            catch( Exception ex ) {
                DeadlyWorld.LOG.error( "Failed server-client sync for spawner mimic at position {}!",
                        spawnerMimic.blockPosition().toString() );
            }
        }
    }
    
    public static void handleSyncPlaceableFeatures( S2CSyncPlaceableFeatures message ) {
        ClientRegister.setFeatureKeys( message.featureKeys() );
    }
}