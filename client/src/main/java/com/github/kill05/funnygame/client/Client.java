package com.github.kill05.funnygame.client;

import com.github.kill05.funnygame.GameConstants;
import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.commands.GameCommandManager;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.connection.NioEventLoop;
import com.github.kill05.funnygame.connection.PlayerConnection;
import com.github.kill05.funnygame.packet.DisconnectPacket;
import com.github.kill05.funnygame.scheduler.GameScheduler;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.utils.NonBlockingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.kill05.funnygame.component.Component.text;
import static com.github.kill05.funnygame.component.TextColor.RED;

public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private final NioEventLoop eventLoop;
    private final GameScheduler scheduler;
    private final CommandManager<CommandSender> commandManager;
    private final ClientTerminal terminal;
    private ClientPlayer player;
    private volatile boolean running;
    private volatile boolean joining; // true when socket connected but waiting for ConnectionAcceptedPacket


    public Client() throws IOException {
        this.player = null;
        this.joining = false;
        this.eventLoop = new NioEventLoop();
        this.scheduler = new GameScheduler(GameConstants.TARGET_TPS, this::tick);
        this.commandManager = new GameCommandManager(scheduler);
        this.terminal = new ClientTerminal(this);
    }


    public void start() {
        synchronized (this) {
            eventLoop.start();
            scheduler.start();
            terminal.start();

            running = true;
        }

        promptConnect();
    }

    public synchronized void stop() {
        if (!running) {
            throw new IllegalStateException("Client is not running!");
        }

        running = false;

        try {
            terminal.close();
            eventLoop.close();
            scheduler.close();
        } catch (IOException e) {
            log.error("Failed to close client.", e);
        }

        // Cannot use sendMessage as terminal is closed
        System.out.println("Client closed.");
    }

    private synchronized void tick() {
        if (player == null) {
            return;
        }

        try {
            NonBlockingReader reader = terminal.getTerminal().reader();
            int read;

            if (!terminal.isRequestingInput()
                    && reader.available() > 0
                    && (read = reader.read()) >= 0
            ) {
                char c = (char) read;
                player.onKeyPressed(c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            player.getConnection().tick();
        } catch (IOException e) {
            log.warn("Failed to process incoming packets.", e);
        }
    }


    private void promptConnect() {
        while (running) {
            InetSocketAddress address = requestAddressOrExit();
            if (address == null) {
                return; // Client is stopping
            }

            try {
                String username = "player" + ThreadLocalRandom.current().nextInt(1000);
                connect(username, address);
                break;
            } catch (UnresolvedAddressException e) {
                sendMessage(text("Failed to connect to server: Unknown hostname.", RED));
            } catch (IOException e) {
                sendMessage(text("Failed to connect to server: " + e.getMessage(), RED));
            }
        }
    }

    // Returns null when the client is going to stop
    @Nullable
    private InetSocketAddress requestAddressOrExit() {
        InetSocketAddress address = null;

        while (address == null) {
            sendMessage("Insert server address (or type '/exit' to exit):");
            String next = terminal.requestInputAndWait();

            if (next.isEmpty() && !running) {
                return null;
            }

            if ("/exit".equals(next)) {
                stop();
                return null;
            }

            try {
                address = AddressUtils.addressFromString(next);
            } catch (IllegalArgumentException e) {
                sendMessage(text(e.getMessage() + " Please try again.", RED));
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
        this.joining = true;

        sendMessage("Joining server...");

        eventLoop.addConnection(connection);
        connection.sendConnectionRequest(username);
    }

    public synchronized void onConnectionAccepted() {
        checkRunning();

        if (!joining) {
            log.warn("Received ConnectionAcceptedPacket while connection was not pending.");
            disconnect("Received illegal packet.", true);
            return;
        }

        this.joining = false;

        // Send connected message and update status message
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) player.getConnection().getChannel().getRemoteAddress();
            sendMessage("Connected to " + remoteAddress.getHostName());
            terminal.updateStatusMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        startHandlingInput();
    }

    private void startHandlingInput() {
        terminal.requestInput().thenAccept(input -> {
            player.onChatMessageSent(input);
            terminal.updateStatusMessage();
            startHandlingInput();
        });
    }

    /**
     * Disconnects from the server and shows a message to the player displaying why it disconnected.
     * This can be called either because the client decided to disconnect from the server or the server sent a
     * disconnect packet to the client
     *
     * @param message the message
     */
    public void disconnect(@Nullable String message, boolean sendPacket) {
        synchronized (this) {
            checkRunning();

            if (!isConnected()) {
                return; // Disconnect can be called twice in some cases
            }

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
        }

        promptConnect();
    }


    public void sendMessage(@NotNull Component message) {
        terminal.sendMessage(message);
    }

    public void sendMessage(@NotNull String message) {
        terminal.sendMessage(message);
    }


    /**
     * Returns {@code true} if the client is currently connected to a server,
     * i.e. the connection has been established and the client has received
     * a {@link com.github.kill05.funnygame.packet.ConnectionAcceptedPacket}.
     *
     * @return {@code true} if connected
     */
    public synchronized boolean isConnected() {
        return player != null && !joining;
    }

    public synchronized ClientStatus getStatus() {
        if (!running) {
            return ClientStatus.NOT_RUNNING;
        }

        if (isConnected()) {
            return ClientStatus.CONNECTED;
        }

        if (joining) {
            return ClientStatus.JOINING;
        }

        return ClientStatus.NOT_CONNECTED;
    }

    @NotNull
    public synchronized ClientPlayer getPlayer() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to a server.");
        }

        return player;
    }

    public NioEventLoop getEventLoop() {
        return eventLoop;
    }

    public GameScheduler getScheduler() {
        return scheduler;
    }

    public CommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}
