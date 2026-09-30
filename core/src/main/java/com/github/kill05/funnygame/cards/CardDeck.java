package com.github.kill05.funnygame.cards;

import java.util.Collection;
import java.util.Collections;
import java.util.Stack;

public abstract class CardDeck {

    protected final Stack<Card> cards;

    public CardDeck() {
        this.cards = new Stack<>();
        reset();
    }


    public void reset() {
        cards.clear();
        cards.addAll(getDefaultCards());
    }

    public void resetAndShuffle() {
        reset();
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card popCard() {
        return cards.pop();
    }

    public Card peekCard() {
        return cards.peek();
    }


    public abstract Collection<Card> getDefaultCards();


    public Stack<Card> getRemainingCards() {
        return cards;
    }

    public int getRemainingAmount() {
        return cards.size();
    }

    public boolean hasCard() {
        return !cards.isEmpty();
    }

}
