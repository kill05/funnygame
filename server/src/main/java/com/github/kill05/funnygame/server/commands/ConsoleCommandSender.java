package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.server.GameServer;
import com.github.kill05.funnygame.server.ServerTerminal;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommandSender implements ServerCommandSender {

    private final GameServer server;
    private final ServerTerminal terminal;

    public ConsoleCommandSender(GameServer server, ServerTerminal terminal) {
        this.server = server;
        this.terminal = terminal;
    }


    @Override
    public void sendMessage(@NotNull Component message) {
        terminal.sendMessage(message);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public GameServer server() {
        return server;
    }
}
