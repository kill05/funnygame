package com.github.kill05.funnygame.commands;

import com.github.kill05.funnygame.component.Component;
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

    boolean hasPermission(@NotNull String permission);

    void executeCommand(@NotNull String input);

}
