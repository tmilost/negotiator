# Build jar file with dependencies
FROM maven:3.9.1-eclipse-temurin-17-focal as BUILD_IMAGE
COPY src /app/src
COPY pom.xml /app
WORKDIR /app
RUN mvn -B clean package -Dmaven.test.skip=true


# Runtime image
FROM eclipse-temurin:17-jre-focal
USER 1001
WORKDIR /app
COPY src/main/resources/data-h2.sql /app
COPY --from=BUILD_IMAGE /app/target/negotiator-spring-boot.jar /app/negotiator.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=${PROFILE}", "negotiator.jar"]