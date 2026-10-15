package com.github.kill05.funnygame.commands;

import com.github.kill05.funnygame.player.Player;
import com.github.kill05.funnygame.scheduler.GameScheduler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.InvalidCommandSenderException;
import org.incendo.cloud.exception.NoSuchCommandException;
import org.incendo.cloud.exception.handling.ExceptionController;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.kill05.funnygame.Messages.error;
import static com.github.kill05.funnygame.component.Component.text;

public class GameCommandManager extends CommandManager<CommandSender> {

    private static final Logger log = LoggerFactory.getLogger(GameCommandManager.class);

    public GameCommandManager(GameScheduler scheduler) {
        super(createCoordinator(scheduler), CommandRegistrationHandler.nullCommandRegistrationHandler());
        registerExceptionHandlers();
    }

    private static ExecutionCoordinator<CommandSender> createCoordinator(GameScheduler scheduler) {
        return ExecutionCoordinator.<CommandSender>builder()
                .executor(scheduler)
                .build();
    }

    private void registerExceptionHandlers() {
        ExceptionController<CommandSender> controller = exceptionController();

        registerDefaultExceptionHandlers(
                triplet -> {
                    CommandContext<CommandSender> context = triplet.first();
                    Caption caption = triplet.second();
                    List<CaptionVariable> variables = triplet.third();

                    String message = context.formatCaption(caption, variables);
                    context.sender().sendMessage(message);
                },
                pair -> {
                    String message = pair.first();
                    Throwable error = pair.second();
                    log.warn("Failed to execute command: {}", message, error);
                });

        controller.registerHandler(NoSuchCommandException.class, ectx -> {
            CommandContext<CommandSender> ctx = ectx.context();

            ctx.sender().sendMessage(text().append(
                    error("Unknown command: "),
                    text(ectx.exception().suppliedCommand())
            ).build());
        });

        controller.registerHandler(InvalidCommandSenderException.class, ectx -> {
            CommandContext<CommandSender> ctx = ectx.context();

            if (ctx.sender() instanceof Player) {
                ctx.sender().sendMessage(error("This command cannot be executed by a player."));
                return;
            }

            ctx.sender().sendMessage(error("This command cannot be executed by console."));
        });
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender, @NonNull String permission) {
        return sender.hasPermission(permission);
    }
}
