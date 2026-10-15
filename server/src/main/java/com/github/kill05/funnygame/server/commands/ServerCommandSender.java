package com.github.kill05.funnygame.server.commands;

import com.github.kill05.funnygame.CommandUtils;
import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.server.GameServer;
import org.incendo.cloud.execution.CommandExecutor;
import org.jetbrains.annotations.NotNull;

public interface ServerCommandSender extends CommandSender {

    GameServer server();

    default void executeCommand(@NotNull String input) {
        CommandExecutor<CommandSender> executor = server().getCommandManager().commandExecutor();
        executor.executeCommand(this, CommandUtils.removeSlash(input));
    }
}
