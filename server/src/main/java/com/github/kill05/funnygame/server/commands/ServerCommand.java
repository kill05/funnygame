package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.commands.GameCommand;
import com.github.kill05.funnygame.server.Server;
import org.jetbrains.annotations.NotNull;

public abstract class ServerCommand extends GameCommand {

    protected final Server server;

    protected ServerCommand(@NotNull Server server, @NotNull String name, @NotNull String... aliases) {
        super(server.getCommandManager(), name, aliases);
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}
