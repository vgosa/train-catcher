package org.group21.trainsearch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JMSProducer {
    private final JmsTemplate jmsTemplate;

    public JMSProducer(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendMessage(Object message, String queueName) {
        try {
            log.info("Message sent to queue: " + queueName);
            jmsTemplate.convertAndSend(queueName, message);
        } catch (Exception e) {
            log.error("Error sending message to queue: " + queueName, e);
        }
    }
}
