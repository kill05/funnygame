package com.github.kill05.funnygame.client;

import com.github.kill05.funnygame.Player;
import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.packet.ClientChatMessagePacket;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.exception.NoSuchCommandException;
import org.jetbrains.annotations.NotNull;

public class ClientPlayer extends Player {

    private final Client client;

    public ClientPlayer(@NotNull Client client, @NotNull String username, @NotNull ClientPlayerConnection connection) {
        super(username, connection);
        this.client = client;
    }


    /**
     * Called from the reader thread when the client wrote a message to the chat
     * This method differs from {@link CommandSender#sendMessage(Component)},
     * as the other method sends a chat message to this player coming from another source.
     *
     * @param message the message
     */
    public void onChatMessageSent(@NotNull String message) {
        if (message.isEmpty()) {
            return;
        }

        sendPacket(new ClientChatMessagePacket(message));
    }

    /**
     * Called from the main thread when a key is pressed
     * and the terminal is not in chat mode
     *
     * @param c the key
     */
    public void onKeyPressed(char c) {

    }


    @Override
    public void sendMessage(@NotNull Component message) {
        client.sendMessage(message);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

    @Override
    public void executeCommand(@NotNull String input) {
        if (!input.startsWith("/")) {
            input = "/" + input;
        }

        CommandManager<CommandSender> manager = client.getCommandManager();
        try {
            // Try to execute client command
            manager.commandExecutor().executeCommand(this, input);
        } catch (NoSuchCommandException e) {
            // If the command does not exist, execute the command on the server
            onChatMessageSent(input);
        }
    }


}
