FROM openjdk:17-jdk-slim AS build

WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jre-slim

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p uploads/photos
VOLUME ["/app/uploads"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]