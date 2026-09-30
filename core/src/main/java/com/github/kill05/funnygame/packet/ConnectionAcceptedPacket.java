package com.github.kill05.funnygame.packet;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Sent by the server to the client to confirm their connection has been accepted.
 * If the server wants to refuse the connection, it will just send a {@link DisconnectPacket}.
 */
public record ConnectionAcceptedPacket() implements Packet {

    // todo: add some server metadata to send to the player
    public ConnectionAcceptedPacket(@NotNull PacketInputStream in) {
        this();
    }

    @Override
    public void serialize(@NotNull PacketOutputStream out) throws IOException {

    }
}
