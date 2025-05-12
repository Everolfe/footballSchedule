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

# Устанавливаем postgresql-client для выполнения скриптов
RUN apk add --no-cache postgresql-client

# Копируем JAR и SQL скрипты
COPY --from=build /app/target/*.jar /app/app.jar
COPY db-init/ /db-init/

# Создаем скрипт для запуска
RUN echo $'#!/bin/sh\n\
java -jar /app/app.jar &\n\
sleep 15\n\
PGPASSWORD=$DB_PASSWORD psql -h db -U $DB_USER -d $DB_NAME -f /db-init/init.sql\n\
wait\n' > /app/entrypoint.sh && \
    chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]