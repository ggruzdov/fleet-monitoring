package com.github.ggruzdov.fleetmonitoring.feeder.config;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.annotation.EnableScheduling;

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
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false); // make the connection durable to survive restarts
        options.setConnectionTimeout(5);
        options.setKeepAliveInterval(60);
        options.setAutomaticReconnect(true);
        options.setUserName(properties.username());
        options.setPassword(properties.password().toCharArray());
        options.setServerURIs(properties.brokerUrls().toArray(String[]::new));
        options.setMaxReconnectDelay((int) Duration.ofSeconds(30).toMillis());
        options.setMaxInflight(properties.maxInflight());
        return options;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "metricsOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(properties.clientId(), mqttClientFactory());
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
