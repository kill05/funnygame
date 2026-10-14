package com.github.kill05.funnygame.scheduler;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a task to be executed synchronously with the game thread at when the current tick is {@link DelayedTask#tick}.
 */
public class DelayedTask extends AbstractTask {

    private final @NotNull Runnable runnable;
    private final long tick;

    /**
     * @param scheduler the scheduler
     * @param runnable the runnable
     * @param tick the current tick
     */
    public DelayedTask(@NotNull GameScheduler scheduler, @NotNull Runnable runnable, long tick) {
        super(scheduler);
        this.runnable = runnable;
        this.tick = tick;
    }

    @Override
    public boolean shouldRun(long currentTick) {
        return currentTick == tick;
    }

    @Override
    public boolean shouldStop(long currentTick) {
        return currentTick >= tick;
    }

    @Override
    public @NotNull Runnable runnable() {
        return runnable;
    }

}
