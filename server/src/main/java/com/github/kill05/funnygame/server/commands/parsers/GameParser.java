package com.github.kill05.funnygame.server.commands.parsers;

import com.github.kill05.funnygame.server.GameServer;
import com.github.kill05.funnygame.server.game.Game;
import com.github.kill05.funnygame.server.game.GameController;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;

import static org.incendo.cloud.parser.ArgumentParseResult.failure;
import static org.incendo.cloud.parser.ArgumentParseResult.success;

public class GameParser<C> implements ArgumentParser<C, Game> {

    public static <C> ParserDescriptor<C, Game> parser(GameServer server, boolean byPlayer, boolean byId) {
        return ParserDescriptor.of(new GameParser<>(server, byPlayer, byId), Game.class);
    }

    private final GameServer server;
    private final boolean byPlayer;
    private final boolean byId;

    /// @param byPlayer true to allow the parser to parse games by players that are playing that game
    /// @param byId     true to allow the parser to parse games by game id
    public GameParser(GameServer server, boolean byPlayer, boolean byId) {
        if (!byId && !byPlayer) {
            throw new IllegalArgumentException("byPlayer and byCode cannot be both false");
        }

        this.server = server;
        this.byPlayer = byPlayer;
        this.byId = byId;
    }


    @Override
    public @NonNull ArgumentParseResult<Game> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        String in = input.readString();
        GameController controller = server.getGameController();

        if (byId) {
            Game game = controller.getGame(in);

            if (game != null) {
                return success(game);
            }

            if (!byPlayer) {
                return failure(new IllegalArgumentException("Invalid game id: " + in));
            }
        }

        if (byPlayer) {
            for (Game game : controller.getActiveGames()) {
                if (game.getPlayers().contains(in)) {
                    return success(game);
                }
            }

            if (!byId) {
                return failure(new IllegalArgumentException("That player is not currently in a game"));
            }
        }

        return failure(new IllegalArgumentException("Failed to find game"));
    }
}
