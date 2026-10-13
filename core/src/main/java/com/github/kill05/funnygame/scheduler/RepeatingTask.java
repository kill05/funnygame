package com.github.kill05.funnygame.scheduler;

public class RepeatingTask implements ScheduledTask {

    protected final GameScheduler scheduler;
    protected final Runnable runnable;
    protected final long startTick;
    protected final long period;

    public RepeatingTask(GameScheduler scheduler, Runnable runnable, long startTick, long period) {
        this.scheduler = scheduler;
        this.runnable = runnable;
        this.startTick = startTick;
        this.period = period;
    }

    @Override
    public GameScheduler scheduler() {
        return null;
    }

    @Override
    public Runnable runnable() {
        return runnable;
    }

    @Override
    public boolean shouldRun(long currentTick) {
        return (currentTick - startTick) % period == 0;
    }

    @Override
    public boolean shouldStop(long currentTick) {
        return false;
    }

    public long startTick() {
        return startTick;
    }

    public long period() {
        return period;
    }

}
