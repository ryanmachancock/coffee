package com.projects.coffee.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String url = resolveJdbcUrl();
        if (url == null) return;

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", url);
        props.put("spring.h2.console.enabled", "false");

        String pgUser = getEnvOrDefault("PGUSER", "");
        String pgPassword = getEnvOrDefault("PGPASSWORD", "");
        if (!pgUser.isBlank()) props.put("spring.datasource.username", pgUser);
        if (!pgPassword.isBlank()) props.put("spring.datasource.password", pgPassword);

        environment.getPropertySources().addFirst(new MapPropertySource("railwayPostgres", props));
    }

    private String resolveJdbcUrl() {
        // Already a JDBC URL
        String datasourceUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (datasourceUrl != null && datasourceUrl.startsWith("jdbc:")) return datasourceUrl;

        // Missing jdbc: prefix — fix it
        if (datasourceUrl != null && datasourceUrl.startsWith("postgresql://"))
            return "jdbc:" + datasourceUrl;

        // Build from individual PG* vars
        String pgHost = System.getenv("PGHOST");
        if (pgHost != null && !pgHost.isBlank()) {
            String pgPort = getEnvOrDefault("PGPORT", "5432");
            String pgDatabase = getEnvOrDefault("PGDATABASE", "");
            return String.format("jdbc:postgresql://%s:%s/%s", pgHost, pgPort, pgDatabase);
        }

        return null;
    }

    private String getEnvOrDefault(String name, String defaultValue) {
        String val = System.getenv(name);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }
}
