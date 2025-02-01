package com.github.ggruzdov.fleetmonitoring.feeder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "generator")
public record GeneratorProperties(
    Integer vehicles,
    Integer offset
) {
}
