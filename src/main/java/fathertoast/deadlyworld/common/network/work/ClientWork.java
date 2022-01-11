package fathertoast.deadlyworld.common.network.work;

import fathertoast.deadlyworld.common.network.message.S2CUpdateSpawnerRenderEntity;
import fathertoast.deadlyworld.common.tile.spawner.DeadlySpawnerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ClientWork {

    public static void handleSpawnerRenderEntityUpdate(S2CUpdateSpawnerRenderEntity message) {
        EntityType<?> entityType = message.entityType;
        BlockPos pos = message.pos;
        ClientWorld world = Minecraft.getInstance().level;

        if (world != null && world.isAreaLoaded(pos, 1)) {
            TileEntity tileEntity = world.getBlockEntity(pos);

            if (tileEntity instanceof DeadlySpawnerTileEntity) {
                DeadlySpawnerTileEntity deadlySpawner = (DeadlySpawnerTileEntity) tileEntity;
                deadlySpawner.setRenderEntity(entityType);
            }
        }
    }
}
