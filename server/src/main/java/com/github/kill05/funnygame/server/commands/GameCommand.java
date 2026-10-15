package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.component.ComponentBuilder;
import com.github.kill05.funnygame.component.TextColor;
import com.github.kill05.funnygame.server.GameServer;
import com.github.kill05.funnygame.server.ServerPlayer;
import com.github.kill05.funnygame.server.commands.parsers.GameParser;
import com.github.kill05.funnygame.server.commands.parsers.GameTypeParser;
import com.github.kill05.funnygame.server.game.Game;
import com.github.kill05.funnygame.server.game.GameController;
import com.github.kill05.funnygame.server.game.GameType;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.github.kill05.funnygame.Messages.error;
import static com.github.kill05.funnygame.Messages.success;
import static com.github.kill05.funnygame.component.Component.newLine;
import static com.github.kill05.funnygame.component.Component.text;
import static com.github.kill05.funnygame.component.TextColor.BLACK;
import static com.github.kill05.funnygame.component.TextColor.WHITE;

public class GameCommand extends ServerCommand {

    public static final int GAMES_PER_PAGE = 10;
    public static final Comparator<Game> GAME_COMPARATOR = (g1, g2) -> {
        int compareName = g1.getClass().getName().compareTo(g2.getClass().getName());
        if (compareName != 0) {
            return compareName;
        }

        int compareId = g1.id().compareTo(g2.id());
        if (compareId != 0) {
            return compareId;
        }

        return Integer.compare(g1.getPlayers().size(), g2.getPlayers().size());
    };


    public GameCommand(@NotNull GameServer server) {
        super(server, "game");

        CloudKey<Game> gameKey = CloudKey.of("game", Game.class);
        CloudKey<GameType> gameTypeKey = CloudKey.of("type", GameType.class);
        CloudKey<Integer> pageKey = CloudKey.of("page", Integer.class);

        register(builder
                .senderType(ServerPlayer.class)
                .literal("start")
                .required(gameTypeKey, GameTypeParser.parser())
                .handler(ctx -> {
                    ServerPlayer sender = ctx.sender();
                    GameController controller = server.getGameController();
                    GameType type = ctx.get(gameTypeKey);

                    Game game = controller.startGame(type);
                    sender.sendMessage(success(String.format("Created new %s game. Type /game join %s to join.", type.typeId(), game.id())));
                }));

        register(builder
                .senderType(ServerPlayer.class)
                .literal("stop")
                .required(gameKey, GameParser.parser(server, true, true))
                .handler(ctx -> {
                    ServerPlayer sender = ctx.sender();
                    GameController controller = server.getGameController();
                    Game game = ctx.get(gameKey);

                    controller.stopGame(game);
                    sender.sendMessage(error(String.format("Stopped game '%s'.", game.id())));
                }));

        register(builder
                .senderType(ServerPlayer.class)
                .literal("join")
                .required(gameKey, GameParser.parser(server, true, true))
                .handler(ctx -> {
                    ServerPlayer sender = ctx.sender();
                    Game currentGame = sender.getCurrentGame();
                    Game joinGame = ctx.get(gameKey);

                    if (currentGame != null) {
                        sender.sendMessage(error("You are already playing another game. Type /game leave to leave."));
                        return;
                    }

                    joinGame.addPlayer(sender.getUsername());
                }));

        register(builder
                .senderType(ServerPlayer.class)
                .literal("leave")
                .handler(ctx -> {
                    ServerPlayer sender = ctx.sender();
                    Game currentGame = sender.getCurrentGame();

                    if (currentGame == null) {
                        sender.sendMessage(error("You are not currently in a game."));
                        return;
                    }

                    currentGame.removePlayer(sender.getUsername());
                }));

        register(builder
                .literal("list")
                .optional(pageKey, IntegerParser.integerParser(1))
                .handler(ctx -> {
                    GameController controller = server.getGameController();
                    CommandSender sender = ctx.sender();
                    int page = ctx.getOrDefault(pageKey, 1);

                    List<Game> games = new ArrayList<>(controller.getActiveGames());
                    games.sort(GAME_COMPARATOR);

                    if (games.isEmpty()) {
                        sender.sendMessage(error("There are no active games."));
                        return;
                    }

                    if (games.size() <= (page - 1) * GAMES_PER_PAGE) {
                        sender.sendMessage(error("This page is empty. Type /games list [page] with a lower page number to see other pages."));
                        return;
                    }

                    // Build text to send
                    ComponentBuilder text = text().append(
                            text().textColor(BLACK).backgroundColor(WHITE).append(
                                    text("Active Games: "),
                                    text(String.valueOf(games.size()), TextColor.GREEN),
                                    text(" | Page: " + page),
                                    newLine()
                            ).build()
                    );

                    int start = (page - 1) * GAMES_PER_PAGE;
                    int end = Math.min(page * GAMES_PER_PAGE, games.size());
                    for (int i = start; i < end; i++) {
                        Game game = games.get(i);
                        String format = String.format("%d) %s-%s, players: %d",
                                start + i + 1,
                                game.getDisplayName(),
                                game.id(),
                                game.getPlayers().size()
                        );

                        text.append(text(format), newLine());
                    }

                    text.append(text(String.format("Displaying games %d-%d", start + 1, end), BLACK, WHITE));
                    sender.sendMessage(text.build());
                }));
    }
}
