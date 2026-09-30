package com.github.kill05.funnygame.cards;

import com.github.kill05.funnygame.component.Component;


public final class Card {

    private final Suit suit;
    private final Rank rank;
    private final Component display;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.display = Component.text(String.valueOf(rank.getDisplayChar()), suit.getColor());
    }

    public Component getDisplay() {
        return display;
    }

    public Suit suit() {
        return suit;
    }

    public Rank rank() {
        return rank;
    }

}
