# Stage 1: Build the application
FROM openjdk:17-jdk-slim as build

# Set working directory
WORKDIR /app

# Copy the Gradle wrapper and the build files into the container
COPY . .

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Download dependencies and build the project inside the container
RUN ./gradlew build --no-daemon

# Ensure build/libs exists and contains the expected .jar file
RUN if [ -d "build/libs" ]; then echo "libs directory exists"; ls -la build/libs; else echo "libs directory not found"; exit 1; fi

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the compiled app from the build stage
COPY --from=build /app/build/libs /app/libs

# Expose the application port
EXPOSE 8081

# Set the entry point to run the app
CMD ["java", "-jar", "libs/KtorAPI-all.jar"]
