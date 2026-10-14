package com.github.kill05.funnygame.scheduler;

public interface ScheduledTask {

    Runnable runnable();

    boolean shouldRun(long currentTick);

    boolean shouldStop(long currentTick);

    /**
     * Cancels this task. Returns {@code true} if the task was canceled
     * or {@code false} if the task has already been canceled.
     *
     * @return {@code true} if canceled
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean cancel();

}

