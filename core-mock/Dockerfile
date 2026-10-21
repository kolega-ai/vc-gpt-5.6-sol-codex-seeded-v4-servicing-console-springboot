FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY . .
RUN ./gradlew :core-mock:bootJar --no-daemon
FROM eclipse-temurin:21-jre
COPY --from=build /src/core-mock/build/libs/core-mock-1.0.0.jar /app.jar
USER 10001
ENTRYPOINT ["java","-jar","/app.jar"]
