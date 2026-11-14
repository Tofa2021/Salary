package com.example.backend.auth.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private Jwt jwt = new Jwt();
    private Refresh refresh = new Refresh();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private Duration accessTtl = Duration.ofMinutes(15);
    }

    @Getter
    @Setter
    public static class Refresh {
        private Duration ttl = Duration.ofDays(7);
    }
}
