package com.github.kill05.funnygame.commands;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.jetbrains.annotations.NotNull;

public class CommandBase {

    protected final Command.Builder<CommandSender> builder;
    private final @NotNull CommandManager<CommandSender> manager;

    public CommandBase(@NotNull CommandManager<CommandSender> manager, String name, String... aliases) {
        this.manager = manager;
        this.builder = manager.commandBuilder(name, aliases).permission("game." + name);
    }

    public void register(@NotNull Command.Builder<? extends CommandSender> builder) {
        manager.command(builder.build());
    }

}
