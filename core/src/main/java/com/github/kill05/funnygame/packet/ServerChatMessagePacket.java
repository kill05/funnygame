package com.github.kill05.funnygame.packet;

import com.github.kill05.funnygame.component.Component;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Sent by the server to send a chat message to a client.
 *
 * @param message
 */
public record ServerChatMessagePacket(Component message) implements Packet {

    public ServerChatMessagePacket(@NotNull PacketInputStream in) throws IOException {
        this(in.readComponent());
    }

    @Override
    public void serialize(@NotNull PacketOutputStream out) throws IOException {
        out.writeComponent(message);
    }
}
