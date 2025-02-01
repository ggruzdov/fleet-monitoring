package com.github.ggruzdov.fleetmonitoring.feeder.domain;

import java.time.Instant;

/**
 * Single value metrics. Speed, mileage, fuel, and temperature.
 */
public record Metric(
    Instant time,
    Integer vehicleId,
    Integer deviceId,
    String name,
    float value
) {
    public enum Name {
        SPEED(1),
        FUEL(2),
        MILEAGE(3),
        TEMPERATURE(4);

        public final Integer deviceIdOffset;

        Name(Integer deviceIdOffset) {
            this.deviceIdOffset = deviceIdOffset;
        }
    }
}
