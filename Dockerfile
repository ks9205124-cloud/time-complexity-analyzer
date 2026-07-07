FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
ENTRYPOINT ["java", "-jar", "target/time-complexity-analyzer-0.0.1-SNAPSHOT.jar"]