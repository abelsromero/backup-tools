package org.backup.tools.time;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;

class TimedExecutionTest {

    @Test
    void shouldTimeRunnable() {
        final Runnable runnable = () -> sleep();

        var result = TimedExecution.run(runnable);

        assertThat(result.millis()).isGreaterThanOrEqualTo(100l);
    }

    @Test
    void shouldReturn1AsMinimum() {
        var result = TimedExecution.run(() -> {});

        assertThat(result.millis()).isGreaterThanOrEqualTo(1l);
    }

    @Test
    void shouldTimeSupplier() throws Exception {
        final Callable<String> callable = () -> {
            sleep();
            return "hello";
        };

        var result = TimedExecution.run(callable);

        assertThat(result.result()).isEqualTo("hello");
        assertThat(result.millis()).isGreaterThanOrEqualTo(100l);
    }

    private static void sleep() {
        try {
            Thread.sleep(100l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
