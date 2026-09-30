package com.github.kill05.funnygame.server;

import com.github.kill05.funnygame.Player;
import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.packet.ServerChatMessagePacket;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public class ServerPlayer extends Player {

    private final Server server;

    public ServerPlayer(@NotNull Server server, @NotNull String username, @NotNull ServerPlayerConnection connection) {
        super(username, connection);
        this.server = server;
    }


    public void onJoin() {
        server.broadcast(getUsername() + " joined the lobby.");
    }

    public void onLeave() {
        server.broadcast(getUsername() + " left the lobby.");
    }


    @Override
    public void sendMessage(@NotNull Component message) {
        sendPacket(new ServerChatMessagePacket(message));
    }

    @Override
    public CommandManager<CommandSender> getCommandManager() {
        return server.getCommandManager();
    }

    public Server getServer() {
        return server;
    }
}
