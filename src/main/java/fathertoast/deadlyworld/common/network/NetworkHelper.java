package fathertoast.deadlyworld.common.network;

import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import fathertoast.deadlyworld.common.network.message.S2CSetSpawnerMimicDE;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class NetworkHelper {


    public static void updateSpawnerMimic( @Nonnull ServerLevel level, @Nonnull SpawnerMimic spawnerMimic ) {
        Objects.requireNonNull( level );
        Objects.requireNonNull( spawnerMimic );

        S2CSetSpawnerMimicDE message = new S2CSetSpawnerMimicDE( spawnerMimic.getId(), spawnerMimic.getSpawner().getRemainingSpawns() );

        for ( ServerPlayer player : level.players() ) {
            PacketHandler.sendToClient( message, player );
        }
    }

    private NetworkHelper() {}
}
