package com.github.kill05.funnygame.packet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Sent by the server to the client to forcefully disconnect them from the server
 * or from the client to the server to notify the server that the player disconnected
 *
 * @param message
 */
public record DisconnectPacket(@Nullable String message) implements Packet {

    public DisconnectPacket(@NotNull PacketInputStream in) throws IOException {
        boolean hasMessage = in.readBoolean();
        String message = hasMessage ? in.readUTF() : null;
        this(message);
    }

    @Override
    public void serialize(@NotNull PacketOutputStream buf) throws IOException {
        boolean hasMessage = message != null;
        buf.writeBoolean(hasMessage);

        if (hasMessage) {
            buf.writeUTF(message);
        }
    }
}
