# Start with a base image containing Java runtime
FROM amazoncorretto:21

# Add Maintainer Info
LABEL maintainer="group21"

# Add a volume pointing to /tmp
VOLUME /tmp

EXPOSE 8095

# The application's jar file
ARG JAR_FILE=build/libs/notification-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} notification-service.jar

# Once the container starts, run below command - Run the jar file  
CMD ["java","-jar","/notification-service.jar"]
