FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
EXPOSE 8080
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-Dserver.port=8080","-jar","/app.jar"]
