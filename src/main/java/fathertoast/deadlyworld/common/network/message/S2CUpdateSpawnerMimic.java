package fathertoast.deadlyworld.common.network.message;

import fathertoast.deadlyworld.common.network.work.ClientWork;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public record S2CUpdateSpawnerMimic(int entityId, @Nullable CompoundTag updateTag) {
    
    public static void handle( S2CUpdateSpawnerMimic message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> ClientWork.handleUpdateSpawnerMimic( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static S2CUpdateSpawnerMimic decode( FriendlyByteBuf buffer ) {
        return new S2CUpdateSpawnerMimic( buffer.readInt(), buffer.readNbt() );
    }
    
    public static void encode( S2CUpdateSpawnerMimic message, FriendlyByteBuf buffer ) {
        buffer.writeInt( message.entityId );
        buffer.writeNbt( message.updateTag );
    }
}