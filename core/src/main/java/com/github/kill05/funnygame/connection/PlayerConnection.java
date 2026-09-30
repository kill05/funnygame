package com.github.kill05.funnygame.connection;

import com.github.kill05.funnygame.Player;
import com.github.kill05.funnygame.packet.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static com.github.kill05.funnygame.connection.ConnectionConstants.*;

public abstract class PlayerConnection {

    public static final int PACKET_BUFFER_SIZE = 128;

    protected final SocketChannel channel;
    private final Queue<Packet> packetInQueue;
    private final Queue<Packet> packetOutQueue;
    private final ByteArrayOutputStream inBuffer;
    private final ByteBuffer buf;
    private volatile long lastKeepAliveSent;
    private volatile long lastKeepAliveReceived;

    public PlayerConnection(@NotNull SocketChannel channel) {
        this.channel = channel;
        this.packetInQueue = new ArrayBlockingQueue<>(PACKET_BUFFER_SIZE);
        this.packetOutQueue = new ArrayBlockingQueue<>(PACKET_BUFFER_SIZE);
        this.inBuffer = new ByteArrayOutputStream(256);
        this.buf = ByteBuffer.allocate(1024);
        this.lastKeepAliveReceived = System.currentTimeMillis();
    }

    /**
     * Called to make the player disconnect from the server.
     * Also sends a {@link DisconnectPacket} to notify the other end of the disconnection.
     *
     * @param reason the reason for the disconnection.
     */
    public void disconnect(@Nullable String reason) {
        disconnect(reason, true);
    }

    /**
     * Called to make the player disconnect from the server.
     *
     * @param reason     the reason for the disconnection
     * @param sendPacket true to send a {@link DisconnectPacket} to the other end
     */
    protected abstract void disconnect(@Nullable String reason, boolean sendPacket);

    protected abstract NioEventLoop getEventLoop();

    /**
     * Returns the player associated to this connection if the connection is established and there is a player
     * associated to this object or {@code null} if the connection is established but there is no player yet.
     *
     * @return the player
     */
    @Nullable
    public abstract Player getPlayer();


