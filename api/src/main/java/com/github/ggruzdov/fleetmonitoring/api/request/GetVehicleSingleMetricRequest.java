package com.github.ggruzdov.fleetmonitoring.api.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record GetVehicleSingleMetricRequest(

    @NotNull
    Integer vehicleId,

    @NotNull
    Instant from,

    @NotNull
    Instant to
) {
}
