package com.github.ggruzdov.fleetmonitoring.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record GetVehicleHistogramMetricRequest(

    @NotNull
    Integer vehicleId,

    @NotNull
    Instant from,

    @NotNull
    Instant to,

    @NotNull
    @Min(1)
    @Max(60)
    Integer interval
) {
}
