package com.github.kill05.funnygame.scheduler;

public abstract class AbstractTask implements ScheduledTask {

    protected final GameScheduler scheduler;
    private boolean cancelled;

    public AbstractTask(GameScheduler scheduler) {
        this.scheduler = scheduler;
        this.cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean cancel() {
        // Must synchronize to avoid other threads cancelling tasks while they are being iterated
        synchronized (scheduler) {
            if (cancelled) {
                return false;
            }

            cancelled = true;
            return true;
        }
    }

    // Equals must be implemented like this because GameScheduler uses a Set to store the tasks
    @Override
    public final boolean equals(Object obj) {
        return obj == this;
    }
}
