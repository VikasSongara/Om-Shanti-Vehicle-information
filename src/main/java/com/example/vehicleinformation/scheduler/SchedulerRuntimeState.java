package com.example.vehicleinformation.scheduler;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runtime scheduler settings (admin can change interval without restart).
 */
public class SchedulerRuntimeState {

    private final AtomicInteger intervalMinutes;
    private final AtomicBoolean enabled;
    private final AtomicReference<Instant> lastRunAt = new AtomicReference<>();
    private final AtomicReference<String> lastRunSummary = new AtomicReference<>("Not run yet");
    private final AtomicReference<String> lastError = new AtomicReference<>();

    public SchedulerRuntimeState(int intervalMinutes, boolean enabled) {
        this.intervalMinutes = new AtomicInteger(Math.max(1, intervalMinutes));
        this.enabled = new AtomicBoolean(enabled);
    }

    public int getIntervalMinutes() {
        return intervalMinutes.get();
    }

    public void setIntervalMinutes(int minutes) {
        intervalMinutes.set(Math.max(1, minutes));
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public void setEnabled(boolean value) {
        enabled.set(value);
    }

    public Instant getLastRunAt() {
        return lastRunAt.get();
    }

    public void setLastRunAt(Instant instant) {
        lastRunAt.set(instant);
    }

    public String getLastRunSummary() {
        return lastRunSummary.get();
    }

    public void setLastRunSummary(String summary) {
        lastRunSummary.set(summary);
    }

    public String getLastError() {
        return lastError.get();
    }

    public void setLastError(String error) {
        lastError.set(error);
    }
}
