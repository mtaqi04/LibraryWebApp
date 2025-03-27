# First stage: Build the app with Maven
FROM maven:3.9.2-eclipse-temurin-17 as builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Second stage: Run the app
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy only the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the app port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
