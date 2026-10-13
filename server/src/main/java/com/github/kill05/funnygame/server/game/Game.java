package com.github.kill05.funnygame.server.game;

import com.github.kill05.funnygame.server.ServerPlayer;
import com.github.kill05.funnygame.server.connection.ServerConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class Game {

    private final GameController controller;
    private final UUID uuid;
    private final Set<UUID> players;
    private final Set<UUID> immutablePlayers;

    protected Game(GameController controller) {
        this.controller = controller;
        this.uuid = UUID.randomUUID();
        this.players = new HashSet<>();
        this.immutablePlayers = Collections.unmodifiableSet(players);
    }


    public void onPlayerReconnect(@NotNull ServerPlayer player) {

    }

    public void onPlayerDisconnect(@NotNull ServerPlayer player) {

    }

    protected void onPlayerAdd(@Nullable ServerPlayer player) {

    }

    protected void onPlayerRemove(@Nullable ServerPlayer player) {

    }

    protected void onStart() {

    }

    protected void onStop() {

    }

    public void tick() {

    }


    public void addPlayer(@NotNull UUID uuid) {
        synchronized (players) {
            players.add(uuid);

            ServerConnection conn = getServerConnection();
            ServerPlayer onlinePlayer = conn.getOnlinePlayer(uuid);
            onPlayerAdd(onlinePlayer);
        }
    }


    public void removePlayer(@NotNull UUID uuid) {
        synchronized (players) {
            ServerConnection conn = getServerConnection();
            ServerPlayer player = conn.getOnlinePlayer(uuid);

            players.remove(uuid);
        }
    }


    public Iterable<ServerPlayer> getOnlinePlayers() {
        return new Iterable<>() {
            @Override
            public @NotNull Iterator<ServerPlayer> iterator() {
                return new Iterator<>() {
                    final Iterator<UUID> iterator = immutablePlayers.iterator();
                    ServerPlayer next;

                    @Override
                    public boolean hasNext() {
                        ServerConnection connection = getServerConnection();
                        ServerPlayer player = null;

                        while (iterator.hasNext() && player == null) {
                            UUID next = iterator.next();
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

    private ServerConnection getServerConnection() {
        return controller.getServer().getConnection();
    }

    public Set<UUID> getPlayers() {
        return immutablePlayers;
    }

    public UUID getUuid() {
        return uuid;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Game game)) return false;
        return Objects.equals(uuid, game.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
