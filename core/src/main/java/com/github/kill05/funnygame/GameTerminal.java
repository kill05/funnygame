package com.github.kill05.funnygame;

import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.component.ComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.PrintAboveWriter;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class GameTerminal implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(GameTerminal.class);
    protected final Terminal terminal;
    protected final LineReader reader;
    protected final PrintAboveWriter writer;
    protected final ExecutorService executor;


    public GameTerminal() throws IOException {
        this.terminal = TerminalBuilder.builder().system(true).build();
        this.reader = LineReaderBuilder.builder().terminal(terminal).build();
        this.writer = new PrintAboveWriter(reader);
        this.executor = Executors.newSingleThreadExecutor();

        terminal.handle(Terminal.Signal.INT, _ -> handleInterruptSignal());
    }


    public void start() {
        // Clear (Remove warning about restricted method)
        //terminal.puts(InfoCmp.Capability.cursor_address, 0, 0);
        //terminal.puts(InfoCmp.Capability.clr_eos);

        // Log unsupported features
        if (!supportsStatus()) {
            log.warn("Status not supported. Status messages will be sent to terminal output instead.");
        }

        if (!supportsClearLine()) {
            log.warn("Line clearing not supported. Input messages sent from the client will be echoed.");
        }
    }

    @Override
    public void close() throws IOException {
        executor.shutdownNow();
        terminal.close();
    }


    /**
     * Called when an interrupt signal is detected, i.e. the user pressed {@code Ctrl+C}.
     */
    protected abstract void handleInterruptSignal();

    public void sendMessage(@NotNull Component message) {
        sendMessage(ComponentSerializer.toAnsi(message));
    }

    public void sendMessage(@NotNull String message) {
        reader.printAbove(message + "\n");
    }


    /**
     * Requests the user to input something once in the terminal.
     * Returns a future completed with the provided string.
     *
     * @return the input read from console
     */
    public synchronized CompletableFuture<String> requestInput() {
        return CompletableFuture.supplyAsync(this::requestInputAndWait, executor);
    }

    public String requestInputAndWait() {
        String input;

        try {
            input = reader.readLine("> ");
        } catch (IllegalStateException e) {
            if (e.getMessage().equals("Terminal has been closed")) {
                return ""; // Ignore and exit
            }

            throw e;
        } catch (UserInterruptException e) {
            handleInterruptSignal();
            return "";
        }

        // Clear line
        if (supportsClearLine()) {
            synchronized (this) {
                terminal.puts(InfoCmp.Capability.cursor_up, 0);
                terminal.puts(InfoCmp.Capability.clr_eos);
                terminal.flush();
                terminal.writer().flush();
            }
        }

        return input;
    }


    public synchronized boolean isRequestingInput() {
        return reader.isReading();
    }

    public synchronized void interruptInputRequest() {
        throw new UnsupportedOperationException();
    }


    public boolean supportsStatus() {
        return terminal.getStringCapability(InfoCmp.Capability.change_scroll_region) != null
                && terminal.getStringCapability(InfoCmp.Capability.save_cursor) != null
                && terminal.getStringCapability(InfoCmp.Capability.restore_cursor) != null
                && terminal.getStringCapability(InfoCmp.Capability.cursor_address) != null;
    }

    public boolean supportsClearLine() {
        return terminal.getStringCapability(InfoCmp.Capability.cursor_up) != null
                && terminal.getStringCapability(InfoCmp.Capability.clr_eol) != null;
    }


    public Terminal getTerminal() {
        return terminal;
    }

    public LineReader getReader() {
        return reader;
    }

    public PrintAboveWriter getWriter() {
        return writer;
    }
}
