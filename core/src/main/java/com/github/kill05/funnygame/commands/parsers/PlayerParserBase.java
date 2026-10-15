package com.github.kill05.funnygame.commands.parsers;

import com.github.kill05.funnygame.player.Player;
import com.github.kill05.funnygame.player.PlayerList;
import com.github.kill05.funnygame.player.PlayerNameIterator;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class PlayerParserBase<C> implements ArgumentParser.FutureArgumentParser<C, Player>, BlockingSuggestionProvider<C> {

    private static final ArgumentParseResult<?> NO_PLAYER = ArgumentParseResult.failure(new IllegalArgumentException("Please provide a player."));

    public static <C> SuggestionProvider<C> playerSuggestions(@NotNull PlayerList list) {
        return SuggestionProvider.suggesting(() -> list.getOnlinePlayers()
                .stream()
                .map(player -> Suggestion.suggestion(player.getUsername()))
                .iterator()
        );
    }


    protected final PlayerList playerList;

    public PlayerParserBase(PlayerList playerList) {
        this.playerList = playerList;
    }


    /**
     * Returns the player result from a username string, or {@code null} if the username is invalid.
     *
     * @param username the username
     * @return the result, {@code null} if invalid
     */
    public abstract CompletableFuture<@Nullable Player> resultFromUsername(@NotNull String username);

    public ParserDescriptor<C, Player> asParser() {
        return ParserDescriptor.of(this, Player.class);
    }

    public CommandComponent.Builder<C, Player> asArgument(CloudKey<Player> key, boolean defaultToSender) {
        CommandComponent.Builder<C, Player> builder = CommandComponent.builder(key, asParser());

        if (defaultToSender) {
            builder.optional(ctx -> {
                C sender = ctx.sender();
                if (sender instanceof Player player) {
                    return ArgumentParseResult.success(player);
                }

                return noPlayer();
            });
        }

        return builder.suggestionProvider(playerSuggestions());
    }

    public SuggestionProvider<C> playerSuggestions() {
        return playerSuggestions(playerList);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull ArgumentParseResult<Player>> parseFuture(@NotNull CommandContext<C> ctx, @NotNull CommandInput input) {
        String username = input.readString();

        return resultFromUsername(username).thenApply(player -> {
            if (player == null) {
                return invalidPlayer(username);
            }

            return ArgumentParseResult.success(player);
        });
    }

    @Override
    public @NotNull Iterable<? extends Suggestion> suggestions(@NotNull CommandContext<C> context, @NotNull CommandInput input) {
        return (Iterable<Suggestion>) () -> new PlayerNameIterator<>(context, input, playerList.iterator(), (_, _) -> true);
    }


    protected static <P> @NotNull ArgumentParseResult<P> invalidPlayer(String input) {
        return ArgumentParseResult.failure(new IllegalArgumentException("Invalid player: " + input));
    }

    @SuppressWarnings("unchecked")
    protected static <P> @NotNull ArgumentParseResult<P> noPlayer() {
        return (ArgumentParseResult<P>) NO_PLAYER;
    }
}
