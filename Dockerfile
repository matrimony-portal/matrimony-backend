FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p uploads/photos && \
    addgroup -S spring && \
    adduser -S -G spring spring && \
    chown -R spring:spring /app

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:--Xms128m -Xmx280m -XX:+UseSerialGC -XX:MaxMetaspaceSize=100m -Xss256k} -jar app.jar"]