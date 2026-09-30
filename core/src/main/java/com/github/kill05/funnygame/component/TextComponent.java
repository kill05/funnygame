package com.github.kill05.funnygame.component;

import com.github.kill05.funnygame.Ansi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public record TextComponent(
        @NotNull String text,
        @Nullable TextColor textColor,
        @Nullable TextColor backgroundColor,
        @NotNull Map<Decoration, Boolean> decorations,
        @NotNull List<Component> children
) implements Component {

    public TextComponent(@NotNull String text) {
        this(text, null, null, Map.of(), List.of());
    }

    @Override
    public @NotNull Component textColor(@Nullable TextColor textColor) {
        return new TextComponent(text, textColor, backgroundColor, decorations, children);
    }

    @Override
    public @NotNull Component backgroundColor(@Nullable TextColor backgroundColor) {
        return new TextComponent(text, textColor, backgroundColor, decorations, children);
    }

    @Override
    public @NotNull Component decorate(@NotNull Decoration... decorations) {
        HashMap<Decoration, Boolean> map = new HashMap<>(this.decorations);

        for (Decoration decoration : decorations) {
            map.put(decoration, true);
        }

        return new TextComponent(text, textColor, backgroundColor, map, children);
    }

    @Override
    public @NotNull Component decorateIfAbsent(@NotNull Decoration... decorations) {
        Map<Decoration, Boolean> map = new HashMap<>(this.decorations);
        boolean modified = false;

        for (Decoration decoration : decorations) {
            if (!this.decorations.containsKey(decoration)) {
                map.put(decoration, true);
            }
        }

        if (!modified) {
            return this;
        }

        return new TextComponent(text, textColor, backgroundColor, map, children);
    }

    @Override
    public @NotNull Component append(@NotNull Component... components) {
        ArrayList<Component> list = new ArrayList<>(children);
        Collections.addAll(list, components);
        return new TextComponent(text, textColor, backgroundColor, decorations, Collections.unmodifiableList(list));
    }

    @Override
    public @NotNull String toConsoleString() {
        StringBuilder builder = new StringBuilder();

        toConsoleString(builder);
        builder.append(Ansi.RESET);

        return builder.toString();
    }

    private void toConsoleString(@NotNull StringBuilder builder) {
        // Decoration
        for (Map.Entry<Decoration, Boolean> entry : decorations.entrySet()) {
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
        if (textColor != null) {
            builder.append(textColor.textAnsi());
        }

        if (backgroundColor != null) {
            builder.append(backgroundColor.backgroundAnsi());
        }

        // Text and children text
        builder.append(text);

        for (Component child : children) {
            builder.append(child.toConsoleString());
        }
    }
}
