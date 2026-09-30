package com.github.kill05.funnygame.cards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FrenchCardDeck extends CardDeck {

    public static final List<Card> DEFAULT = generateDeck();

    private static List<Card> generateDeck() {
        List<Card> cards = new ArrayList<>();

        for (Suit suit : Suit.VALUES) {
            for (Rank rank : Rank.VALUES) {
                cards.add(new Card(suit, rank));
            }
        }

        return cards;
    }

    @Override
    public Collection<Card> getDefaultCards() {
        return DEFAULT;
    }
}
