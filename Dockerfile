# Use official OpenJDK 17 image
FROM eclipse-temurin:17-jdk

# Set working directory inside the container
WORKDIR /app

# Copy Maven wrapper and pom.xml from current folder
COPY mvnw ./
COPY pom.xml ./
COPY .mvn .mvn

# Give execute permission to mvnw
RUN chmod +x mvnw

# Copy source code
COPY src src

# Build the project inside Docker
RUN ./mvnw clean package -DskipTests

# Copy the generated JAR to a simple name
RUN cp target/Ecommerce-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Environment variables (will be injected by Render)
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}

# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
