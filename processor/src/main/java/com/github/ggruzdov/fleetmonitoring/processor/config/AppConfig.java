package com.github.ggruzdov.fleetmonitoring.processor.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MqttProperties.class)
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
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public Mqttv5PahoMessageDrivenChannelAdapter inbound() {
        var sharedTopic = String.format("$share/%s/%s", properties.groupId(), properties.topic());
        var adapter = new Mqttv5PahoMessageDrivenChannelAdapter(mqttConnectionOptions(), properties.clientId(), sharedTopic);
        adapter.setCompletionTimeout(properties.completionTimeout().toMillis());
        adapter.setQos(properties.qos());
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }
}
