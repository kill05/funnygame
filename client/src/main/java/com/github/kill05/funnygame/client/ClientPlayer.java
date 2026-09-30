package com.github.kill05.funnygame.client;

import com.github.kill05.funnygame.Player;
import com.github.kill05.funnygame.commands.CommandSender;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.packet.ClientChatMessagePacket;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public class ClientPlayer extends Player {

    private final Client client;

    public ClientPlayer(@NotNull Client client, @NotNull String username, @NotNull ClientPlayerConnection connection) {
        super(username, connection);
        this.client = client;
    }


    /**
     * Called when the client wrote a message to the chat
     * This method differs from {@link CommandSender#sendMessage(Component)},
     * as the other method sends a chat message to this player coming from another source.
     *
     * @param message the message
     */
    public void onChatMessageSent(@NotNull String message) {
        sendPacket(new ClientChatMessagePacket(message));
    }


    @Override
    public void sendMessage(@NotNull Component message) {
        Client.sendMessage(message);
    }

    @Override
    public void executeCommand(@NotNull String input) {
        if (!input.startsWith("/")) {
            input = "/" + input;
        }

        onChatMessageSent(input);
    }

    @Override
    public CommandManager<CommandSender> getCommandManager() {
        return null;
    }

}
