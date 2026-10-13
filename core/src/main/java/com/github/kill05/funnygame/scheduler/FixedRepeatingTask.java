package com.github.kill05.funnygame.scheduler;

public class FixedRepeatingTask extends RepeatingTask {

    private final int repeatAmount;

    public FixedRepeatingTask(GameScheduler scheduler, Runnable runnable, long startTick, long period, int repeatAmount) {
        super(scheduler, runnable, startTick, period);
        this.repeatAmount = repeatAmount;
    }

    @Override
    public boolean shouldStop(long currentTick) {
        return (currentTick - startTick) / period >= repeatAmount;
    }
}
