package com.github.kill05.funnygame.server;

import com.github.kill05.funnygame.Player;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.component.TextColor;
import com.github.kill05.funnygame.connection.NioEventLoop;
import com.github.kill05.funnygame.connection.PlayerConnection;
import com.github.kill05.funnygame.packet.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import static com.github.kill05.funnygame.component.Component.text;
import static com.github.kill05.funnygame.connection.ConnectionConstants.REQUEST_TIMEOUT_MILLIS;

public class ServerPlayerConnection extends PlayerConnection {

    private final ServerConnection serverConnection;
    private long connectionCreatedTime; // negative value = no pending request

    public ServerPlayerConnection(@NotNull ServerConnection serverConnection, @NotNull SocketChannel socket) {
        super(socket);
        this.serverConnection = serverConnection;
        this.connectionCreatedTime = System.currentTimeMillis();
    }


    @Override
    public void tick() throws IOException {
        super.tick();

        if (connectionCreatedTime > 0 && System.currentTimeMillis() > connectionCreatedTime + REQUEST_TIMEOUT_MILLIS) {
            disconnect("Timed out while waiting for client to request connection.");
            return;
        }
    }

    @Override
    public void disconnect(@Nullable String message, boolean sendPacket) {
        serverConnection.disconnectPlayer(this, message, sendPacket);
    }


    @Override
    protected NioEventLoop getEventLoop() {
        return serverConnection.getEventLoop();
    }

    @Override
    public @Nullable Player getPlayer() {
        return serverConnection.getOnlinePlayer(this);
    }


    @Override
    public void handleConnectionRequestPacket(@NotNull ConnectionRequestPacket packet) throws IOException {
        connectionCreatedTime = -1; // mark there is no pending request
        serverConnection.handleConnectionRequest(this, packet);
    }

    @Override
    public void handleConnectionAcceptedPacket(@NotNull ConnectionAcceptedPacket packet) throws IOException {
        handleUnexpectedPacket(packet);
    }

    @Override
    public void handleDisconnectPacket(@NotNull DisconnectPacket packet) {
        // Ignore message
        serverConnection.disconnectPlayer(this, null, false);
    }

    @Override
    public void handleClientChatMessagePacket(@NotNull ClientChatMessagePacket packet) throws IOException {
        Player player = getPlayer();
        if (player == null) {
            throw new IOException("Received ClientChatMessagePacket while connection is in progress.");
        }

        if (packet.rawMessage().startsWith("/")) {
            player.executeCommand(packet.rawMessage());
            return;
        }

        Component message = text().append(
                text(player.getUsername(), TextColor.RED),
                text(" » "),
                text(packet.rawMessage())
        ).build();

        serverConnection.getServer().broadcast(message);
    }

    @Override
    public void handleServerChatMessagePacket(@NotNull ServerChatMessagePacket packet) throws IOException {
        handleUnexpectedPacket(packet);
    }

}
