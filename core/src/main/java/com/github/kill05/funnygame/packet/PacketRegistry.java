package com.github.kill05.funnygame.packet;

import com.github.kill05.funnygame.connection.PlayerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PacketRegistry {

    private static final List<RegisteredPacket<?>> ID_PACKET_LIST = new ArrayList<>();
    private static final Map<Class<? extends Packet>, RegisteredPacket<?>> TYPE_PACKET_MAP = new HashMap<>();

    static {
        register(
                ConnectionRequestPacket.class,
                ConnectionRequestPacket::new,
                PlayerConnection::handleConnectionRequestPacket
        );

        register(
                ConnectionAcceptedPacket.class,
                ConnectionAcceptedPacket::new,
                PlayerConnection::handleConnectionAcceptedPacket
        );

        register(
                KeepAlivePacket.class,
                KeepAlivePacket::new,
                PlayerConnection::handleKeepAlivePacket
        );

        register(
                DisconnectPacket.class,
                DisconnectPacket::new,
                PlayerConnection::handleDisconnectPacket
        );

        register(
                ClientChatMessagePacket.class,
                ClientChatMessagePacket::new,
                PlayerConnection::handleClientChatMessagePacket
        );

        register(
                ServerChatMessagePacket.class,
                ServerChatMessagePacket::new,
                PlayerConnection::handleServerChatMessagePacket
        );
    }


    private static <T extends Packet> void register(
            @NotNull Class<T> clazz,
            @NotNull PacketDeserializer<T> deserializer,
            @NotNull PacketHandler<T> handler
    ) {
        int id = ID_PACKET_LIST.size();
        RegisteredPacket<T> packet = new RegisteredPacket<>(id, clazz, deserializer, handler);

        ID_PACKET_LIST.add(packet);
        if (TYPE_PACKET_MAP.put(clazz, packet) != null) {
            throw new IllegalArgumentException("Duplicate packet registration: " + packet + ".");
        }
    }


    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends Packet> RegisteredPacket<T> getRegistered(Class<T> clazz) {
        return (RegisteredPacket<T>) TYPE_PACKET_MAP.get(clazz);
    }

    @Nullable
    public static RegisteredPacket<?> getRegistered(int id) {
        if (id < 0 || id >= ID_PACKET_LIST.size()) {
            return null;
        }

        return ID_PACKET_LIST.get(id);
    }

}
