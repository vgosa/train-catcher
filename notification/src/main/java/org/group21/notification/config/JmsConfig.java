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
//TODO: Configure JMS to retry emails if the email server is down!

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${activemq.broker-username}")
    private String brokerUsername;

    @Value("${activemq.broker-password}")
    private String brokerPassword;

    @Value("${activemq.redelivery.delay}")
    private long redeliveryDelay;


    public JmsConfig() {

    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();

        activeMQConnectionFactory.setBrokerURL(brokerUrl);

        log.info("Connect to ActiveMQ host: {}", brokerUrl);

        if (brokerUsername != null && !brokerUsername.isEmpty() && brokerPassword != null && !brokerPassword.isEmpty()) {
            activeMQConnectionFactory.setUserName(brokerUsername);
            activeMQConnectionFactory.setPassword(brokerPassword);
        }

        configureDeliveryPolicy(activeMQConnectionFactory.getRedeliveryPolicy());

        return activeMQConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory());
        factory.setErrorHandler((e) -> {
            log.error("An error occured while processing a MQ message", e);
        });
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
