package com.github.kill05.funnygame.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;

public interface PlayerList extends Iterable<Player> {

    /**
     * Returns an immutable view of the online players.
     *
     * @return the players
     */
    @NotNull
    Collection<Player> getOnlinePlayers();

    @Nullable
    Player getOnlinePlayer(@NotNull String player);

    @Override
    default @NotNull Iterator<Player> iterator() {
        return getOnlinePlayers().iterator();
    }
}
