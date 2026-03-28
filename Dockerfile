FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM openjdk:21-slim
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app/shop.jar
ENTRYPOINT ["java", "-jar", "/app/shop.jar"]