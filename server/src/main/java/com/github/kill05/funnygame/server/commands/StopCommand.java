package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.server.GameServer;
import org.jetbrains.annotations.NotNull;

public class StopCommand extends ServerCommand {

    public StopCommand(@NotNull GameServer server) {
        super(server, "stop", "close");

        register(builder.handler(_ -> {
            server.stop();
        }));
    }

}
