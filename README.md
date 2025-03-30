# Train-Catcher: The Train Catching App


## Pre-requisites:

- Java 17
- Docker

## Running the application:

1. Clone the repository
2. Setup the needed docker containers (ActiveMQ, MailHog and Consul)
3. Run the services (Development through IntelliJ Services or Docker Compose)

## Consul Setup

This service requires a **Consul** service discovery container to be running. Below are the steps to set it up.

```bash
docker run --env=PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin --env=BIN_NAME=consul --env=PRODUCT_VERSION=1.20.4 --env=PRODUCT_NAME=consul --volume=/consul/data --network=bridge --workdir=/ -p 8500:8500 -p 8600:8600/udp --restart=no --runtime=runc -d hashicorp/consul
```

## Notification Service Setup

This service requires two Docker containers to be running: **ActiveMQ** (for message queuing) and **MailHog** (for email testing). Below are the steps to set them up.

---

### **1. ActiveMQ Setup**

ActiveMQ is used as the message broker for handling notifications. Run the following command to start an ActiveMQ container:

```bash
docker run -d --name activemq -p 61616:61616 -p 8161:8161 rmohr/activemq:latest
```

### **2. Mailhog Setup**

MailHog is used to simulate an email server for testing purposes. It captures all outgoing emails and provides a web interface to view them.
```bash
docker run -d --name mailhog -p 1025:1025 -p 8025:8025 mailhog/mailhog
```

---

## Camunda

Camunda is used as the workflow orchestration engine for our application. It is embedded in our primary service, **trainSearch** and it can be accessed
via the following URL, once the service is running:

```bash
http://localhost:<trainSearch_port>/camunda
```

---

## OPENApi Documentation

The API documentation can be accessed <a href="https://vgosa.github.io/train-catcher/">here</a>.