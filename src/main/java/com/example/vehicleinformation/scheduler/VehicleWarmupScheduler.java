package com.example.vehicleinformation.scheduler;

import com.example.vehicleinformation.config.SchedulerProperties;
import com.example.vehicleinformation.dto.VehiclePageDto;
import com.example.vehicleinformation.model.Vehicle;
import com.example.vehicleinformation.repository.VehicleRepository;
import com.example.vehicleinformation.service.VehicleService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Runs every N minutes (default 14): random vehicle-number search + random house-no search.
 */
@Component
public class VehicleWarmupScheduler {

    private static final Logger log = LoggerFactory.getLogger(VehicleWarmupScheduler.class);

    private final TaskScheduler taskScheduler;
    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository;
    private final SchedulerRuntimeState state;

    private ScheduledFuture<?> scheduledFuture;

    public VehicleWarmupScheduler(@Qualifier("vehicleTaskScheduler") TaskScheduler vehicleTaskScheduler,
                                  VehicleService vehicleService,
                                  VehicleRepository vehicleRepository,
                                  SchedulerProperties properties) {
        this.taskScheduler = vehicleTaskScheduler;
        this.vehicleService = vehicleService;
        this.vehicleRepository = vehicleRepository;
        this.state = new SchedulerRuntimeState(properties.getIntervalMinutes(), properties.isEnabled());
    }

    @PostConstruct
    public void start() {
        reschedule();
        log.info("Vehicle warmup scheduler started. enabled={}, intervalMinutes={}",
                state.isEnabled(), state.getIntervalMinutes());
    }

    @PreDestroy
    public void stop() {
        cancelCurrent();
    }

    public SchedulerRuntimeState getState() {
        return state;
    }

    public synchronized void updateSettings(int intervalMinutes, boolean enabled) {
        if (intervalMinutes < 1 || intervalMinutes > 1440) {
            throw new IllegalArgumentException("Interval must be between 1 and 1440 minutes");
        }
        state.setIntervalMinutes(intervalMinutes);
        state.setEnabled(enabled);
        reschedule();
        log.info("Scheduler settings updated. enabled={}, intervalMinutes={}", enabled, intervalMinutes);
    }

    public synchronized void runNow() {
        executeWarmup();
    }

    private synchronized void reschedule() {
        cancelCurrent();
        if (!state.isEnabled()) {
            log.info("Scheduler disabled — not scheduling next run");
            return;
        }
        Duration interval = Duration.ofMinutes(state.getIntervalMinutes());
        // First run after one full interval (avoid hammering DB at startup)
        scheduledFuture = taskScheduler.scheduleAtFixedRate(
                this::executeWarmup,
                Instant.now().plus(interval),
                interval
        );
        log.info("Scheduler registered to run every {} minute(s)", state.getIntervalMinutes());
    }

    private void cancelCurrent() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }
    }

    private void executeWarmup() {
        if (!state.isEnabled()) {
            return;
        }
        try {
            List<Vehicle> active = vehicleRepository.findByStatus(Vehicle.STATUS_ACTIVE);
            if (active.isEmpty()) {
                state.setLastRunAt(Instant.now());
                state.setLastRunSummary("Skipped — no active vehicles");
                state.setLastError(null);
                log.info("Scheduler skipped: no active vehicles");
                return;
            }

            Vehicle randomForNumber = active.get(ThreadLocalRandom.current().nextInt(active.size()));
            Vehicle randomForHouse = active.get(ThreadLocalRandom.current().nextInt(active.size()));

            String vehicleNumber = randomForNumber.getVehicleNumber();
            String houseNo = randomForHouse.getHouseNo();

            // 1) Random vehicle number search
            VehiclePageDto byNumber = vehicleService.search(
                    null, vehicleNumber, null, null, null, null,
                    0, 10, "house", "asc", false
            );

            // 2) Random house no search
            VehiclePageDto byHouse = vehicleService.search(
                    null, null, null, houseNo, null, null,
                    0, 10, "house", "asc", false
            );

            String summary = String.format(
                    "Vehicle Number '%s' → %d result(s); House No '%s' → %d result(s)",
                    vehicleNumber, byNumber.getTotalElements(),
                    houseNo, byHouse.getTotalElements()
            );
            state.setLastRunAt(Instant.now());
            state.setLastRunSummary(summary);
            state.setLastError(null);
            log.info("Scheduler run completed. {}", summary);
        } catch (Exception ex) {
            state.setLastRunAt(Instant.now());
            state.setLastError(ex.getMessage());
            state.setLastRunSummary("Failed");
            log.error("Scheduler run failed: {}", ex.getMessage(), ex);
        }
    }
}
