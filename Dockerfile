FROM openjdk:11-jdk
VOLUME /tmp
COPY ./build/libs/sample-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
