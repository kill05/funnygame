package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.Player;
import com.github.kill05.funnygame.connection.PlayerConnection;
import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.server.Server;
import com.github.kill05.funnygame.server.ServerConnection;
import com.github.kill05.funnygame.server.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.github.kill05.funnygame.Ansi.GREEN;
import static com.github.kill05.funnygame.Ansi.RESET;
import static com.github.kill05.funnygame.Unicode.CIRCLE;

public class ListCommand extends ServerCommand {

    public ListCommand(@NotNull Server server) {
        super(server, "list", "players");

        register(builder
                .handler(ctx -> {
                    ServerConnection connection = server.getConnection();
                    Map<PlayerConnection, ServerPlayer> players = connection.getOnlinePlayers();

                    CommandSender sender = ctx.sender();
                    sender.sendMessage("Online players: " + GREEN + players.size() + RESET);

                    for (Player value : players.values()) {
                        sender.sendMessage(CIRCLE + value.getUsername());
                    }
                }));
    }
}