    public void sendPacket(@NotNull Packet packet) {
        if (!channel.isOpen()) {
            throw new IllegalStateException("Channel closed.");
        }

        synchronized (packetOutQueue) {
            boolean added = packetOutQueue.offer(packet);

            if (!added) {
                // Handle full queue by disconnecting the player
                packetOutQueue.clear();
                disconnect("Server is attempting to send too many packets.");
                return;
            }
        }

        // Add OP_WRITE to selector so the selector will wake up when the channel is writeable
        Selector selector = getEventLoop().getSelector();
        SelectionKey key = channel.keyFor(selector);

        key.interestOpsOr(SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    public void tick() throws IOException {
        long time = System.currentTimeMillis();

        if (time > lastKeepAliveReceived + TIMEOUT_MILLIS) {
            disconnect("Timed out.");
            return;
        }

        if (time > lastKeepAliveSent + KEEP_ALIVE_INTERVAL) {
            lastKeepAliveSent = time;
            sendPacket(new KeepAlivePacket());
        }

        synchronized (packetInQueue) {
            while (!packetInQueue.isEmpty()) {
                Packet packet = packetInQueue.poll();
                handleIncomingPacket(packet);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends Packet> void handleIncomingPacket(@NotNull T packet) throws IOException {
        RegisteredPacket<? extends Packet> registered = PacketRegistry.getRegistered(packet.getClass());
        if (registered == null) {
            throw new IllegalStateException("Unregistered packet: " + packet.getClass());
        }

        PacketHandler<T> handler = (PacketHandler<T>) registered.handler();
        handler.handlePacket(this, packet);
    }


    public void writeSocket() throws IOException {
        ByteArrayOutputStream out;

        synchronized (packetOutQueue) {
            if (packetOutQueue.isEmpty()) {
                return;
            }

            out = new ByteArrayOutputStream();
            PacketOutputStream packetOut = new PacketOutputStream(out); // No need to close

            while (!packetOutQueue.isEmpty()) {
                Packet packet = packetOutQueue.poll();
                writePacket(packet, packetOut);
            }
        }

        byte[] packetData = out.toByteArray();
        ByteBuffer buf = ByteBuffer.wrap(packetData);

        int wroteAmount = channel.write(buf);

        // todo: better full buffer handling
        if (wroteAmount != packetData.length) {
            disconnect("Filled socket output buffer.");
        }
    }

    protected void writePacket(@NotNull Packet packet, @NotNull PacketOutputStream output) throws IOException {
        RegisteredPacket<? extends Packet> registered = PacketRegistry.getRegistered(packet.getClass());

        if (registered == null) {
            throw new IOException("Unknown outbound packet: " + packet.getClass());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PacketOutputStream packetOut = new PacketOutputStream(out)) {
            packet.serialize(packetOut);
        }

        output.writeShort(PACKET_MAGIC_BYTES);
        output.writeInt(out.size());
        output.write(registered.id());
        output.write(out.toByteArray());
    }

    // Only called when there is data in the socket to read
    public void readSocket() throws IOException {
        byte[] data;

        synchronized (inBuffer) {
            boolean keepReading = true;
            while (keepReading) {
                buf.clear();

                int read;
                try {
                    read = channel.read(buf);
                } catch (SocketException e) {
                    disconnect(e.getMessage(), false);
                    return;
                }

                if (read < 0) {
                    // End of stream
                    disconnect("Connection closed.", false);
                    return;
                }

                if (read < buf.capacity()) {
                    keepReading = false;
                }

                inBuffer.write(buf.array(), 0, buf.position());
            }

            data = inBuffer.toByteArray();
            inBuffer.reset();
        }

        PacketInputStream in = new PacketInputStream(new ByteArrayInputStream(data));

        synchronized (packetInQueue) {
            while (true) {
                if (in.available() < PACKET_HEADER_SIZE) {
                    // Wait for more data
                    break;
                }

                if (in.readShort() != PACKET_MAGIC_BYTES) {
                    // Every packet is supposed to begin with magic bytes
                    throw new IOException("Bad packet header (magic bytes).");
                }

                int length = in.readInt();
                int id = in.readByte();

                RegisteredPacket<?> registered = PacketRegistry.getRegistered(id);
                if (registered == null) {
                    throw new IOException("Invalid packet id: " + id);
                }

                if (data.length - PACKET_HEADER_SIZE < length) {
                    // Wait for more data
                    break;
                }

                int before = in.available();
                Packet packet = registered.deserializer().deserialize(in);
                int deserializedAmount = before - in.available();

                if (deserializedAmount != length) {
                    throw new IOException(String.format("Incomplete packet read (expected: %d, read: %d)", length, deserializedAmount));
                }

                boolean result = packetInQueue.add(packet);
                if (!result) {
                    packetInQueue.clear();
                    disconnect("You are sending too many packets.");
                    break;
                }
            }
        }

        // Remove OP_WRITE from selector to avoid waking up the selector when there is nothing to write
        Selector selector = getEventLoop().getSelector();
        SelectionKey key = channel.keyFor(selector);

        key.interestOpsAnd(~SelectionKey.OP_WRITE);
        selector.wakeup();
    }


    // Any of these methods can throw an IOException to cause a disconnection
    public abstract void handleConnectionRequestPacket(@NotNull ConnectionRequestPacket packet) throws IOException;

    public abstract void handleConnectionAcceptedPacket(@NotNull ConnectionAcceptedPacket packet) throws IOException;

    public void handleKeepAlivePacket(@NotNull KeepAlivePacket packet) throws IOException {
        this.lastKeepAliveReceived = System.currentTimeMillis();
    }

    public abstract void handleDisconnectPacket(@NotNull DisconnectPacket packet) throws IOException;

    public abstract void handleServerChatMessagePacket(@NotNull ServerChatMessagePacket packet) throws IOException;

    public abstract void handleClientChatMessagePacket(@NotNull ClientChatMessagePacket packet) throws IOException;

    /**
     * Called to handle an unexpected packet received (by throwing an {@link IOException}).
     *
     * @param packet the packet
     */
    protected void handleUnexpectedPacket(@NotNull Packet packet) throws IOException {
        throw new IOException(String.format("Received unexpected %s.", packet.getClass()));
    }

    public SocketChannel getChannel() {
        return channel;
    }
}
