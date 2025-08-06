# Этап 1: сборка проекта
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Копируем всё в контейнер
COPY . .

# Собираем проект (без тестов)
RUN ./mvnw clean package -DskipTests

# Этап 2: финальный образ
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Запускаем Spring Boot приложение
ENTRYPOINT ["java", "-jar", "app.jar"]