package fathertoast.deadlyworld.common.network;

import fathertoast.deadlyworld.common.core.DeadlyWorld;
import fathertoast.deadlyworld.common.network.message.S2CSetSpawnerMimicDE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PacketHandler {

    private static final String PROTOCOL_NAME = "DEADLYWORLD";
    /** The network channel our mod will be
     *  using when sending messages. */
    public static final SimpleChannel CHANNEL = createChannel();

    private static int messageIndex;

    private static SimpleChannel createChannel() {
        return NetworkRegistry.ChannelBuilder
                .named( DeadlyWorld.resourceLoc( "channel" ) )
                .serverAcceptedVersions( PROTOCOL_NAME::equals )
                .clientAcceptedVersions( PROTOCOL_NAME::equals )
                .networkProtocolVersion( () -> PROTOCOL_NAME )
                .simpleChannel();
    }

    public void registerMessages() {
        // Server -> Client
        registerMessage( S2CSetSpawnerMimicDE.class, S2CSetSpawnerMimicDE::encode, S2CSetSpawnerMimicDE::decode, S2CSetSpawnerMimicDE::handle );

        // Client -> Server
    }

    public <MSG> void registerMessage( Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder,
                                       BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer ) {

        CHANNEL.registerMessage( messageIndex++, messageType, encoder, decoder, messageConsumer, Optional.empty() );
    }

    /**
     * Sends the specified message to the client.
     *
     * @param message The message to send to the client.
     * @param player The player client that should receive this message.
     * @param <MSG> Packet type.
     */
    public static <MSG> void sendToClient( MSG message, ServerPlayer player ) {
        CHANNEL.sendTo( message, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT );
    }
}
