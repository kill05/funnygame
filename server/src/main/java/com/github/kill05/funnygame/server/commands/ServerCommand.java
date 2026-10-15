package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.commands.CommandBase;
import com.github.kill05.funnygame.server.GameServer;
import org.jetbrains.annotations.NotNull;

public abstract class ServerCommand extends CommandBase {

    protected final GameServer server;

    protected ServerCommand(@NotNull GameServer server, @NotNull String name, @NotNull String... aliases) {
        super(server.getCommandManager(), name, aliases);
        this.server = server;
    }

    public GameServer getServer() {
        return server;
    }
}
