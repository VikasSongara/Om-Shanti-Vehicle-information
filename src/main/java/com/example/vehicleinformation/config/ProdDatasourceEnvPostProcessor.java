package com.example.vehicleinformation.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * On Render, prefer DB_HOST / DB_USER / DB_PASSWORD (common naming) over a broken
 * SPRING_DATASOURCE_URL that embeds user:password@ (and breaks when password contains #).
 */
public class ProdDatasourceEnvPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!isProd(environment)) {
            return;
        }

        String dbHost = firstNonBlank(environment, "DB_HOST", "db.host");
        String dbUser = firstNonBlank(environment, "DB_USER", "db.user");
        String dbPassword = firstNonBlank(environment, "DB_PASSWORD", "db.password");
        String dbPort = firstNonBlank(environment, "DB_PORT", "db.port");
        String dbName = firstNonBlank(environment, "DB_NAME", "db.name");

        if (!StringUtils.hasText(dbHost) || !StringUtils.hasText(dbUser)) {
            return;
        }

        if (!StringUtils.hasText(dbPort)) {
            dbPort = "5432";
        }
        if (!StringUtils.hasText(dbName)) {
            dbName = "postgres";
        }

        String jdbcUrl = "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName + "?sslmode=require";

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", jdbcUrl);
        props.put("spring.datasource.username", dbUser.trim());
        if (StringUtils.hasText(dbPassword)) {
            props.put("spring.datasource.password", dbPassword);
        }

        // Highest precedence so broken SPRING_DATASOURCE_URL cannot override
        environment.getPropertySources().addFirst(new MapPropertySource("prodDbHostOverride", props));
    }

    private static boolean isProd(ConfigurableEnvironment environment) {
        for (String profile : environment.getActiveProfiles()) {
            if ("prod".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        String active = environment.getProperty("spring.profiles.active", "");
        String fromEnv = environment.getProperty("SPRING_PROFILES_ACTIVE", "");
        return active.toLowerCase().contains("prod") || fromEnv.toLowerCase().contains("prod");
    }

    private static String firstNonBlank(ConfigurableEnvironment environment, String... keys) {
        for (String key : keys) {
            String value = environment.getProperty(key);
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return "";
    }
}
