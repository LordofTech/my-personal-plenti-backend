# Use an official Maven image to build the application
FROM maven:3.8.1-openjdk-17 AS build

# Set the working directory to /app
WORKDIR /app

# Copy the pom.xml and the src directory to the working directory
COPY pom.xml .
COPY src ./src

# Run Maven to build the project and package the application into a JAR file
RUN mvn clean package -DskipTests

# Use an official OpenJDK runtime image to run the application
FROM maven:3.8.1-openjdk-17

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/plenti-backend-0.0.1-SNAPSHOT.jar plenti-backend.jar

# Expose port 8080 for the application
EXPOSE 8080

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "plenti-backend.jar"]

