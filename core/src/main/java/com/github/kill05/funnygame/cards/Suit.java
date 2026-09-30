package com.github.kill05.funnygame.cards;


import com.github.kill05.funnygame.component.TextColor;

public enum Suit {
    SPADES(TextColor.BLACK, 0x1F0A0),
    HEARTS(TextColor.RED, 0x1F0B0),
    DIAMONDS(TextColor.RED, 0x1F0C0),
    CLUBS(TextColor.BLACK, 0x1F0D0);

    public static final Suit[] VALUES = values();

    private final TextColor color;
    private final int baseUnicode;

    Suit(TextColor color, int baseCodePoint) {
        this.color = color;
        this.baseUnicode = baseCodePoint;
    }

    public int getBaseUnicode() {
        return baseUnicode;
    }

    public TextColor getColor() {
        return color;
    }
}