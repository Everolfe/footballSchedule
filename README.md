# FootballSchedule

**footballSchedule** — это веб-приложение, которое позволяет пользователям просматривать информацию о футбольных матчах, включая расписание, команды, состав команд и другую статистику. Приложение разработано на Java с использованием Spring Boot Framework.

##  Функционал

-  Просмотр расписания футбольных матчей
-  Информация о командах и игроках
-  Поиск матчей по разным параметрам
-  Обновление данных в реальном времени (если используется API)

##  Технологии

- **Язык программирования**: Java 17
- **Фреймворк**: Spring Boot 3.x
- **База данных**: PostgreSQL
- **Сборка**: Maven
- **Документация API**: Swagger
- **Контейнеризация**: Docker

## [SonarCloud](https://sonarcloud.io/project/overview?id=Everolfe_footballSchedule)

##  Установка и запуск

### Требования

- Установленная Java 17 или выше.
- Установленный Maven.
- Установленная PostgreSQL.
- Установленный Docker (опционально, для запуска в контейнере).

### Как запустить проект
1. Clone the repository: `git clone https://github.com/Everolfe/footballSchedule.git`
2. Navigate to the project directory: `cd footballSchedule`
3. Build the project: `mvn clean install`
4. Run the application: `mvn spring-boot:run`

The application will start, and you can access the API at `http://localhost:8080/{request}`.
