package com.github.kill05.funnygame.server;

import com.github.kill05.funnygame.GameConstants;
import com.github.kill05.funnygame.player.Player;
import com.github.kill05.funnygame.player.PlayerList;
import com.github.kill05.funnygame.scheduler.GameScheduler;
import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.commands.GameCommandManager;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.server.commands.GameCommand;
import com.github.kill05.funnygame.server.commands.ListCommand;
import com.github.kill05.funnygame.server.commands.StopCommand;
import com.github.kill05.funnygame.server.connection.ServerConnection;
import com.github.kill05.funnygame.server.game.GameController;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GameServer {

    private static final Logger log = LoggerFactory.getLogger(GameServer.class);

    private final ServerConnection connection;
    private final GameScheduler scheduler;
    private final GameController gameController;
    private final CommandManager<CommandSender> commandManager;
    private final ServerTerminal terminal;
    private volatile boolean running;
    private volatile boolean stop; // Set to true to start the stopping sequence, not when the server HAS stopped

    public GameServer() throws IOException {
        this.connection = new ServerConnection(this);
        this.scheduler = new GameScheduler(GameConstants.TARGET_TPS, this::tick);
        this.gameController = new GameController(this);
        this.commandManager = new GameCommandManager(scheduler);
        this.terminal = new ServerTerminal(this);
        this.running = false;
        this.stop = false;

        // Stop the server gracefully on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!running) {
                return;
            }

            doStop();
        }));
    }

    public synchronized void start() {
        if (running) {
            throw new IllegalStateException("Server is already running!");
        }

        log.info("Starting server...");
        long time = System.currentTimeMillis();

        try {
            registerCommands();

            // Starting sequence
            connection.start();
            scheduler.start();
            gameController.start();

            running = true;
            terminal.start();
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
        this.stop = true;
        this.running = false;

        try {
            terminal.close();
            gameController.close();
            scheduler.close();
            connection.close();
        } catch (Exception e) {
            log.error("Failed to stop server.", e);
            return;
        }

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
        new GameCommand(this);
    }


    public void broadcast(@NotNull String message) {
        broadcast(Component.text(message));
    }

    public void broadcast(@NotNull Component message) {
        for (Player player : connection.getConnectedPlayerMap().values()) {
            player.sendMessage(message);
        }

        log.info(message.toAnsi());
    }


    /**
     * Throws an exception if the method is not called from the ticking thread.
     */
    public void assertTickingThread() throws IllegalCallerException {
        if (Thread.currentThread() != scheduler.getTickingThread()) {
            throw new IllegalCallerException("This method must be called from the ticking thread.");
        }
    }


    public ServerConnection getConnection() {
        return connection;
    }

    public PlayerList getPlayerList() {
        return connection;
    }

    public CommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }

    public GameController getGameController() {
        return gameController;
    }

    public long getCurrentTick() {
        return scheduler.getCurrentTick();
    }

    public boolean isStopping() {
        return stop;
    }

    public boolean isRunning() {
        return running;
    }

    public GameScheduler getScheduler() {
        return scheduler;
    }
}
