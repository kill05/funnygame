package com.github.kill05.funnygame.player;

import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.suggestion.Suggestion;

import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.BiPredicate;

public class PlayerNameIterator<C> implements Iterator<Suggestion> {

    private final CommandContext<C> context;
    private final String input;
    private final Iterator<? extends Player> playerIterator;
    private final BiPredicate<CommandContext<C>, Player> filter;
    private String next;

    public PlayerNameIterator(
            CommandContext<C> context,
            CommandInput input,
            Iterator<? extends Player> iterator,
            BiPredicate<CommandContext<C>, Player> filter
    ) {
        this.context = context;
        this.input = input.peekString();
        this.playerIterator = iterator;
        this.filter = filter;
        advance();
    }

    private void advance() {
        next = null;

        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            String username = player.getUsername();

            if (username.toLowerCase(Locale.ENGLISH).startsWith(input.toLowerCase(Locale.ENGLISH)) && filter.test(context, player)) {
                next = username;
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Suggestion next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        String current = next;
        advance();
        return Suggestion.suggestion(current);
    }
}
