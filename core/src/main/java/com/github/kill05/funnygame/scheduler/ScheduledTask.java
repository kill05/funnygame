package com.github.kill05.funnygame.scheduler;

public interface ScheduledTask {

    GameScheduler scheduler();

    Runnable runnable();

    boolean shouldRun(long currentTick);

    boolean shouldStop(long currentTick);

    default void cancel() {
        scheduler().cancel(this);
    }

}

