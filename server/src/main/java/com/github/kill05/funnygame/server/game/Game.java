package com.github.kill05.funnygame.server.game;

import com.github.kill05.funnygame.StringUtils;
import com.github.kill05.funnygame.scheduler.GameScheduler;
import com.github.kill05.funnygame.server.ServerPlayer;
import com.github.kill05.funnygame.server.connection.ServerConnection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Game {

    private static final int MAX_ID_ATTEMPTS = 100;

    private final GameController controller;
    private final String id;
    private final Set<String> players;
    private final Set<String> immutablePlayers;
    private boolean running;

    protected Game(GameController controller) {
        this.controller = controller;
        this.id = generateId(controller);
        this.players = new HashSet<>();
        this.immutablePlayers = Collections.unmodifiableSet(players);
        this.running = false;
    }

    private static String generateId(GameController controller) {
        for (int i = 0; i < MAX_ID_ATTEMPTS; i++) {
            String id = StringUtils.generateAlphanumeric(5);

            if (controller.getGame(id) != null) {
                continue; // try again
            }

            return id;
        }

        throw new RuntimeException("Max attempts reached.");
    }


    /**
     * Returns the display name of this game
     *
     * @return the display name
     */
    public abstract String getDisplayName();

    @ApiStatus.Internal
    public void onPlayerReconnect(@NotNull ServerPlayer player) {

    }

    @ApiStatus.Internal
    public void onPlayerDisconnect(@NotNull ServerPlayer player) {

    }

    protected void onPlayerAdd(@NotNull String username, @Nullable ServerPlayer player) {

    }

    protected void onPlayerRemove(@NotNull String username, @Nullable ServerPlayer player) {

    }

    protected void onStart() {

    }

    protected void onStop() {

    }


    @ApiStatus.Internal
    public void tick() {

    }


    @ApiStatus.Internal
    public synchronized void start_unsafe() {
        getScheduler().run(this::onStart);
        running = true;
    }

    @ApiStatus.Internal
    public synchronized void stop_unsafe() {
        running = false;
        getScheduler().run(() -> {
            for (String player : players) {
                removePlayer(player);
            }

            onStop();
        });
    }

    public synchronized boolean addPlayer(@NotNull String username) {
        ServerConnection conn = getServerConnection();
        ServerPlayer onlinePlayer = conn.getOnlinePlayer(username);

        getScheduler().run(() -> {
            onPlayerAdd(username, onlinePlayer);
        });

        return players.add(username);
    }

    public synchronized boolean removePlayer(@NotNull String username) {
        ServerConnection conn = getServerConnection();
        ServerPlayer player = conn.getOnlinePlayer(username);

        getScheduler().run(() -> {
            onPlayerRemove(username, player);
        });

        return players.remove(username);
    }


    public Iterable<ServerPlayer> getOnlinePlayers() {
        return new Iterable<>() {
            @Override
            public @NotNull Iterator<ServerPlayer> iterator() {
                return new Iterator<>() {
                    final Iterator<String> iterator = immutablePlayers.iterator();
                    ServerPlayer next;

                    @Override
                    public boolean hasNext() {
                        ServerConnection connection = getServerConnection();
                        ServerPlayer player = null;

                        while (iterator.hasNext() && player == null) {
                            String next = iterator.next();
                            player = connection.getOnlinePlayer(next);
                        }

                        next = player;
                        return next != null;
                    }

                    @Override
                    public ServerPlayer next() {
                        if (next == null) {
                            throw new NoSuchElementException();
                        }

                        return next;
                    }
                };
            }
        };
    }


    private GameScheduler getScheduler() {
        return controller.getServer().getScheduler();
    }

    private ServerConnection getServerConnection() {
        return controller.getServer().getConnection();
    }


    public Set<String> getPlayers() {
        return immutablePlayers;
    }

    public String id() {
        return id;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Game game)) return false;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
