FROM maven:3.8.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
RUN mvn dependency:go-offline -B

COPY src/ src/
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-alpine

WORKDIR /app
EXPOSE 8080

COPY --from=build /app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]