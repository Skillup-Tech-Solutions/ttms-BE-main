# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
COPY config ./config

RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/TTMS-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/config ./config

ENV SPRING_PROFILES_ACTIVE=dev
ENV SPRING_DATA_MONGODB_DATABASE=ttms
ENV SERVER_PORT=8081

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]