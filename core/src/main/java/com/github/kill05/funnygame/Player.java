package com.github.kill05.funnygame;

import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.connection.PlayerConnection;
import com.github.kill05.funnygame.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class Player implements CommandSender {

    private final UUID uuid;
    private final String username;
    private final PlayerConnection connection;

    public Player(@NotNull String username, @NotNull PlayerConnection connection) {
        this.username = username;
        this.connection = connection;
        this.uuid = new UUID(0, username.hashCode());
    }


    public void sendPacket(@NotNull Packet packet) {
        connection.sendPacket(packet);
    }


    public PlayerConnection getConnection() {
        return connection;
    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }
}
