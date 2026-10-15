package com.github.kill05.funnygame.server.commands.parsers;

import com.github.kill05.funnygame.server.game.GameController;
import com.github.kill05.funnygame.server.game.GameType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.BlockingSuggestionProvider;
import org.incendo.cloud.suggestion.Suggestion;

import static org.incendo.cloud.parser.ArgumentParseResult.failure;
import static org.incendo.cloud.parser.ArgumentParseResult.success;

public class GameTypeParser<C> implements ArgumentParser<C, GameType>, BlockingSuggestionProvider<C> {

    public static final GameTypeParser<?> PARSER = new GameTypeParser<>();

    @SuppressWarnings("unchecked")
    public static <C> ParserDescriptor<C, GameType> parser() {
        return (ParserDescriptor<C, GameType>) ParserDescriptor.of(PARSER, GameType.class);
    }

    @Override
    public @NonNull ArgumentParseResult<GameType> parse(@NonNull CommandContext<C> ctx, @NonNull CommandInput input) {
        String s = input.readString();
        GameType type = GameController.GAME_TYPE_MAP.get(s);

        if (type == null) {
            return failure(new IllegalArgumentException("Invalid game type: " + s));
        }

        return success(type);
    }

    @Override
    public @NonNull Iterable<? extends @NonNull Suggestion> suggestions(
            @NonNull CommandContext<C> ctx,
            @NonNull CommandInput input
    ) {
        return GameController.GAME_TYPE_MAP.keySet().stream()
                .map(Suggestion::suggestion)
                .toList();
    }
}
