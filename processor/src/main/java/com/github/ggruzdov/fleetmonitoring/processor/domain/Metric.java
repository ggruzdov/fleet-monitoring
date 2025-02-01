package com.github.ggruzdov.fleetmonitoring.processor.domain;

import java.time.Instant;

public record Metric(
    Instant time,
    Integer vehicleId,
    Integer deviceId,
    String name,
    float value
) {
}