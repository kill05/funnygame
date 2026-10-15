package com.github.kill05.funnygame.client;

import com.github.kill05.funnygame.GameTerminal;
import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.component.ComponentBuilder;
import com.github.kill05.funnygame.component.TextColor;
import org.jetbrains.annotations.Nullable;
import org.jline.terminal.Attributes;
import org.jline.utils.AttributedString;
import org.jline.utils.Status;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import static com.github.kill05.funnygame.component.Component.text;

public class ClientTerminal extends GameTerminal implements Closeable {

    private final GameClient client;
    private Component statusMessage;

    public ClientTerminal(GameClient client) throws IOException {
        this.client = client;

        // Change attributes
        Attributes attributes = terminal.getAttributes();
        attributes.setLocalFlag(Attributes.LocalFlag.ECHO, false);
        terminal.setAttributes(attributes);
    }

    @Override
    public void start() {
        super.start();

        // Enable raw mode by default
        // this.originalAttributes = terminal.enterRawMode();
        updateStatusMessage(); // Init status message
    }

    @Override
    public void close() throws IOException {
        removeStatusMessage();
        super.close();
    }

    @Override
    protected void handleInterruptSignal() {
        sendMessage("Interrupt signal detected! Closing client...");
        client.stop();
    }


    public void setStatusMessage(@Nullable Component message) {
        setStatusMessage(message, true);
    }

    public void setStatusMessage(@Nullable Component message, boolean update) {
        this.statusMessage = message;

        if (update) {
            updateStatusMessage();
        }
    }

    /**
     * Updates the status message (the message displayed below the player input)
     * Displays the custom line + the client status line (connected, disconnected, ...)
     */
    public void updateStatusMessage() {
        //todo: remove once status messages are fixed
        if (true) {
            return;
        }

        // Send normal message if terminal does not support status message
        if (!supportsStatus()) {
            if (statusMessage != null) {
                sendMessage(statusMessage);
            }

            return;
        }

        Status status = Status.getStatus(terminal);
        AttributedString clientStatusMessage = new AttributedString("prova"); //new AttributedString(getClientStatusMessage().toAnsi());

        if (statusMessage != null) {
            status.update(List.of(
                    new AttributedString(statusMessage.toAnsi()),
                    clientStatusMessage
            ));
        } else {
            status.update(List.of(clientStatusMessage));
        }

        terminal.flush();
    }

    /**
     * Removes the entire status message from the terminal to clean up before shutdown
     */
    private void removeStatusMessage() {
        Status status = Status.getStatus(terminal);
        status.update(List.of());

        terminal.flush();
    }

    private Component getClientStatusMessage() {
        ComponentBuilder builder = text().append(text("Status: "));

        switch (client.getStatus()) {
            case NOT_RUNNING -> builder.append(text("OFF", TextColor.RED));
            case NOT_CONNECTED -> builder.append(text("Not Connected", TextColor.RED));
            case JOINING -> builder.append(text("Joining", TextColor.YELLOW));
            case CONNECTED -> builder.append(text("Connected", TextColor.GREEN));
        }

        return builder.build();
    }

}
