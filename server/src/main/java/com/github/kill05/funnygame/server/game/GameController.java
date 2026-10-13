package com.github.kill05.funnygame.server.game;

import com.github.kill05.funnygame.server.Server;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GameController {

    private final Server server;
    private final Set<Game> activeGames;

    public GameController(Server server) {
        this.server = server;
        this.activeGames = Collections.synchronizedSet(new HashSet<>());
    }


    public void tick() {
        synchronized (activeGames) {
            for (Game game : activeGames) {
                game.tick();
            }
        }
    }

    public void startGame(@NotNull Game game) {
        synchronized (activeGames) {
            if (activeGames.contains(game)) {
                throw new IllegalStateException("Game is already started!");
            }

            activeGames.add(game);
            game.onStart();
        }
    }

    public void stopGame(@NotNull Game game) {
        synchronized (activeGames) {
            if (!activeGames.contains(game)) {
                throw new IllegalStateException("The game is not running!");
            }

            game.onStop();
            activeGames.remove(game);
        }
    }

    public Set<Game> getActiveGames() {
        return activeGames;
    }

    public Server getServer() {
        return server;
    }
}
