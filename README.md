# train-catcher
# Notification Service Setup

This service requires two Docker containers to be running: **ActiveMQ** (for message queuing) and **MailHog** (for email testing). Below are the steps to set them up.

---

## **1. ActiveMQ Setup**

ActiveMQ is used as the message broker for handling notifications. Run the following command to start an ActiveMQ container:

```bash
docker run -d --name activemq -p 61616:61616 -p 8161:8161 rmohr/activemq:latest
```

## **2. Mailhog Setup**

MailHog is used to simulate an email server for testing purposes. It captures all outgoing emails and provides a web interface to view them.
```bash
docker run -d --name mailhog -p 1025:1025 -p 8025:8025 mailhog/mailhog
```