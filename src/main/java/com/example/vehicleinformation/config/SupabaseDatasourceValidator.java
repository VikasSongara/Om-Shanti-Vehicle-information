package com.example.vehicleinformation.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Runs before DataSource/JPA init so misconfigured Supabase pooler creds fail with a clear message.
 * Pooler username must be: postgres.&lt;project-ref&gt; (not plain "postgres").
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
                        Prod datasource env vars are missing. On Render set ALL three:
                          SPRING_DATASOURCE_URL=jdbc:postgresql://aws-0-<region>.pooler.supabase.com:5432/postgres?sslmode=require
                          SPRING_DATASOURCE_USERNAME=postgres.<your-project-ref>
                          SPRING_DATASOURCE_PASSWORD=<your-db-password>
                        DB_HOST / DB_NAME are ignored. Username must be postgres.<project-ref>, not just postgres.
                        """);
            }
            if (!username.contains(".")) {
                throw new IllegalStateException("""
                        Invalid SPRING_DATASOURCE_USERNAME='%s'.
                        Supabase pooler needs the project tenant in the username, for example:
                          postgres.meurezasnwbhdiflnxzb
                        Using only 'postgres' causes: no tenant identifier provided (ENOIDENTIFIER).
                        Copy Username from Supabase → Settings → Database → Connection string → Session pooler.
                        """.formatted(username));
            }
        };
    }
}
