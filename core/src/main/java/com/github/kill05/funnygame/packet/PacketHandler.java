package com.github.kill05.funnygame.packet;

import com.github.kill05.funnygame.connection.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@FunctionalInterface
public interface PacketHandler<T> {

    void handlePacket(@NotNull PlayerConnection connection, @NotNull T packet) throws IOException;

}
