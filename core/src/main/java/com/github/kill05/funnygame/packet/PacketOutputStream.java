package com.github.kill05.funnygame.packet;

import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.component.Decoration;
import com.github.kill05.funnygame.component.TextColor;
import com.github.kill05.funnygame.component.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class PacketOutputStream extends DataOutputStream {

    /**
     * Creates a new data output stream to write data to the specified
     * underlying output stream. The counter {@code written} is
     * set to zero.
     *
     * @param out the underlying output stream, to be saved for later
     *            use.
     * @see FilterOutputStream#out
     */
    public PacketOutputStream(OutputStream out) {
        super(out);
    }

    public void writeComponent(@NotNull Component component) throws IOException {
        if (!(component instanceof TextComponent(
                String text,
                TextColor textColor,
                TextColor bgColor,
                Map<Decoration, Boolean> decorations,
                List<Component> children
        ))) {
            throw new IOException("Cannot serialize component of type: " + component.getClass());
        }

        writeUTF(text);
        writeByte(textColor != null ? textColor.id() : -1);
        writeByte(bgColor != null ? bgColor.id() : -1);

        writeByte(decorations.size());
        for (Map.Entry<Decoration, Boolean> entry : decorations.entrySet()) {
            Boolean state = entry.getValue();
            if (state == null) {
                throw new IOException("Null value for TextComponent decoration");
            }

            writeByte(entry.getKey().id());
            writeBoolean(state);
        }

        writeInt(children.size());
        for (Component child : children) {
            writeComponent(child);
        }
    }

}
