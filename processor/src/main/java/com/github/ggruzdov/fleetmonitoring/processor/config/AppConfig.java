package com.github.ggruzdov.fleetmonitoring.processor.config;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MqttProperties.class)
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
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        var adapter = new MqttPahoMessageDrivenChannelAdapter(
            properties.clientId(),
            mqttClientFactory(),
            properties.topic()
        );
        adapter.setCompletionTimeout(properties.completionTimeout().toMillis());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(properties.qos());
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }
}
