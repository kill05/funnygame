package com.github.kill05.funnygame.commands.parsers;

import com.github.kill05.funnygame.player.Player;
import com.github.kill05.funnygame.player.PlayerList;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.parser.ParserDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;


public class PlayerParser<C> extends PlayerParserBase<C> {

    @SuppressWarnings("unchecked")
    public static <C> ParserDescriptor<C, Player> parser(PlayerList list) {
        return (ParserDescriptor<C, Player>) new PlayerParser<>(list).asParser();
    }

    @SuppressWarnings("unchecked")
    public static <C> CommandComponent.Builder<C, Player> argument(PlayerList list, CloudKey<Player> key, boolean defaultToSender) {
        return (CommandComponent.Builder<C, Player>) new PlayerParser<>(list).asArgument(key, defaultToSender);
    }


    public PlayerParser(PlayerList list) {
        super(list);
    }


    @Override
    public CompletableFuture<Player> resultFromUsername(@NotNull String username) {
        return CompletableFuture.completedFuture(playerList.getOnlinePlayer(username));
    }
}
