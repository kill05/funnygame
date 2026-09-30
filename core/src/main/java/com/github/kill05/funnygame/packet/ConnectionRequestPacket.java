package com.github.kill05.funnygame.packet;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Packet sent by the client to the server after the connection is established.
 * The client will then wait for the server to reply with a {@link ConnectionAcceptedPacket}.
 * After this exchange is completed, the
 *
 * @param username
 * @param protocolVersion
 */
public record ConnectionRequestPacket(@NotNull String username, int protocolVersion) implements Packet {

    public ConnectionRequestPacket(@NotNull PacketInputStream in) throws IOException {
        this(in.readUTF(), in.readInt());
    }

    @Override
    public void serialize(@NotNull PacketOutputStream out) throws IOException {
        out.writeUTF(username);
        out.writeInt(protocolVersion);
    }
}