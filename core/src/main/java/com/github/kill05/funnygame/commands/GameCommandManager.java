package com.github.kill05.funnygame.commands;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.jetbrains.annotations.NotNull;

public class GameCommandManager extends CommandManager<CommandSender> {

    public GameCommandManager() {
        super(ExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
    }

    @Override
    public boolean hasPermission(@NotNull CommandSender sender, @NonNull String permission) {
        return true;
    }
}
