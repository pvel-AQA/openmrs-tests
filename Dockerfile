FROM maven:3.9.8-eclipse-temurin-21

ARG TEST_PROFILE=api,ui
ARG APIBASEURL=http://localhost:80/openmrs
ARG UIBASEURL=http://putYourIp/openmrs/spa
ARG BROWSERREMOTE=http://putYourIp:4444/wd/hub

ENV TEST_PROFILE=${TEST_PROFILE}
ENV APIBASEURL=${APIBASEURL}
ENV UIBASEURL=${UIBASEURL}
ENV BROWSERREMOTE=${BROWSERREMOTE}

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

#copy all project in current folder
COPY . .

USER root

CMD /bin/bash -c " \
    mkdir -p /app/logs ; \
    { \
    echo '>>> Running tests with profile ${TEST_PROFILE}' ; \
    mvn test -q -P ${TEST_PROFILE} ; \
    \
    echo '>>> Running surefire-report:report' ; \
    mvn -DskipTests=true surefire-report:report ; \
    } 2>&1 | tee /app/logs/run.log"

