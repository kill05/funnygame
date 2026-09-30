package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.server.Server;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public class ConsoleCommandSender implements CommandSender {

    private final Server server;

    public ConsoleCommandSender(Server server) {
        this.server = server;
    }


    @Override
    public void sendMessage(@NotNull Component message) {
        System.out.println(message.toConsoleString());
    }

    @Override
    public CommandManager<CommandSender> getCommandManager() {
        return server.getCommandManager();
    }
}
