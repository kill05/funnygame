package com.github.kill05.funnygame;

import com.github.kill05.funnygame.component.Component;
import com.github.kill05.funnygame.component.TextColor;
import org.jetbrains.annotations.NotNull;

public final class Messages {

    public static Component success(@NotNull String message) {
        return Component.text("(!) " + message, TextColor.GREEN);
    }

    public static Component info(@NotNull String message) {
        return Component.text("(!) " + message, TextColor.YELLOW);
    }

    public static Component error(@NotNull String message) {
        return Component.text("(!) " + message, TextColor.RED);
    }
}
