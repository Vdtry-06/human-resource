package com.spring.human.resource;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.util.Locale;

@SpringBootApplication
@EntityScan(basePackages = "com.spring.human.resource.server.entities")
@EnableJpaRepositories
@Log4j2
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        String url = "http://localhost:8080/swagger-ui/index.html";
        log.info("Swagger UI URL: " + url);

        // Skip browser opening in Docker environment
        if (isRunningInDocker()) {
            log.info("Running in Docker, skipping browser auto-open. Open the URL manually: {}", url);
            return;
        }

        try {
            if (isWindows()) {
                new ProcessBuilder("cmd", "/c", "start", url).start();
            } else if (isMac()) {
                new ProcessBuilder("open", url).start();
            } else if (isLinux()) {
                new ProcessBuilder("xdg-open", url).start();
            } else {
                log.error("Operating system not supported, open link manually: {}", url);
            }
        } catch (IOException e) {
            log.error("Failed to open browser: {}", e.getMessage());
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
    }

    private static boolean isMac() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");
    }

    private static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("nux");
    }

    private static boolean isRunningInDocker() {
        // Check for common Docker environment indicators
        return System.getenv("DOCKER_CONTAINER") != null ||
                new java.io.File("/.dockerenv").exists() ||
                new java.io.File("/proc/self/cgroup").exists() &&
                        java.nio.file.Files.exists(java.nio.file.Paths.get("/proc/self/cgroup"));
    }
}