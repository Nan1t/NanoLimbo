FROM eclipse-temurin:8 AS j8-builder
ENV APP_SRC=/src
WORKDIR $APP_SRC
COPY gradlew build.gradle settings.gradle $APP_SRC/

COPY gradle $APP_SRC/gradle
COPY --chmod=0755 . $APP_SRC

RUN $APP_SRC/gradlew shadowJar

FROM eclipse-temurin:8
WORKDIR /data
RUN mkdir /opt/app
COPY --from=j8-builder /src/build/libs/*.jar /opt/app/nanolimbo.jar
EXPOSE 65535
CMD ["java", "-jar", "/opt/app/nanolimbo.jar"]
