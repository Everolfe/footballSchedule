# Этап сборки
FROM maven:3-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Копируем только POM сначала для кэширования зависимостей
COPY pom.xml .
# Скачиваем зависимости (отдельный шаг для кэширования)
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение (указываем явный профиль если нужно)
RUN mvn package -DskipTests \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true \
    -Dcheckstyle.skip=true

# Этап выполнения
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем только JAR из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Добавляем non-root пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Открываем порт
EXPOSE 8080

# Запуск с JVM параметрами
ENTRYPOINT ["java", "-jar", "app.jar"]