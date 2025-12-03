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

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/TTMS-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/config ./config

ENV SPRING_PROFILES_ACTIVE=dev
ENV SPRING_DATA_MONGODB_DATABASE=ttms
ENV COM_CUSTOM_FRONTENDURL=https://ttms.skilluptechbuzz.in

# Render provides PORT environment variable dynamically
EXPOSE ${PORT:-8081}

CMD ["sh", "-c", "java -Dserver.port=${PORT:-8081} -jar app.jar"]