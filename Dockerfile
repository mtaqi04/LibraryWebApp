# Start with an OpenJDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy all project files into the container
COPY . .

# Package the Spring Boot app using Maven
RUN ./mvnw clean package -DskipTests

# Expose the port the app runs on
EXPOSE 8080

# Run the .jar file
CMD ["java", "-jar", "target/*.jar"]
