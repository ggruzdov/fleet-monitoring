package com.github.ggruzdov.fleetmonitoring.processor.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ggruzdov.fleetmonitoring.processor.domain.Metric;
import com.github.ggruzdov.fleetmonitoring.processor.service.MetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetricHandler {

    private final MetricService metricService;
    private final ObjectMapper objectMapper;

    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public void handleMessage(Message<?> message) {
        try {
            String payload = new String((byte[]) message.getPayload());
            log.debug("Received message {}", payload);
            var metric = objectMapper.readValue(payload, Metric.class);
            metricService.queueMetric(metric);
        } catch (Exception e) {
            log.error("Error processing MQTT message", e);
        }
    }
}