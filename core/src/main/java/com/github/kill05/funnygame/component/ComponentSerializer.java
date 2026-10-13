package com.github.kill05.funnygame.component;

import com.github.kill05.funnygame.Ansi;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ComponentSerializer {

    private ComponentSerializer() {

    }

    /**
     * Transforms this component into a string with ANSI codes
     * that can be sent to the console.
     *
     * @return the string
     */
    public static @NotNull String toAnsi(@NotNull Component component) {
        StringBuilder builder = new StringBuilder();

        componentToAnsi(component, builder);
        builder.append(Ansi.RESET);

        return builder.toString();
    }

    private static void componentToAnsi(@NotNull Component component, @NotNull StringBuilder builder) {
        if (component instanceof TextComponent text) {
            textComponentToAnsi(text, builder);
            return;
        }

        throw new IllegalArgumentException("Unsupported component: " + component + ".");
    }

    private static void textComponentToAnsi(@NotNull TextComponent component, @NotNull StringBuilder builder) {
        // Decoration
        for (Map.Entry<Decoration, Boolean> entry : component.decorations().entrySet()) {
            Decoration decoration = entry.getKey();
            Boolean state = entry.getValue();

            if (state == null) {
                continue;
            }

            if (state) {
                builder.append(decoration.ansi());
            } else {
                builder.append(decoration.resetAnsi());
            }
        }

        // Color
        TextColor textColor = component.textColor();
        if (textColor != null) {
            builder.append(textColor.textAnsi());
        }

        TextColor backgroundColor = component.backgroundColor();
        if (backgroundColor != null) {
            builder.append(backgroundColor.backgroundAnsi());
        }

        // Text and children text
        builder.append(component.text());

        for (Component child : component.children()) {
            componentToAnsi(child, builder);
            builder.append(Ansi.RESET);
        }
    }
}
