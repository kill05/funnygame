package com.github.kill05.funnygame.component;

import com.github.kill05.funnygame.Ansi;

import java.util.ArrayList;
import java.util.List;

public final class Decoration {

    private static final List<Decoration> DECORATIONS = new ArrayList<>();

    public static final Decoration BOLD = new Decoration(Ansi.BOLD, Ansi.RESET_BOLD_DIM);
    public static final Decoration DIM = new Decoration(Ansi.DIM, Ansi.RESET_BOLD_DIM);
    public static final Decoration ITALIC = new Decoration(Ansi.ITALIC, Ansi.RESET_ITALIC);
    public static final Decoration UNDERLINE = new Decoration(Ansi.UNDERLINE, Ansi.RESET_UNDERLINE);
    public static final Decoration BLINK = new Decoration(Ansi.BLINK, Ansi.RESET_BLINK);
    public static final Decoration REVERSE = new Decoration(Ansi.INVERSE, Ansi.RESET_INVERSE);
    public static final Decoration HIDDEN = new Decoration(Ansi.HIDDEN, Ansi.RESET_HIDDEN);
    public static final Decoration STRIKETHROUGH = new Decoration(Ansi.STRIKETHROUGH, Ansi.RESET_STRIKETHROUGH);

    private static int currentId;

    public static Decoration fromId(int decoration) {
        if (decoration < 0 || decoration >= DECORATIONS.size()) {
            return null;
        }

        return DECORATIONS.get(decoration);
    }

    private final int id;
    private final String ansi;
    private final String resetAnsi;

    public Decoration(String ansi, String resetAnsi) {
        this.ansi = ansi;
        this.resetAnsi = resetAnsi;
        this.id = currentId++;

        DECORATIONS.add(this);
    }

    public String ansi() {
        return ansi;
    }

    public String resetAnsi() {
        return resetAnsi;
    }

    public int id() {
        return id;
    }

}
