# Stage 1: Build the application
FROM maven:3.9.9-amazoncorretto-17 AS build

# Set the working directory inside the container
WORKDIR /Brewview

COPY . ./

# Build the Spring Boot application using Maven
# -DskipTests is used to skip tests during the build, which might be desired for Docker image creation
RUN mvn clean package -DskipTests

# Stage 2: Create the final runtime image
FROM amazoncorretto:17

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the 'build' stage
# Replace 'Brewview-1.0-SNAPSHOT.jar' with the actual name of your generated JAR file
COPY --from=build /Brewview/target/Brewview-1.0-SNAPSHOT.jar .

# Expose the port your Spring Boot application listens on (default is 8080)
EXPOSE 8080

# Define the command to run the Spring Boot application
CMD ["java", "-jar", "Brewview-1.0-SNAPSHOT.jar"]