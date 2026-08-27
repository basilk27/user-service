package com.mbsystems.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring")
public record UserServiceConfigs(
        ApplicationProperties application
) {
    public record ApplicationProperties(
            String name
    ) {}
}
