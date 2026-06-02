FROM maven:3.9.6-eclipse-temurin-21

# Аргументы сборки
ARG TEST_PROFILE=api,ui
ARG BASEAPIURL=http://putYourIp/openmrs/
ARG BASEUIURL=http://putYourIp/openmrs/spa
ARG BROWSERREMOTE=http://host.docker.internal:4444/wd/hub

# Переменные окружения (ВАЖНО: единые имена)
ENV TEST_PROFILE=${TEST_PROFILE}
ENV BASEAPIURL=${BASEAPIURL}
ENV BASEUIURL=${BASEUIURL}
ENV BROWSERREMOTE=${BROWSERREMOTE}

# Рабочая директория
WORKDIR /app

# Копируем pom.xml и скачиваем зависимости
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем проект
COPY . .

USER root

# Папка для логов
# RUN mkdir -p /app/logs

# Запуск тестов
CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    echo '>>> Running tests with profile ${TEST_PROFILE}' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    \
    echo '>>> Running surefire-report:report' ; \
    mvn -DskipTests=true surefire-report:report ; \
    } 2>&1 | tee /app/logs/run.log"