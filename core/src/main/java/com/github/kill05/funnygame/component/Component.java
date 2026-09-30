package com.github.kill05.funnygame.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed interface Component permits TextComponent {

    TextComponent EMPTY = text("");
    TextComponent SPACE = text(" ");
    TextComponent NEW_LINE = text("\n");

    static TextComponent empty() {
        return Component.EMPTY;
    }

    static TextComponent space() {
        return Component.SPACE;
    }

    static TextComponent newLine() {
        return Component.NEW_LINE;
    }


    static ComponentBuilder text() {
        return new ComponentBuilder();
    }


    static TextComponent text(@NotNull String text) {
        return new TextComponent(text);
    }

    static TextComponent text(@NotNull String text, @NotNull TextColor textColor) {
        return new TextComponent(text, textColor, null, Map.of(), List.of());
    }

    static TextComponent text(@NotNull String text, @NotNull TextColor textColor, @NotNull Decoration... decorations) {
        Map<Decoration, Boolean> map = createDecorationMap(decorations);

        return new TextComponent(text, textColor, null, map, List.of());
    }

    static TextComponent text(@NotNull String text, @NotNull TextColor textColor, @NotNull TextColor backgroundColor) {
        return new TextComponent(text, textColor, backgroundColor, Map.of(), List.of());
    }

    static TextComponent text(
            @NotNull String text,
            @NotNull TextColor textColor,
            @NotNull TextColor backgroundColor,
            @NotNull Decoration... decorations
    ) {
        Map<Decoration, Boolean> map = createDecorationMap(decorations);

        return new TextComponent(text, textColor, backgroundColor, map, List.of());
    }

    private static @NotNull Map<Decoration, Boolean> createDecorationMap(@NotNull Decoration[] decorations) {
        Map<Decoration, Boolean> map = new HashMap<>();

        for (Decoration decoration : decorations) {
            map.put(decoration, true);
        }

        return Collections.unmodifiableMap(map);
    }


    @NotNull
    Component textColor(@Nullable TextColor textColor);

    @NotNull
    Component backgroundColor(@Nullable TextColor backgroundColor);

    @NotNull
    Component decorate(@NotNull Decoration... decorations);
// <3

    @NotNull
    Component decorateIfAbsent(@NotNull Decoration... decorations);

    @NotNull
    Component append(@NotNull Component... components);


    @NotNull
    Map<Decoration, Boolean> decorations();

    @NotNull
    default Component append(@NotNull String text) {
        return append(text(text));
    }


    /**
     * Transforms this component into a string with ANSI codes
     * that can be sent to the console.
     *
     * @return the string
     */
    @NotNull
    String toConsoleString();
}
