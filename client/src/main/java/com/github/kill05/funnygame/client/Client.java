package com.github.kill05.funnygame.client;

import com.github.kill05.funnygame.GameConstants;
import com.github.kill05.funnygame.Ticker;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.connection.NioEventLoop;
import com.github.kill05.funnygame.connection.PlayerConnection;
import com.github.kill05.funnygame.packet.DisconnectPacket;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);


    public static void sendMessage(@NotNull Component component) {
        sendMessage(component.toConsoleString());
    }

    public static void sendMessage(@NotNull String message) {
        System.out.println(message);
    }


    private final NioEventLoop eventLoop;
    private final Ticker ticker;
    private final Scanner scanner;
    private ClientPlayer player;
    private final Thread consoleReaderThread;
    private volatile boolean running;
    private volatile boolean pending;

    public Client() throws IOException {
        this.player = null;
        this.pending = false;
        this.eventLoop = new NioEventLoop();
        this.ticker = new Ticker(GameConstants.TARGET_TPS, this::tick);
        this.scanner = new Scanner(System.in);

        this.consoleReaderThread = Thread.ofVirtual().unstarted(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                String input = scanner.nextLine();

                synchronized (Client.this) {
                    if (player == null) {
                        continue;
                    }

                    player.onChatMessageSent(input);
                }
            }
        });
    }


    public void start() {
        synchronized (this) {
            eventLoop.start();
            ticker.start();

            running = true;
        }

        promptConnect();
    }

    public synchronized void stop() {
        running = false;

        try {
            eventLoop.close();
            ticker.close();
        } catch (IOException e) {
            log.error("Failed to close client.", e);
        }

        sendMessage("Client closed.");
    }


    private void promptConnect() {
        while (true) {
            InetSocketAddress address = requestAddressOrExit();
            if (address == null) {
                return; // Client is stopping
            }

            try {
                String username = "player" + ThreadLocalRandom.current().nextInt(1000);
                connect(username, address);
                break;
            } catch (IOException | UnresolvedAddressException e) {
                Client.sendMessage("Failed to connect to server: " + e.getMessage());
            }
        }
    }

    // Returns null when the client is going to stop
    @Nullable
    private InetSocketAddress requestAddressOrExit() {
        InetSocketAddress address = null;

        while (address == null) {
            Client.sendMessage("Insert server address (or type '/exit' to exit):");
            String next = scanner.nextLine();

            if ("/exit".equals(next)) {
                stop();
                return null;
            }

            try {
                address = AddressUtils.addressFromString(next);
            } catch (IllegalArgumentException e) {
                Client.sendMessage(e.getMessage() + " Please try again.");
            }
        }

        return address;
    }

    private void checkRunning() {
        if (!running) {
            throw new IllegalStateException("The client is not running!");
        }
    }


    public void connect(@NotNull String username, @NotNull String address) throws IOException {
        connect(username, AddressUtils.addressFromString(address));
    }

    @Blocking
    public synchronized void connect(@NotNull String username, @NotNull InetSocketAddress address) throws IOException, UnresolvedAddressException {
        checkRunning();

        if (player != null) {
            throw new IllegalStateException("The client is already connected to a server!");
        }

        sendMessage("Connecting to server...");

        // Open socket
        SocketChannel socket = SocketChannel.open(address);

        // Create player connection and send connection request
        ClientPlayerConnection connection = new ClientPlayerConnection(this, socket);
        this.player = new ClientPlayer(this, username, connection);
        this.pending = true;

        eventLoop.addConnection(connection);
        connection.sendConnectionRequest(username);
    }

    public synchronized void onConnectionAccepted() {
        checkRunning();

        if (!pending) {
            log.warn("Received ConnectionAcceptedPacket while connection was not pending.");
            disconnect("Received illegal packet.", true);
            return;
        }

        this.pending = false;
        this.consoleReaderThread.start();
        sendMessage("Connected.");
    }

    /**
     * Disconnects from the server and shows a message to the player displaying why it disconnected.
     * This can be called either because the client decided to disconnect from the server or the server sent a
     * disconnect packet to the client
     *
     * @param message the message
     */
    public synchronized void disconnect(@Nullable String message, boolean sendPacket) {
        checkRunning();

        if (player == null) {
            throw new IllegalStateException("The client is not connected to a server.");
        }

        this.consoleReaderThread.interrupt();
        PlayerConnection connection = player.getConnection();

        if (sendPacket) {
            // Message sent from the client is ignored by the server
            connection.sendPacket(new DisconnectPacket((String) null));
        }

        try {
            connection.writeSocket(); // Flush before closing
            connection.getChannel().close();
        } catch (IOException e) {
            log.warn("Failed to disconnect from the server.", e);
        }

        this.player = null;

        if (message != null) {
            sendMessage("Disconnected from server: " + message);
        } else {
            sendMessage("Disconnected from server.");
        }

        promptConnect();
    }

    private synchronized void tick() {
        if (player == null) {
            return;
        }

        try {
            player.getConnection().tick();
        } catch (IOException e) {
            log.warn("Failed to process incoming packets.", e);
        }
    }


    @NotNull
    public synchronized ClientPlayer getPlayer() {
        if (player == null) {
            throw new IllegalStateException("Not connected to a server.");
        }

        return player;
    }

    public NioEventLoop getEventLoop() {
        return eventLoop;
    }
}
