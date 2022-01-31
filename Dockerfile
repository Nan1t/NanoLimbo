FROM openjdk:17-alpine3.14

ENV JAVA_HEAP_SIZE="1G"
ENV JAVA_ARGS=""

WORKDIR /app

COPY build/libs/NanoLimbo-*-all.jar server.jar

ENTRYPOINT java $JAVA_ARGS -Xmx$JAVA_HEAP_SIZE -Xms$JAVA_HEAP_SIZE -jar server.jar