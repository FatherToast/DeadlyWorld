package fathertoast.deadlyworld.common.network.work;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.entity.SpawnerMimic;
import fathertoast.deadlyworld.common.network.message.S2CSetSpawnerMimicDE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

public class ClientWork {

    public static void handleSetSpawnerMimicDE( S2CSetSpawnerMimicDE message ) {
        ClientLevel level = Minecraft.getInstance().level;

        if ( level == null ) return;

        Entity entity = level.getEntity( message.entityId );

        if ( entity instanceof SpawnerMimic spawnerMimic ) {
            try {
                spawnerMimic.getSpawner().getOrCreateDisplayEntity(level, level.random, entity.blockPosition());
            }
            catch ( Exception e ) {
                DeadlyWorld.LOG.error( "Failed to update display entity for spawner mimic at position {}!",
                        entity.blockPosition().toString() );
            }
        }
    }
}
