FROM openjdk:17.0.2-jdk-alpine3.14
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle/
COPY src $APP_HOME/src/
RUN ./gradlew build --no-daemon -x test

FROM openjdk:17.0.2-slim-buster
EXPOSE 8080
RUN mkdir /app
WORKDIR /app
RUN addgroup appuser && adduser --ingroup appuser appuser
COPY run.sh .
RUN chmod +x run.sh
COPY --from=builder /usr/app/build/libs/*-SNAPSHOT.jar app.jar
RUN chown -R appuser:appuser /app
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]