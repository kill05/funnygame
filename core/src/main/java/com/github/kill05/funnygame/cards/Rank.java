package com.github.kill05.funnygame.cards;

/**
 * Represents card ranks from Ace (1) to King (14), including the historical Knight (12).
 */
public enum Rank {
    ACE('A', 1),
    TWO('2', 2),
    THREE('3', 3),
    FOUR('4', 4),
    FIVE('5', 5),
    SIX('6', 6),
    SEVEN('7', 7),
    EIGHT('8', 8),
    NINE('9', 9),
    TEN('T', 10),
    JACK('J', 11),
    QUEEN('Q', 13),
    KING('K', 14);

    public static final Rank[] VALUES = values();

    private final char displayChar;
    private final int unicodeOffset;

    Rank(char displayChar, int unicodeOffset) {
        this.unicodeOffset = unicodeOffset;
        this.displayChar = displayChar;
    }


    public int getUnicodeOffset() {
        return unicodeOffset;
    }

    public char getDisplayChar() {
        return displayChar;
    }
}