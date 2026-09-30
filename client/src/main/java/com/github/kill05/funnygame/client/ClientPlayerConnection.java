package com.github.kill05.funnygame.client;

import com.github.kill05.funnygame.Player;
import com.github.kill05.funnygame.connection.NioEventLoop;
import com.github.kill05.funnygame.connection.PlayerConnection;
import com.github.kill05.funnygame.packet.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import static com.github.kill05.funnygame.connection.ConnectionConstants.PROTOCOL_VERSION;
import static com.github.kill05.funnygame.connection.ConnectionConstants.REQUEST_TIMEOUT_MILLIS;

public class ClientPlayerConnection extends PlayerConnection {

    private final Client client;
    private long requestSentTime; // If negative there is no pending request

    public ClientPlayerConnection(@NotNull Client client, @NotNull SocketChannel socket) {
        super(socket);
        this.client = client;
    }


    @Override
    public void tick() throws IOException {
        super.tick();

        if (requestSentTime > 0 && System.currentTimeMillis() > requestSentTime + REQUEST_TIMEOUT_MILLIS) {
            disconnect("Timed out while waiting for server to accept connection.");
            return;
        }
    }

    public void sendConnectionRequest(@NotNull String username) {
        sendPacket(new ConnectionRequestPacket(username, PROTOCOL_VERSION));
        this.requestSentTime = System.currentTimeMillis();
    }


    @Override
    public void disconnect(@Nullable String reason, boolean sendPacket) {
        client.disconnect(reason, sendPacket);
    }

    @Override
    protected NioEventLoop getEventLoop() {
        return client.getEventLoop();
    }

    @Override
    public @Nullable Player getPlayer() {
        return client.getPlayer();
    }


    @Override
    public void handleConnectionRequestPacket(@NotNull ConnectionRequestPacket packet) throws IOException {
        handleUnexpectedPacket(packet);
    }

    @Override
    public void handleConnectionAcceptedPacket(@NotNull ConnectionAcceptedPacket packet) {
        requestSentTime = -1; // Setting to -1 marks that there is no pending connection request
        client.onConnectionAccepted();
    }

    @Override
    public void handleDisconnectPacket(@NotNull DisconnectPacket packet) {
        client.disconnect(packet.message(), false);
    }

    @Override
    public void handleServerChatMessagePacket(@NotNull ServerChatMessagePacket packet) {
        Client.sendMessage(packet.message());
    }

    @Override
    public void handleClientChatMessagePacket(@NotNull ClientChatMessagePacket packet) throws IOException {
        handleUnexpectedPacket(packet);
    }
}
