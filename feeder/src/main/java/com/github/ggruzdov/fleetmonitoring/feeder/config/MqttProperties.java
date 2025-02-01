package com.github.ggruzdov.fleetmonitoring.feeder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "mqtt")
public record MqttProperties(
    String clientId,
    String username,
    String password,
    List<String> brokerUrls,
    String topic,
    Integer qos,
    Duration completionTimeout,
    Long sessionExpiryInterval
) {
}
