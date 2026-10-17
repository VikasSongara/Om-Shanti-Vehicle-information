package com.example.vehicleinformation.config;

import com.example.vehicleinformation.model.Vehicle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
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
        dropVehicleNumberUniqueConstraintIfPresent();
    }

    /**
     * Blank/"-" vehicle numbers are allowed to duplicate; remove legacy unique constraint.
     */
    private void dropVehicleNumberUniqueConstraintIfPresent() {
        try {
            Session session = entityManager.unwrap(Session.class);
            session.doWork(connection -> {
                String product = connection.getMetaData().getDatabaseProductName().toLowerCase();
                try (var statement = connection.createStatement()) {
                    if (product.contains("postgresql")) {
                        statement.execute("ALTER TABLE vehicles DROP CONSTRAINT IF EXISTS uk_vehicles_vehicle_number");
                    } else if (product.contains("microsoft") || product.contains("sql server")) {
                        statement.execute("""
                                IF EXISTS (
                                    SELECT 1 FROM sys.key_constraints
                                    WHERE name = 'uk_vehicles_vehicle_number'
                                      AND parent_object_id = OBJECT_ID('vehicles')
                                )
                                ALTER TABLE vehicles DROP CONSTRAINT uk_vehicles_vehicle_number
                                """);
                    }
                }
            });
            log.info("Ensured vehicle_number unique constraint is dropped (blank/- duplicates allowed)");
        } catch (Exception ex) {
            log.warn("Could not drop vehicle_number unique constraint (may already be absent): {}", ex.getMessage());
        }
    }
}
