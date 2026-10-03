package com.example.vehicleinformation.config;

import com.example.vehicleinformation.model.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class VehicleStatusBackfill {

    private static final Logger log = LoggerFactory.getLogger(VehicleStatusBackfill.class);

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void ensureActiveStatusDefault() {
        int updated = entityManager
                .createQuery("update Vehicle v set v.status = :active where v.status is null")
                .setParameter("active", Vehicle.STATUS_ACTIVE)
                .executeUpdate();
        if (updated > 0) {
            log.info("Backfilled status=1 for {} existing vehicle record(s)", updated);
        }
    }
}
