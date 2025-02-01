package com.github.ggruzdov.fleetmonitoring.feeder.component;

import com.github.ggruzdov.fleetmonitoring.feeder.config.GeneratorProperties;
import com.github.ggruzdov.fleetmonitoring.feeder.domain.Metric;
import com.github.ggruzdov.fleetmonitoring.feeder.messaging.MetricSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class Feeder {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final GeneratorProperties properties;
    private final MetricGenerator metricGenerator;
    private final MetricSender metricSender;

    @Scheduled(fixedDelayString = "${generator.interval}")
    public void feed() {
        IntStream
            .range(properties.offset() + 1, properties.vehicles() + 1)
            .boxed()
            .forEach(i -> executor.execute(
                    () -> Arrays.stream(Metric.Name.values())
                        .forEach(metricName -> metricSender.send(metricGenerator.generateMetric(i, metricName)))
                )
            );
    }
}
