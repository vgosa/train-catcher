# Start with a base image containing Java runtime
FROM amazoncorretto:21

# Add Maintainer Info
LABEL maintainer="group21"

# Add a volume pointing to /tmp
VOLUME /tmp

# Optionally expose a default port (this is for documentation purposes;
# the actual port is set via your instance-specific config)
EXPOSE 8090

# The application's jar file
ARG JAR_FILE=build/libs/trainOperator-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} train-operator-service.jar

# When the container starts, run the jar file.
# At runtime, you can override the Spring config location if needed.
CMD ["java", "-jar", "/train-operator-service.jar"]
