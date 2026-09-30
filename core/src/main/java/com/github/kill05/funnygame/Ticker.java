package com.github.kill05.funnygame;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Ticker implements Closeable {

    private final ScheduledExecutorService task;
    private final Runnable tickFunction;
    private final int targetTps;
    private final AtomicInteger currentTick;

    public Ticker(int targetTps, @NotNull Runnable tickFunction) {
        this.task = Executors.newSingleThreadScheduledExecutor();
        this.tickFunction = tickFunction;
        this.targetTps = targetTps;
        this.currentTick = new AtomicInteger();
    }

    public void start() {
        task.scheduleAtFixedRate(tickFunction, 0L, 1000L / targetTps, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        task.shutdown();
    }


    private void doTick() {
        tickFunction.run();
        currentTick.getAndIncrement();
    }


    public int getCurrentTick() {
        return currentTick.get();
    }
}
