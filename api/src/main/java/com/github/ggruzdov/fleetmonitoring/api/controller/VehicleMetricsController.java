package com.github.ggruzdov.fleetmonitoring.api.controller;

import com.github.ggruzdov.fleetmonitoring.api.domain.MetricName;
import com.github.ggruzdov.fleetmonitoring.api.repository.MetricsRepository;
import com.github.ggruzdov.fleetmonitoring.api.request.GetVehicleHistogramMetricRequest;
import com.github.ggruzdov.fleetmonitoring.api.request.GetVehicleSingleMetricRequest;
import com.github.ggruzdov.fleetmonitoring.api.response.GetVehicleHistogramMetricResponse;
import com.github.ggruzdov.fleetmonitoring.api.response.GetVehicleSingleMetricResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VehicleMetricsController {

    private final MetricsRepository metricsRepository;

    @GetMapping("/avg/speed")
    public GetVehicleSingleMetricResponse getAvgSpeed(@Valid GetVehicleSingleMetricRequest request) {
        var value = metricsRepository.getAvg(request, MetricName.SPEED);
        return new GetVehicleSingleMetricResponse(value);
    }

    @GetMapping("/max/speed")
    public GetVehicleSingleMetricResponse getMaxSpeed(@Valid GetVehicleSingleMetricRequest request) {
        var value = metricsRepository.getMax(request, MetricName.SPEED);
        return new GetVehicleSingleMetricResponse(value);
    }

    @GetMapping("/avg/temperature")
    public GetVehicleSingleMetricResponse getAvgTemperature(@Valid GetVehicleSingleMetricRequest request) {
        var value = metricsRepository.getAvg(request, MetricName.TEMPERATURE);
        return new GetVehicleSingleMetricResponse(value);
    }

    @GetMapping("/max/temperature")
    public GetVehicleSingleMetricResponse getMaxTemperature(@Valid GetVehicleSingleMetricRequest request) {
        var value = metricsRepository.getMax(request, MetricName.TEMPERATURE);
        return new GetVehicleSingleMetricResponse(value);
    }

    @GetMapping("/total/mileage")
    public GetVehicleSingleMetricResponse getTotalMileage(@Valid GetVehicleSingleMetricRequest request) {
        var value = metricsRepository.getTotalMileage(request);
        return new GetVehicleSingleMetricResponse(value);
    }

    @GetMapping("/histogram/max/fuel")
    public List<GetVehicleHistogramMetricResponse> getTotalFuel(@Valid GetVehicleHistogramMetricRequest request) {
        return metricsRepository.getMaxByInterval(request, MetricName.FUEL);
    }

    @GetMapping("/histogram/max/temperature")
    public List<GetVehicleHistogramMetricResponse> getHistogramMileage(@Valid GetVehicleHistogramMetricRequest request) {
        return metricsRepository.getMaxByInterval(request, MetricName.TEMPERATURE);
    }
}
