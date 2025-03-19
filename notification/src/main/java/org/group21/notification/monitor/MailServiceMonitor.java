package org.group21.notification.monitor;

import lombok.extern.slf4j.Slf4j;
import org.group21.notification.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailServiceMonitor {

    private final EmailService emailService;
    private final JmsListenerEndpointRegistry jmsListenerEndpointRegistry;

    @Autowired
    public MailServiceMonitor(EmailService emailService, JmsListenerEndpointRegistry jmsListenerEndpointRegistry) {
        this.emailService = emailService;
        this.jmsListenerEndpointRegistry = jmsListenerEndpointRegistry;
    }

    @Scheduled(fixedDelay = 5000)
    public void checkMailServiceConnectivity() {
        boolean available = emailService.isMailServerAvailable();
        if (!available) {
            log.warn("Mail service is down. Stopping JMS listener containers.");
            jmsListenerEndpointRegistry.getListenerContainers().forEach(container -> {
                if (container.isRunning()) {
                    container.stop();
                    log.info("Stopped container: {}", container);
                }
            });
        } else {
            log.info("Mail service is up. Starting JMS listener containers if not already running.");
            jmsListenerEndpointRegistry.getListenerContainers().forEach(container -> {
                if (!container.isRunning()) {
                    container.start();
                    log.info("Started container: {}", container);
                }
            });
        }
    }
}
