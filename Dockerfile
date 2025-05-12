# Стадия сборки
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Копируем только файлы, необходимые для загрузки зависимостей
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src/ src/
RUN mvn clean package -DskipTests

# Финальная стадия
FROM eclipse-temurin:17-alpine

WORKDIR /app
EXPOSE 8080

# Копируем только JAR файл (без postgresql-client и скриптов)
COPY --from=build /app/target/*.jar /app/app.jar

# Упрощенная команда запуска
ENTRYPOINT ["java", "-jar", "/app/app.jar"]