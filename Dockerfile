# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory to /app
WORKDIR /app

# Copy the build artifact from the host to the container
COPY target/*.jar app.jar

# Make port 9000 available to the world outside this container
EXPOSE 9000

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
