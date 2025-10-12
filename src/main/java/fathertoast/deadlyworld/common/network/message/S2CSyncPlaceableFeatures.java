package fathertoast.deadlyworld.common.network.message;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.network.work.ClientWork;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record S2CSyncPlaceableFeatures(List<String> featureKeys) {
    
    public static void handle( S2CSyncPlaceableFeatures message, Supplier<NetworkEvent.Context> contextSupplier ) {
        NetworkEvent.Context context = contextSupplier.get();
        
        if( context.getDirection().getReceptionSide().isClient() ) {
            context.enqueueWork( () -> ClientWork.handleSyncPlaceableFeatures( message ) );
        }
        context.setPacketHandled( true );
    }
    
    public static S2CSyncPlaceableFeatures decode( FriendlyByteBuf buffer ) {
        List<String> featureKeys = new ArrayList<>();
        try {
            int size = buffer.readShort();
            for( int i = 0; i < size; i++ ) featureKeys.add( buffer.readUtf() );
        }
        catch( IndexOutOfBoundsException | DecoderException ex ) {
            DeadlyWorld.LOG.error( ex );
            ex.printStackTrace();
        }
        return new S2CSyncPlaceableFeatures( featureKeys );
    }
    
    public static void encode( S2CSyncPlaceableFeatures message, FriendlyByteBuf buffer ) {
        buffer.writeShort( message.featureKeys.size() );
        for( String key : message.featureKeys ) buffer.writeUtf( key );
    }
}