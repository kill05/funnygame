package com.github.kill05.funnygame.component;

import org.checkerframework.common.returnsreceiver.qual.This;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ComponentBuilder {

    private final List<Component> components;
    private final Map<Decoration, Boolean> decorations;
    private TextColor textColor;
    private TextColor backgroundColor;

    public ComponentBuilder() {
        this.components = new ArrayList<>();
        this.decorations = new HashMap<>();
    }

    @This
    public ComponentBuilder append(@NotNull Component... components) {
        Collections.addAll(this.components, components);
        return this;
    }

    @This
    public ComponentBuilder textColor(@Nullable TextColor textColor) {
        this.textColor = textColor;
        return this;
    }

    @This
    public ComponentBuilder backgroundColor(@Nullable TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    @This
    public ComponentBuilder decorate(@NotNull Decoration... decorations) {
        for (Decoration decoration : decorations) {
            this.decorations.put(decoration, true);
        }

        return this;
    }

    @This
    public Component build() {
        return new TextComponent(
                "",
                textColor,
                backgroundColor,
                Collections.unmodifiableMap(decorations),
                Collections.unmodifiableList(components)
        );
    }
}
