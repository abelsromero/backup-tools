package org.backup.tools.time;

import java.util.concurrent.Callable;

sealed interface TimedExecution permits TimedExecution.TimedResult, TimedExecution.TimedValue {

    static <T> TimedValue<T> run(Callable<T> callable) throws Exception {
        long startTime = System.currentTimeMillis();
        T result = callable.call();
        double duration = calculateDuration(startTime);
        return new TimedValue(result, (long) duration);
    }

    static TimedResult run(Runnable supplier) {
        long startTime = System.currentTimeMillis();
        supplier.run();
        double duration = calculateDuration(startTime);
        return new TimedResult((long) duration);
    }

    private static long calculateDuration(long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        return duration == 0 ? 1 : duration;
    }

    record TimedValue<T>(T result, long millis) implements TimedExecution {
    }

    record TimedResult(long millis) implements TimedExecution {
    }
}
