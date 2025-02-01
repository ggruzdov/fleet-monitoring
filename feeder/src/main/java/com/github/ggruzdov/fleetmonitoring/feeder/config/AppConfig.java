package com.github.ggruzdov.fleetmonitoring.feeder.config;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@EnableConfigurationProperties({
    GeneratorProperties.class,
    MqttProperties.class
})
public class AppConfig {

    private final MqttProperties properties;

    @Bean
    public MqttConnectionOptions mqttConnectionOptions() {
        var options = new MqttConnectionOptions();
        options.setCleanStart(false); // make the connection durable to survive restarts
        options.setSessionExpiryInterval(properties.sessionExpiryInterval());
        options.setConnectionTimeout(5);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
        options.setUserName(properties.username());
        options.setPassword(properties.password().getBytes(StandardCharsets.UTF_8));
        options.setServerURIs(properties.brokerUrls().toArray(String[]::new));
        options.setMaxReconnectDelay((int) Duration.ofSeconds(30).toMillis());
        return options;
    }

    @Bean
    @ServiceActivator(inputChannel = "metricsOutboundChannel")
    public Mqttv5PahoMessageHandler mqttOutbound() {
        var messageHandler = new Mqttv5PahoMessageHandler(mqttConnectionOptions(), properties.clientId());
        messageHandler.setCompletionTimeout(properties.completionTimeout().toMillis());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(properties.topic());
        messageHandler.setDefaultQos(properties.qos());
        messageHandler.setDefaultRetained(true);
        return messageHandler;
    }

    @Bean
    public MessageChannel metricsOutboundChannel() {
        return new DirectChannel();
    }
}
