package fathertoast.deadlyworld.common.network.message;

import fathertoast.deadlyworld.common.network.work.ClientWork;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CUpdateSpawnerRenderEntity {

    public final EntityType<?> entityType;
    public final BlockPos pos;

    public S2CUpdateSpawnerRenderEntity(EntityType<?> entityType, BlockPos spawnerPos) {
        this.entityType = entityType;
        this.pos = spawnerPos;
    }

    public static void handle(S2CUpdateSpawnerRenderEntity message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> ClientWork.handleSpawnerRenderEntityUpdate(message));
        }
        context.setPacketHandled(true);
    }

    public static S2CUpdateSpawnerRenderEntity decode(PacketBuffer buffer) {
        return new S2CUpdateSpawnerRenderEntity(buffer.readRegistryIdSafe(EntityType.class), buffer.readBlockPos());
    }

    public static void encode(S2CUpdateSpawnerRenderEntity message, PacketBuffer buffer) {
        buffer.writeRegistryId(message.entityType);
        buffer.writeBlockPos(message.pos);
    }
}
