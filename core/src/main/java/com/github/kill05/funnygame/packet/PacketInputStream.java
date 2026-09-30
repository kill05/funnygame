package com.github.kill05.funnygame.packet;

import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.component.Decoration;
import com.github.kill05.funnygame.component.TextColor;
import com.github.kill05.funnygame.component.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketInputStream extends DataInputStream {

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param in the specified input stream
     */
    public PacketInputStream(@NotNull InputStream in) {
        super(in);
    }

    public Component readComponent() throws IOException {
        String text = readUTF();
        TextColor textColor = readTextColor();
        TextColor bgColor = readTextColor();

        int decorationAmount = readByte();
        Map<Decoration, Boolean> decorationMap = new HashMap<>();
        for (int i = 0; i < decorationAmount; i++) {
            Decoration decoration = readDecoration();
            decorationMap.put(decoration, readBoolean());
        }

        int childAmount = readInt();
        List<Component> components = new ArrayList<>();
        for (int i = 0; i < childAmount; i++) {
            components.add(readComponent());
        }

        return new TextComponent(text, textColor, bgColor, decorationMap, components);
    }

    private @Nullable Decoration readDecoration() throws IOException {
        byte id = readByte();
        if (id == -1) {
            return null;
        }

        Decoration decoration = Decoration.fromId(id);

        if (decoration == null) {
            throw new IOException("Invalid Decoration id: " + id + ".");
        }

        return decoration;
    }

    private @Nullable TextColor readTextColor() throws IOException {
        byte id = readByte();
        if (id == -1) {
            return null;
        }

        TextColor color = TextColor.fromId(id);

        if (color == null) {
            throw new IOException("Invalid TextColor id: " + id + ".");
        }

        return color;
    }
}
