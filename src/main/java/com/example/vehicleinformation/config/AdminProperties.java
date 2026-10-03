package com.example.vehicleinformation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        String username,
        String password
) {
    public AdminProperties {
        if (username == null || username.isBlank()) {
            throw new IllegalStateException(
                    "app.admin.username is missing. For production set ADMIN_USERNAME env var.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "app.admin.password is missing. For production set ADMIN_PASSWORD env var.");
        }
    }
}
