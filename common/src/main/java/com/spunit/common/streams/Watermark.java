package com.spunit.common.streams;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Lightweight watermark tracker for handling late/out-of-order events in stream-like consumers.
 * This is a placeholder utility to document the pattern; real implementations should integrate with
 * the streaming framework in use (e.g., Kafka Streams, Flink) and advance watermarks based on event-time.
 */
public final class Watermark {
    private final AtomicReference<Instant> watermark = new AtomicReference<>(Instant.EPOCH);
    private final long allowedLatenessMillis;

    public Watermark(long allowedLatenessMillis) {
        if (allowedLatenessMillis < 0) throw new IllegalArgumentException("allowedLatenessMillis must be >= 0");
        this.allowedLatenessMillis = allowedLatenessMillis;
    }

    /**
     * Advances the watermark to max(current, candidate - allowedLateness).
     */
    public void advance(Instant eventTime) {
        watermark.updateAndGet(current -> {
            Instant candidate = eventTime.minusMillis(allowedLatenessMillis);
            return candidate.isAfter(current) ? candidate : current;
        });
    }

    /**
     * Returns true if the given eventTime is considered late according to the current watermark + allowed lateness.
     */
    public boolean isLate(Instant eventTime) {
        Instant wm = watermark.get();
        return eventTime.isBefore(wm);
    }

    public Instant current() {
        return watermark.get();
    }
}
