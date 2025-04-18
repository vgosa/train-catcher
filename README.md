# Train-Catcher: The Train Catching App

## Development Environment Setup
## Pre-requisites:

- Java 21
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

Camunda is used as the workflow orchestration engine for our application. It is embedded in our primary service, **trainSearch** and it can be accessed via the following URL, once the service is running:

```
http://localhost:<trainSearch_port>/camunda
```

---

## OpenAPI Documentation

The API documentation can be accessed [here](https://vgosa.github.io/train-catcher/).

---

## Kubernetes Deployment

This section outlines the steps to deploy the application using Kubernetes with a local Minikube cluster.

### 1. Start Minikube

Start a local Kubernetes cluster using Minikube with default parameters:

```bash
minikube start
```

You can also specify the number of CPU cores and memory size for Minikube:

```bash
minikube start --cpus 2 --memory 4096
```

### 2. Configure Docker Environment

Point your local Docker environment to the Minikube Docker daemon:

```bash
minikube docker-env | Invoke-Expression
```

Set up the required environment variables for service discovery and messaging. Depending on your shell, apply the following commands:

**For PowerShell:**

```powershell
$env:SPRING_CLOUD_CONSUL_HOST = "consul"
$env:SPRING_MAIL_HOST = "mailhog"
$env:SPRING_ACTIVEMQ_BROKER_URL = "tcp://activemq:61616"
```

**For Bash:**

```bash
export SPRING_CLOUD_CONSUL_HOST=consul
export SPRING_MAIL_HOST=mailhog
export SPRING_ACTIVEMQ_BROKER_URL="tcp://activemq:61616"
```

### 3. Build JAR files

Build the jar files for all the services via the dedicated script:

```bash
chmod +x build-all.sh
./build-all.sh
```

### 4. Build Docker Images

Build the Docker images for all the services:

```bash
docker build -t booking ./booking
docker build -t notification ./notification
docker build -t payment ./payment
docker build -t ticket ./ticket
docker build -t train-search ./trainSearch
docker build -t user ./user
docker build -t train-operator ./trainOperator
```

### 5. Deploy Kubernetes Manifests

Deploy the application manifests to your Minikube Kubernetes cluster:

```bash
kubectl apply -f ./k8s-manifests
```

### 6. Finding Service URLs

After deploying, you can easily find the URLs for the relevant services using the following commands:

- For **train-search**:

```bash
minikube service train-search --url
```

- For **MailHog** (if deployed as a service):

```bash
minikube service mailhog --url
```

These commands will output the accessible URLs for the services running inside your Minikube cluster.

## Kubernetes Cluster Description

The cluster consists of 12 deployments and 5 services, each serving a specific purpose. The services are designed to work together to provide a seamless experience for users searching and booking train tickets. Below is a brief description of each component in the cluster:

### 1. Deployments
- **train-search**: The main service for searching and booking train tickets.
- **train-operator**: Manages train operations and schedules. Each independent train operator has its own deployment (by default, 1 and 2 represent NS and Arriva).
- **user**: Manages user accounts and in-app balances.
- **booking**: Handles booking operations and interactions with the train-search service.
- **notification**: Manages notifications and email sending (sends tickets to users).
- **payment**: Handles payment operations (mostly persists payments to the database for auditing).
- **ticket**: Manages ticket creation and auditing.
- **activemq**: The message broker for handling emails (ticket service -> notification service -> mailhog).
- **nginx**: The reverse proxy and API gateway for the application. It secures the internal services and only exposes the necessary endpoints to the outside world. (train search only)
- **consul**: The service discovery tool for the application. It allows services to discover each other and communicate with static hostnames.
- **mailhog**: The email testing tool for capturing outgoing emails. It provides a web interface to view the emails sent by the application.

### 2. Services
- **train-search**: Exposes the train-search service to the outside world. It is accessible by declaring a service with NodePort type.
- **nginx**: Exposes the nginx service to the outside world. It is accessible by declaring a service with NodePort type. (for debugging purposes)
- **consul**: Exposes the consul service to the outside world. It is accessible by declaring a service with NodePort type. (for debugging purposes)
- **mailhog**: Exposes the mailhog service to the outside world. It is accessible by declaring a service with NodePort type. (for email mocking)
- **activemq**: Exposes the activemq service to the outside world. It is accessible by declaring a service with NodePort type. (for debugging purposes)

### 3. ConfigMaps
- **trainoperator-instances**: Since we want to differentiate between train operators, we need to set up a config map that contains the train operator instances. This is used by the train-operator service to differentiate between the different train operators. The default values are 1 and 2, which represent NS and Arriva respectively. (in development they are managed with local SpringBoot configuration files for instance1, instance2, etc.)
- **nginx-config**: The nginx configuration file for the nginx service. It is used to configure the reverse proxy and API gateway for the application.

### 4. Persistent Volumes
- **train-operator-data**: Our application uses H2 in-memory databases for the services. However, we need to persist the data for the train-operator service. This is done by creating a persistent volume that is mounted to the train-operator service. The default values are 1 and 2, which represent NS and Arriva respectively. (in development they are managed with local SpringBoot configuration files for instance1, instance2, etc.)





