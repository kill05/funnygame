package com.github.kill05.funnygame.packet;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Sent by the client to notify the server that it sent a chat message
 *
 * @param rawMessage the raw message
 */
public record ClientChatMessagePacket(String rawMessage) implements Packet {

    public ClientChatMessagePacket(@NotNull PacketInputStream in) throws IOException {
        this(in.readUTF());
    }

    @Override
    public void serialize(@NotNull PacketOutputStream out) throws IOException {
        out.writeUTF(rawMessage);
    }
}
