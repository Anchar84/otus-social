FROM gradle:8.3.0-jdk17

WORKDIR /app
COPY . /app/.

RUN cd /app && chmod +x ./gradlew && ./gradlew build
RUN mv ./build/libs/*.jar ./app.jar

CMD exec java $JAVA_OPTS -jar /app/app.jar

EXPOSE 8081
