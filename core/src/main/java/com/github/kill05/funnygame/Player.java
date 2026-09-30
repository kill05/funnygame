package com.github.kill05.funnygame;

import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.connection.PlayerConnection;
import com.github.kill05.funnygame.packet.Packet;
import org.jetbrains.annotations.NotNull;

public abstract class Player implements CommandSender {

    private final String username;
    private final PlayerConnection connection;

    public Player(@NotNull String username, @NotNull PlayerConnection connection) {
        this.username = username;
        this.connection = connection;
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
}
