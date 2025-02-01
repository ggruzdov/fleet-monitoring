package com.github.ggruzdov.fleetmonitoring.feeder.messaging;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "metricsOutboundChannel")
public interface MetricsMqttGateway {

    void sendToMqtt(String message);
}