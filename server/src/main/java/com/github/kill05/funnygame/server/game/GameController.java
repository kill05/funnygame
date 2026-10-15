package com.github.kill05.funnygame.server.game;

import com.github.kill05.funnygame.player.Player;
import com.github.kill05.funnygame.server.GameServer;
import com.github.kill05.funnygame.server.poker.PokerGame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class GameController implements Closeable {

    public static final Map<String, GameType> GAME_TYPE_MAP = new HashMap<>();

    public static GameType register(String id, Function<GameController, Game> factory) {
        GameType type = new GameType(id, factory);
        GAME_TYPE_MAP.put(id, type);
        return type;
    }


    public static final GameType POKER = GameController.register("poker", PokerGame::new);



    private final GameServer server;
    private final Map<String, Game> activeGames;
    private final Collection<Game> immutableGameView;

    public GameController(GameServer server) {
        this.server = server;
        this.activeGames = new HashMap<>();
        this.immutableGameView = Collections.unmodifiableCollection(activeGames.values());
    }

    public void start() {

    }

    @Override
    public void close() {

    }

    public void tick() {
        synchronized (activeGames) {
            for (Game game : activeGames.values()) {
                if (!game.isRunning()) {
                    continue;
                }

                game.tick();
            }
        }
    }


    public Game startGame(GameType type) {
        Game game = type.factory().apply(this);
        startGame(game);
        return game;
    }

    public void startGame(@NotNull Game game) {
        synchronized (activeGames) {
            if (activeGames.containsKey(game.id())) {
                throw new IllegalStateException("Game is already started!");
            }

            activeGames.put(game.id(), game);
            game.start_unsafe();
        }
    }

    public void stopGame(@NotNull Game game) {
        synchronized (activeGames) {
            if (!activeGames.containsKey(game.id())) {
                throw new IllegalStateException("The game is not running!");
            }

            game.stop_unsafe();
            activeGames.remove(game.id());
        }
    }

    @Nullable
    public Game getGame(@NotNull Player player) {
        for (Game game : activeGames.values()) {
            if (game.getPlayers().contains(player.getUsername())) {
                return game;
            }
        }

        return null;
    }


    @Nullable
    public Game getGame(String id) {
        return activeGames.get(id);
    }

    public Collection<Game> getActiveGames() {
        return immutableGameView;
    }

    public GameServer getServer() {
        return server;
    }

}
