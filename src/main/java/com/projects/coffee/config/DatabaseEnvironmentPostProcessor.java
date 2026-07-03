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
        String pgHost = System.getenv("PGHOST");
        if (pgHost == null || pgHost.isBlank()) return;

        String pgPort = getEnvOrDefault("PGPORT", "5432");
        String pgDatabase = getEnvOrDefault("PGDATABASE", "");
        String pgUser = getEnvOrDefault("PGUSER", "");
        String pgPassword = getEnvOrDefault("PGPASSWORD", "");

        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url",
                String.format("jdbc:postgresql://%s:%s/%s", pgHost, pgPort, pgDatabase));
        props.put("spring.datasource.username", pgUser);
        props.put("spring.datasource.password", pgPassword);
        props.put("spring.h2.console.enabled", "false");

        environment.getPropertySources().addFirst(new MapPropertySource("railwayPostgres", props));
    }

    private String getEnvOrDefault(String name, String defaultValue) {
        String val = System.getenv(name);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }
}
