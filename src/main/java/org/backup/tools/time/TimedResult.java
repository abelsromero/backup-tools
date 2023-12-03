package org.backup.tools.time;

import java.util.function.Supplier;

record TimedResult<T>(T result, long seconds) {

  static <T> TimedResult<T> timed(Supplier<T> supplier) {
    long startTime = System.currentTimeMillis();
    T result = supplier.get();
    double duration = calculateDuration(startTime);
    return new TimedResult(result, (long) duration);
  }

  static TimedResult<Void> timed(Runnable supplier) {
    long startTime = System.currentTimeMillis();
    supplier.run();
    double duration = calculateDuration(startTime);
    return new TimedResult(null, (long) duration);
  }

  private static long calculateDuration(long startTime) {
    long time = System.currentTimeMillis() - startTime;
    double duration = Math.ceil(time / 1000);
    return duration == 0 ? 1 : (long) duration;
  }
}
