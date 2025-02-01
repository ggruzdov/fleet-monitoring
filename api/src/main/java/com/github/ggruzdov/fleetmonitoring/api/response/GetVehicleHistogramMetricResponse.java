package com.github.ggruzdov.fleetmonitoring.api.response;

import java.time.Instant;

public record GetVehicleHistogramMetricResponse(
    Instant timeBucket,
    Float value
) {
}
