package com.github.kill05.funnygame.server;

import com.github.kill05.funnygame.Player;
import com.github.kill05.funnygame.connection.ConnectionConstants;
import com.github.kill05.funnygame.connection.NioEventLoop;
import com.github.kill05.funnygame.connection.PlayerConnection;
import com.github.kill05.funnygame.packet.ConnectionAcceptedPacket;
import com.github.kill05.funnygame.packet.ConnectionRequestPacket;
import com.github.kill05.funnygame.packet.DisconnectPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.kill05.funnygame.connection.ConnectionConstants.PROTOCOL_VERSION;

public class ServerConnection implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(ServerConnection.class);
    private final Server server;
    private final ServerSocketChannel channel;
    private final NioEventLoop eventLoop;
    private final Map<PlayerConnection, ServerPlayer> players;
    private final Set<PlayerConnection> awaitingConnections;

    public ServerConnection(@NotNull Server server) throws IOException {
        this.server = server;
        this.players = new ConcurrentHashMap<>();
        this.awaitingConnections = Collections.synchronizedSet(new HashSet<>());
        this.channel = ServerSocketChannel.open();

        this.eventLoop = new NioEventLoop() {
            @Override
            public PlayerConnection accept() throws IOException {
                return acceptSocketConnection();
            }
        };

        channel.configureBlocking(false);
        channel.register(eventLoop.getSelector(), SelectionKey.OP_ACCEPT);
    }

    public void start() throws IOException {
        eventLoop.start();
        channel.bind(new InetSocketAddress(ConnectionConstants.DEFAULT_PORT));

        log.info("Listening on port {}.", channel.socket().getLocalPort());
    }

    @Override
    public void close() throws IOException {
        disconnectPlayers("Server closed.");

        channel.close();
        eventLoop.close();

        log.info("Closed server connection.");
    }

    public void tick() {
        for (PlayerConnection connection : players.keySet()) {
            tickConnection(connection);
        }

        for (PlayerConnection connection : awaitingConnections) {
            tickConnection(connection);
        }
    }

    private void tickConnection(PlayerConnection connection) {
        try {
            connection.tick();
        } catch (IOException e) {
            Player player = connection.getPlayer();

            if (player != null) {
                log.warn("Failed to process incoming packets for player '{}.'", player.getUsername(), e);
            } else {
                log.warn("Failed to process incoming packets for pending connection", e);
            }

            connection.disconnect(e.getMessage());
        }
    }

    private PlayerConnection acceptSocketConnection() throws IOException {
        if (!channel.socket().isBound()) {
            return null;
        }

        SocketChannel clientChannel = this.channel.accept();
        if (clientChannel == null) {
            return null;
        }

        PlayerConnection connection = new ServerPlayerConnection(this, clientChannel);

        awaitingConnections.add(connection);
        return connection;
    }

    @ApiStatus.Internal
    public void handleConnectionRequest(@NotNull ServerPlayerConnection connection, @NotNull ConnectionRequestPacket packet) throws IOException {
        ServerPlayer player;

        synchronized (awaitingConnections) {
            if (!awaitingConnections.remove(connection)) {
                throw new IOException("Invalid connection request: server is not awaiting for ConnectionRequestPacket.");
            }

            // Check protocol version
            int version = packet.protocolVersion();
            if (version > PROTOCOL_VERSION) {
                connection.disconnect(String.format("Server is running an outdated version: %d.", PROTOCOL_VERSION));
                return;
            }

            if (version < PROTOCOL_VERSION) {
                connection.disconnect(String.format("Client outdated version. Version required: %d.", PROTOCOL_VERSION));
                return;
            }

            // Check duplicate username
            String username = packet.username();
            if (getOnlinePlayer(username) != null) {
                connection.disconnect(String.format("There is already a player connected with the username '%s'.", username));
                return;
            }

            // Create player and add it to map
            player = new ServerPlayer(server, username, connection);
            players.put(connection, player);
        }

        connection.sendPacket(new ConnectionAcceptedPacket());
        player.onJoin(); // todo: make this call synchronous with the ticking thread once i introduce task scheduling
    }


    public void disconnectPlayers(@Nullable String message) {
        for (PlayerConnection connection : awaitingConnections) {
            handleDisconnect(connection, message, true);
        }

        for (PlayerConnection connection : players.keySet()) {
            handleDisconnect(connection, message, true);
        }

        awaitingConnections.clear();
        players.clear();
    }


    @Blocking
    public void disconnectPlayer(@NotNull PlayerConnection connection, @Nullable String message, boolean sendPacket) {
        handleDisconnect(connection, message, sendPacket);

        ServerPlayer player = players.remove(connection);
        awaitingConnections.remove(connection);

        if (player != null) {
            log.info("{} disconnected: {}", player.getUsername(), message);
            player.onLeave();
        }
    }

    private void handleDisconnect(@NotNull PlayerConnection connection, @Nullable String message, boolean sendPacket) {
        if (sendPacket) {
            connection.sendPacket(new DisconnectPacket(message));
        }

        try {
            connection.writeSocket(); // Flush before closing
            connection.getChannel().close();
        } catch (IOException e) {
            log.warn("Failed to close socket.", e);
        }
    }


    public Player getOnlinePlayer(@NotNull PlayerConnection connection) {
        return players.get(connection);
    }

    @Nullable
    public Player getOnlinePlayer(@NotNull String username) {
        for (Player player : players.values()) {
            String name = player.getUsername();
            if (username.equals(name)) {
                return player;
            }
        }

        return null;
    }


    public Server getServer() {
        return server;
    }

    public ServerSocketChannel getChannel() {
        return channel;
    }

    public Map<PlayerConnection, ServerPlayer> getOnlinePlayers() {
        return players;
    }

    public Set<PlayerConnection> getAwaitingConnections() {
        return awaitingConnections;
    }

    public NioEventLoop getEventLoop() {
        return eventLoop;
    }
}
