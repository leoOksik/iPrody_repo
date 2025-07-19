# Используем минимальный runtime-образ с JDK 21
FROM eclipse-temurin:21-jre-alpine

# Аргумент — имя jar-файла
ARG JAR_FILE=target/payment-service-app-0.0.1-SNAPSHOT.jar

# Рабочая директория
WORKDIR /app

# Копируем jar внутрь контейнера
COPY ${JAR_FILE} app.jar

# Запускаем
ENTRYPOINT ["java", "-jar", "app.jar"]
