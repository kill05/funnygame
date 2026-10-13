package com.github.kill05.funnygame.scheduler;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameScheduler implements Closeable, Executor {

    private final ScheduledExecutorService tickTask;
    private final Runnable tickFunction;
    private final Set<ScheduledTask> tasks;
    private final int targetTps;
    private long currentTick;
    private Thread tickingThread;

    public GameScheduler(int targetTps, @NotNull Runnable tickFunction) {
        this.tickTask = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "tick-thread"));
        this.tasks = Collections.synchronizedSet(new HashSet<>());
        this.tickFunction = tickFunction;
        this.targetTps = targetTps;
        this.currentTick = 0;
    }

    public void start() {
        tickTask.scheduleAtFixedRate(this::doTick, 0L, 1000L / targetTps, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        tickTask.shutdown();
    }

    private synchronized void doTick() {
        tickFunction.run();
        this.tickingThread = Thread.currentThread();

        Iterator<ScheduledTask> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            ScheduledTask task = iterator.next();
            boolean stop = task.shouldStop(currentTick);

            if (task.shouldRun(currentTick)) {
                task.runnable().run();
            }

            if (stop) {
                iterator.remove();
            }
        }

        currentTick++;
    }


    /**
     * Runs a task on the ticking thread.
     *
     * @param runnable the runnable to run
     * @return the scheduled task
     */
    //todo: check why sometimes this does not execute commands
    public synchronized ScheduledTask run(@NotNull Runnable runnable) {
        if (Thread.currentThread() == getTickingThread()) {
            runnable.run();
        }

        return scheduleAtTick(runnable, currentTick);
    }

    public synchronized ScheduledTask run(@NotNull Runnable runnable, long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be positive.");
        }

        if (delay == 0) {
            return run(runnable);
        }

        return scheduleAtTick(runnable, currentTick + delay);
    }

    public synchronized ScheduledTask runRepeating(@NotNull Runnable runnable, long period) {
        return runRepeating(runnable, 0L, period);
    }

    public synchronized ScheduledTask runRepeating(@NotNull Runnable runnable, long delay, long period) {
        return runRepeating(runnable, delay, period, -1);
    }

    public synchronized ScheduledTask runRepeating(@NotNull Runnable runnable, long delay, long period, int times) {
        if (delay != -1 && delay < 0) {
            throw new IllegalArgumentException("Delay must be greater than 0 (or -1 for infinite).");
        }

        if (period <= 0) {
            throw new IllegalArgumentException("Period must be > 0.");
        }


        if (times == -1) {
            return runTask(new RepeatingTask(this, runnable, currentTick + delay, period));
        }

        return runTask(new FixedRepeatingTask(this, runnable, currentTick + delay, period, times));
    }


    private ScheduledTask scheduleAtTick(@NotNull Runnable runnable, long tick) {
        return runTask(new DelayedTask(this, runnable, tick));
    }

    private ScheduledTask runTask(ScheduledTask task) {
        tasks.add(task);
        return task;
    }

    public void cancel(@NotNull ScheduledTask task) {
        tasks.remove(task);
    }


    public long getCurrentTick() {
        return currentTick;
    }

    public Thread getTickingThread() {
        if (tickingThread == null) {
            throw new IllegalStateException("The scheduler has not been started");
        }

        return tickingThread;
    }

    @Override
    public void execute(@NotNull Runnable command) {
        run(command);
    }
}
