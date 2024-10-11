# Use an official OpenJDK runtime as a parent image
FROM openjdk:22-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file into the container at /app
COPY target/final-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port the Spring Boot app runs on (default 8080)
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
