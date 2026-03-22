FROM maven:3.9.14-eclipse-temurin-25 AS builder


WORKDIR /home/build
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests -Pnative-jlink

RUN jar xvf target/*jar &&\
    jdeps \
    --ignore-missing-deps \
    -q \
    --recursive \
    --multi-release 25 \
    --print-module-deps \
    --class-path 'BOOT-INF/lib/*' \
    --module-path 'BOOT-INF/lib/*' \
    target/*.jar > jre-deps.txt &&\
    jlink \
    --verbose \
    --add-modules $(cat jre-deps.txt) \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress 2 \
    --output jre

FROM debian:bullseye-slim

RUN useradd -m app

WORKDIR /home/app

COPY --from=builder /home/build/jre ./jre
COPY --from=builder /home/build/target/*.jar app.jar

ENV JAVA_HOME=/home/app/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

EXPOSE 8080
USER app

ENTRYPOINT ["java", "-jar", "app.jar"]
