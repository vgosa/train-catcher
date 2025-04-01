# Start with a base image containing Java runtime
FROM amazoncorretto:21

# Add Maintainer Info
LABEL maintainer="group21"

# Add a volume pointing to /tmp
VOLUME /tmp

EXPOSE 8070

# The application's jar file
ARG JAR_FILE=build/libs/booking-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} booking-service.jar

# Once the container starts, run below command - Run the jar file  
CMD ["java","-jar","/booking-service.jar"]
