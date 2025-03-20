package org.group21.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@EnableJms
@Configuration
@Slf4j
public class JmsConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${activemq.broker-username}")
    private String brokerUsername;

    @Value("${activemq.broker-password}")
    private String brokerPassword;

    @Value("${activemq.redelivery.delay}")
    private long redeliveryDelay;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        log.info("Connect to ActiveMQ host: {}", brokerUrl);

        if (brokerUsername != null && !brokerUsername.isEmpty() &&
                brokerPassword != null && !brokerPassword.isEmpty()) {
            connectionFactory.setUserName(brokerUsername);
            connectionFactory.setPassword(brokerPassword);
        }

        configureDeliveryPolicy(connectionFactory.getRedeliveryPolicy());
        return connectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory());
        // Enable transacted session so that exceptions result in rollback.
        factory.setSessionTransacted(true);
        factory.setErrorHandler(e -> log.error("An error occurred while processing a MQ message", e));
        return factory;
    }

    private RedeliveryPolicy configureDeliveryPolicy(RedeliveryPolicy redeliveryPolicy) {
        redeliveryPolicy.setInitialRedeliveryDelay(redeliveryDelay);
        redeliveryPolicy.setMaximumRedeliveries(2);
        redeliveryPolicy.setUseExponentialBackOff(true);
        redeliveryPolicy.setBackOffMultiplier(2);
        return redeliveryPolicy;
    }
}