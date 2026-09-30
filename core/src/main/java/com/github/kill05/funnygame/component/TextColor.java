package com.github.kill05.funnygame.component;


import com.github.kill05.funnygame.Ansi;

import java.util.ArrayList;
import java.util.List;

public final class TextColor {

    private static final List<TextColor> COLORS = new ArrayList<>();

    public static final TextColor BLACK = new TextColor(Ansi.BLACK, Ansi.BG_BLACK);
    public static final TextColor RED = new TextColor(Ansi.RED, Ansi.BG_RED);
    public static final TextColor GREEN = new TextColor(Ansi.GREEN, Ansi.BG_GREEN);
    public static final TextColor YELLOW = new TextColor(Ansi.YELLOW, Ansi.BG_YELLOW);
    public static final TextColor BLUE = new TextColor(Ansi.BLUE, Ansi.BG_BLUE);
    public static final TextColor MAGENTA = new TextColor(Ansi.MAGENTA, Ansi.BG_MAGENTA);
    public static final TextColor CYAN = new TextColor(Ansi.CYAN, Ansi.BG_CYAN);
    public static final TextColor WHITE = new TextColor(Ansi.WHITE, Ansi.BG_WHITE);

    public static final TextColor BRIGHT_BLACK = new TextColor(Ansi.BRIGHT_BLACK, Ansi.BG_BRIGHT_BLACK);
    public static final TextColor BRIGHT_RED = new TextColor(Ansi.BRIGHT_RED, Ansi.BG_BRIGHT_RED);
    public static final TextColor BRIGHT_GREEN = new TextColor(Ansi.BRIGHT_GREEN, Ansi.BG_BRIGHT_GREEN);
    public static final TextColor BRIGHT_YELLOW = new TextColor(Ansi.BRIGHT_YELLOW, Ansi.BG_BRIGHT_YELLOW);
    public static final TextColor BRIGHT_BLUE = new TextColor(Ansi.BRIGHT_BLUE, Ansi.BG_BRIGHT_BLUE);
    public static final TextColor BRIGHT_MAGENTA = new TextColor(Ansi.BRIGHT_MAGENTA, Ansi.BG_BRIGHT_MAGENTA);
    public static final TextColor BRIGHT_CYAN = new TextColor(Ansi.BRIGHT_CYAN, Ansi.BG_BRIGHT_CYAN);
    public static final TextColor BRIGHT_WHITE = new TextColor(Ansi.BRIGHT_WHITE, Ansi.BG_BRIGHT_WHITE);


    private static int currentId = 0;

    public static TextColor fromId(int id) {
        if (id < 0 || id >= COLORS.size()) {
            return null;
        }

        return COLORS.get(id);
    }

    private final int id;
    private final String textAnsi;
    private final String backgroundAnsi;

    public TextColor(String textAnsi, String backgroundAnsi) {
        this.textAnsi = textAnsi;
        this.backgroundAnsi = backgroundAnsi;
        this.id = currentId++;

        COLORS.add(this);
    }

    public String textAnsi() {
        return textAnsi;
    }

    public String backgroundAnsi() {
        return backgroundAnsi;
    }

    public int id() {
        return id;
    }
}
