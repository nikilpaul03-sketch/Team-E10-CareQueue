FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -Dspring.profiles.active=demo package

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=demo \
    PORT=8081
COPY --from=build --chown=10001:10001 /workspace/target/healthcare-system-0.0.1-SNAPSHOT.jar /app/app.jar

USER 10001:10001
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
