package com.example.vehicleinformation.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Runs before DataSource/JPA init so misconfigured Supabase pooler creds fail with a clear message.
 */
@Configuration
@Profile("prod")
public class SupabaseDatasourceValidator {

    @Bean
    static BeanFactoryPostProcessor validateSupabaseDatasource(Environment environment) {
        return beanFactory -> {
            String url = environment.getProperty("spring.datasource.url", "");
            String username = environment.getProperty("spring.datasource.username", "");
            String password = environment.getProperty("spring.datasource.password", "");

            if (!StringUtils.hasText(url) || !StringUtils.hasText(username) || !StringUtils.hasText(password)) {
                throw new IllegalStateException("""
                        Prod DB credentials are incomplete (url='%s', username='%s', passwordSet=%s).
                        Preferred on Render (use these exact keys):
                          DB_HOST=aws-0-<region>.pooler.supabase.com
                          DB_PORT=5432
                          DB_NAME=postgres
                          DB_USER=postgres.<project-ref>
                          DB_PASSWORD=<real-password>
                        Or set:
                          SPRING_DATASOURCE_URL=jdbc:postgresql://HOST:5432/postgres?sslmode=require
                          SPRING_DATASOURCE_USERNAME=postgres.<project-ref>
                          SPRING_DATASOURCE_PASSWORD=<real-password>
                        Do NOT put user:password@ inside the URL (password with # will break).
                        """.formatted(url, username, StringUtils.hasText(password)));
            }
            if (!url.startsWith("jdbc:postgresql://")) {
                throw new IllegalStateException("""
                        Invalid spring.datasource.url='%s'
                        Must start with jdbc:postgresql://HOST:5432/postgres?sslmode=require
                        (no postgresql://user:pass@... URI form).
                        """.formatted(url));
            }
            if (!username.contains(".")) {
                throw new IllegalStateException("""
                        Invalid username='%s'. Supabase pooler needs: postgres.<project-ref>
                        Example: postgres.meurezasnwbhdiflnxzb
                        """.formatted(username));
            }
        };
    }
}
