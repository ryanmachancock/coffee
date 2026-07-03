package com.projects.coffee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoffeeApplication {

    public static void main(String[] args) {
        applyDatasourceFromEnv();
        SpringApplication.run(CoffeeApplication.class, args);
    }

    private static void applyDatasourceFromEnv() {
        String url = System.getenv("SPRING_DATASOURCE_URL");
        if (url == null || url.isBlank()) return;

        if (!url.startsWith("jdbc:")) {
            // Strip any scheme and re-add as proper JDBC URL
            url = "jdbc:postgresql://" + url.replaceFirst("^[^:]+://", "");
        }

        System.setProperty("spring.datasource.url", url);
        System.setProperty("spring.h2.console.enabled", "false");

        String user = System.getenv("SPRING_DATASOURCE_USERNAME");
        String pass = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (user != null && !user.isBlank()) System.setProperty("spring.datasource.username", user);
        if (pass != null && !pass.isBlank()) System.setProperty("spring.datasource.password", pass);
    }
}
