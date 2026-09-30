package com.github.kill05.funnygame.commands;

import com.github.kill05.funnygame.component.Component;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public interface CommandSender {

    /**
     * Sends a chat message to the command sender
     *
     * @param message the message
     */
    default void sendMessage(String message) {
        sendMessage(Component.text(message));
    }

    /**
     * Sends a chat message to the command sender
     *
     * @param message the message
     */
    void sendMessage(@NotNull Component message);

    CommandManager<CommandSender> getCommandManager();

    default void executeCommand(@NotNull String input) {
        if (input.startsWith("/")) {
            input = input.substring(1);
        }

        CommandManager<CommandSender> manager = getCommandManager();
        manager.commandExecutor().executeCommand(this, input);
    }

}
