package com.github.ggruzdov.fleetmonitoring.feeder.component;

import com.github.ggruzdov.fleetmonitoring.feeder.config.GeneratorProperties;
import com.github.ggruzdov.fleetmonitoring.feeder.domain.Metric;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Slf4j
@Component
public class MetricGenerator {

    private static final float MIN_FUEL_VALUE = 10f;
    private static final float MAX_FUEL_VALUE = 200f;
    private static final float MAX_SPEED = 180f;
    private static final float MIN_TEMPERATURE = -20f;
    private static final float MAX_TEMPERATURE = 100f;

    private final Random random;
    private final Map<String, Metric> vehicleMetrics;

    public MetricGenerator(GeneratorProperties properties) {
        random = new SecureRandom();
        vehicleMetrics = new ConcurrentHashMap<>(properties.vehicles());

        // Set initial metrics
        IntStream
            .range(properties.offset() + 1, properties.vehicles() + 1)
            .boxed()
            .forEach(vehicleId -> Arrays.stream(Metric.Name.values())
                .forEach(metricName -> vehicleMetrics.put(vehicleId + metricName.name(), new Metric(
                    Instant.now(),
                    vehicleId,
                    vehicleId + metricName.deviceIdOffset,
                    metricName.name(),
                    generateNextValue(metricName, 1f)
                ))));
    }

    public Metric generateMetric(Integer vehicleId, Metric.Name metricName) {
        return vehicleMetrics.computeIfPresent(vehicleId + metricName.name(), (key, val) -> new Metric(
            Instant.now(),
            vehicleId,
            vehicleId + metricName.deviceIdOffset,
            metricName.name(),
            generateNextValue(metricName, val.value())
        ));
    }

    private float generateNextValue(Metric.Name metricName, float currentValue) {
        return switch (metricName) {
            case FUEL -> currentValue <= MIN_FUEL_VALUE ? MAX_FUEL_VALUE : round(currentValue - (currentValue / 1000));
            case MILEAGE -> round(currentValue + (currentValue / 100));
            case SPEED -> round(random.nextFloat(MAX_SPEED));
            case TEMPERATURE -> round(random.nextFloat(MIN_TEMPERATURE, MAX_TEMPERATURE));
        };
    }

    private float round(float val) {
        return BigDecimal.valueOf(val).setScale(2, RoundingMode.CEILING).floatValue();
    }
}
