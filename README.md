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

Camunda is used as the workflow orchestration engine for our application. It is embedded in our primary service, **trainSearch** and it can be accessed via the following URL, once the service is running:

```
http://localhost:<trainSearch_port>/camunda
```

---

## OPENApi Documentation

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

### 3. Build Docker Images

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

### 4. Deploy Kubernetes Manifests

Deploy the application manifests to your Minikube Kubernetes cluster:

```bash
kubectl apply -f ./k8s-manifests
```

### 5. Finding Service URLs

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
