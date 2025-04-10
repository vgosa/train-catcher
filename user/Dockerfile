# Start with a base image containing Java runtime
FROM alpine/java:21-jre

# Add Maintainer Info
LABEL maintainer="group21"

# Add a volume pointing to /tmp
VOLUME /tmp

EXPOSE 8085

# The application's jar file
ARG JAR_FILE=build/libs/user-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} user-service.jar

# Once the container starts, run below command - Run the jar file  
CMD ["java","-jar","/user-service.jar"]
