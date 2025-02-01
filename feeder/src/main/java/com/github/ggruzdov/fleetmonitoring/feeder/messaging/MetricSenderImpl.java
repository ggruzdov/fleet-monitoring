package com.github.ggruzdov.fleetmonitoring.feeder.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ggruzdov.fleetmonitoring.feeder.domain.Metric;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricSenderImpl implements MetricSender {

    private final MetricsMqttGateway metricsMqttGateway;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void send(Metric metric) {
        var payload = objectMapper.writeValueAsString(metric);
        metricsMqttGateway.sendToMqtt(payload);
    }
}
