package com.github.kill05.funnygame.scheduler;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handles the main ticking thread and allows for any thread to schedule tasks to be executed
 * on the ticking thread at a certain tick.
 *
 * @see GameScheduler#run(Runnable)
 * @see GameScheduler#run(Runnable, long)
 * @see GameScheduler#runRepeating(Runnable, long)
 */
public class GameScheduler implements Closeable, Executor {

    private static final Logger log = LoggerFactory.getLogger(GameScheduler.class);
    private final ScheduledExecutorService tickTask;
    private final Runnable tickFunction;
    private final Set<AbstractTask> tasks;
    private final Set<AbstractTask> tasksToAdd; // Avoid ConcurrentModificationException when adding tasks while executing a task
    private final int targetTps;
    private long currentTick;
    private Thread tickingThread;

    public GameScheduler(int targetTps, @NotNull Runnable tickFunction) {
        this.tickTask = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "tick-thread"));
        this.tasks = new HashSet<>();
        this.tasksToAdd = new HashSet<>();
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
        try {
            tickFunction.run();
            this.tickingThread = Thread.currentThread();

            // Run tasks
            tasks.addAll(tasksToAdd);
            tasksToAdd.clear();

            Iterator<AbstractTask> iterator = tasks.iterator();
            while (iterator.hasNext()) {
                AbstractTask task = iterator.next();

                if (task.isCancelled()) {
                    iterator.remove();
                    continue;
                }

                if (task.shouldRun(currentTick)) {
                    task.runnable().run();
                }

                if (task.shouldStop(currentTick)) {
                    iterator.remove();
                }
            }

            currentTick++;
        } catch (Exception e) {
            log.error("Failed to execute tick.", e);
            System.exit(1);
        }
    }


    /**
     * Runs a task on the ticking thread.
     *
     * @param runnable the runnable to run
     * @return the scheduled task
     */
    public synchronized ScheduledTask run(@NotNull Runnable runnable) {
        if (Thread.currentThread() == getTickingThread()) {
            runnable.run();
        }

        return scheduleAtTick_unsafe(runnable, currentTick);
    }

    public synchronized ScheduledTask run(@NotNull Runnable runnable, long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be positive.");
        }

        if (delay == 0) {
            return run(runnable);
        }

        return scheduleAtTick_unsafe(runnable, currentTick + delay);
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
            return addTask_unsafe(new RepeatingTask(this, runnable, currentTick + delay, period));
        }

        return addTask_unsafe(new FixedRepeatingTask(this, runnable, currentTick + delay, period, times));
    }


    private ScheduledTask scheduleAtTick_unsafe(@NotNull Runnable runnable, long tick) {
        return addTask_unsafe(new DelayedTask(this, runnable, tick));
    }

    // Every method that invokes this is synchronized
    private ScheduledTask addTask_unsafe(AbstractTask task) {
        tasksToAdd.add(task);
        return task;
    }

    public void cancel(@NotNull ScheduledTask task) {
        task.cancel();
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
    public void execute(@NotNull Runnable runnable) {
        run(runnable);
    }
}
