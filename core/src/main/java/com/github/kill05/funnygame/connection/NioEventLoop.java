package com.github.kill05.funnygame.connection;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioEventLoop implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(NioEventLoop.class);
    private final Selector selector;
    private final Thread thread;

    public NioEventLoop() throws IOException {
        this.selector = Selector.open();
        this.thread = new Thread(this::eventLoopTask, "nio-event-loop-thread");
    }

    public void start() {
        thread.start();
    }

    @Override
    public void close() throws IOException {
        thread.interrupt();
        selector.close();
    }


    private void eventLoopTask() {
        selector.selectedKeys().clear();

        while (!Thread.currentThread().isInterrupted()) {
            try {
                selector.select();
            } catch (IOException e) {
                log.warn("Failed to select active sockets.", e);
                continue;
            }

            if (!selector.isOpen()) {
                return;
            }

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    try {
                        PlayerConnection connection = accept();

                        if (connection != null) {
                            addConnection(connection);
                        }
                    } catch (IOException e) {
                        log.warn("Failed to accept new connection.");
                    }
                }

                if (key.isConnectable()) {
                    try {
                        PlayerConnection connection = connect();
                        addConnection(connection);
                    } catch (IOException e) {
                        log.warn("Failed to connect.");
                    }
                }

                if (key.isWritable()) {
                    PlayerConnection connection = (PlayerConnection) key.attachment();

                    try {
                        write(connection);
                    } catch (IOException e) {
                        log.warn("Failed to write data to socket.", e);
                        connection.disconnect("Failed to write data: " + e.getMessage(), false);
                    }
                }

                if (key.isReadable()) {
                    PlayerConnection connection = (PlayerConnection) key.attachment();

                    try {
                        read(connection);
                    } catch (IOException e) {
                        log.warn("Failed to read data from socket.", e);
                        connection.disconnect("Failed to read data: " + e.getMessage(), false);
                    }
                }
            }
        }
    }


    public PlayerConnection accept() throws IOException {
        throw new UnsupportedOperationException();
    }

    public PlayerConnection connect() throws IOException {
        throw new UnsupportedOperationException();
    }

    public void write(@NotNull PlayerConnection connection) throws IOException {
        connection.writeSocket();
    }

    public void read(@NotNull PlayerConnection connection) throws IOException {
        connection.readSocket();
    }


    public void addConnection(@NotNull PlayerConnection connection) throws IOException {
        SocketChannel channel = connection.getChannel();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, connection);
    }

    public Selector getSelector() {
        return selector;
    }
}
