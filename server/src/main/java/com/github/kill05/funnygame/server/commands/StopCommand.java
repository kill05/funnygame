package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.server.Server;
import org.jetbrains.annotations.NotNull;

public class StopCommand extends ServerCommand {

    public StopCommand(@NotNull Server server) {
        super(server, "stop", "close");

        register(builder.handler(_ -> {
            server.stop();
        }));
    }

}
