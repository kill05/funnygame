package com.github.kill05.funnygame.server;

import com.github.kill05.funnygame.GameConstants;
import com.github.kill05.funnygame.Player;
import com.github.kill05.funnygame.Ticker;
import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.commands.GameCommandManager;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.server.commands.ListCommand;
import com.github.kill05.funnygame.server.commands.StopCommand;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final ServerConnection connection;
    private final Ticker ticker;
    private final CommandManager<CommandSender> commandManager;
    private final AtomicLong currentTick;
    private volatile boolean running;
    private volatile boolean stop; // Set to true to start the stopping sequence, not when the server HAS stopped

    public Server() throws IOException {
        this.connection = new ServerConnection(this);
        this.ticker = new Ticker(GameConstants.TARGET_TPS, this::tick);
        this.commandManager = new GameCommandManager();
        this.currentTick = new AtomicLong(0);
        this.running = false;
        this.stop = false;
    }

    public synchronized void start() {
        if (running) {
            throw new IllegalStateException("Server is already running!");
        }

        log.info("Starting server...");
        long time = System.currentTimeMillis();

        try {
            // Starting sequence
            registerCommands();
            connection.start();

            // Server has started
            running = true;

            // Start ticking
            ticker.start();
        } catch (Throwable t) {
            log.error("Failed to start server.", t);
            System.exit(1);
        }

        log.info("Server started. ({}ms)", System.currentTimeMillis() - time);
    }

    public synchronized void stop() {
        checkRunning();

        this.stop = true;
    }

    private synchronized void doStop() {
        checkRunning();

        this.running = false;

        try {
            connection.close();
        } catch (IOException e) {
            log.error("Failed to close server connection.", e);
        }

        ticker.close();

        log.info("Server closed.");
    }

    private void checkRunning() {
        if (!running) {
            throw new IllegalStateException("Server is not running!");
        }
    }


    private synchronized void tick() {
        if (!running) {
            return;
        }

        if (stop) {
            doStop();
            return;
        }

        // Process tick
        try {
            connection.tick();
        } catch (Throwable t) {
            log.error("Failed to process tick.", t);
        }
    }

    private void registerCommands() {
        new ListCommand(this);
        new StopCommand(this);
    }


    public void broadcast(@NotNull String message) {
        broadcast(Component.text(message));
    }

    public void broadcast(@NotNull Component message) {
        for (Player player : connection.getOnlinePlayers().values()) {
            player.sendMessage(message);
        }

        log.info(message.toConsoleString());
    }


    public ServerConnection getConnection() {
        return connection;
    }

    public CommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public long getCurrentTick() {
        return currentTick.get();
    }

    public boolean isRunning() {
        return running;
    }
}
