# Start with a base image containing Java runtime
FROM amazoncorretto:21

# Add Maintainer Info
LABEL maintainer="group21"

# Add a volume pointing to /tmp
VOLUME /tmp

EXPOSE 8072

# The application's jar file
ARG JAR_FILE=build/libs/payment-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} payment-service.jar

# Once the container starts, run below command - Run the jar file  
CMD ["java","-jar","/payment-service.jar"]
