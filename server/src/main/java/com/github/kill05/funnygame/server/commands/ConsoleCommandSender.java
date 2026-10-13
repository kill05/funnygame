package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.server.Server;
import com.github.kill05.funnygame.server.ServerTerminal;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommandSender implements CommandSender {

    private final Server server;
    private final ServerTerminal terminal;

    public ConsoleCommandSender(Server server, ServerTerminal terminal) {
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
    public CommandManager<CommandSender> getCommandManager() {
        return server.getCommandManager();
    }
}
