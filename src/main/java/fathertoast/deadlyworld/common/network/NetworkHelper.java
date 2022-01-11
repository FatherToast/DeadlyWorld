package fathertoast.deadlyworld.common.network;

import fathertoast.deadlyworld.common.network.message.S2CUpdateSpawnerRenderEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.PacketDistributor;

public class NetworkHelper {

    public static void updateSpawnerRenderEntity(EntityType<?> entityType, BlockPos pos) {
        PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new S2CUpdateSpawnerRenderEntity(entityType, pos));
    }
}
