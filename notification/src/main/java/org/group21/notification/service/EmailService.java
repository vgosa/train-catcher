package org.group21.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Checks if the mail server is reachable by attempting a socket connection.
     * @return true if reachable, false otherwise.
     */
    public boolean isMailServerAvailable() {
        try (Socket socket = new Socket()) {
            // Attempt connection with a 2-second timeout.
            socket.connect(new InetSocketAddress(mailHost, mailPort), 2000);
            return true;
        } catch (IOException e) {
            log.error("Mail server is not reachable at {}:{}", mailHost, mailPort);
            return false;
        }
    }

    public void sendEmail(String recipient, String subject, String text) throws MailException {
        if (!isMailServerAvailable()) {
            throw new RuntimeException("Mail service is down. Email not sent.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(text);
        log.info("Sending email to " + recipient);
        mailSender.send(message);
    }
}