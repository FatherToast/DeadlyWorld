package fathertoast.deadlyworld.common.network;

import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import fathertoast.deadlyworld.common.item.FeaturePlacerItem;
import fathertoast.deadlyworld.common.network.message.S2CSetSpawnerMimicDE;
import fathertoast.deadlyworld.common.network.message.S2CSyncPlaceableFeatures;
import fathertoast.deadlyworld.common.world.logic.ProgressiveDelaySpawner;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class NetworkHelper {
    
    public static void updateSpawnerMimic( ServerLevel level, SpawnerMimic spawnerMimic ) {
        ProgressiveDelaySpawner spawnerLogic = spawnerMimic.getSpawner();
        S2CSetSpawnerMimicDE message = new S2CSetSpawnerMimicDE( spawnerMimic.getId(),
                spawnerLogic == null ? 0 : spawnerLogic.getRemainingSpawns() );
        
        for( ServerPlayer player : level.players() ) {
            PacketHandler.sendToClient( message, player );
        }
    }
    
    public static void syncPlaceableFeatures( List<ServerPlayer> players ) {
        S2CSyncPlaceableFeatures message = null;
        
        for( ServerPlayer player : players ) {
            if( message == null ) {
                message = new S2CSyncPlaceableFeatures( FeaturePlacerItem.buildFeatureKeysList( player.serverLevel() ) );
            }
            PacketHandler.sendToClient( message, player );
        }
    }
    
    private NetworkHelper() { }
}