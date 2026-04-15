# Use lightweight Java runtime
FROM maven:3.9.4-eclipse-temurin-21-alpine

# Set working directory
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src

# Run the application
CMD ["./mvnw", "spring-boot:run"]