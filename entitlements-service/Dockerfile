FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY . .
RUN ./gradlew :entitlements-service:bootJar --no-daemon
FROM eclipse-temurin:21-jre
COPY --from=build /src/entitlements-service/build/libs/entitlements-service-1.0.0.jar /app.jar
USER 10001
ENTRYPOINT ["java","-jar","/app.jar"]
