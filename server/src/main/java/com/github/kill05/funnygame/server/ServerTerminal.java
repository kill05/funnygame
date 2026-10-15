package com.github.kill05.funnygame.server;

import com.github.kill05.funnygame.GameTerminal;
import com.github.kill05.funnygame.server.commands.ConsoleCommandSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ServerTerminal extends GameTerminal {

    private static final Logger log = LoggerFactory.getLogger(ServerTerminal.class);
    private final GameServer server;
    private final ConsoleCommandSender sender;

    public ServerTerminal(GameServer server) throws IOException {
        this.server = server;
        this.sender = new ConsoleCommandSender(server, this);
    }

    @Override
    public void start() {
        super.start();

        executor.submit(() -> {
            while (!server.isStopping()) {
                try {
                    String input = requestInputAndWait();
                    sender.executeCommand(input);
                } catch (Exception e) {
                    log.warn("Failed to handle console input.", e);
                }
            }
        });
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    protected void handleInterruptSignal() {
        sendMessage("Interrupt signal detected! Closing server...");
        server.stop();
    }

    @Override
    public synchronized boolean isRequestingInput() {
        return true;
    }
}
