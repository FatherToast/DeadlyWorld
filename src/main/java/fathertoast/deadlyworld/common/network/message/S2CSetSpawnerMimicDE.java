package fathertoast.deadlyworld.common.network.message;

import fathertoast.deadlyworld.common.network.work.ClientWork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSetSpawnerMimicDE {

    public final int entityId;
    public final int remainingSpawns;

    public S2CSetSpawnerMimicDE( int entityId, int remainingSpawns ) {
        this.entityId = entityId;
        this.remainingSpawns = remainingSpawns;
    }

    public static void handle( S2CSetSpawnerMimicDE message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();

        if ( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork(() -> ClientWork.handleSetSpawnerMimicDE( message ) );
        }
        context.setPacketHandled( true );
    }

    public static S2CSetSpawnerMimicDE decode( FriendlyByteBuf buffer ) {
        return new S2CSetSpawnerMimicDE( buffer.readInt(), buffer.readInt() );
    }

    public static void encode( S2CSetSpawnerMimicDE message, FriendlyByteBuf buffer ) {
        buffer.writeInt( message.entityId );
        buffer.writeInt( message.remainingSpawns );
    }
}
