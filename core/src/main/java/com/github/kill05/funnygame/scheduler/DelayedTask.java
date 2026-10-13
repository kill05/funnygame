package com.github.kill05.funnygame.scheduler;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a task to be executed synchronously with the game thread at some tick.
 *
 * @param runnable the runnable
 * @param tick
 */
public record DelayedTask(@NotNull GameScheduler scheduler, @NotNull Runnable runnable, long tick) implements ScheduledTask {

    @Override
    public boolean shouldRun(long currentTick) {
        return currentTick == tick;
    }

    @Override
    public boolean shouldStop(long currentTick) {
        return currentTick >= tick;
    }

}
