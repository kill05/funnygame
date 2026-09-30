package com.github.kill05.funnygame.cards;

public class CardUnicode {

    /**
     * Retrieves a card Unicode string by its Suit and an integer number (1 to 14).
     * 1 = Ace, 11 = Jack, 12 = Knight, 13 = Queen, 14 = King.
     */
    public static String getCard(Suit suit, Rank rank) {
        int codePoint = suit.getBaseUnicode() + rank.getUnicodeOffset();
        return new String(Character.toChars(codePoint));
    }

    public static final String BACK = "\uD83C\uDC20";                 // U+1F0A0
    public static final String BLACK_JOKER = "\uD83C\uDCF9";           // U+1F0CF
    public static final String WHITE_JOKER = "\uD83C\uDCBF";           // U+1F0BF
    public static final String FOOL = "\uD83C\uDCE0";                  // U+1F0E0 (Tarot)
}
