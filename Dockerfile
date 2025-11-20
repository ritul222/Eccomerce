# Use official OpenJDK 17 slim image


FROM eclipse-temurin:17-jdk-alpine

# Set working directory inside the container
WORKDIR /app

# Copy Maven wrapper and pom.xml first (for dependency caching)
COPY mvnw pom.xml ./
COPY .mvn .mvn

# Give execute permission to mvnw
RUN chmod +x mvnw

# Copy source code
COPY src src

# Build the Spring Boot application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests

# Copy the generated JAR
COPY target/sb-ecom-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Set environment variables from Render (these will be injected automatically)
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
