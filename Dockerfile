# Use a base image with Java
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the build artifact (JAR file) to the container
COPY . .

# build the project avoiding tests
RUN ./gradlew clean build -x test

# Expose the application port (default is 8080)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "./build/libs/project-0.0.1-SNAPSHOT.jar"]