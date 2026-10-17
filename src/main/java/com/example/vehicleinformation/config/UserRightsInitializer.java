package com.example.vehicleinformation.config;

import com.example.vehicleinformation.service.UserRightsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ensures allow_add exists on user_rights (SQL Server needs DEFAULT for non-empty tables).
 */
@Component
public class UserRightsInitializer {

    private static final Logger log = LoggerFactory.getLogger(UserRightsInitializer.class);

    private final UserRightsService userRightsService;

    @PersistenceContext
    private EntityManager entityManager;

    public UserRightsInitializer(UserRightsService userRightsService) {
        this.userRightsService = userRightsService;
    }

    @Order(1)
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        ensureAllowAddColumn();
        userRightsService.ensureDefaults();
    }

    private void ensureAllowAddColumn() {
        try {
            Session session = entityManager.unwrap(Session.class);
            session.doWork(connection -> {
                String product = connection.getMetaData().getDatabaseProductName().toLowerCase();
                try (var statement = connection.createStatement()) {
                    if (product.contains("microsoft") || product.contains("sql server")) {
                        statement.execute("""
                                IF COL_LENGTH('user_rights', 'allow_add') IS NULL
                                BEGIN
                                    ALTER TABLE user_rights
                                        ADD allow_add bit NOT NULL
                                        CONSTRAINT DF_user_rights_allow_add DEFAULT 0;
                                END
                                """);
                    } else if (product.contains("postgresql")) {
                        statement.execute("""
                                ALTER TABLE user_rights
                                    ADD COLUMN IF NOT EXISTS allow_add boolean NOT NULL DEFAULT false
                                """);
                    }
                }
            });
            log.info("Ensured user_rights.allow_add column exists");
        } catch (Exception ex) {
            log.warn("Could not ensure user_rights.allow_add column: {}", ex.getMessage());
        }
    }
}
