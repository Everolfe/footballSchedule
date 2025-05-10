FROM maven:3.8.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

COPY mvnw pom.xml ./
# загрузка зависимостей
COPY ./src ./src
RUN mvn clean install -DskipTests


FROM eclipse-temurin:17-alpine

WORKDIR /app
EXPOSE 8080
COPY --from=build /app/target/*.jar /app/*.jar

ENTRYPOINT ["java","-jar","/app/*.jar"]