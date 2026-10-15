package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.player.Player;
import com.github.kill05.funnygame.connection.PlayerConnection;
import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.server.GameServer;
import com.github.kill05.funnygame.server.connection.ServerConnection;
import com.github.kill05.funnygame.server.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.github.kill05.funnygame.Ansi.GREEN;
import static com.github.kill05.funnygame.Ansi.RESET;
import static com.github.kill05.funnygame.Unicode.CIRCLE;

public class ListCommand extends ServerCommand {

    public ListCommand(@NotNull GameServer server) {
        super(server, "list", "players");

        register(builder
                .handler(ctx -> {
                    ServerConnection connection = server.getConnection();
                    Map<PlayerConnection, ServerPlayer> players = connection.getConnectedPlayerMap();

                    CommandSender sender = ctx.sender();
                    sender.sendMessage("Online players: " + GREEN + players.size() + RESET);

                    for (Player value : players.values()) {
                        sender.sendMessage(CIRCLE + value.getUsername());
                    }
                }));
    }
}
