package com.github.kill05.funnygame.server;

import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.packet.ServerChatMessagePacket;
import com.github.kill05.funnygame.player.Player;
import com.github.kill05.funnygame.server.commands.ServerCommandSender;
import com.github.kill05.funnygame.server.connection.ServerPlayerConnection;
import com.github.kill05.funnygame.server.game.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerPlayer extends Player implements ServerCommandSender {

    private final GameServer server;

    public ServerPlayer(@NotNull GameServer server, @NotNull String username, @NotNull ServerPlayerConnection connection) {
        super(username, connection);
        this.server = server;
    }


    public void onJoin() {
        Game game = getCurrentGame();
        if (game != null) {
            game.onPlayerReconnect(this);
        }

        server.broadcast(getUsername() + " joined the lobby.");
    }

    public void onLeave() {
        Game game = getCurrentGame();
        if (game != null) {
            game.onPlayerDisconnect(this);
        }

        server.broadcast(getUsername() + " left the lobby.");
    }


    public @Nullable Game getCurrentGame() {
        return server.getGameController().getGame(this);
    }


    @Override
    public void sendMessage(@NotNull Component message) {
        sendPacket(new ServerChatMessagePacket(message));
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true; // todo: manage permission
    }

    @Override
    public GameServer server() {
        return server;
    }
}
