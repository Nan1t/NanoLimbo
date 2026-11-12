FROM eclipse-temurin:21-jre-noble

COPY build/libs/NanoLimbo.jar /opt/nanolimbo/NanoLimbo.jar
COPY src/main/resources/settings.yml /opt/nanolimbo/settings.yml
COPY --chmod=755 docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN mkdir /data \
    && chown 10001:10001 /data \
    && sed -i 's/^  ip: "localhost"$/  ip: ""/' /opt/nanolimbo/settings.yml \
    && grep -q '^  ip: ""$' /opt/nanolimbo/settings.yml

USER 10001:10001
WORKDIR /data
EXPOSE 65535/tcp

ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD ["java", "-jar", "/opt/nanolimbo/NanoLimbo.jar"]
