FROM openjdk:17

ENV PREFER_IPV4_STACK ${PREFER_IPV4_STACK}
ENV PROFILES_ACTIVE ${PROFILES_ACTIVE}
ENV JVM_USE_CONTAINER_SUPPORT ${JVM_USE_CONTAINER_SUPPORT}
ENV JVM_MIN_RAM ${JVM_MIN_RAM}
ENV JVM_MAX_RAM ${JVM_MAX_RAM}
ENV SERVER_PORT ${SERVER_PORT}
ENV FILE_ENCODING ${FILE_ENCODING}


RUN groupadd -r appuser -g 1000 && useradd -u 1000 -r -g appuser -m -d /home/appuser -s /sbin/nologin -c "Docker image user" appuser
RUN mkdir /app && chown appuser:appuser /app
WORKDIR /app
USER appuser

#COPY --chown=appuser:appuser . .

#ARG JAR_FILE=build/libs/HyundaiUserAuthApplication-0.0.1.jar

COPY --chown=appuser:appuser build/libs/*[^plain].jar app.jar

RUN chmod +x app.jar

EXPOSE 8081
ENTRYPOINT ["java",\
            "-jar",\
            "app.jar",\
            "-Djava.net.preferIPv4Stack=${PREFER_IPV4_STACK}",\
            "-Dspring.profiles.active=${PROFILES_ACTIVE}",\
            "-XX:${JVM_USE_CONTAINER_SUPPORT}",\
            "-Dserver.port=${SERVER_PORT}",\
            "-Dfile.encoding=${FILE_ENCODING}",\
            "-XX:MinRAMPercentage=${JVM_MIN_RAM}",\
            "-XX:MaxRAMPercentage=${JVM_MAX_RAM}"\
           ]


#FROM amazoncorretto:21.0.4
#ARG JAR_FILE=build/libs/*.jar
#COPY ${JAR_FILE} parkingcloudervice.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-Dspring.profiles.active=local", "-XX:+UseContainerSupport", "-Dserver.port=8080", "-Dfile.encoding=UTF-8", "-jar", "/parkingcloudervice.jar"]
