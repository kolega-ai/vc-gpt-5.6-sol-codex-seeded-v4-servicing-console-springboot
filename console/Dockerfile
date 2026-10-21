FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY . .
RUN ./gradlew :console:bootJar --no-daemon
FROM eclipse-temurin:21-jre
COPY --from=build /src/console/build/libs/console-1.0.0.jar /app.jar
USER root
RUN mkdir -p /data/uploads && chown -R 10001:10001 /data
USER 10001
ENTRYPOINT ["java","-jar","/app.jar"]
